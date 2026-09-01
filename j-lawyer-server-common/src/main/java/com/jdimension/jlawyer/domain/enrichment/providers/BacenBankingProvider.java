/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.providers;

import com.jdimension.jlawyer.domain.enrichment.model.BankingInstitutionResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderCapabilities;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import com.jdimension.jlawyer.domain.enrichment.spi.BankingDirectoryProvider;
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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provedor de catálogo bancário do BACEN (COMPE 3 dígitos, ISPB 8 dígitos, PIX).
 *
 * @author BR-LAWYER Team
 */
public class BacenBankingProvider implements BankingDirectoryProvider {

    private static final Logger log = Logger.getLogger(BacenBankingProvider.class.getName());
    public static final String PROVIDER_ID = "bacen-banks";
    private static final String DEFAULT_BASE_URL = "https://brasilapi.com.br/api/banks/v1";

    private final HttpClient httpClient;
    private final List<BankingInstitutionResult> banksCache = new CopyOnWriteArrayList<>();

    public BacenBankingProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        // Inicializa com os 10 principais bancos brasileiros offline
        initDefaultBanks();
    }

    private void initDefaultBanks() {
        banksCache.add(new BankingInstitutionResult("00000000", "001", "BCO DO BRASIL S.A.", "BANCO DO BRASIL S.A.", true));
        banksCache.add(new BankingInstitutionResult("00360305", "104", "CAIXA ECONOMICA FEDERAL", "CAIXA ECONOMICA FEDERAL", true));
        banksCache.add(new BankingInstitutionResult("60746948", "237", "BCO BRADESCO S.A.", "BANCO BRADESCO S.A.", true));
        banksCache.add(new BankingInstitutionResult("60701190", "341", "ITAÚ UNIBANCO S.A.", "ITAÚ UNIBANCO S.A.", true));
        banksCache.add(new BankingInstitutionResult("90400888", "033", "BCO SANTANDER (BRASIL) S.A.", "BANCO SANTANDER (BRASIL) S.A.", true));
        banksCache.add(new BankingInstitutionResult("18236120", "260", "NU PAGAMENTOS - IP", "NU PAGAMENTOS S.A.", true));
        banksCache.add(new BankingInstitutionResult("41696665", "077", "BANCO INTER", "BANCO INTER S.A.", true));
        banksCache.add(new BankingInstitutionResult("30306294", "208", "BANCO BTG PACTUAL S.A.", "BANCO BTG PACTUAL S.A.", true));
        banksCache.add(new BankingInstitutionResult("00038166", "756", "BANCOOB", "BANCO COOPERATIVO SICOOB S.A.", true));
        banksCache.add(new BankingInstitutionResult("01181521", "748", "BANCO COOPERATIVO SICREDI S.A.", "BANCO COOPERATIVO SICREDI S.A.", true));
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayName() {
        return "Bacen Catálogo Bancário & PIX (COMPE / ISPB)";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        ProviderCapabilities cap = new ProviderCapabilities();
        cap.setSupportsBanking(true);
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
                .uri(URI.create(baseUrl + "/001"))
                .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 4000))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    @Override
    public List<BankingInstitutionResult> listBanks(ProviderConfig config) {
        // Se o cache já possui a lista completa (>50 itens), retorna
        if (banksCache.size() > 50) {
            return new ArrayList<>(banksCache);
        }

        try {
            String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                    ? config.getBaseUrl() : DEFAULT_BASE_URL;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Accept", "application/json")
                    .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                    .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 6000))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response.body());
                List<BankingInstitutionResult> freshList = new ArrayList<>();
                for (Object obj : array) {
                    JSONObject bJson = (JSONObject) obj;
                    Object codeObj = bJson.get("code");
                    String code = codeObj != null ? String.format("%03d", Integer.parseInt(codeObj.toString())) : null;
                    String ispb = (String) bJson.get("ispb");
                    String name = (String) bJson.get("name");
                    String fullName = (String) bJson.get("fullName");

                    freshList.add(new BankingInstitutionResult(ispb, code, name, fullName, true));
                }
                banksCache.clear();
                banksCache.addAll(freshList);
                return freshList;
            }
        } catch (Exception e) {
            log.warn("Usando catálogo bancário embutido do BR-LAWYER devido a erro na API: " + e.getMessage());
        }

        return new ArrayList<>(banksCache);
    }

    @Override
    public BankingInstitutionResult findBank(String codeOrIspb, ProviderConfig config) {
        if (codeOrIspb == null || codeOrIspb.trim().isEmpty()) return null;
        String clean = codeOrIspb.trim();
        for (BankingInstitutionResult b : listBanks(config)) {
            if (clean.equals(b.getCompeCode()) || clean.equals(b.getIspb())) {
                return b;
            }
        }
        return null;
    }
}
