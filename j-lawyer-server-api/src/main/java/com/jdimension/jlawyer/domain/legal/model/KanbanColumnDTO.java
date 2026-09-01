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
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma coluna do quadro Kanban (TODO, IN_PROGRESS, WAITING, DONE).
 *
 * @author BR-LAWYER Team
 */
public class KanbanColumnDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String title;
    private int count;
    private List<TaskOverviewDTO> tasks = new ArrayList<>();

    public KanbanColumnDTO() {
    }

    public KanbanColumnDTO(String status, String title) {
        this.status = status;
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<TaskOverviewDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskOverviewDTO> tasks) {
        this.tasks = tasks;
        this.count = (tasks != null) ? tasks.size() : 0;
    }
}