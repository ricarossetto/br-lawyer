/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.persistence;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidade JPA para Registro de Auditoria e Eventos do Ciclo de Vida do Workflow Brasileiro.
 * Registra imutavelmente eventos de publicações, triagem, vinculação e tarefas.
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_publication_events")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrPublicationEvent.findAll", query = "SELECT e FROM BrPublicationEvent e ORDER BY e.createdAt DESC"),
    @NamedQuery(name = "BrPublicationEvent.findById", query = "SELECT e FROM BrPublicationEvent e WHERE e.id = :id"),
    @NamedQuery(name = "BrPublicationEvent.findByPublicationId", query = "SELECT e FROM BrPublicationEvent e WHERE e.publicationId = :publicationId ORDER BY e.createdAt ASC"),
    @NamedQuery(name = "BrPublicationEvent.findByTaskId", query = "SELECT e FROM BrPublicationEvent e WHERE e.taskId = :taskId ORDER BY e.createdAt ASC"),
    @NamedQuery(name = "BrPublicationEvent.findByProcessId", query = "SELECT e FROM BrPublicationEvent e WHERE e.processId = :processId ORDER BY e.createdAt DESC")
})
public class BrPublicationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String EVENT_RECEIVED = "RECEIVED";
    public static final String EVENT_READ = "READ";
    public static final String EVENT_UNREAD = "UNREAD";
    public static final String EVENT_LINKED = "LINKED";
    public static final String EVENT_UNLINKED = "UNLINKED";
    public static final String EVENT_ASSIGNED = "ASSIGNED";
    public static final String EVENT_TREATED = "TREATED";
    public static final String EVENT_DISPENSED = "DISPENSED";
    public static final String EVENT_ARCHIVED = "ARCHIVED";
    public static final String EVENT_TASK_CREATED = "TASK_CREATED";
    public static final String EVENT_DEADLINE_CONFIRMED = "DEADLINE_CONFIRMED";

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "publication_id", length = 36)
    private String publicationId;

    @Column(name = "task_id", length = 36)
    private String taskId;

    @Column(name = "process_id", length = 250)
    private String processId;

    @Basic(optional = false)
    @Column(name = "event_type", length = 50)
    private String eventType;

    @Basic(optional = false)
    @Column(name = "actor", length = 100)
    private String actor;

    @Lob
    @Column(name = "details")
    private String details;

    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public BrPublicationEvent() {
        this.createdAt = new Date();
    }

    public BrPublicationEvent(String id) {
        this.id = id;
        this.createdAt = new Date();
    }

    public BrPublicationEvent(String id, String publicationId, String eventType, String actor, String details) {
        this.id = id;
        this.publicationId = publicationId;
        this.eventType = eventType;
        this.actor = actor;
        this.details = details;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BrPublicationEvent)) {
            return false;
        }
        BrPublicationEvent other = (BrPublicationEvent) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrPublicationEvent[type=" + eventType + ", pub=" + publicationId + ", actor=" + actor + "]";
    }
}
