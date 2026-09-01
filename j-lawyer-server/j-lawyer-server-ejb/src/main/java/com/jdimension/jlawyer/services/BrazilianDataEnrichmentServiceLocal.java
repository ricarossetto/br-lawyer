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
import javax.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 * Interface local EJB para o serviço de enriquecimento de dados cadastrais.
 *
 * @author BR-LAWYER Team
 */
@Local
public interface BrazilianDataEnrichmentServiceLocal {

    CompanyRegistryResult lookupCompany(String cnpj, boolean forceRefresh) throws Exception;

    PersonRegistryResult lookupPerson(String cpf, Date birthDate, boolean forceRefresh) throws Exception;

    AddressResult lookupAddress(String cep, boolean forceRefresh) throws Exception;

    ProfessionalRegistrationResult lookupProfessional(String registrationNumber, String state, boolean forceRefresh) throws Exception;

    List<BankingInstitutionResult> listBanks() throws Exception;

    boolean testProvider(String providerId) throws Exception;

    List<ProviderConfig> getProviderConfigs() throws Exception;

    void saveProviderConfig(ProviderConfig config) throws Exception;

    List<ContactDeduplicationMatch> checkDuplicateContact(String identifier, String name, String city, String state) throws Exception;

    ConflictCheckEnrichmentResult checkConflicts(String identifier, String name, List<String> relatedMembers) throws Exception;
}
