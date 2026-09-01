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

import com.jdimension.jlawyer.domain.legal.model.PublicationEventDTO;

public class RestfulPublicationEventV8 {

    private String id;
    private String publicationId;
    private String taskId;
    private String processId;
    private String eventType;
    private String actor;
    private String details;
    private Long createdAt;

    public RestfulPublicationEventV8() {
    }

    public static RestfulPublicationEventV8 fromDTO(PublicationEventDTO dto) {
        if (dto == null) return null;
        RestfulPublicationEventV8 r = new RestfulPublicationEventV8();
        r.setId(dto.getId());
        r.setPublicationId(dto.getPublicationId());
        r.setTaskId(dto.getTaskId());
        r.setProcessId(dto.getProcessId());
        r.setEventType(dto.getEventType());
        r.setActor(dto.getActor());
        r.setDetails(dto.getDetails());
        r.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().getTime() : null);
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPublicationId() { return publicationId; }
    public void setPublicationId(String publicationId) { this.publicationId = publicationId; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}