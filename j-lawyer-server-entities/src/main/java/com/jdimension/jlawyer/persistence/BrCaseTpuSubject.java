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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidade JPA para relacionamento normalizado e histórico entre Processos (Casos) e Assuntos TPU/CNJ.
 * Permite múltiplos assuntos por processo, distinção de assunto principal e rastreamento de proveniência.
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_case_tpu_subjects")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrCaseTpuSubject.findAll", query = "SELECT s FROM BrCaseTpuSubject s"),
    @NamedQuery(name = "BrCaseTpuSubject.findById", query = "SELECT s FROM BrCaseTpuSubject s WHERE s.id = :id"),
    @NamedQuery(name = "BrCaseTpuSubject.findByCaseId", query = "SELECT s FROM BrCaseTpuSubject s WHERE s.caseId = :caseId ORDER BY s.primarySubject DESC, s.subjectCode"),
    @NamedQuery(name = "BrCaseTpuSubject.findByCaseIdAndSubjectCode", query = "SELECT s FROM BrCaseTpuSubject s WHERE s.caseId = :caseId AND s.subjectCode = :subjectCode")
})
public class BrCaseTpuSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "case_id", length = 250)
    private String caseId;

    @Basic(optional = false)
    @Column(name = "subject_code")
    private int subjectCode;

    @Column(name = "subject_id", length = 36)
    private String subjectId;

    @Column(name = "subject_name", length = 255)
    private String subjectName;

    @Basic(optional = false)
    @Column(name = "primary_subject")
    private boolean primarySubject;

    @Column(name = "provenance", length = 50)
    private String provenance;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public BrCaseTpuSubject() {
    }

    public BrCaseTpuSubject(String id) {
        this.id = id;
    }

    public BrCaseTpuSubject(String id, String caseId, int subjectCode, boolean primarySubject) {
        this.id = id;
        this.caseId = caseId;
        this.subjectCode = subjectCode;
        this.primarySubject = primarySubject;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public int getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(int subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isPrimarySubject() {
        return primarySubject;
    }

    public void setPrimarySubject(boolean primarySubject) {
        this.primarySubject = primarySubject;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
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
        if (!(object instanceof BrCaseTpuSubject)) {
            return false;
        }
        BrCaseTpuSubject other = (BrCaseTpuSubject) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrCaseTpuSubject[case=" + caseId + ", code=" + subjectCode + ", primary=" + primarySubject + "]";
    }
}
