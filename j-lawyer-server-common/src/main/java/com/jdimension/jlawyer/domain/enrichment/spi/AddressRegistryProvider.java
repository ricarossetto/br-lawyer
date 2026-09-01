/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.spi;

import com.jdimension.jlawyer.domain.enrichment.model.AddressResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;

/**
 * SPI para consulta e autocompletar de endereços a partir do CEP brasileiro.
 *
 * @author BR-LAWYER Team
 */
public interface AddressRegistryProvider extends RegistryProvider {

    /**
     * Consulta endereço a partir do CEP.
     *
     * @param cep CEP de 8 dígitos
     * @param config Configuração do provedor
     * @return Endereço normalizado com código IBGE de 7 dígitos
     * @throws Exception em caso de erro ou CEP não localizado
     */
    AddressResult lookupAddress(String cep, ProviderConfig config) throws Exception;
}
