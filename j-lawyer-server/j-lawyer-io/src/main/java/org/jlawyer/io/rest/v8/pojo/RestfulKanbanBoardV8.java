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

import com.jdimension.jlawyer.domain.legal.model.KanbanBoardDTO;
import com.jdimension.jlawyer.domain.legal.model.KanbanColumnDTO;
import java.util.ArrayList;
import java.util.List;

public class RestfulKanbanBoardV8 {

    private int totalTasks;
    private List<RestfulKanbanColumnV8> columns = new ArrayList<>();

    public RestfulKanbanBoardV8() {
    }

    public static RestfulKanbanBoardV8 fromDTO(KanbanBoardDTO dto) {
        if (dto == null) return null;
        RestfulKanbanBoardV8 r = new RestfulKanbanBoardV8();
        r.setTotalTasks(dto.getTotalTasks());
        if (dto.getColumns() != null) {
            for (KanbanColumnDTO c : dto.getColumns()) {
                r.getColumns().add(RestfulKanbanColumnV8.fromDTO(c));
            }
        }
        return r;
    }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    public List<RestfulKanbanColumnV8> getColumns() { return columns; }
    public void setColumns(List<RestfulKanbanColumnV8> columns) { this.columns = columns; }
}