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
import javax.ejb.Remote;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contrato remoto EJB para o subsistema de enriquecimento de dados cadastrais brasileiros.
 *
 * @author BR-LAWYER Team
 */
@Remote
public interface BrazilianDataEnrichmentServiceRemote {

    /**
     * Consulta dados empresariais e QSA a partir do CNPJ com fallback resiliente.
     */
    CompanyRegistryResult lookupCompany(String cnpj, boolean forceRefresh) throws Exception;

    /**
     * Consulta dados cadastrais de pessoa física a partir do CPF e data de nascimento.
     */
    PersonRegistryResult lookupPerson(String cpf, Date birthDate, boolean forceRefresh) throws Exception;

    /**
     * Consulta endereço a partir do CEP brasileiro com fallback (BrasilAPI -> ViaCEP -> IBGE).
     */
    AddressResult lookupAddress(String cep, boolean forceRefresh) throws Exception;

    /**
     * Consulta registro de advogado na OAB / CNA.
     */
    ProfessionalRegistrationResult lookupProfessional(String registrationNumber, String state, boolean forceRefresh) throws Exception;

    /**
     * Lista instituições bancárias participantes do SPB/PIX.
     */
    List<BankingInstitutionResult> listBanks() throws Exception;

    /**
     * Executa teste de conectividade e latência para um provedor específico.
     */
    boolean testProvider(String providerId) throws Exception;

    /**
     * Retorna a lista de provedores registrados e suas configurações ativas.
     */
    List<ProviderConfig> getProviderConfigs() throws Exception;

    /**
     * Atualiza a configuração de um provedor (prioridade, timeout, credenciais).
     */
    void saveProviderConfig(ProviderConfig config) throws Exception;

    /**
     * Realiza análise de deduplicação de um contato contra a base existente no BR-LAWYER.
     */
    List<ContactDeduplicationMatch> checkDuplicateContact(String identifier, String name, String city, String state) throws Exception;

    /**
     * Realiza verificação de conflitos de interesse enriquecida com QSA e entidades relacionadas.
     */
    ConflictCheckEnrichmentResult checkConflicts(String identifier, String name, List<String> relatedMembers) throws Exception;
}
