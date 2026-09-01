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
import com.jdimension.jlawyer.persistence.ArchiveFileBean;
import com.jdimension.jlawyer.persistence.ArchiveFileReviewsBean;
import com.jdimension.jlawyer.persistence.BrTask;
import com.jdimension.jlawyer.persistence.BrTaskChecklistItem;
import com.jdimension.jlawyer.persistence.BrTaskComment;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class TaskDeadlineIntegrationTest {

    @Test
    public void testTaskLifecycleAndCalendarSyncMapping() {
        BrTask task = new BrTask();
        task.setId("TASK-TEST-001");
        task.setTitle("Elaborar Recurso Inominado");
        task.setDescription("Prazo fatal de 10 dias úteis.");
        task.setPriority("URGENT");
        task.setCategory("PETICAO");
        task.setStatus("TODO");
        task.setAssignedUser("ricardo");
        task.setProcessId("1234");
        Date due = new Date(System.currentTimeMillis() + 86400000L * 10);
        task.setDueDate(due);
        task.setDueTime("18:00");

        Assert.assertEquals("TASK-TEST-001", task.getId());
        Assert.assertEquals("URGENT", task.getPriority());
        Assert.assertEquals("PETICAO", task.getCategory());
        Assert.assertEquals("TODO", task.getStatus());

        // Calendar event synchronization representation
        ArchiveFileReviewsBean review = new ArchiveFileReviewsBean();
        review.setId("REV-999");
        ArchiveFileBean mockCase = new ArchiveFileBean();
        mockCase.setId("1234");
        review.setArchiveFileKey(mockCase);
        review.setBeginDate(task.getDueDate());
        review.setEndDate(task.getDueDate());
        review.setEventType(20); // EVENTTYPE_RESPITE (Frist / Prazo fatal)
        review.setSummary(task.getTitle());
        review.setDescription(task.getDescription());
        review.setDone(false);

        task.setCalendarEventId("REV-999");
        Assert.assertEquals("REV-999", task.getCalendarEventId());
        Assert.assertEquals(20, review.getEventType());
        Assert.assertFalse(review.isDone());

        // Completion transition
        task.setStatus("DONE");
        task.setCompletedAt(new Date());
        task.setCompletedBy("ricardo");
        review.setDone(true);

        Assert.assertEquals("DONE", task.getStatus());
        Assert.assertNotNull(task.getCompletedAt());
        Assert.assertTrue(review.isDone());
    }

    @Test
    public void testTaskChecklistAndComments() {
        BrTask task = new BrTask();
        task.setId("TASK-TEST-002");
        task.setTitle("Preparar Audiência");

        BrTaskChecklistItem item1 = new BrTaskChecklistItem();
        item1.setId("ITEM-1");
        item1.setTask(task);
        item1.setTitle("Ligar para testemunhas");
        item1.setDone(false);

        BrTaskChecklistItem item2 = new BrTaskChecklistItem();
        item2.setId("ITEM-2");
        item2.setTask(task);
        item2.setTitle("Imprimir procuração");
        item2.setDone(true);

        task.getChecklistItems().add(item1);
        task.getChecklistItems().add(item2);

        Assert.assertEquals(2, task.getChecklistItems().size());

        BrTaskComment comment = new BrTaskComment();
        comment.setId("COMM-1");
        comment.setTask(task);
        comment.setUserName("ricardo");
        comment.setCommentText("Testemunha confirmou presença.");
        comment.setCreatedAt(new Date());

        task.getComments().add(comment);
        Assert.assertEquals(1, task.getComments().size());
        Assert.assertEquals("ricardo", task.getComments().get(0).getUserName());
        Assert.assertEquals("Testemunha confirmou presença.", task.getComments().get(0).getCommentText());
    }
}