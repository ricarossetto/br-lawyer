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
 * DTO para filtros de consulta de tarefas com paginação.
 *
 * @author BR-LAWYER Team
 */
public class TaskFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String assignedUser;
    private String processId;
    private String priority;
    private String category;
    private Boolean overdue;
    private Boolean dueToday;
    private Date fromDueDate;
    private Date toDueDate;
    private String searchText;
    private int page = 0;
    private int pageSize = 50;

    public TaskFilterDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }

    public Boolean getDueToday() {
        return dueToday;
    }

    public void setDueToday(Boolean dueToday) {
        this.dueToday = dueToday;
    }

    public Date getFromDueDate() {
        return fromDueDate;
    }

    public void setFromDueDate(Date fromDueDate) {
        this.fromDueDate = fromDueDate;
    }

    public Date getToDueDate() {
        return toDueDate;
    }

    public void setToDueDate(Date toDueDate) {
        this.toDueDate = toDueDate;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}