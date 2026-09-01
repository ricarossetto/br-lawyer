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
import com.jdimension.jlawyer.services.BrazilianTaskServiceLocal;
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
@Path("/v8/tasks")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(tags = {"Brazilian Legal Tasks"})
public class TasksEndpointV8 implements TasksEndpointLocalV8 {

    private static final Logger log = Logger.getLogger(TasksEndpointV8.class.getName());
    private static final String LOOKUP_TASK = "java:global/j-lawyer-server/j-lawyer-server-ejb/BrazilianTaskService!com.jdimension.jlawyer.services.BrazilianTaskServiceLocal";

    @Resource
    private SessionContext sessionContext;

    private BrazilianTaskServiceLocal getService() throws Exception {
        InitialContext ic = new InitialContext();
        return (BrazilianTaskServiceLocal) ic.lookup(LOOKUP_TASK);
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
    @io.swagger.annotations.ApiOperation(value = "Lists legal tasks with filters", response = RestfulTaskOverviewV8.class, responseContainer = "List")
    public Response listTasks(
            @QueryParam("status") String status,
            @QueryParam("assignedUser") String assignedUser,
            @QueryParam("processId") String processId,
            @QueryParam("priority") String priority,
            @QueryParam("category") String category,
            @QueryParam("overdue") Boolean overdue,
            @QueryParam("dueToday") Boolean dueToday,
            @QueryParam("fromDueDate") String fromDueDate,
            @QueryParam("toDueDate") String toDueDate,
            @QueryParam("searchText") String searchText,
            @QueryParam("limit") @DefaultValue("50") int limit) {
        try {
            TaskFilterDTO filter = new TaskFilterDTO();
            filter.setStatus(status);
            filter.setAssignedUser(assignedUser);
            filter.setProcessId(processId);
            filter.setPriority(priority);
            filter.setCategory(category);
            filter.setOverdue(overdue);
            filter.setDueToday(dueToday);
            filter.setFromDueDate(parseDate(fromDueDate));
            filter.setToDueDate(parseDate(toDueDate));
            filter.setSearchText(searchText);
            filter.setPageSize(limit <= 0 ? 50 : Math.min(limit, 500));

            List<TaskOverviewDTO> list = getService().listTasks(filter);
            List<RestfulTaskOverviewV8> res = new ArrayList<>(list.size());
            for (TaskOverviewDTO dto : list) {
                res.add(RestfulTaskOverviewV8.fromDTO(dto));
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            log.error("Can not list tasks", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @GET
    @Path("/page")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Paginated query for legal tasks", response = RestfulTaskPageV8.class)
    public Response getTaskPage(
            @QueryParam("status") String status,
            @QueryParam("assignedUser") String assignedUser,
            @QueryParam("processId") String processId,
            @QueryParam("priority") String priority,
            @QueryParam("category") String category,
            @QueryParam("overdue") Boolean overdue,
            @QueryParam("dueToday") Boolean dueToday,
            @QueryParam("fromDueDate") String fromDueDate,
            @QueryParam("toDueDate") String toDueDate,
            @QueryParam("searchText") String searchText,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("25") int pageSize) {
        try {
            TaskFilterDTO filter = new TaskFilterDTO();
            filter.setStatus(status);
            filter.setAssignedUser(assignedUser);
            filter.setProcessId(processId);
            filter.setPriority(priority);
            filter.setCategory(category);
            filter.setOverdue(overdue);
            filter.setDueToday(dueToday);
            filter.setFromDueDate(parseDate(fromDueDate));
            filter.setToDueDate(parseDate(toDueDate));
            filter.setSearchText(searchText);
            filter.setPage(Math.max(0, page));
            filter.setPageSize(pageSize <= 0 ? 25 : Math.min(pageSize, 200));

            long total = getService().countTasks(filter);
            List<TaskOverviewDTO> list = getService().listTasks(filter);
            List<RestfulTaskOverviewV8> items = new ArrayList<>(list.size());
            for (TaskOverviewDTO dto : list) {
                items.add(RestfulTaskOverviewV8.fromDTO(dto));
            }

            RestfulTaskPageV8 result = new RestfulTaskPageV8(total, filter.getPage(), filter.getPageSize(), items);
            return Response.ok(result).build();
        } catch (Exception ex) {
            log.error("Can not get task page", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @GET
    @Path("/kanban")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Retrieves Kanban board of legal tasks", response = RestfulKanbanBoardV8.class)
    public Response getKanbanBoard(@QueryParam("assignedUser") String assignedUser, @QueryParam("processId") String processId) {
        try {
            KanbanBoardDTO board = getService().getKanbanBoard(assignedUser, processId);
            return Response.ok(RestfulKanbanBoardV8.fromDTO(board)).build();
        } catch (Exception ex) {
            log.error("Can not get Kanban board", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Gets task details by ID", response = RestfulTaskDetailV8.class)
    public Response getTask(@PathParam("id") String id) {
        try {
            TaskDetailDTO dto = getService().getTask(id);
            if (dto == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulTaskDetailV8.fromDTO(dto)).build();
        } catch (Exception ex) {
            log.error("Can not get task " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Creates a legal task", response = RestfulTaskDetailV8.class)
    public Response createTask(TaskDetailDTO dto, @QueryParam("syncCalendar") @DefaultValue("true") boolean syncCalendar) {
        try {
            if (dto == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            TaskDetailDTO created = getService().saveTask(dto, getCallerPrincipal(), syncCalendar);
            return Response.status(Response.Status.CREATED).entity(RestfulTaskDetailV8.fromDTO(created)).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            log.error("Can not create task", ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Updates a legal task by ID", response = RestfulTaskDetailV8.class)
    public Response updateTask(@PathParam("id") String id, TaskDetailDTO dto, @QueryParam("syncCalendar") @DefaultValue("true") boolean syncCalendar) {
        try {
            if (dto == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            dto.setId(id);
            TaskDetailDTO updated = getService().saveTask(dto, getCallerPrincipal(), syncCalendar);
            return Response.ok(RestfulTaskDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not update task " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/status")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Changes task status (TODO, IN_PROGRESS, WAITING, DONE, CANCELLED)", response = RestfulTaskDetailV8.class)
    public Response changeStatus(@PathParam("id") String id, TaskStatusChangeDTO request) {
        try {
            if (request == null) {
                request = new TaskStatusChangeDTO();
            }
            if (request.getUser() == null || request.getUser().trim().isEmpty()) {
                request.setUser(getCallerPrincipal());
            }
            TaskDetailDTO updated = getService().changeStatus(id, request);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulTaskDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not change task status " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/assign")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Assigns task to a user", response = RestfulTaskDetailV8.class)
    public Response assignTask(@PathParam("id") String id, @QueryParam("user") String assignedUser) {
        try {
            TaskDetailDTO updated = getService().assignTask(id, assignedUser, getCallerPrincipal());
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulTaskDetailV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not assign task " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Adds a comment to a task", response = RestfulTaskCommentV8.class)
    public Response addComment(@PathParam("id") String id, @QueryParam("text") String commentText) {
        try {
            TaskCommentDTO created = getService().addComment(id, getCallerPrincipal(), commentText);
            return Response.status(Response.Status.CREATED).entity(RestfulTaskCommentV8.fromDTO(created)).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            log.error("Can not add comment to task " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @GET
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"readArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Gets task comments", response = RestfulTaskCommentV8.class, responseContainer = "List")
    public Response getComments(@PathParam("id") String id) {
        try {
            List<TaskCommentDTO> list = getService().getComments(id);
            List<RestfulTaskCommentV8> res = new ArrayList<>(list.size());
            for (TaskCommentDTO dto : list) {
                res.add(RestfulTaskCommentV8.fromDTO(dto));
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            log.error("Can not get task comments " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/{id}/checklist")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Adds a checklist item to a task", response = RestfulTaskChecklistItemV8.class)
    public Response addChecklistItem(@PathParam("id") String id, @QueryParam("title") String title, @QueryParam("order") @DefaultValue("0") int order) {
        try {
            TaskChecklistItemDTO created = getService().addChecklistItem(id, title, order);
            return Response.status(Response.Status.CREATED).entity(RestfulTaskChecklistItemV8.fromDTO(created)).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            log.error("Can not add checklist item to task " + id, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @POST
    @Path("/checklist/{itemId}/toggle")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Toggles completion state of a checklist item", response = RestfulTaskChecklistItemV8.class)
    public Response toggleChecklistItem(@PathParam("itemId") String itemId, @QueryParam("done") @DefaultValue("true") boolean done) {
        try {
            TaskChecklistItemDTO updated = getService().toggleChecklistItem(itemId, done, getCallerPrincipal());
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(RestfulTaskChecklistItemV8.fromDTO(updated)).build();
        } catch (Exception ex) {
            log.error("Can not toggle checklist item " + itemId, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @DELETE
    @Path("/checklist/{itemId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Deletes a checklist item")
    public Response deleteChecklistItem(@PathParam("itemId") String itemId) {
        try {
            getService().deleteChecklistItem(itemId);
            return Response.noContent().build();
        } catch (Exception ex) {
            log.error("Can not delete checklist item " + itemId, ex);
            return RestErrorResponses.serverError(ex);
        }
    }

    @Override
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed({"writeArchiveFileRole"})
    @io.swagger.annotations.ApiOperation(value = "Deletes a task by ID")
    public Response deleteTask(@PathParam("id") String id) {
        try {
            getService().deleteTask(id, getCallerPrincipal());
            return Response.noContent().build();
        } catch (Exception ex) {
            log.error("Can not delete task " + id, ex);
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