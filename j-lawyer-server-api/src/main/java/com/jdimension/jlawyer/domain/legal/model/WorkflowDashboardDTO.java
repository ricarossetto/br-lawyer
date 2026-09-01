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
 * DTO com métricas consolidadas e listas prioritárias para o Dashboard Operacional Brasileiro.
 * Calculado a partir de dados reais do banco, otimizado para chamada única pelo frontend.
 *
 * @author BR-LAWYER Team
 */
public class WorkflowDashboardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // Métricas de Publicações
    private long totalNewPublications;
    private long totalUnreadPublications;
    private long totalUntreatedPublications;

    // Métricas de Tarefas
    private long totalOpenTasks;
    private long totalOverdueTasks;
    private long totalDueTodayTasks;
    private long totalDueNext7DaysTasks;
    private long totalMyOpenTasks;

    // Distribuição de Tarefas por Prioridade
    private long urgentTasksCount;
    private long highTasksCount;
    private long normalTasksCount;
    private long lowTasksCount;

    // Distribuição de Tarefas por Status
    private long todoCount;
    private long inProgressCount;
    private long waitingCount;
    private long doneRecentlyCount;

    // Listas de Ação Rápida para o Dashboard
    private List<PublicationOverviewDTO> urgentPublications = new ArrayList<>();
    private List<TaskOverviewDTO> urgentOverdueTasks = new ArrayList<>();
    private List<TaskOverviewDTO> todayTasks = new ArrayList<>();

    public WorkflowDashboardDTO() {
    }

    public long getTotalNewPublications() {
        return totalNewPublications;
    }

    public void setTotalNewPublications(long totalNewPublications) {
        this.totalNewPublications = totalNewPublications;
    }

    public long getTotalUnreadPublications() {
        return totalUnreadPublications;
    }

    public void setTotalUnreadPublications(long totalUnreadPublications) {
        this.totalUnreadPublications = totalUnreadPublications;
    }

    public long getTotalUntreatedPublications() {
        return totalUntreatedPublications;
    }

    public void setTotalUntreatedPublications(long totalUntreatedPublications) {
        this.totalUntreatedPublications = totalUntreatedPublications;
    }

    public long getTotalOpenTasks() {
        return totalOpenTasks;
    }

    public void setTotalOpenTasks(long totalOpenTasks) {
        this.totalOpenTasks = totalOpenTasks;
    }

    public long getTotalOverdueTasks() {
        return totalOverdueTasks;
    }

    public void setTotalOverdueTasks(long totalOverdueTasks) {
        this.totalOverdueTasks = totalOverdueTasks;
    }

    public long getTotalDueTodayTasks() {
        return totalDueTodayTasks;
    }

    public void setTotalDueTodayTasks(long totalDueTodayTasks) {
        this.totalDueTodayTasks = totalDueTodayTasks;
    }

    public long getTotalDueNext7DaysTasks() {
        return totalDueNext7DaysTasks;
    }

    public void setTotalDueNext7DaysTasks(long totalDueNext7DaysTasks) {
        this.totalDueNext7DaysTasks = totalDueNext7DaysTasks;
    }

    public long getTotalMyOpenTasks() {
        return totalMyOpenTasks;
    }

    public void setTotalMyOpenTasks(long totalMyOpenTasks) {
        this.totalMyOpenTasks = totalMyOpenTasks;
    }

    public long getUrgentTasksCount() {
        return urgentTasksCount;
    }

    public void setUrgentTasksCount(long urgentTasksCount) {
        this.urgentTasksCount = urgentTasksCount;
    }

    public long getHighTasksCount() {
        return highTasksCount;
    }

    public void setHighTasksCount(long highTasksCount) {
        this.highTasksCount = highTasksCount;
    }

    public long getNormalTasksCount() {
        return normalTasksCount;
    }

    public void setNormalTasksCount(long normalTasksCount) {
        this.normalTasksCount = normalTasksCount;
    }

    public long getLowTasksCount() {
        return lowTasksCount;
    }

    public void setLowTasksCount(long lowTasksCount) {
        this.lowTasksCount = lowTasksCount;
    }

    public long getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(long todoCount) {
        this.todoCount = todoCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(long waitingCount) {
        this.waitingCount = waitingCount;
    }

    public long getDoneRecentlyCount() {
        return doneRecentlyCount;
    }

    public void setDoneRecentlyCount(long doneRecentlyCount) {
        this.doneRecentlyCount = doneRecentlyCount;
    }

    public List<PublicationOverviewDTO> getUrgentPublications() {
        return urgentPublications;
    }

    public void setUrgentPublications(List<PublicationOverviewDTO> urgentPublications) {
        this.urgentPublications = urgentPublications;
    }

    public List<TaskOverviewDTO> getUrgentOverdueTasks() {
        return urgentOverdueTasks;
    }

    public void setUrgentOverdueTasks(List<TaskOverviewDTO> urgentOverdueTasks) {
        this.urgentOverdueTasks = urgentOverdueTasks;
    }

    public List<TaskOverviewDTO> getTodayTasks() {
        return todayTasks;
    }

    public void setTodayTasks(List<TaskOverviewDTO> todayTasks) {
        this.todayTasks = todayTasks;
    }
}