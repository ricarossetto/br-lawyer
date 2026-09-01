/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.model;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO para tratamento de publicação com opção de criação direta de tarefa e prazo jurídico.
 *
 * @author BR-LAWYER Team
 */
public class PublicationTreatRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String user;
    private String notes;
    private boolean createFollowUpTask;
    private String taskTitle;
    private String taskDescription;
    private String taskCategory;
    private String taskPriority;
    private String taskAssignedUser;
    private Date taskDueDate;
    private String taskDueTime;
    private boolean syncWithCalendar = true; // Sincroniza diretamente como Frist/Wiedervorlage no case_events

    public PublicationTreatRequestDTO() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCreateFollowUpTask() {
        return createFollowUpTask;
    }

    public void setCreateFollowUpTask(boolean createFollowUpTask) {
        this.createFollowUpTask = createFollowUpTask;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    public String getTaskAssignedUser() {
        return taskAssignedUser;
    }

    public void setTaskAssignedUser(String taskAssignedUser) {
        this.taskAssignedUser = taskAssignedUser;
    }

    public Date getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(Date taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public String getTaskDueTime() {
        return taskDueTime;
    }

    public void setTaskDueTime(String taskDueTime) {
        this.taskDueTime = taskDueTime;
    }

    public boolean isSyncWithCalendar() {
        return syncWithCalendar;
    }

    public void setSyncWithCalendar(boolean syncWithCalendar) {
        this.syncWithCalendar = syncWithCalendar;
    }
}