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

public class RestfulTaskChecklistItemV8 {

    private String id;
    private String taskId;
    private String title;
    private boolean done;
    private int itemOrder;
    private Long completedAt;
    private String completedBy;

    public RestfulTaskChecklistItemV8() {
    }

    public static RestfulTaskChecklistItemV8 fromDTO(TaskChecklistItemDTO dto) {
        if (dto == null) return null;
        RestfulTaskChecklistItemV8 r = new RestfulTaskChecklistItemV8();
        r.setId(dto.getId());
        r.setTaskId(dto.getTaskId());
        r.setTitle(dto.getTitle());
        r.setDone(dto.isDone());
        r.setItemOrder(dto.getItemOrder());
        r.setCompletedAt(dto.getCompletedAt() != null ? dto.getCompletedAt().getTime() : null);
        r.setCompletedBy(dto.getCompletedBy());
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public int getItemOrder() { return itemOrder; }
    public void setItemOrder(int itemOrder) { this.itemOrder = itemOrder; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
}