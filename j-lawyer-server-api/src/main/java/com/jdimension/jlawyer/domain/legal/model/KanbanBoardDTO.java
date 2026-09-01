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
 * DTO para o quadro Kanban de tarefas jurídicas completo.
 *
 * @author BR-LAWYER Team
 */
public class KanbanBoardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalTasks;
    private List<KanbanColumnDTO> columns = new ArrayList<>();

    public KanbanBoardDTO() {
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public List<KanbanColumnDTO> getColumns() {
        return columns;
    }

    public void setColumns(List<KanbanColumnDTO> columns) {
        this.columns = columns;
    }
}