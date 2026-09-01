/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.providers;

import com.jdimension.jlawyer.domain.enrichment.model.ProviderCapabilities;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import com.jdimension.jlawyer.domain.enrichment.spi.RegistryProvider;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provedor de dados geográficos e códigos municipais oficiais do IBGE (7 dígitos).
 * Mantém cache local de estados e cidades brasileiras.
 *
 * @author BR-LAWYER Team
 */
public class IbgeGeographicProvider implements RegistryProvider {

    private static final Logger log = Logger.getLogger(IbgeGeographicProvider.class.getName());
    public static final String PROVIDER_ID = "ibge-localidades";
    private static final String DEFAULT_BASE_URL = "https://servicodados.ibge.gov.br/api/v1/localidades";

    private final HttpClient httpClient;
    private final Map<String, List<MunicipalityEntry>> municipalitiesCache = new ConcurrentHashMap<>();

    public static class MunicipalityEntry {
        private final String ibgeCode;
        private final String name;
        private final String uf;

        public MunicipalityEntry(String ibgeCode, String name, String uf) {
            this.ibgeCode = ibgeCode;
            this.name = name;
            this.uf = uf;
        }
        public String getIbgeCode() { return ibgeCode; }
        public String getName() { return name; }
        public String getUf() { return uf; }
    }

    public IbgeGeographicProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayName() {
        return "IBGE Localidades (Oficial / Códigos 7 Dígitos)";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        ProviderCapabilities cap = new ProviderCapabilities();
        cap.setSupportsAddress(true);
        cap.setRequiresCredentials(false);
        cap.setSelfHostable(false);
        cap.setOfficialGovSource(true);
        return cap;
    }

    @Override
    public boolean testConnection(ProviderConfig config) throws Exception {
        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/estados/SP"))
                .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 4000))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public List<MunicipalityEntry> getMunicipalitiesByUf(String uf, ProviderConfig config) {
        if (uf == null || uf.trim().isEmpty()) return Collections.emptyList();
        String upperUf = uf.toUpperCase().trim();

        if (municipalitiesCache.containsKey(upperUf)) {
            return municipalitiesCache.get(upperUf);
        }

        try {
            String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                    ? config.getBaseUrl() : DEFAULT_BASE_URL;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/estados/" + upperUf + "/municipios"))
                    .header("Accept", "application/json")
                    .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                    .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 6000))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response.body());
                List<MunicipalityEntry> list = new ArrayList<>();
                for (Object obj : array) {
                    JSONObject item = (JSONObject) obj;
                    String id = String.valueOf(item.get("id"));
                    String nome = (String) item.get("nome");
                    list.add(new MunicipalityEntry(id, nome, upperUf));
                }
                municipalitiesCache.put(upperUf, list);
                return list;
            }
        } catch (Exception e) {
            log.error("Erro ao buscar municípios no IBGE para UF " + upperUf, e);
        }

        return Collections.emptyList();
    }
}
