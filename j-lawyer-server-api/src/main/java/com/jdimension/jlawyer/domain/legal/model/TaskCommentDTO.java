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
 * DTO para comentários de uma tarefa.
 *
 * @author BR-LAWYER Team
 */
public class TaskCommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String taskId;
    private String userName;
    private String commentText;
    private Date createdAt;

    public TaskCommentDTO() {
    }

    public TaskCommentDTO(String id, String taskId, String userName, String commentText, Date createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.userName = userName;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}