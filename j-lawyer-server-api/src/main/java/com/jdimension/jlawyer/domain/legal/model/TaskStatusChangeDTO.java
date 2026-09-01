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

/**
 * DTO para requisição de alteração de status de uma tarefa.
 *
 * @author BR-LAWYER Team
 */
public class TaskStatusChangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String newStatus;
    private String user;
    private String comment;
    private Integer actualMinutes;

    public TaskStatusChangeDTO() {
    }

    public TaskStatusChangeDTO(String newStatus, String user) {
        this.newStatus = newStatus;
        this.user = user;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getActualMinutes() {
        return actualMinutes;
    }

    public void setActualMinutes(Integer actualMinutes) {
        this.actualMinutes = actualMinutes;
    }
}