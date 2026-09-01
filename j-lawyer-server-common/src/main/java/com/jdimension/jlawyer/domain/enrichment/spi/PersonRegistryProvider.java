/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.spi;

import com.jdimension.jlawyer.domain.enrichment.model.PersonRegistryResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import java.util.Date;

/**
 * SPI para consulta e enriquecimento de dados de Pessoas Físicas (CPF).
 *
 * @author BR-LAWYER Team
 */
public interface PersonRegistryProvider extends RegistryProvider {

    /**
     * Consulta dados cadastrais de uma pessoa física.
     *
     * @param cpf CPF (11 dígitos)
     * @param birthDate Data de nascimento (obrigatória para provedores oficiais como SERPRO v3 e RFB)
     * @param config Configuração do provedor
     * @return Resultado normalizado
     * @throws Exception em caso de erro ou divergência cadastral
     */
    PersonRegistryResult lookupPerson(String cpf, Date birthDate, ProviderConfig config) throws Exception;
}
