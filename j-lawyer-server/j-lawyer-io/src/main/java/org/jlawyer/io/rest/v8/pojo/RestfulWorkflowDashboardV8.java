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

import com.jdimension.jlawyer.domain.legal.model.PublicationOverviewDTO;
import com.jdimension.jlawyer.domain.legal.model.TaskOverviewDTO;
import com.jdimension.jlawyer.domain.legal.model.WorkflowDashboardDTO;
import java.util.ArrayList;
import java.util.List;

public class RestfulWorkflowDashboardV8 {

    private long totalNewPublications;
    private long totalUnreadPublications;
    private long totalUntreatedPublications;

    private long totalOpenTasks;
    private long totalOverdueTasks;
    private long totalDueTodayTasks;
    private long totalDueNext7DaysTasks;
    private long totalMyOpenTasks;

    private long urgentTasksCount;
    private long highTasksCount;
    private long normalTasksCount;
    private long lowTasksCount;

    private long todoCount;
    private long inProgressCount;
    private long waitingCount;
    private long doneRecentlyCount;

    private List<RestfulPublicationOverviewV8> urgentPublications = new ArrayList<>();
    private List<RestfulTaskOverviewV8> urgentOverdueTasks = new ArrayList<>();
    private List<RestfulTaskOverviewV8> todayTasks = new ArrayList<>();

    public RestfulWorkflowDashboardV8() {
    }

    public static RestfulWorkflowDashboardV8 fromDTO(WorkflowDashboardDTO dto) {
        if (dto == null) return null;
        RestfulWorkflowDashboardV8 r = new RestfulWorkflowDashboardV8();
        r.setTotalNewPublications(dto.getTotalNewPublications());
        r.setTotalUnreadPublications(dto.getTotalUnreadPublications());
        r.setTotalUntreatedPublications(dto.getTotalUntreatedPublications());
        r.setTotalOpenTasks(dto.getTotalOpenTasks());
        r.setTotalOverdueTasks(dto.getTotalOverdueTasks());
        r.setTotalDueTodayTasks(dto.getTotalDueTodayTasks());
        r.setTotalDueNext7DaysTasks(dto.getTotalDueNext7DaysTasks());
        r.setTotalMyOpenTasks(dto.getTotalMyOpenTasks());
        r.setUrgentTasksCount(dto.getUrgentTasksCount());
        r.setHighTasksCount(dto.getHighTasksCount());
        r.setNormalTasksCount(dto.getNormalTasksCount());
        r.setLowTasksCount(dto.getLowTasksCount());
        r.setTodoCount(dto.getTodoCount());
        r.setInProgressCount(dto.getInProgressCount());
        r.setWaitingCount(dto.getWaitingCount());
        r.setDoneRecentlyCount(dto.getDoneRecentlyCount());

        if (dto.getUrgentPublications() != null) {
            for (PublicationOverviewDTO p : dto.getUrgentPublications()) {
                r.getUrgentPublications().add(RestfulPublicationOverviewV8.fromDTO(p));
            }
        }
        if (dto.getUrgentOverdueTasks() != null) {
            for (TaskOverviewDTO t : dto.getUrgentOverdueTasks()) {
                r.getUrgentOverdueTasks().add(RestfulTaskOverviewV8.fromDTO(t));
            }
        }
        if (dto.getTodayTasks() != null) {
            for (TaskOverviewDTO t : dto.getTodayTasks()) {
                r.getTodayTasks().add(RestfulTaskOverviewV8.fromDTO(t));
            }
        }
        return r;
    }

    public long getTotalNewPublications() { return totalNewPublications; }
    public void setTotalNewPublications(long totalNewPublications) { this.totalNewPublications = totalNewPublications; }
    public long getTotalUnreadPublications() { return totalUnreadPublications; }
    public void setTotalUnreadPublications(long totalUnreadPublications) { this.totalUnreadPublications = totalUnreadPublications; }
    public long getTotalUntreatedPublications() { return totalUntreatedPublications; }
    public void setTotalUntreatedPublications(long totalUntreatedPublications) { this.totalUntreatedPublications = totalUntreatedPublications; }
    public long getTotalOpenTasks() { return totalOpenTasks; }
    public void setTotalOpenTasks(long totalOpenTasks) { this.totalOpenTasks = totalOpenTasks; }
    public long getTotalOverdueTasks() { return totalOverdueTasks; }
    public void setTotalOverdueTasks(long totalOverdueTasks) { this.totalOverdueTasks = totalOverdueTasks; }
    public long getTotalDueTodayTasks() { return totalDueTodayTasks; }
    public void setTotalDueTodayTasks(long totalDueTodayTasks) { this.totalDueTodayTasks = totalDueTodayTasks; }
    public long getTotalDueNext7DaysTasks() { return totalDueNext7DaysTasks; }
    public void setTotalDueNext7DaysTasks(long totalDueNext7DaysTasks) { this.totalDueNext7DaysTasks = totalDueNext7DaysTasks; }
    public long getTotalMyOpenTasks() { return totalMyOpenTasks; }
    public void setTotalMyOpenTasks(long totalMyOpenTasks) { this.totalMyOpenTasks = totalMyOpenTasks; }
    public long getUrgentTasksCount() { return urgentTasksCount; }
    public void setUrgentTasksCount(long urgentTasksCount) { this.urgentTasksCount = urgentTasksCount; }
    public long getHighTasksCount() { return highTasksCount; }
    public void setHighTasksCount(long highTasksCount) { this.highTasksCount = highTasksCount; }
    public long getNormalTasksCount() { return normalTasksCount; }
    public void setNormalTasksCount(long normalTasksCount) { this.normalTasksCount = normalTasksCount; }
    public long getLowTasksCount() { return lowTasksCount; }
    public void setLowTasksCount(long lowTasksCount) { this.lowTasksCount = lowTasksCount; }
    public long getTodoCount() { return todoCount; }
    public void setTodoCount(long todoCount) { this.todoCount = todoCount; }
    public long getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(long inProgressCount) { this.inProgressCount = inProgressCount; }
    public long getWaitingCount() { return waitingCount; }
    public void setWaitingCount(long waitingCount) { this.waitingCount = waitingCount; }
    public long getDoneRecentlyCount() { return doneRecentlyCount; }
    public void setDoneRecentlyCount(long doneRecentlyCount) { this.doneRecentlyCount = doneRecentlyCount; }
    public List<RestfulPublicationOverviewV8> getUrgentPublications() { return urgentPublications; }
    public void setUrgentPublications(List<RestfulPublicationOverviewV8> urgentPublications) { this.urgentPublications = urgentPublications; }
    public List<RestfulTaskOverviewV8> getUrgentOverdueTasks() { return urgentOverdueTasks; }
    public void setUrgentOverdueTasks(List<RestfulTaskOverviewV8> urgentOverdueTasks) { this.urgentOverdueTasks = urgentOverdueTasks; }
    public List<RestfulTaskOverviewV8> getTodayTasks() { return todayTasks; }
    public void setTodayTasks(List<RestfulTaskOverviewV8> todayTasks) { this.todayTasks = todayTasks; }
}