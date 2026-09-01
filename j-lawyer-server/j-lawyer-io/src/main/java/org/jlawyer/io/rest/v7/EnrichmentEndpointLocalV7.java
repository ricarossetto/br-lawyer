/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package org.jlawyer.io.rest.v7;

import javax.ws.rs.core.Response;

/**
 * Interface local para o endpoint REST de enriquecimento de dados cadastrais brasileiros.
 *
 * @author BR-LAWYER Team
 */
public interface EnrichmentEndpointLocalV7 {

    Response lookupCompany(String cnpj);

    Response lookupAddress(String cep);

    Response lookupProfessional(String state, String oabNumber);

    Response listBanks();

    Response getProviders();

    Response testProvider(String providerId);
}
