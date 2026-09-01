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
 * DTO resumido para exibição de tarefas jurídicas em listas e quadros Kanban.
 *
 * @author BR-LAWYER Team
 */
public class TaskOverviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String processId;
    private String caseFileNumber;
    private String caseName;
    private String cnjNumber;
    private String publicationId;
    private String calendarEventId;
    private String assignedUser;
    private String createdBy;
    private String status;
    private String priority;
    private Date dueDate;
    private String dueTime;
    private boolean overdue;
    private boolean dueToday;
    private String category;
    private int checklistTotalCount;
    private int checklistDoneCount;
    private int commentCount;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private Date createdAt;
    private Date completedAt;

    public TaskOverviewDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getCaseFileNumber() {
        return caseFileNumber;
    }

    public void setCaseFileNumber(String caseFileNumber) {
        this.caseFileNumber = caseFileNumber;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCnjNumber() {
        return cnjNumber;
    }

    public void setCnjNumber(String cnjNumber) {
        this.cnjNumber = cnjNumber;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public boolean isDueToday() {
        return dueToday;
    }

    public void setDueToday(boolean dueToday) {
        this.dueToday = dueToday;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getChecklistTotalCount() {
        return checklistTotalCount;
    }

    public void setChecklistTotalCount(int checklistTotalCount) {
        this.checklistTotalCount = checklistTotalCount;
    }

    public int getChecklistDoneCount() {
        return checklistDoneCount;
    }

    public void setChecklistDoneCount(int checklistDoneCount) {
        this.checklistDoneCount = checklistDoneCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Integer getActualMinutes() {
        return actualMinutes;
    }

    public void setActualMinutes(Integer actualMinutes) {
        this.actualMinutes = actualMinutes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
}