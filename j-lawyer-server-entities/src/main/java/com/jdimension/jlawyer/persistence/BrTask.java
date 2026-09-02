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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidade JPA para Tarefas JurÃƒÂ­dicas no BR-LAWYER.
 * Suporta ciclo de vida: TODO -> IN_PROGRESS -> WAITING -> DONE / CANCELLED.
 * IntegraÃƒÂ§ÃƒÂ£o direta com prazo fatal/duedate do j-lawyer (calendar_event_id).
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_tasks")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrTask.findAll", query = "SELECT t FROM BrTask t ORDER BY t.dueDate ASC, t.priority DESC, t.createdAt DESC"),
    @NamedQuery(name = "BrTask.findById", query = "SELECT t FROM BrTask t WHERE t.id = :id"),
    @NamedQuery(name = "BrTask.findByStatus", query = "SELECT t FROM BrTask t WHERE t.status = :status ORDER BY t.dueDate ASC"),
    @NamedQuery(name = "BrTask.findByAssignedUser", query = "SELECT t FROM BrTask t WHERE t.assignedUser = :assignedUser ORDER BY t.dueDate ASC"),
    @NamedQuery(name = "BrTask.findByProcessId", query = "SELECT t FROM BrTask t WHERE t.processId = :processId ORDER BY t.dueDate ASC"),
    @NamedQuery(name = "BrTask.findByPublicationId", query = "SELECT t FROM BrTask t WHERE t.publicationId = :publicationId ORDER BY t.createdAt DESC"),
    @NamedQuery(name = "BrTask.findOpenOverdue", query = "SELECT t FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.dueDate < :referenceDate ORDER BY t.dueDate ASC"),
    @NamedQuery(name = "BrTask.findOpenDueToday", query = "SELECT t FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.dueDate >= :startOfDay AND t.dueDate <= :endOfDay ORDER BY t.priority DESC"),
    @NamedQuery(name = "BrTask.findOpenDueNextDays", query = "SELECT t FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.dueDate >= :startDate AND t.dueDate <= :endDate ORDER BY t.dueDate ASC"),
    @NamedQuery(name = "BrTask.countOpenByStatus", query = "SELECT t.status, COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') GROUP BY t.status"),
    @NamedQuery(name = "BrTask.countOpenTotal", query = "SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED')")
})
public class BrTask implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_TODO = "TODO";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_WAITING = "WAITING";
    public static final String STATUS_DONE = "DONE";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_NORMAL = "NORMAL";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";

    public static final String CATEGORY_ANALISE = "ANALISE";
    public static final String CATEGORY_PETICAO = "PETICAO";
    public static final String CATEGORY_RECURSO = "RECURSO";
    public static final String CATEGORY_AUDIENCIA = "AUDIENCIA";
    public static final String CATEGORY_DILIGENCIA = "DILIGENCIA";
    public static final String CATEGORY_ATENDIMENTO = "ATENDIMENTO";
    public static final String CATEGORY_CUMPRIMENTO_PRAZO = "CUMPRIMENTO_PRAZO";
    public static final String CATEGORY_OUTROS = "OUTROS";

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "title", length = 255)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "process_id", length = 250)
    private String processId;

    @Column(name = "publication_id", length = 36)
    private String publicationId;

    @Column(name = "calendar_event_id", length = 250)
    private String calendarEventId;

    @Column(name = "assigned_user", length = 100)
    private String assignedUser;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Basic(optional = false)
    @Column(name = "status", length = 30)
    private String status = STATUS_TODO;

    @Basic(optional = false)
    @Column(name = "priority", length = 20)
    private String priority = PRIORITY_NORMAL;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "due_time", length = 10)
    private String dueTime;

    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    @Column(name = "completed_by", length = 100)
    private String completedBy;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes = 0;

    @Column(name = "actual_minutes")
    private Integer actualMinutes = 0;

    @Column(name = "category", length = 50)
    private String category = CATEGORY_ANALISE;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Basic(optional = false)
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BrTaskComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("itemOrder ASC")
    private List<BrTaskChecklistItem> checklistItems = new ArrayList<>();

    public BrTask() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public BrTask(String id) {
        this.id = id;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Integer getActualMinutes() {
        return actualMinutes;
    }

    public void setActualMinutes(Integer actualMinutes) {
        this.actualMinutes = actualMinutes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<BrTaskComment> getComments() {
        return comments;
    }

    public void setComments(List<BrTaskComment> comments) {
        this.comments = comments;
    }

    public List<BrTaskChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<BrTaskChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BrTask)) {
            return false;
        }
        BrTask other = (BrTask) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrTask[id=" + id + ", title=" + title + ", status=" + status + ", due=" + dueDate + "]";
    }
}
