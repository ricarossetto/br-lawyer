/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.*;

import javax.ejb.Remote;
import java.util.List;

/**
 * Interface EJB Remota para Gestão de Tarefas Jurídicas, Prazos e Kanban.
 *
 * @author BR-LAWYER Team
 */
@Remote
public interface BrazilianTaskServiceRemote {

    TaskDetailDTO getTask(String id) throws Exception;

    TaskDetailDTO saveTask(TaskDetailDTO dto, String user, boolean syncCalendar) throws Exception;

    List<TaskOverviewDTO> listTasks(TaskFilterDTO filter) throws Exception;

    long countTasks(TaskFilterDTO filter) throws Exception;

    TaskDetailDTO changeStatus(String taskId, TaskStatusChangeDTO changeRequest) throws Exception;

    TaskDetailDTO assignTask(String taskId, String assignedUser, String actor) throws Exception;

    TaskCommentDTO addComment(String taskId, String userName, String commentText) throws Exception;

    List<TaskCommentDTO> getComments(String taskId) throws Exception;

    TaskChecklistItemDTO addChecklistItem(String taskId, String title, int order) throws Exception;

    TaskChecklistItemDTO toggleChecklistItem(String checklistItemId, boolean done, String user) throws Exception;

    void deleteChecklistItem(String checklistItemId) throws Exception;

    KanbanBoardDTO getKanbanBoard(String assignedUser, String processId) throws Exception;

    void deleteTask(String taskId, String user) throws Exception;
}