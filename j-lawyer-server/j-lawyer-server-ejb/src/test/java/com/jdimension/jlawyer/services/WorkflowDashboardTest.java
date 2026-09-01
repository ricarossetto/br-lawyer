/*
 * Copyright (C) j-lawyer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class WorkflowDashboardTest {

    @Test
    public void testWorkflowDashboardDtoAggregation() {
        WorkflowDashboardDTO dto = new WorkflowDashboardDTO();
        dto.setTotalNewPublications(15);
        dto.setTotalUntreatedPublications(8);
        dto.setTotalOpenTasks(22);
        dto.setTotalOverdueTasks(3);
        dto.setTotalDueTodayTasks(5);

        Assert.assertEquals(15, dto.getTotalNewPublications());
        Assert.assertEquals(8, dto.getTotalUntreatedPublications());
        Assert.assertEquals(22, dto.getTotalOpenTasks());
        Assert.assertEquals(3, dto.getTotalOverdueTasks());
        Assert.assertEquals(5, dto.getTotalDueTodayTasks());

        dto.setUrgentOverdueTasks(new ArrayList<TaskOverviewDTO>());
        dto.setUrgentPublications(new ArrayList<PublicationOverviewDTO>());
        dto.setTodayTasks(new ArrayList<TaskOverviewDTO>());

        Assert.assertNotNull(dto.getUrgentOverdueTasks());
        Assert.assertNotNull(dto.getUrgentPublications());
        Assert.assertNotNull(dto.getTodayTasks());
    }

    @Test
    public void testKanbanBoardDtoGeneration() {
        KanbanBoardDTO board = new KanbanBoardDTO();
        board.getColumns().add(new KanbanColumnDTO("TODO", "A Fazer"));
        board.getColumns().add(new KanbanColumnDTO("IN_PROGRESS", "Em Andamento"));
        board.getColumns().add(new KanbanColumnDTO("WAITING", "Aguardando Terceiro"));
        board.getColumns().add(new KanbanColumnDTO("DONE", "Concluído"));

        Assert.assertEquals(4, board.getColumns().size());
        Assert.assertEquals("TODO", board.getColumns().get(0).getStatus());
        Assert.assertEquals("IN_PROGRESS", board.getColumns().get(1).getStatus());
        Assert.assertEquals("WAITING", board.getColumns().get(2).getStatus());
        Assert.assertEquals("DONE", board.getColumns().get(3).getStatus());
    }
}