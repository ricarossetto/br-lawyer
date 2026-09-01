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

import com.jdimension.jlawyer.domain.legal.model.KanbanColumnDTO;
import com.jdimension.jlawyer.domain.legal.model.TaskOverviewDTO;
import java.util.ArrayList;
import java.util.List;

public class RestfulKanbanColumnV8 {

    private String status;
    private String title;
    private int count;
    private List<RestfulTaskOverviewV8> tasks = new ArrayList<>();

    public RestfulKanbanColumnV8() {
    }

    public static RestfulKanbanColumnV8 fromDTO(KanbanColumnDTO dto) {
        if (dto == null) return null;
        RestfulKanbanColumnV8 r = new RestfulKanbanColumnV8();
        r.setStatus(dto.getStatus());
        r.setTitle(dto.getTitle());
        r.setCount(dto.getCount());
        if (dto.getTasks() != null) {
            for (TaskOverviewDTO t : dto.getTasks()) {
                r.getTasks().add(RestfulTaskOverviewV8.fromDTO(t));
            }
        }
        return r;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<RestfulTaskOverviewV8> getTasks() { return tasks; }
    public void setTasks(List<RestfulTaskOverviewV8> tasks) { this.tasks = tasks; }
}