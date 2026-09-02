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
package org.jlawyer.io.rest.v8.pojo;

import com.jdimension.jlawyer.domain.legal.model.PublicationTreatRequestDTO;
import java.util.Date;

public class RestfulPublicationTreatRequestV8 {

    private String user;
    private String notes;
    private String action;
    private Boolean createTask;
    private Boolean createFollowUpTask;
    private String taskTitle;
    private String taskDescription;
    private String taskCategory;
    private String taskPriority;
    private String taskAssignedUser;
    private Long taskDueDate;
    private String taskDueTime;
    private Boolean syncCalendar;
    private Boolean syncWithCalendar;

    public RestfulPublicationTreatRequestV8() {
    }

    public PublicationTreatRequestDTO toDTO() {
        PublicationTreatRequestDTO dto = new PublicationTreatRequestDTO();
        dto.setUser(user);
        dto.setNotes(notes);
        boolean shouldCreate = (createTask != null && createTask) || (createFollowUpTask != null && createFollowUpTask);
        dto.setCreateFollowUpTask(shouldCreate);
        dto.setTaskTitle(taskTitle);
        dto.setTaskDescription(taskDescription);
        dto.setTaskCategory(taskCategory);
        dto.setTaskPriority(taskPriority);
        dto.setTaskAssignedUser(taskAssignedUser);
        if (taskDueDate != null) {
            dto.setTaskDueDate(new Date(taskDueDate));
        }
        dto.setTaskDueTime(taskDueTime);
        if (syncCalendar != null) {
            dto.setSyncWithCalendar(syncCalendar);
        } else if (syncWithCalendar != null) {
            dto.setSyncWithCalendar(syncWithCalendar);
        }
        return dto;
    }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Boolean getCreateTask() { return createTask; }
    public void setCreateTask(Boolean createTask) { this.createTask = createTask; }

    public Boolean getCreateFollowUpTask() { return createFollowUpTask; }
    public void setCreateFollowUpTask(Boolean createFollowUpTask) { this.createFollowUpTask = createFollowUpTask; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public String getTaskCategory() { return taskCategory; }
    public void setTaskCategory(String taskCategory) { this.taskCategory = taskCategory; }

    public String getTaskPriority() { return taskPriority; }
    public void setTaskPriority(String taskPriority) { this.taskPriority = taskPriority; }

    public String getTaskAssignedUser() { return taskAssignedUser; }
    public void setTaskAssignedUser(String taskAssignedUser) { this.taskAssignedUser = taskAssignedUser; }

    public Long getTaskDueDate() { return taskDueDate; }
    public void setTaskDueDate(Long taskDueDate) { this.taskDueDate = taskDueDate; }

    public String getTaskDueTime() { return taskDueTime; }
    public void setTaskDueTime(String taskDueTime) { this.taskDueTime = taskDueTime; }

    public Boolean getSyncCalendar() { return syncCalendar; }
    public void setSyncCalendar(Boolean syncCalendar) { this.syncCalendar = syncCalendar; }

    public Boolean getSyncWithCalendar() { return syncWithCalendar; }
    public void setSyncWithCalendar(Boolean syncWithCalendar) { this.syncWithCalendar = syncWithCalendar; }
}