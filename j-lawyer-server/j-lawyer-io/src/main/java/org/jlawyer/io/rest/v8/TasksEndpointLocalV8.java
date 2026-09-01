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

import com.jdimension.jlawyer.domain.legal.model.TaskDetailDTO;
import com.jdimension.jlawyer.domain.legal.model.TaskStatusChangeDTO;
import javax.ejb.Local;
import javax.ws.rs.core.Response;

@Local
public interface TasksEndpointLocalV8 {

    Response listTasks(String status, String assignedUser, String processId, String priority,
                       String category, Boolean overdue, Boolean dueToday, String fromDueDate,
                       String toDueDate, String searchText, int limit);

    Response getTaskPage(String status, String assignedUser, String processId, String priority,
                         String category, Boolean overdue, Boolean dueToday, String fromDueDate,
                         String toDueDate, String searchText, int page, int pageSize);

    Response getTask(String id);

    Response createTask(TaskDetailDTO dto, boolean syncCalendar);

    Response updateTask(String id, TaskDetailDTO dto, boolean syncCalendar);

    Response changeStatus(String id, TaskStatusChangeDTO request);

    Response assignTask(String id, String assignedUser);

    Response addComment(String id, String commentText);

    Response getComments(String id);

    Response addChecklistItem(String id, String title, int order);

    Response toggleChecklistItem(String itemId, boolean done);

    Response deleteChecklistItem(String itemId);

    Response getKanbanBoard(String assignedUser, String processId);

    Response deleteTask(String id);
}