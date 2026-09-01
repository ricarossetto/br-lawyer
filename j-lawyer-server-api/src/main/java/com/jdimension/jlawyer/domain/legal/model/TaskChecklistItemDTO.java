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
 * DTO para itens de checklist de uma tarefa.
 *
 * @author BR-LAWYER Team
 */
public class TaskChecklistItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String taskId;
    private String title;
    private boolean done;
    private int itemOrder;
    private Date completedAt;
    private String completedBy;

    public TaskChecklistItemDTO() {
    }

    public TaskChecklistItemDTO(String id, String taskId, String title, boolean done, int itemOrder) {
        this.id = id;
        this.taskId = taskId;
        this.title = title;
        this.done = done;
        this.itemOrder = itemOrder;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }
}