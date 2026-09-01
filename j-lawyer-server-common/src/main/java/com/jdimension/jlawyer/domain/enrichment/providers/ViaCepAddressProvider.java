/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.providers;

import com.jdimension.jlawyer.domain.enrichment.model.AddressResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderCapabilities;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import com.jdimension.jlawyer.domain.enrichment.model.RegistryProvenance;
import com.jdimension.jlawyer.domain.enrichment.spi.AddressRegistryProvider;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Provedor de consulta de CEP via ViaCEP (https://viacep.com.br/ws/{cep}/json/).
 * Provê código IBGE de 7 dígitos, GIA, DDD e código SIAFI.
 *
 * @author BR-LAWYER Team
 */
public class ViaCepAddressProvider implements AddressRegistryProvider {

    private static final Logger log = Logger.getLogger(ViaCepAddressProvider.class.getName());
    public static final String PROVIDER_ID = "viacep";
    private static final String DEFAULT_BASE_URL = "https://viacep.com.br/ws";

    private final HttpClient httpClient;

    public ViaCepAddressProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayName() {
        return "ViaCEP (Comunitária / IBGE / Fallback)";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        ProviderCapabilities cap = new ProviderCapabilities();
        cap.setSupportsAddress(true);
        cap.setRequiresCredentials(false);
        cap.setOfficialGovSource(false);
        cap.setSelfHostable(false);
        return cap;
    }

    @Override
    public boolean testConnection(ProviderConfig config) throws Exception {
        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/01310100/json/"))
                .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 4000))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    @Override
    public AddressResult lookupAddress(String cep, ProviderConfig config) throws Exception {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP inválido ou vazio.");
        }

        String clean = cep.replaceAll("[^0-9]", "").trim();
        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;

        String targetUrl = baseUrl.endsWith("/") ? baseUrl + clean + "/json/" : baseUrl + "/" + clean + "/json/";
        int timeout = config != null ? config.getTimeoutMs() : 4000;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .header("Accept", "application/json")
                .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                .timeout(Duration.ofMillis(timeout))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao consultar ViaCEP. HTTP " + response.statusCode());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body());

        Object erro = json.get("erro");
        if (erro != null && (erro.toString().equalsIgnoreCase("true") || erro.equals(Boolean.TRUE))) {
            log.warn("CEP não encontrado no ViaCEP: " + clean);
            return null;
        }

        AddressResult addr = new AddressResult();
        addr.setCep(BrazilianDocumentValidator.formatCep(clean));
        addr.setStreet((String) json.get("logradouro"));
        addr.setComplement((String) json.get("complemento"));
        addr.setNeighborhood((String) json.get("bairro"));
        addr.setCity((String) json.get("localidade"));
        addr.setState((String) json.get("uf"));
        addr.setIbgeCityCode((String) json.get("ibge"));
        addr.setGiaCode((String) json.get("gia"));
        addr.setDdd((String) json.get("ddd"));
        addr.setSiafiCode((String) json.get("siafi"));

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "ViaCEP");
        prov.setConfidenceScore(0.96);
        prov.addFieldProvenance("logradouro", addr.getStreet());
        prov.addFieldProvenance("ibge", addr.getIbgeCityCode());
        prov.addFieldProvenance("cidade", addr.getCity());
        prov.addFieldProvenance("uf", addr.getState());
        addr.setProvenance(prov);

        return addr;
    }
}
