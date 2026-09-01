/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.providers;

import com.jdimension.jlawyer.domain.enrichment.model.*;
import com.jdimension.jlawyer.domain.enrichment.spi.CompanyRegistryProvider;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Provedor oficial SERPRO Consulta CNPJ Empresa (https://gateway.apiserpro.serpro.gov.br/consulta-cnpj-df/v2/empresa/{cnpj}).
 *
 * @author BR-LAWYER Team
 */
public class SerproCnpjProvider implements CompanyRegistryProvider {

    private static final Logger log = Logger.getLogger(SerproCnpjProvider.class.getName());
    public static final String PROVIDER_ID = "serpro-cnpj";
    private static final String DEFAULT_BASE_URL = "https://gateway.apiserpro.serpro.gov.br/consulta-cnpj-df/v2/empresa";
    private static final String TOKEN_URL = "https://gateway.apiserpro.serpro.gov.br/token";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final HttpClient httpClient;
    private String cachedToken;
    private long tokenExpiryTimestamp;

    public SerproCnpjProvider() {
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
        return "SERPRO Consulta CNPJ Oficial (RFB)";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        ProviderCapabilities cap = new ProviderCapabilities();
        cap.setSupportsCpf(false);
        cap.setSupportsCnpj(true);
        cap.setSupportsQsa(true);
        cap.setSupportsAddress(true);
        cap.setSupportsCnae(true);
        cap.setSupportsCorporateStatus(true);
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
        this.tokenExpiryTimestamp = System.currentTimeMillis() + (expiresIn - 300) * 1000L;

        return this.cachedToken;
    }

    @Override
    public boolean testConnection(ProviderConfig config) throws Exception {
        String token = obtainBearerToken(config);
        return token != null && !token.isEmpty();
    }

    @Override
    public CompanyRegistryResult lookupCompany(String cnpj, ProviderConfig config) throws Exception {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ inválido ou vazio.");
        }

        String clean = cnpj.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        String token = obtainBearerToken(config);
        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;

        String targetUrl = baseUrl.endsWith("/") ? baseUrl + clean : baseUrl + "/" + clean;

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
            log.warn("CNPJ não localizado na base oficial SERPRO: " + clean);
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na consulta CNPJ SERPRO. HTTP " + response.statusCode() + " - " + response.body());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body());

        CompanyRegistryResult res = new CompanyRegistryResult();
        res.setCnpj(BrazilianDocumentValidator.formatCnpj(clean));
        res.setCleanCnpj(clean);
        res.setLegalName((String) json.get("nomeEmpresarial"));
        res.setTradeName((String) json.get("nomeFantasia"));

        JSONObject situacao = (JSONObject) json.get("situacaoCadastral");
        if (situacao != null) {
            String desc = (String) situacao.get("descricao");
            res.setStatusDescription(desc);
            if (desc != null) {
                String u = desc.toUpperCase();
                if (u.contains("ATIVA")) res.setStatus(CompanyRegistryResult.CorporateStatus.ATIVA);
                else if (u.contains("BAIXADA")) res.setStatus(CompanyRegistryResult.CorporateStatus.BAIXADA);
                else if (u.contains("INAPTA")) res.setStatus(CompanyRegistryResult.CorporateStatus.INAPTA);
                else if (u.contains("SUSPENSA")) res.setStatus(CompanyRegistryResult.CorporateStatus.SUSPENSA);
                else if (u.contains("NULA")) res.setStatus(CompanyRegistryResult.CorporateStatus.NULA);
            }
            res.setStatusDate(parseDate((String) situacao.get("data")));
            res.setStatusReason((String) situacao.get("motivo"));
        }

        res.setOpeningDate(parseDate((String) json.get("dataAbertura")));
        res.setCompanySize((String) json.get("porte"));

        Object capSoc = json.get("capitalSocial");
        if (capSoc != null) {
            try { res.setCapitalSocial(new BigDecimal(capSoc.toString())); } catch (Exception ignored) {}
        }

        JSONObject natJur = (JSONObject) json.get("naturezaJuridica");
        if (natJur != null) {
            res.setLegalNatureCode(String.valueOf(natJur.get("codigo")));
            res.setLegalNatureDescription((String) natJur.get("descricao"));
        }

        JSONObject cnaePrinc = (JSONObject) json.get("cnaePrincipal");
        if (cnaePrinc != null) {
            res.setMainCnaeCode(String.valueOf(cnaePrinc.get("codigo")));
            res.setMainCnaeDescription((String) cnaePrinc.get("descricao"));
        }

        // Endereço
        JSONObject end = (JSONObject) json.get("endereco");
        if (end != null) {
            AddressResult addr = new AddressResult();
            addr.setCep(BrazilianDocumentValidator.formatCep((String) end.get("cep")));
            addr.setStreet((String) end.get("logradouro"));
            addr.setStreetType((String) end.get("tipoLogradouro"));
            addr.setNumber((String) end.get("numero"));
            addr.setComplement((String) end.get("complemento"));
            addr.setNeighborhood((String) end.get("bairro"));
            addr.setCity((String) end.get("municipio"));
            addr.setState((String) end.get("uf"));
            addr.setIbgeCityCode((String) end.get("codigoMunicipioIbge"));
            res.setAddress(addr);
        }

        // Sócios
        JSONArray socios = (JSONArray) json.get("socios");
        if (socios != null) {
            List<CompanyMemberResult> members = new ArrayList<>();
            for (Object obj : socios) {
                JSONObject sJson = (JSONObject) obj;
                CompanyMemberResult m = new CompanyMemberResult();
                m.setName((String) sJson.get("nomeSocio"));
                m.setIdentifier((String) sJson.get("cpfRepresentanteLegal"));
                m.setQualificationDescription((String) sJson.get("qualificacaoSocio"));
                m.setEntryDate(parseDate((String) sJson.get("dataEntradaSociedade")));
                Object pct = sJson.get("percentualCapitalSocial");
                if (pct != null) m.setCapitalPercentage(Double.parseDouble(pct.toString()));
                members.add(m);
            }
            res.setMembers(members);
        }

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "SERPRO Consulta CNPJ");
        prov.setConfidenceScore(1.0);
        res.setProvenance(prov);

        return res;
    }

    private Date parseDate(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return ISO_DATE_FORMAT.parse(str.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
