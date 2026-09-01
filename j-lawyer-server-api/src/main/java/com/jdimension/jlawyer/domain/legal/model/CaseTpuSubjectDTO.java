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
 * DTO para relacionamento normalizado entre Processo (Caso) e Assunto TPU/CNJ.
 *
 * @author BR-LAWYER Team
 */
public class CaseTpuSubjectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String caseId;
    private int subjectCode;
    private String subjectId;
    private String subjectName;
    private boolean primarySubject;
    private String provenance;
    private Date createdAt;

    public CaseTpuSubjectDTO() {
    }

    public CaseTpuSubjectDTO(String caseId, int subjectCode, String subjectName, boolean primarySubject) {
        this.caseId = caseId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.primarySubject = primarySubject;
        this.provenance = "MANUAL";
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
    public String toString() {
        return subjectCode + " - " + subjectName + (primarySubject ? " (Principal)" : "");
    }
}
