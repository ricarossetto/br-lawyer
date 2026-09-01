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
 * Entidade JPA para Comentários em Tarefas Jurídicas.
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_task_comments")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrTaskComment.findAll", query = "SELECT c FROM BrTaskComment c ORDER BY c.createdAt ASC"),
    @NamedQuery(name = "BrTaskComment.findById", query = "SELECT c FROM BrTaskComment c WHERE c.id = :id"),
    @NamedQuery(name = "BrTaskComment.findByTaskId", query = "SELECT c FROM BrTaskComment c WHERE c.task.id = :taskId ORDER BY c.createdAt ASC")
})
public class BrTaskComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BrTask task;

    @Basic(optional = false)
    @Column(name = "user_name", length = 100)
    private String userName;

    @Lob
    @Basic(optional = false)
    @Column(name = "comment_text")
    private String commentText;

    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public BrTaskComment() {
        this.createdAt = new Date();
    }

    public BrTaskComment(String id) {
        this.id = id;
        this.createdAt = new Date();
    }

    public BrTaskComment(String id, BrTask task, String userName, String commentText) {
        this.id = id;
        this.task = task;
        this.userName = userName;
        this.commentText = commentText;
        this.createdAt = new Date();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
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
        if (!(object instanceof BrTaskComment)) {
            return false;
        }
        BrTaskComment other = (BrTaskComment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrTaskComment[id=" + id + ", user=" + userName + "]";
    }
}
