/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.providers;

import com.jdimension.jlawyer.domain.enrichment.model.PersonRegistryResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderCapabilities;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import com.jdimension.jlawyer.domain.enrichment.model.RegistryProvenance;
import com.jdimension.jlawyer.domain.enrichment.spi.PersonRegistryProvider;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

/**
 * Provedor oficial SERPRO Consulta CPF V3 (https://gateway.apiserpro.serpro.gov.br/consulta-cpf-df/v3/{cpf}).
 * Requer autenticação OAuth2 (Bearer Token) e envio de CPF + Data de Nascimento.
 *
 * @author BR-LAWYER Team
 */
public class SerproCpfProvider implements PersonRegistryProvider {

    private static final Logger log = Logger.getLogger(SerproCpfProvider.class.getName());
    public static final String PROVIDER_ID = "serpro-cpf";
    private static final String DEFAULT_BASE_URL = "https://gateway.apiserpro.serpro.gov.br/consulta-cpf-df/v3";
    private static final String TOKEN_URL = "https://gateway.apiserpro.serpro.gov.br/token";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final HttpClient httpClient;
    private String cachedToken;
    private long tokenExpiryTimestamp;

    public SerproCpfProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayName() {
        return "SERPRO Consulta CPF V3 (Oficial / RFB)";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        ProviderCapabilities cap = new ProviderCapabilities();
        cap.setSupportsCpf(true);
        cap.setSupportsCnpj(false);
        cap.setSupportsAddress(false);
        cap.setRequiresCredentials(true);
        cap.setSelfHostable(false);
        cap.setOfficialGovSource(true);
        return cap;
    }

    private synchronized String obtainBearerToken(ProviderConfig config) throws Exception {
        if (config == null || config.getApiKey() == null || config.getApiKey().trim().isEmpty() ||
            config.getApiSecret() == null || config.getApiSecret().trim().isEmpty()) {
            throw new IllegalStateException("Credenciais SERPRO não configuradas. Preencha o Consumer Key e Consumer Secret.");
        }

        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTimestamp) {
            return cachedToken;
        }

        String authHeader = "Basic " + Base64.getEncoder().encodeToString(
                (config.getApiKey().trim() + ":" + config.getApiSecret().trim()).getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest tokenReq = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", authHeader)
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpResponse<String> response = httpClient.send(tokenReq, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na autenticação OAuth2 SERPRO: HTTP " + response.statusCode() + " - " + response.body());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body());
        this.cachedToken = (String) json.get("access_token");
        Object expiresInObj = json.get("expires_in");
        long expiresIn = (expiresInObj instanceof Number) ? ((Number) expiresInObj).longValue() : 3600L;
        // Expira 5 minutos antes para margem de segurança
        this.tokenExpiryTimestamp = System.currentTimeMillis() + (expiresIn - 300) * 1000L;

        return this.cachedToken;
    }

    @Override
    public boolean testConnection(ProviderConfig config) throws Exception {
        String token = obtainBearerToken(config);
        return token != null && !token.isEmpty();
    }

    @Override
    public PersonRegistryResult lookupPerson(String cpf, Date birthDate, ProviderConfig config) throws Exception {
        if (cpf == null || !BrazilianDocumentValidator.isValidCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido ou não informado.");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("A Consulta CPF V3 do SERPRO exige obrigatoriamente a Data de Nascimento.");
        }

        String cleanCpf = cpf.replaceAll("[^0-9]", "").trim();
        String formattedBirthDate = ISO_DATE_FORMAT.format(birthDate);
        String token = obtainBearerToken(config);

        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;

        String targetUrl = baseUrl.endsWith("/")
                ? baseUrl + cleanCpf + "?dataNascimento=" + formattedBirthDate
                : baseUrl + "/" + cleanCpf + "?dataNascimento=" + formattedBirthDate;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "BR-LAWYER-Legal-Suite/1.0")
                .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 6000))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            log.warn("CPF não localizado na base do SERPRO: " + cleanCpf);
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na Consulta CPF SERPRO: HTTP " + response.statusCode() + " - " + response.body());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body());

        PersonRegistryResult res = new PersonRegistryResult();
        res.setCpf(BrazilianDocumentValidator.formatCpf(cleanCpf));
        res.setCleanCpf(cleanCpf);
        res.setFullName((String) json.get("nome"));
        res.setSocialName((String) json.get("nomeSocial"));
        res.setBirthDate(birthDate);

        JSONObject situacao = (JSONObject) json.get("situacao");
        if (situacao != null) {
            String desc = (String) situacao.get("descricao");
            res.setStatusDescription(desc);
            if (desc != null) {
                String upper = desc.toUpperCase();
                if (upper.contains("REGULAR")) res.setStatus(PersonRegistryResult.PersonStatus.REGULAR);
                else if (upper.contains("PENDENTE")) res.setStatus(PersonRegistryResult.PersonStatus.PENDENTE_REGULARIZACAO);
                else if (upper.contains("SUSPENSA")) res.setStatus(PersonRegistryResult.PersonStatus.SUSPENSA);
                else if (upper.contains("CANCELADA")) res.setStatus(PersonRegistryResult.PersonStatus.CANCELADA_MULTIPLICIDADE);
                else if (upper.contains("FALECIDO") || upper.contains("OBITO")) res.setStatus(PersonRegistryResult.PersonStatus.TITULAR_FALECIDO);
                else if (upper.contains("NULA")) res.setStatus(PersonRegistryResult.PersonStatus.NULA);
            }
        }

        Object resExt = json.get("residenteExterior");
        if (resExt != null) res.setResidentAbroad(Boolean.parseBoolean(resExt.toString()));

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "SERPRO / Receita Federal do Brasil");
        prov.setConfidenceScore(1.0);
        prov.addFieldProvenance("nome", res.getFullName());
        prov.addFieldProvenance("situacao", res.getStatusDescription());
        res.setProvenance(prov);

        return res;
    }
}
