/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package org.jlawyer.io.rest.v7;

import org.jlawyer.io.rest.tools.RestErrorResponses;

import com.jdimension.jlawyer.domain.enrichment.model.*;
import com.jdimension.jlawyer.services.BrazilianDataEnrichmentServiceLocal;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST v7 para enriquecimento e inteligência registral de dados brasileiros.
 *
 * http://localhost:8080/j-lawyer-io/rest/v7/enrichment
 *
 * @author BR-LAWYER Team
 */
@Stateless
@Path("/v7/enrichment")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(tags = {"Brazilian Data Enrichment"})
public class EnrichmentEndpointV7 implements EnrichmentEndpointLocalV7 {

    private static final Logger log = Logger.getLogger(EnrichmentEndpointV7.class.getName());

    private static final String LOOKUP_ENRICHMENT = "java:global/j-lawyer-server/j-lawyer-server-ejb/BrazilianDataEnrichmentService!com.jdimension.jlawyer.services.BrazilianDataEnrichmentServiceLocal";

    private BrazilianDataEnrichmentServiceLocal lookupEnrichment() throws Exception {
        InitialContext ic = new InitialContext();
        return (BrazilianDataEnrichmentServiceLocal) ic.lookup(LOOKUP_ENRICHMENT);
    }

    /**
     * Consulta dados empresariais completos e QSA a partir do CNPJ.
     *
     * @param cnpj CNPJ com 14 dígitos (numérico ou alfanumérico IN RFB 2.229/2024)
     * @response 200 Dados empresariais retornados com sucesso
     * @response 400 CNPJ inválido
     * @response 404 CNPJ não localizado
     * @response 401 Usuário não autorizado
     */
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/company/{cnpj}")
    @RolesAllowed({"readAddressRole"})
    @io.swagger.annotations.ApiOperation(value = "Consulta dados empresariais e QSA pelo CNPJ", response = CompanyRegistryResult.class)
    public Response lookupCompany(@PathParam("cnpj") String cnpj) {
        try {
            BrazilianDataEnrichmentServiceLocal svc = lookupEnrichment();
            CompanyRegistryResult result = svc.lookupCompany(cnpj, false);
            if (result == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorMap("CNPJ não localizado: " + cnpj))
                        .build();
            }
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMap(e.getMessage()))
                    .build();
        } catch (Exception ex) {
            log.error("Erro ao consultar CNPJ: " + cnpj, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    /**
     * Consulta endereço completo a partir do CEP.
     *
     * @param cep CEP de 8 dígitos
     * @response 200 Endereço retornado com sucesso
     * @response 404 CEP não localizado
     */
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/address/{cep}")
    @RolesAllowed({"readAddressRole"})
    @io.swagger.annotations.ApiOperation(value = "Consulta endereço pelo CEP brasileiro", response = AddressResult.class)
    public Response lookupAddress(@PathParam("cep") String cep) {
        try {
            BrazilianDataEnrichmentServiceLocal svc = lookupEnrichment();
            AddressResult result = svc.lookupAddress(cep, false);
            if (result == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorMap("CEP não localizado: " + cep))
                        .build();
            }
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMap(e.getMessage()))
                    .build();
        } catch (Exception ex) {
            log.error("Erro ao consultar CEP: " + cep, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    /**
     * Consulta registro profissional de advogado na OAB.
     *
     * @param state UF da seccional (ex: SP, RJ, MG)
     * @param oabNumber Número de inscrição na OAB
     * @response 200 Dados do advogado retornados
     */
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/oab/{state}/{oabNumber}")
    @RolesAllowed({"readAddressRole"})
    @io.swagger.annotations.ApiOperation(value = "Consulta inscrição OAB por UF e número", response = ProfessionalRegistrationResult.class)
    public Response lookupProfessional(@PathParam("state") String state, @PathParam("oabNumber") String oabNumber) {
        try {
            BrazilianDataEnrichmentServiceLocal svc = lookupEnrichment();
            ProfessionalRegistrationResult result = svc.lookupProfessional(oabNumber, state, false);
            if (result == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorMap("Inscrição OAB não localizada"))
                        .build();
            }
            return Response.ok(result).build();
        } catch (Exception ex) {
            log.error("Erro ao consultar OAB: " + state + "/" + oabNumber, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    /**
     * Lista todas as instituições bancárias brasileiras (COMPE/ISPB/PIX).
     *
     * @response 200 Lista de bancos retornada
     */
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/banks")
    @RolesAllowed({"readAddressRole"})
    @io.swagger.annotations.ApiOperation(value = "Lista instituições bancárias participantes do SPB/PIX", response = BankingInstitutionResult.class, responseContainer = "List")
    public Response listBanks() {
        try {
            BrazilianDataEnrichmentServiceLocal svc = lookupEnrichment();
            List<BankingInstitutionResult> banks = svc.listBanks();
            return Response.ok(banks).build();
        } catch (Exception ex) {
            log.error("Erro ao listar bancos", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    /**
     * Lista os provedores de enriquecimento configurados e suas capacidades.
     *
     * @response 200 Lista de provedores retornada
     */
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/providers")
    @RolesAllowed({"adminRole"})
    @io.swagger.annotations.ApiOperation(value = "Lista provedores de enriquecimento de dados configurados", response = ProviderConfig.class, responseContainer = "List")
    public Response getProviders() {
        try {
            BrazilianDataEnrichmentServiceLocal svc = lookupEnrichment();
            List<ProviderConfig> configs = svc.getProviderConfigs();
            return Response.ok(configs).build();
        } catch (Exception ex) {
            log.error("Erro ao listar provedores", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    /**
     * Executa teste de conectividade para um provedor específico.
     *
     * @param providerId ID do provedor (ex: brasilapi-cnpj, viacep, serpro-cpf)
     * @response 200 Resultado do teste de conexão
     */
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/providers/test/{providerId}")
    @RolesAllowed({"adminRole"})
    @io.swagger.annotations.ApiOperation(value = "Testa a conectividade de um provedor de dados cadastrais")
    public Response testProvider(@PathParam("providerId") String providerId) {
        try {
            BrazilianDataEnrichmentServiceLocal svc = lookupEnrichment();
            long start = System.currentTimeMillis();
            boolean success = svc.testProvider(providerId);
            long latency = System.currentTimeMillis() - start;

            Map<String, Object> result = new HashMap<>();
            result.put("providerId", providerId);
            result.put("connected", success);
            result.put("latencyMs", latency);

            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMap(e.getMessage()))
                    .build();
        } catch (Exception ex) {
            log.error("Erro no teste de conectividade do provedor: " + providerId, ex);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("providerId", providerId);
            errorResult.put("connected", false);
            errorResult.put("error", ex.getMessage());
            return Response.ok(errorResult).build();
        }
    }

    private Map<String, String> errorMap(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
