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
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.services.BrazilianLegalDomainServiceLocal;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Endpoints REST v7 para domínio jurídico brasileiro: Processos NPU/CNJ, OAB, Tribunais e TPU.
 *
 * http://localhost:8080/j-lawyer-io/rest/v7/brazil/domain
 *
 * @author BR-LAWYER Team
 */
@Stateless
@Path("/v7/brazil/domain")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(tags = {"Brazilian Legal Domain"})
public class BrazilianLegalDomainEndpointV7 implements BrazilianLegalDomainEndpointLocalV7 {

    private static final Logger log = Logger.getLogger(BrazilianLegalDomainEndpointV7.class.getName());

    private static final String LOOKUP_LEGAL_DOMAIN = "java:global/j-lawyer-server/j-lawyer-server-ejb/BrazilianLegalDomainService!com.jdimension.jlawyer.services.BrazilianLegalDomainServiceLocal";

    private BrazilianLegalDomainServiceLocal lookupService() throws Exception {
        InitialContext ic = new InitialContext();
        return (BrazilianLegalDomainServiceLocal) ic.lookup(LOOKUP_LEGAL_DOMAIN);
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"status\":400,\"message\":\"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"status\":404,\"message\":\"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // --- PROCESSOS (CASES) ---

    @Override
    @GET
    @Path("/cases/{caseId}")
    @RolesAllowed({"user", "admin"})
    public Response getCaseDetails(@PathParam("caseId") String caseId) {
        try {
            BrazilianCaseDetailsDTO details = lookupService().getCaseDetails(caseId);
            if (details == null) {
                return notFound("Processo não localizado: " + caseId);
            }
            return Response.ok(details, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao buscar detalhes processuais brasileiros para caso " + caseId, e);
            return RestErrorResponses.serverError(e);
        }
    }

    @Override
    @PUT
    @Path("/cases/{caseId}")
    @RolesAllowed({"user", "admin"})
    public Response saveCaseDetails(@PathParam("caseId") String caseId, BrazilianCaseDetailsDTO details) {
        try {
            if (details == null) {
                return badRequest("Corpo da requisição não pode ser vazio");
            }
            details.setCaseId(caseId);
            BrazilianCaseDetailsDTO saved = lookupService().saveCaseDetails(details);
            return Response.ok(saved, MediaType.APPLICATION_JSON).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao salvar detalhes processuais brasileiros para caso " + caseId, e);
            return RestErrorResponses.serverError(e);
        }
    }

    @Override
    @GET
    @Path("/cases/by-cnj/{cnjNumber}")
    @RolesAllowed({"user", "admin"})
    public Response findCaseByCnj(@PathParam("cnjNumber") String cnjNumber) {
        try {
            BrazilianCaseDetailsDTO details = lookupService().findCaseByCnjNumber(cnjNumber);
            if (details == null) {
                return notFound("Nenhum processo localizado com NPU: " + cnjNumber);
            }
            return Response.ok(details, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao buscar processo por CNJ: " + cnjNumber, e);
            return RestErrorResponses.serverError(e);
        }
    }

    // --- INSCRIÇÕES OAB (CONTACTS) ---

    @Override
    @GET
    @Path("/contacts/{contactId}/oab")
    @RolesAllowed({"user", "admin"})
    public Response getLawyerRegistrations(@PathParam("contactId") String contactId) {
        try {
            List<LawyerRegistrationDTO> registrations = lookupService().getLawyerRegistrations(contactId);
            return Response.ok(registrations, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao listar inscrições OAB para contato " + contactId, e);
            return RestErrorResponses.serverError(e);
        }
    }

    @Override
    @POST
    @Path("/contacts/{contactId}/oab")
    @RolesAllowed({"user", "admin"})
    public Response saveLawyerRegistration(@PathParam("contactId") String contactId, LawyerRegistrationDTO registration) {
        try {
            if (registration == null) {
                return badRequest("Corpo da requisição não pode ser vazio");
            }
            registration.setContactId(contactId);
            LawyerRegistrationDTO saved = lookupService().saveLawyerRegistration(registration);
            return Response.ok(saved, MediaType.APPLICATION_JSON).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao salvar inscrição OAB para contato " + contactId, e);
            return RestErrorResponses.serverError(e);
        }
    }

    @Override
    @DELETE
    @Path("/oab/{registrationId}")
    @RolesAllowed({"user", "admin"})
    public Response deleteLawyerRegistration(@PathParam("registrationId") String registrationId) {
        try {
            lookupService().deleteLawyerRegistration(registrationId);
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir inscrição OAB " + registrationId, e);
            return RestErrorResponses.serverError(e);
        }
    }

    // --- CATÁLOGO DE TRIBUNAIS ---

    @Override
    @GET
    @Path("/courts")
    @RolesAllowed({"user", "admin"})
    public Response listCourts(@QueryParam("segment") Integer segment) {
        try {
            List<JudiciaryCourtDTO> courts;
            if (segment != null) {
                courts = lookupService().listCourtsBySegment(segment);
            } else {
                courts = lookupService().listCourts();
            }
            return Response.ok(courts, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao listar tribunais", e);
            return RestErrorResponses.serverError(e);
        }
    }

    @Override
    @GET
    @Path("/courts/{code}")
    @RolesAllowed({"user", "admin"})
    public Response getCourtByCode(@PathParam("code") String code) {
        try {
            JudiciaryCourtDTO court = lookupService().getCourtByCode(code);
            if (court == null) {
                return notFound("Tribunal não encontrado com sigla: " + code);
            }
            return Response.ok(court, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao buscar tribunal por código: " + code, e);
            return RestErrorResponses.serverError(e);
        }
    }

    // --- CATÁLOGO TPU ---

    @Override
    @GET
    @Path("/tpu/classes")
    @RolesAllowed({"user", "admin"})
    public Response listTpuClasses(@QueryParam("q") String query) {
        try {
            List<TpuClassDTO> classes = lookupService().searchTpuClasses(query);
            return Response.ok(classes, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao pesquisar classes TPU", e);
            return RestErrorResponses.serverError(e);
        }
    }

    @Override
    @GET
    @Path("/tpu/subjects")
    @RolesAllowed({"user", "admin"})
    public Response listTpuSubjects(@QueryParam("q") String query) {
        try {
            List<TpuSubjectDTO> subjects = lookupService().searchTpuSubjects(query);
            return Response.ok(subjects, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Erro ao pesquisar assuntos TPU", e);
            return RestErrorResponses.serverError(e);
        }
    }
}
