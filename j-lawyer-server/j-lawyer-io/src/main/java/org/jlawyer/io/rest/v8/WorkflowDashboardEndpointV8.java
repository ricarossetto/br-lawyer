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

import com.jdimension.jlawyer.domain.legal.model.WorkflowDashboardDTO;
import com.jdimension.jlawyer.services.BrazilianWorkflowDashboardServiceLocal;
import org.jboss.logging.Logger;
import org.jlawyer.io.rest.tools.RestErrorResponses;
import org.jlawyer.io.rest.v8.pojo.RestfulWorkflowDashboardV8;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/v8/workflow")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(tags = {"Brazilian Workflow Dashboard"})
public class WorkflowDashboardEndpointV8 implements WorkflowDashboardEndpointLocalV8 {

    private static final Logger log = Logger.getLogger(WorkflowDashboardEndpointV8.class.getName());
    private static final String LOOKUP_DASH = "java:global/j-lawyer-server/j-lawyer-server-ejb/BrazilianWorkflowDashboardService!com.jdimension.jlawyer.services.BrazilianWorkflowDashboardServiceLocal";

    @Resource
    private SessionContext sessionContext;

    private BrazilianWorkflowDashboardServiceLocal getService() throws Exception {
        InitialContext ic = new InitialContext();
        return (BrazilianWorkflowDashboardServiceLocal) ic.lookup(LOOKUP_DASH);
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
    @Path("/dashboard")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Retrieves aggregated Brazilian legal workflow metrics and priority items for dashboard", response = RestfulWorkflowDashboardV8.class)
    public Response getDashboard() {
        try {
            WorkflowDashboardDTO dto = getService().getDashboard(getCallerPrincipal());
            return Response.ok(RestfulWorkflowDashboardV8.fromDTO(dto)).build();
        } catch (Exception ex) {
            log.error("Can not get workflow dashboard", ex);
            return RestErrorResponses.serverError(ex);
        }
    }
}