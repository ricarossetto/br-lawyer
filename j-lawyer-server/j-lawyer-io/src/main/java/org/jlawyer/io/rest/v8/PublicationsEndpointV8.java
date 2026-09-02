/*
 * Copyright (C) 2026 Jens Kutschke / BR-LAWYER Team
 *
 * This file is part of j-lawyer.org.
 *
 * j-lawyer.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.jlawyer.io.rest.v8;

import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.services.BrazilianPublicationServiceLocal;
import org.jboss.logging.Logger;
import org.jlawyer.io.rest.tools.RestErrorResponses;
import org.jlawyer.io.rest.v8.pojo.*;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Path("/v8/publications")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(tags = {"Brazilian Publications"})
public class PublicationsEndpointV8 implements PublicationsEndpointLocalV8 {

    private static final Logger log = Logger.getLogger(PublicationsEndpointV8.class.getName());
    private static final String LOOKUP_PUB = "java:global/j-lawyer-server/j-lawyer-server-ejb/BrazilianPublicationService!com.jdimension.jlawyer.services.BrazilianPublicationServiceLocal";

    @Resource
    private SessionContext sessionContext;

    private BrazilianPublicationServiceLocal getService() throws Exception {
        InitialContext ic = new InitialContext();
        return (BrazilianPublicationServiceLocal) ic.lookup(LOOKUP_PUB);
    }

    private String getCallerPrincipal() {
        try {
            if (sessionContext != null && sessionContext.getCallerPrincipal() != null) {
                String name = sessionContext.getCallerPrincipal().getName();
                if (name != null && !name.trim().isEmpty() && !"anonymous".equalsIgnoreCase(name.trim())) {
                    return name.trim();
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "system";
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Lists Brazilian legal publications with filters", response = RestfulPublicationOverviewV8.class, responseContainer = "List")
    public Response listPublications(
            @QueryParam("status") String status,
            @QueryParam("readStatus") String readStatus,
            @QueryParam("treatmentStatus") String treatmentStatus,
            @QueryParam("courtCode") String courtCode,
            @QueryParam("processId") String processId,
            @QueryParam("cnjNumber") String cnjNumber,
            @QueryParam("assignedUser") String assignedUser,
            @QueryParam("lawyerOab") String lawyerOab,
            @QueryParam("searchText") String searchText,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("limit") @DefaultValue("50") int limit) {
        try {
            PublicationFilterDTO filter = new PublicationFilterDTO();
            filter.setStatus(status);
            filter.setReadStatus(readStatus);
            filter.setTreatmentStatus(treatmentStatus);
            filter.setCourtCode(courtCode);
            filter.setProcessId(processId);
            filter.setCnjNumber(cnjNumber);
            filter.setAssignedUser(assignedUser);
            filter.setLawyerOab(lawyerOab);
            filter.setSearchText(searchText);
            filter.setFromDate(parseDate(fromDate));
            filter.setToDate(parseDate(toDate));
            filter.setPageSize(limit <= 0 ? 50 : Math.min(limit, 500));

            List<PublicationOverviewDTO> list = getService().listPublications(filter);
            List<RestfulPublicationOverviewV8> res = new ArrayList<>(list.size());
            for (PublicationOverviewDTO dto : list) {
                res.add(RestfulPublicationOverviewV8.fromDTO(dto));
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            log.error("Can not list publications", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @GET
    @Path("/page")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Paginated query for Brazilian legal publications", response = RestfulPublicationPageV8.class)
    public Response getPublicationPage(
            @QueryParam("status") String status,
            @QueryParam("readStatus") String readStatus,
            @QueryParam("treatmentStatus") String treatmentStatus,
            @QueryParam("courtCode") String courtCode,
            @QueryParam("processId") String processId,
            @QueryParam("cnjNumber") String cnjNumber,
            @QueryParam("assignedUser") String assignedUser,
            @QueryParam("lawyerOab") String lawyerOab,
            @QueryParam("searchText") String searchText,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("25") int pageSize) {
        try {
            PublicationFilterDTO filter = new PublicationFilterDTO();
            filter.setStatus(status);
            filter.setReadStatus(readStatus);
            filter.setTreatmentStatus(treatmentStatus);
            filter.setCourtCode(courtCode);
            filter.setProcessId(processId);
            filter.setCnjNumber(cnjNumber);
            filter.setAssignedUser(assignedUser);
            filter.setLawyerOab(lawyerOab);
            filter.setSearchText(searchText);
            filter.setFromDate(parseDate(fromDate));
            filter.setToDate(parseDate(toDate));
            filter.setPage(Math.max(0, page));
            filter.setPageSize(pageSize <= 0 ? 25 : Math.min(pageSize, 200));

            long total = getService().countPublications(filter);
            List<PublicationOverviewDTO> list = getService().listPublications(filter);
            List<RestfulPublicationOverviewV8> items = new ArrayList<>(list.size());
            for (PublicationOverviewDTO dto : list) {
                items.add(RestfulPublicationOverviewV8.fromDTO(dto));
            }

            RestfulPublicationPageV8 result = new RestfulPublicationPageV8(total, filter.getPage(), filter.getPageSize(), items);
            return Response.ok(result).build();
        } catch (Exception ex) {
            log.error("Can not get publications page", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Gets publication details by ID", response = RestfulPublicationDetailV8.class)
    public Response getPublication(@PathParam("id") String id) {
        try {
            PublicationDetailDTO dto = getService().getPublication(id);
            if (dto == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(dto)).build();
        } catch (Exception ex) {
            log.error("Can not get publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Creates or ingests a publication with deduplication", response = RestfulPublicationDetailV8.class)
    public Response createPublication(PublicationDetailDTO dto) {
        try {
            if (dto == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            PublicationDetailDTO created = getService().deduplicateAndIngest(dto, getCallerPrincipal());
            return Response.status(Response.Status.CREATED).entity(RestfulPublicationDetailV8.fromDTO(created)).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            log.error("Can not create publication", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Updates a publication by ID", response = RestfulPublicationDetailV8.class)
    public Response updatePublication(@PathParam("id") String id, PublicationDetailDTO dto) {
        try {
            if (dto == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            dto.setId(id);
            PublicationDetailDTO updated = getService().savePublication(dto, getCallerPrincipal());
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not update publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/assign")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Assigns publication to a user", response = RestfulPublicationDetailV8.class)
    public Response assignPublication(@PathParam("id") String id, @QueryParam("user") String assignedUser) {
        try {
            PublicationDetailDTO updated = getService().assignPublication(id, assignedUser, getCallerPrincipal());
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not assign publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/link-case")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Links publication to a legal case", response = RestfulPublicationDetailV8.class)
    public Response linkCase(@PathParam("id") String id, PublicationLinkRequestDTO request) {
        try {
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (request.getUser() == null || request.getUser().trim().isEmpty() || "CURRENT_USER".equalsIgnoreCase(request.getUser().trim())) {
                request.setUser(getCallerPrincipal());
            }
            PublicationDetailDTO updated = getService().linkToCase(id, request);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            log.error("Can not link case to publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/unlink-case")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Unlinks publication from legal case", response = RestfulPublicationDetailV8.class)
    public Response unlinkCase(@PathParam("id") String id) {
        try {
            PublicationDetailDTO updated = getService().unlinkFromCase(id, getCallerPrincipal());
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not unlink case from publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/mark-read")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Toggles read status of publication", response = RestfulPublicationDetailV8.class)
    public Response markRead(@PathParam("id") String id, @QueryParam("read") @DefaultValue("true") boolean read) {
        try {
            PublicationDetailDTO updated = getService().markRead(id, read, getCallerPrincipal());
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not mark publication read " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/treat")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Marks publication as treated, optionally creating follow-up task/deadline", response = RestfulPublicationDetailV8.class)
    public Response treatPublication(@PathParam("id") String id, RestfulPublicationTreatRequestV8 request) {
        try {
            PublicationTreatRequestDTO dto = request != null ? request.toDTO() : new PublicationTreatRequestDTO();
            if (dto.getUser() == null || dto.getUser().trim().isEmpty() || "CURRENT_USER".equalsIgnoreCase(dto.getUser().trim())) {
                dto.setUser(getCallerPrincipal());
            }
            PublicationDetailDTO updated = getService().treatPublication(id, dto);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not treat publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/archive")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Archives/dispenses publication", response = RestfulPublicationDetailV8.class)
    public Response archivePublication(@PathParam("id") String id, @QueryParam("reason") String reason) {
        try {
            PublicationDetailDTO updated = getService().archivePublication(id, getCallerPrincipal(), reason);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulPublicationDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not archive publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Deletes publication by ID")
    public Response deletePublication(@PathParam("id") String id) {
        try {
            getService().deletePublication(id, getCallerPrincipal());
            return Response.noContent().build();
        } catch (Exception ex) {
            log.error("Can not delete publication " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    private static Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            fmt.setLenient(false);
            return fmt.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}