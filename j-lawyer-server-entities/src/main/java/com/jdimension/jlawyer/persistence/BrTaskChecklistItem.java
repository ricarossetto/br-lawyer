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
 * Entidade JPA para Itens de Checklist / Subtarefas da Tarefa Jurídica.
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_task_checklist_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrTaskChecklistItem.findAll", query = "SELECT i FROM BrTaskChecklistItem i ORDER BY i.itemOrder ASC"),
    @NamedQuery(name = "BrTaskChecklistItem.findById", query = "SELECT i FROM BrTaskChecklistItem i WHERE i.id = :id"),
    @NamedQuery(name = "BrTaskChecklistItem.findByTaskId", query = "SELECT i FROM BrTaskChecklistItem i WHERE i.task.id = :taskId ORDER BY i.itemOrder ASC")
})
public class BrTaskChecklistItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BrTask task;

    @Basic(optional = false)
    @Column(name = "title", length = 255)
    private String title;

    @Basic(optional = false)
    @Column(name = "done")
    private boolean done = false;

    @Basic(optional = false)
    @Column(name = "item_order")
    private int itemOrder = 0;

    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    @Column(name = "completed_by", length = 100)
    private String completedBy;

    public BrTaskChecklistItem() {
    }

    public BrTaskChecklistItem(String id) {
        this.id = id;
    }

    public BrTaskChecklistItem(String id, BrTask task, String title, int itemOrder) {
        this.id = id;
        this.task = task;
        this.title = title;
        this.itemOrder = itemOrder;
        this.done = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BrTask getTask() {
        return task;
    }

    public void setTask(BrTask task) {
        this.task = task;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
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

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BrTaskChecklistItem)) {
            return false;
        }
        BrTaskChecklistItem other = (BrTaskChecklistItem) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrTaskChecklistItem[id=" + id + ", title=" + title + ", done=" + done + "]";
    }
}
