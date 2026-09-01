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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provedor de consulta de dados de Pessoa Jurídica via BrasilAPI (https://brasilapi.com.br/api/cnpj/v1/{cnpj}).
 *
 * @author BR-LAWYER Team
 */
public class BrasilApiCompanyProvider implements CompanyRegistryProvider {

    private static final Logger log = Logger.getLogger(BrasilApiCompanyProvider.class.getName());
    public static final String PROVIDER_ID = "brasilapi-cnpj";
    private static final String DEFAULT_BASE_URL = "https://brasilapi.com.br/api/cnpj/v1";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final HttpClient httpClient;

    public BrasilApiCompanyProvider() {
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
        return "BrasilAPI CNPJ (Pública / Comunitária)";
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
        cap.setSupportsProfessionalRegistration(false);
        cap.setSupportsBanking(false);
        cap.setRequiresCredentials(false);
        cap.setSelfHostable(false);
        cap.setOfficialGovSource(true);
        return cap;
    }

    @Override
    public boolean testConnection(ProviderConfig config) throws Exception {
        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;
        // Testa com CNPJ estável (Banco do Brasil)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/00000000000191"))
                .header("User-Agent", "BR-LAWYER-Enrichment/1.0")
                .timeout(Duration.ofMillis(config != null ? config.getTimeoutMs() : 5000))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    @Override
    public CompanyRegistryResult lookupCompany(String cnpj, ProviderConfig config) throws Exception {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ inválido ou vazio.");
        }

        String clean = cnpj.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        String baseUrl = (config != null && config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl() : DEFAULT_BASE_URL;

        String targetUrl = baseUrl.endsWith("/") ? baseUrl + clean : baseUrl + "/" + clean;
        int timeout = config != null ? config.getTimeoutMs() : 6000;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .header("Accept", "application/json")
                .header("User-Agent", "BR-LAWYER-Legal-Suite/1.0")
                .timeout(Duration.ofMillis(timeout))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            log.warn("CNPJ não encontrado na BrasilAPI: " + clean);
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao consultar BrasilAPI CNPJ. Código HTTP: " + response.statusCode() + " Resposta: " + response.body());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body());

        return parseCompanyJson(json, clean);
    }

    private CompanyRegistryResult parseCompanyJson(JSONObject json, String cleanCnpj) {
        CompanyRegistryResult res = new CompanyRegistryResult();
        res.setCnpj(BrazilianDocumentValidator.formatCnpj(cleanCnpj));
        res.setCleanCnpj(cleanCnpj);

        res.setLegalName((String) json.get("razao_social"));
        res.setTradeName((String) json.get("nome_fantasia"));

        String descSituacao = (String) json.get("descricao_situacao_cadastral");
        res.setStatusDescription(descSituacao);
        if (descSituacao != null) {
            String upper = descSituacao.toUpperCase();
            if (upper.contains("ATIVA")) res.setStatus(CompanyRegistryResult.CorporateStatus.ATIVA);
            else if (upper.contains("BAIXADA")) res.setStatus(CompanyRegistryResult.CorporateStatus.BAIXADA);
            else if (upper.contains("INAPTA")) res.setStatus(CompanyRegistryResult.CorporateStatus.INAPTA);
            else if (upper.contains("SUSPENSA")) res.setStatus(CompanyRegistryResult.CorporateStatus.SUSPENSA);
            else if (upper.contains("NULA")) res.setStatus(CompanyRegistryResult.CorporateStatus.NULA);
        }

        res.setStatusDate(parseDate((String) json.get("data_situacao_cadastral")));
        res.setStatusReason(String.valueOf(json.get("motivo_situacao_cadastral")));
        res.setOpeningDate(parseDate((String) json.get("data_inicio_atividade")));

        Object matFil = json.get("identificador_matriz_filial");
        if (matFil != null && (matFil.toString().equals("1") || matFil.toString().equalsIgnoreCase("MATRIZ"))) {
            res.setEstablishmentType(CompanyRegistryResult.EstablishmentType.MATRIZ);
        } else {
            res.setEstablishmentType(CompanyRegistryResult.EstablishmentType.FILIAL);
        }

        res.setLegalNatureCode(String.valueOf(json.get("codigo_natureza_juridica")));
        res.setCompanySize((String) json.get("porte"));

        Object capSoc = json.get("capital_social");
        if (capSoc != null) {
            try {
                res.setCapitalSocial(new BigDecimal(capSoc.toString()));
            } catch (Exception ignored) {}
        }

        Object simples = json.get("opcao_pelo_simples");
        if (simples != null) res.setSimplesOptant(Boolean.parseBoolean(simples.toString()));

        Object mei = json.get("opcao_pelo_mei");
        if (mei != null) res.setMeiopting(Boolean.parseBoolean(mei.toString()));

        res.setMainCnaeCode(String.valueOf(json.get("cnae_fiscal")));
        res.setMainCnaeDescription((String) json.get("cnae_fiscal_descricao"));

        JSONArray secCnaes = (JSONArray) json.get("cnaes_secundarios");
        if (secCnaes != null) {
            List<CompanyRegistryResult.CnaeEntry> secList = new ArrayList<>();
            for (Object obj : secCnaes) {
                JSONObject cnaeJson = (JSONObject) obj;
                secList.add(new CompanyRegistryResult.CnaeEntry(
                        String.valueOf(cnaeJson.get("codigo")),
                        (String) cnaeJson.get("descricao")
                ));
            }
            res.setSecondaryCnaes(secList);
        }

        // Endereço
        AddressResult addr = new AddressResult();
        addr.setCep(BrazilianDocumentValidator.formatCep((String) json.get("cep")));
        addr.setStreet((String) json.get("logradouro"));
        addr.setStreetType((String) json.get("descricao_tipo_de_logradouro"));
        addr.setNumber((String) json.get("numero"));
        addr.setComplement((String) json.get("complemento"));
        addr.setNeighborhood((String) json.get("bairro"));
        addr.setCity((String) json.get("municipio"));
        addr.setState((String) json.get("uf"));
        Object codMun = json.get("codigo_municipio");
        if (codMun != null) addr.setIbgeCityCode(codMun.toString());
        res.setAddress(addr);

        // Telefones e Emails
        String tel1 = (String) json.get("ddd_telefone_1");
        if (tel1 != null && !tel1.trim().isEmpty()) res.getPhones().add(tel1.trim());
        String tel2 = (String) json.get("ddd_telefone_2");
        if (tel2 != null && !tel2.trim().isEmpty()) res.getPhones().add(tel2.trim());

        String email = (String) json.get("email");
        if (email != null && !email.trim().isEmpty()) res.getEmails().add(email.trim().toLowerCase());

        // QSA - Quadro Societário
        JSONArray qsaArray = (JSONArray) json.get("qsa");
        if (qsaArray != null) {
            List<CompanyMemberResult> members = new ArrayList<>();
            for (Object obj : qsaArray) {
                JSONObject memberJson = (JSONObject) obj;
                CompanyMemberResult member = new CompanyMemberResult();
                member.setName((String) memberJson.get("nome_socio"));
                member.setIdentifier((String) memberJson.get("cnpj_cpf_do_socio"));
                member.setQualificationCode(String.valueOf(memberJson.get("codigo_qualificacao_socio")));
                member.setQualificationDescription((String) memberJson.get("qualificacao_socio"));
                member.setAgeGroup((String) memberJson.get("faixa_etaria"));
                member.setEntryDate(parseDate((String) memberJson.get("data_entrada_sociedade")));
                member.setCountry((String) memberJson.get("pais"));
                member.setLegalRepresentativeName((String) memberJson.get("nome_representante_legal"));
                member.setLegalRepresentativeIdentifier((String) memberJson.get("cpf_representante_legal"));
                members.add(member);
            }
            res.setMembers(members);
        }

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "BrasilAPI / Dados Públicos RFB");
        prov.setConfidenceScore(0.98);
        prov.addFieldProvenance("razao_social", res.getLegalName());
        prov.addFieldProvenance("situacao_cadastral", res.getStatusDescription());
        prov.addFieldProvenance("endereco", addr.getFullAddress());
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
