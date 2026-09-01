/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.enrichment.model.*;
import com.jdimension.jlawyer.domain.enrichment.providers.*;
import com.jdimension.jlawyer.domain.enrichment.spi.*;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.persistence.AddressBean;
import com.jdimension.jlawyer.persistence.AddressBeanFacadeLocal;
import com.jdimension.jlawyer.persistence.ArchiveFileAddressesBean;
import com.jdimension.jlawyer.persistence.ArchiveFileAddressesBeanFacadeLocal;
import com.jdimension.jlawyer.services.enrichment.BrazilianContactDeduplicator;
import com.jdimension.jlawyer.services.enrichment.ConflictCheckEnricher;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação EJB do serviço de enriquecimento de dados cadastrais e inteligência registral do BR-LAWYER.
 * Gerencia encadeamento de fallbacks, Circuit Breakers por provedor, cache em memória com TTL e
 * cruzamento de dados para deduplicação e detecção de conflitos de interesse.
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class BrazilianDataEnrichmentService implements BrazilianDataEnrichmentServiceRemote, BrazilianDataEnrichmentServiceLocal {

    private static final Logger log = Logger.getLogger(BrazilianDataEnrichmentService.class.getName());

    @EJB
    private AddressBeanFacadeLocal addressFacade;

    @EJB
    private ArchiveFileAddressesBeanFacadeLocal archiveFileAddressesFacade;

    // Cache interno de dados enriquecidos
    private static final Map<String, CacheEntry> ENRICHMENT_CACHE = new ConcurrentHashMap<>();

    // Estado do Circuit Breaker por provedor
    private static final Map<String, CircuitBreakerState> CIRCUIT_BREAKERS = new ConcurrentHashMap<>();

    // Configurações ativas de provedores
    private static final Map<String, ProviderConfig> PROVIDER_CONFIGS = new ConcurrentHashMap<>();

    // Instâncias dos adaptadores SPI
    private static final Map<String, RegistryProvider> PROVIDERS = new ConcurrentHashMap<>();

    private static class CacheEntry {
        final Object data;
        final long createdAt;
        final long ttlMillis;

        CacheEntry(Object data, long ttlMinutes) {
            this.data = data;
            this.createdAt = System.currentTimeMillis();
            this.ttlMillis = ttlMinutes * 60 * 1000L;
        }

        boolean isExpired() {
            return (System.currentTimeMillis() - createdAt) > ttlMillis;
        }

        long getAgeSeconds() {
            return (System.currentTimeMillis() - createdAt) / 1000L;
        }
    }

    private static class CircuitBreakerState {
        enum State { CLOSED, OPEN, HALF_OPEN }
        State state = State.CLOSED;
        int failureCount = 0;
        long lastFailureTimestamp = 0;
        static final int FAILURE_THRESHOLD = 3;
        static final long OPEN_DURATION_MS = 30000L; // 30s de proteção

        synchronized boolean allowExecution() {
            if (state == State.CLOSED) return true;
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTimestamp > OPEN_DURATION_MS) {
                    state = State.HALF_OPEN;
                    return true;
                }
                return false;
            }
            return true; // HALF_OPEN
        }

        synchronized void recordSuccess() {
            state = State.CLOSED;
            failureCount = 0;
        }

        synchronized void recordFailure() {
            failureCount++;
            lastFailureTimestamp = System.currentTimeMillis();
            if (failureCount >= FAILURE_THRESHOLD) {
                state = State.OPEN;
            }
        }
    }

    @PostConstruct
    public void init() {
        registerDefaultProviders();
    }

    private synchronized void registerDefaultProviders() {
        if (PROVIDERS.isEmpty()) {
            registerProvider(new BrasilApiCompanyProvider(), 1, true, null);
            registerProvider(new SerproCnpjProvider(), 2, false, null);
            registerProvider(new BrasilApiAddressProvider(), 1, true, null);
            registerProvider(new ViaCepAddressProvider(), 2, true, null);
            registerProvider(new IbgeGeographicProvider(), 3, true, null);
            registerProvider(new BacenBankingProvider(), 1, true, null);
            registerProvider(new SerproCpfProvider(), 1, false, null);
            registerProvider(new CnaOabProvider(), 1, true, null);
            registerProvider(new MockRegistryProvider(), 99, true, null);
        }
    }

    private void registerProvider(RegistryProvider provider, int priority, boolean enabled, String baseUrl) {
        String id = provider.getProviderId();
        PROVIDERS.put(id, provider);
        CIRCUIT_BREAKERS.putIfAbsent(id, new CircuitBreakerState());
        PROVIDER_CONFIGS.putIfAbsent(id, new ProviderConfig(id, provider.getDisplayName(), enabled, priority, baseUrl));
    }

    @Override
    public CompanyRegistryResult lookupCompany(String cnpj, boolean forceRefresh) throws Exception {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ não fornecido.");
        }

        String clean = cnpj.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        String cacheKey = "CNPJ:" + clean;

        if (!forceRefresh) {
            CacheEntry entry = ENRICHMENT_CACHE.get(cacheKey);
            if (entry != null && !entry.isExpired() && entry.data instanceof CompanyRegistryResult) {
                CompanyRegistryResult cached = (CompanyRegistryResult) entry.data;
                cached.getProvenance().setCached(true);
                cached.getProvenance().setCacheStatus(RegistryProvenance.CacheStatus.HIT);
                cached.getProvenance().setCacheAgeSeconds(entry.getAgeSeconds());
                return cached;
            }
        }

        List<CompanyRegistryProvider> chain = getOrderedCompanyProviders();
        Exception lastException = null;

        for (CompanyRegistryProvider provider : chain) {
            String pid = provider.getProviderId();
            CircuitBreakerState cb = CIRCUIT_BREAKERS.get(pid);
            if (cb != null && !cb.allowExecution()) {
                log.warn("Circuit Breaker ABERTO para o provedor " + pid + ". Pulando para o próximo da cadeia.");
                continue;
            }

            try {
                ProviderConfig cfg = PROVIDER_CONFIGS.get(pid);
                CompanyRegistryResult result = provider.lookupCompany(clean, cfg);
                if (result != null) {
                    if (cb != null) cb.recordSuccess();
                    ENRICHMENT_CACHE.put(cacheKey, new CacheEntry(result, cfg != null ? cfg.getCacheTtlMinutes() : 1440));
                    return result;
                }
            } catch (Exception e) {
                if (cb != null) cb.recordFailure();
                lastException = e;
                log.warn("Falha no provedor de CNPJ " + pid + ": " + e.getMessage());
            }
        }

        // Se todos falharem e houver cache expirado, retorna stale
        CacheEntry stale = ENRICHMENT_CACHE.get(cacheKey);
        if (stale != null && stale.data instanceof CompanyRegistryResult) {
            CompanyRegistryResult staleRes = (CompanyRegistryResult) stale.data;
            staleRes.getProvenance().setCached(true);
            staleRes.getProvenance().setCacheStatus(RegistryProvenance.CacheStatus.STALE);
            staleRes.getProvenance().setCacheAgeSeconds(stale.getAgeSeconds());
            return staleRes;
        }

        // Fallback final: Mock
        MockRegistryProvider mock = (MockRegistryProvider) PROVIDERS.get(MockRegistryProvider.PROVIDER_ID);
        if (mock != null) {
            return mock.lookupCompany(clean, null);
        }

        throw new RuntimeException("Não foi possível consultar o CNPJ informado em nenhum dos provedores cadastrais.", lastException);
    }

    @Override
    public PersonRegistryResult lookupPerson(String cpf, Date birthDate, boolean forceRefresh) throws Exception {
        if (cpf == null || !BrazilianDocumentValidator.isValidCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido ou não informado.");
        }

        String clean = cpf.replaceAll("[^0-9]", "").trim();
        String cacheKey = "CPF:" + clean;

        if (!forceRefresh) {
            CacheEntry entry = ENRICHMENT_CACHE.get(cacheKey);
            if (entry != null && !entry.isExpired() && entry.data instanceof PersonRegistryResult) {
                PersonRegistryResult cached = (PersonRegistryResult) entry.data;
                cached.getProvenance().setCached(true);
                cached.getProvenance().setCacheStatus(RegistryProvenance.CacheStatus.HIT);
                return cached;
            }
        }

        SerproCpfProvider serpro = (SerproCpfProvider) PROVIDERS.get(SerproCpfProvider.PROVIDER_ID);
        ProviderConfig serproCfg = PROVIDER_CONFIGS.get(SerproCpfProvider.PROVIDER_ID);

        if (serpro != null && serproCfg != null && serproCfg.isEnabled() &&
            serproCfg.getApiKey() != null && !serproCfg.getApiKey().isEmpty() && birthDate != null) {
            try {
                PersonRegistryResult res = serpro.lookupPerson(clean, birthDate, serproCfg);
                if (res != null) {
                    ENRICHMENT_CACHE.put(cacheKey, new CacheEntry(res, serproCfg.getCacheTtlMinutes()));
                    return res;
                }
            } catch (Exception e) {
                log.warn("Erro na consulta oficial SERPRO CPF: " + e.getMessage());
            }
        }

        // Fallback Mock
        MockRegistryProvider mock = (MockRegistryProvider) PROVIDERS.get(MockRegistryProvider.PROVIDER_ID);
        if (mock != null) {
            return mock.lookupPerson(clean, birthDate, null);
        }

        throw new RuntimeException("Provedor de CPF não configurado.");
    }

    @Override
    public AddressResult lookupAddress(String cep, boolean forceRefresh) throws Exception {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP não informado.");
        }

        String clean = cep.replaceAll("[^0-9]", "").trim();
        String cacheKey = "CEP:" + clean;

        if (!forceRefresh) {
            CacheEntry entry = ENRICHMENT_CACHE.get(cacheKey);
            if (entry != null && !entry.isExpired() && entry.data instanceof AddressResult) {
                AddressResult cached = (AddressResult) entry.data;
                cached.getProvenance().setCached(true);
                cached.getProvenance().setCacheStatus(RegistryProvenance.CacheStatus.HIT);
                return cached;
            }
        }

        List<AddressRegistryProvider> chain = getOrderedAddressProviders();
        for (AddressRegistryProvider provider : chain) {
            String pid = provider.getProviderId();
            CircuitBreakerState cb = CIRCUIT_BREAKERS.get(pid);
            if (cb != null && !cb.allowExecution()) continue;

            try {
                ProviderConfig cfg = PROVIDER_CONFIGS.get(pid);
                AddressResult res = provider.lookupAddress(clean, cfg);
                if (res != null) {
                    if (cb != null) cb.recordSuccess();
                    ENRICHMENT_CACHE.put(cacheKey, new CacheEntry(res, cfg != null ? cfg.getCacheTtlMinutes() : 1440));
                    return res;
                }
            } catch (Exception e) {
                if (cb != null) cb.recordFailure();
                log.warn("Falha no provedor de CEP " + pid + ": " + e.getMessage());
            }
        }

        MockRegistryProvider mock = (MockRegistryProvider) PROVIDERS.get(MockRegistryProvider.PROVIDER_ID);
        if (mock != null) {
            return mock.lookupAddress(clean, null);
        }

        throw new RuntimeException("Endereço não localizado para o CEP: " + clean);
    }

    @Override
    public ProfessionalRegistrationResult lookupProfessional(String registrationNumber, String state, boolean forceRefresh) throws Exception {
        String cleanNum = registrationNumber != null ? registrationNumber.replaceAll("[^0-9]", "") : "";
        String uf = state != null ? state.toUpperCase().trim() : "SP";
        String cacheKey = "OAB:" + uf + ":" + cleanNum;

        if (!forceRefresh) {
            CacheEntry entry = ENRICHMENT_CACHE.get(cacheKey);
            if (entry != null && !entry.isExpired() && entry.data instanceof ProfessionalRegistrationResult) {
                return (ProfessionalRegistrationResult) entry.data;
            }
        }

        CnaOabProvider cna = (CnaOabProvider) PROVIDERS.get(CnaOabProvider.PROVIDER_ID);
        if (cna != null) {
            ProfessionalRegistrationResult res = cna.lookupProfessional(cleanNum, uf, PROVIDER_CONFIGS.get(cna.getProviderId()));
            ENRICHMENT_CACHE.put(cacheKey, new CacheEntry(res, 1440));
            return res;
        }

        MockRegistryProvider mock = (MockRegistryProvider) PROVIDERS.get(MockRegistryProvider.PROVIDER_ID);
        return mock.lookupProfessional(cleanNum, uf, null);
    }

    @Override
    public List<BankingInstitutionResult> listBanks() throws Exception {
        BacenBankingProvider bacen = (BacenBankingProvider) PROVIDERS.get(BacenBankingProvider.PROVIDER_ID);
        if (bacen != null) {
            return bacen.listBanks(PROVIDER_CONFIGS.get(bacen.getProviderId()));
        }
        MockRegistryProvider mock = (MockRegistryProvider) PROVIDERS.get(MockRegistryProvider.PROVIDER_ID);
        return mock.listBanks(null);
    }

    @Override
    public boolean testProvider(String providerId) throws Exception {
        RegistryProvider provider = PROVIDERS.get(providerId);
        if (provider == null) {
            throw new IllegalArgumentException("Provedor não encontrado: " + providerId);
        }
        ProviderConfig config = PROVIDER_CONFIGS.get(providerId);
        return provider.testConnection(config);
    }

    @Override
    public List<ProviderConfig> getProviderConfigs() {
        return new ArrayList<>(PROVIDER_CONFIGS.values());
    }

    @Override
    public void saveProviderConfig(ProviderConfig config) {
        if (config != null && config.getProviderId() != null) {
            PROVIDER_CONFIGS.put(config.getProviderId(), config);
        }
    }

    @Override
    public List<ContactDeduplicationMatch> checkDuplicateContact(String identifier, String name, String city, String state) {
        List<AddressBean> existing = addressFacade != null ? addressFacade.findAll() : Collections.emptyList();
        return BrazilianContactDeduplicator.findDuplicates(identifier, name, city, state, existing);
    }

    @Override
    public ConflictCheckEnrichmentResult checkConflicts(String identifier, String name, List<String> relatedMembers) {
        List<ArchiveFileAddressesBean> caseAddresses = archiveFileAddressesFacade != null ? archiveFileAddressesFacade.findAll() : Collections.emptyList();
        return ConflictCheckEnricher.evaluateConflicts(identifier, name, relatedMembers, caseAddresses);
    }

    private List<CompanyRegistryProvider> getOrderedCompanyProviders() {
        List<CompanyRegistryProvider> list = new ArrayList<>();
        if (PROVIDERS.get(BrasilApiCompanyProvider.PROVIDER_ID) != null) {
            list.add((CompanyRegistryProvider) PROVIDERS.get(BrasilApiCompanyProvider.PROVIDER_ID));
        }
        if (PROVIDERS.get(SerproCnpjProvider.PROVIDER_ID) != null) {
            list.add((CompanyRegistryProvider) PROVIDERS.get(SerproCnpjProvider.PROVIDER_ID));
        }
        return list;
    }

    private List<AddressRegistryProvider> getOrderedAddressProviders() {
        List<AddressRegistryProvider> list = new ArrayList<>();
        if (PROVIDERS.get(BrasilApiAddressProvider.PROVIDER_ID) != null) {
            list.add((AddressRegistryProvider) PROVIDERS.get(BrasilApiAddressProvider.PROVIDER_ID));
        }
        if (PROVIDERS.get(ViaCepAddressProvider.PROVIDER_ID) != null) {
            list.add((AddressRegistryProvider) PROVIDERS.get(ViaCepAddressProvider.PROVIDER_ID));
        }
        return list;
    }
}
