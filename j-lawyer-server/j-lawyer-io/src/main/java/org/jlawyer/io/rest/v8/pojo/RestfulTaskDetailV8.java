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

import com.jdimension.jlawyer.domain.legal.model.TaskChecklistItemDTO;
import com.jdimension.jlawyer.domain.legal.model.TaskCommentDTO;
import com.jdimension.jlawyer.domain.legal.model.TaskDetailDTO;
import java.util.ArrayList;
import java.util.List;

public class RestfulTaskDetailV8 {

    private String id;
    private String title;
    private String description;
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
    private Long dueDate;
    private String dueTime;
    private boolean overdue;
    private boolean dueToday;
    private Long completedAt;
    private String completedBy;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private String category;
    private String notes;
    private Long createdAt;
    private Long updatedAt;

    private List<RestfulTaskCommentV8> comments = new ArrayList<>();
    private List<RestfulTaskChecklistItemV8> checklistItems = new ArrayList<>();

    public RestfulTaskDetailV8() {
    }

    public static RestfulTaskDetailV8 fromDTO(TaskDetailDTO dto) {
        if (dto == null) return null;
        RestfulTaskDetailV8 r = new RestfulTaskDetailV8();
        r.setId(dto.getId());
        r.setTitle(dto.getTitle());
        r.setDescription(dto.getDescription());
        r.setProcessId(dto.getProcessId());
        r.setCaseFileNumber(dto.getCaseFileNumber());
        r.setCaseName(dto.getCaseName());
        r.setCnjNumber(dto.getCnjNumber());
        r.setPublicationId(dto.getPublicationId());
        r.setCalendarEventId(dto.getCalendarEventId());
        r.setAssignedUser(dto.getAssignedUser());
        r.setCreatedBy(dto.getCreatedBy());
        r.setStatus(dto.getStatus());
        r.setPriority(dto.getPriority());
        r.setDueDate(dto.getDueDate() != null ? dto.getDueDate().getTime() : null);
        r.setDueTime(dto.getDueTime());
        r.setOverdue(dto.isOverdue());
        r.setDueToday(dto.isDueToday());
        r.setCompletedAt(dto.getCompletedAt() != null ? dto.getCompletedAt().getTime() : null);
        r.setCompletedBy(dto.getCompletedBy());
        r.setEstimatedMinutes(dto.getEstimatedMinutes());
        r.setActualMinutes(dto.getActualMinutes());
        r.setCategory(dto.getCategory());
        r.setNotes(dto.getNotes());
        r.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().getTime() : null);
        r.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().getTime() : null);

        if (dto.getComments() != null) {
            for (TaskCommentDTO c : dto.getComments()) {
                r.getComments().add(RestfulTaskCommentV8.fromDTO(c));
            }
        }
        if (dto.getChecklistItems() != null) {
            for (TaskChecklistItemDTO i : dto.getChecklistItems()) {
                r.getChecklistItems().add(RestfulTaskChecklistItemV8.fromDTO(i));
            }
        }
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    public String getCaseFileNumber() { return caseFileNumber; }
    public void setCaseFileNumber(String caseFileNumber) { this.caseFileNumber = caseFileNumber; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public String getCnjNumber() { return cnjNumber; }
    public void setCnjNumber(String cnjNumber) { this.cnjNumber = cnjNumber; }
    public String getPublicationId() { return publicationId; }
    public void setPublicationId(String publicationId) { this.publicationId = publicationId; }
    public String getCalendarEventId() { return calendarEventId; }
    public void setCalendarEventId(String calendarEventId) { this.calendarEventId = calendarEventId; }
    public String getAssignedUser() { return assignedUser; }
    public void setAssignedUser(String assignedUser) { this.assignedUser = assignedUser; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Long getDueDate() { return dueDate; }
    public void setDueDate(Long dueDate) { this.dueDate = dueDate; }
    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
    public boolean isDueToday() { return dueToday; }
    public void setDueToday(boolean dueToday) { this.dueToday = dueToday; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
    public Integer getActualMinutes() { return actualMinutes; }
    public void setActualMinutes(Integer actualMinutes) { this.actualMinutes = actualMinutes; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public List<RestfulTaskCommentV8> getComments() { return comments; }
    public void setComments(List<RestfulTaskCommentV8> comments) { this.comments = comments; }
    public List<RestfulTaskChecklistItemV8> getChecklistItems() { return checklistItems; }
    public void setChecklistItems(List<RestfulTaskChecklistItemV8> checklistItems) { this.checklistItems = checklistItems; }
}