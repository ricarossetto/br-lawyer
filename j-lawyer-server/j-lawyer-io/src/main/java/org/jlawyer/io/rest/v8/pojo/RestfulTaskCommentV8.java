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

import com.jdimension.jlawyer.domain.legal.model.TaskCommentDTO;

public class RestfulTaskCommentV8 {

    private String id;
    private String taskId;
    private String userName;
    private String commentText;
    private Long createdAt;

    public RestfulTaskCommentV8() {
    }

    public static RestfulTaskCommentV8 fromDTO(TaskCommentDTO dto) {
        if (dto == null) return null;
        RestfulTaskCommentV8 r = new RestfulTaskCommentV8();
        r.setId(dto.getId());
        r.setTaskId(dto.getTaskId());
        r.setUserName(dto.getUserName());
        r.setCommentText(dto.getCommentText());
        r.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().getTime() : null);
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}