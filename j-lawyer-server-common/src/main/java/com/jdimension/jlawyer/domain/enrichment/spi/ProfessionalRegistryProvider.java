/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.spi;

import com.jdimension.jlawyer.domain.enrichment.model.ProfessionalRegistrationResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import java.util.List;

/**
 * SPI para consulta de registros profissionais de advogados (OAB / CNA).
 *
 * @author BR-LAWYER Team
 */
public interface ProfessionalRegistryProvider extends RegistryProvider {

    /**
     * Consulta inscrição na OAB.
     *
     * @param registrationNumber Número da OAB
     * @param state UF da seccional (ex: "SP")
     * @param config Configuração do provedor
     * @return Dados cadastrais da habilitação profissional
     * @throws Exception em caso de erro
     */
    ProfessionalRegistrationResult lookupProfessional(String registrationNumber, String state, ProviderConfig config) throws Exception;

    /**
     * Pesquisa advogados por nome e UF.
     *
     * @param fullName Nome completo ou parcial
     * @param state UF da seccional
     * @param config Configuração do provedor
     * @return Lista de inscrições encontradas
     * @throws Exception em caso de erro
     */
    List<ProfessionalRegistrationResult> searchByName(String fullName, String state, ProviderConfig config) throws Exception;
}
