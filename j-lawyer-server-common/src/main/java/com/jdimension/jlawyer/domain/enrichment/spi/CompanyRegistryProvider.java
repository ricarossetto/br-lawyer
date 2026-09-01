/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.spi;

import com.jdimension.jlawyer.domain.enrichment.model.CompanyRegistryResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;

/**
 * SPI para consulta e enriquecimento de dados de Pessoas Jurídicas (CNPJ).
 *
 * @author BR-LAWYER Team
 */
public interface CompanyRegistryProvider extends RegistryProvider {

    /**
     * Consulta dados cadastrais completos de uma empresa a partir do CNPJ.
     *
     * @param cnpj CNPJ (14 dígitos ou formato alfanumérico)
     * @param config Configuração do provedor
     * @return Resultado normalizado com dados empresariais e QSA
     * @throws Exception em caso de erro na consulta ou indisponibilidade
     */
    CompanyRegistryResult lookupCompany(String cnpj, ProviderConfig config) throws Exception;
}
