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
 * DTO resumido para listagem de publicações judiciais em tabelas e inboxes.
 *
 * @author BR-LAWYER Team
 */
public class PublicationOverviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String externalId;
    private String source;
    private String sourceType;
    private String courtCode;
    private String processId;
    private String caseFileNumber; // Número da pasta/dossiê no j-lawyer (ex: 2026/001)
    private String caseName;
    private String cnjNumber;
    private String cnjNumberClean;
    private Date publicationDate;
    private Date availabilityDate;
    private String publicationType;
    private String recipient;
    private String lawyerName;
    private String lawyerOab;
    private String status;
    private String readStatus;
    private String treatmentStatus;
    private String assignedUser;
    private String linkProvenance;
    private Double linkConfidence;
    private Date suggestedDueDate;
    private Integer suggestedDeadlineDays;
    private String snippet; // Primeiros 200 caracteres do teor
    private Date createdAt;
    private Date treatedAt;

    public PublicationOverviewDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getCaseFileNumber() {
        return caseFileNumber;
    }

    public void setCaseFileNumber(String caseFileNumber) {
        this.caseFileNumber = caseFileNumber;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCnjNumber() {
        return cnjNumber;
    }

    public void setCnjNumber(String cnjNumber) {
        this.cnjNumber = cnjNumber;
    }

    public String getCnjNumberClean() {
        return cnjNumberClean;
    }

    public void setCnjNumberClean(String cnjNumberClean) {
        this.cnjNumberClean = cnjNumberClean;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(Date availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public String getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getLawyerName() {
        return lawyerName;
    }

    public void setLawyerName(String lawyerName) {
        this.lawyerName = lawyerName;
    }

    public String getLawyerOab() {
        return lawyerOab;
    }

    public void setLawyerOab(String lawyerOab) {
        this.lawyerOab = lawyerOab;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getTreatmentStatus() {
        return treatmentStatus;
    }

    public void setTreatmentStatus(String treatmentStatus) {
        this.treatmentStatus = treatmentStatus;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getLinkProvenance() {
        return linkProvenance;
    }

    public void setLinkProvenance(String linkProvenance) {
        this.linkProvenance = linkProvenance;
    }

    public Double getLinkConfidence() {
        return linkConfidence;
    }

    public void setLinkConfidence(Double linkConfidence) {
        this.linkConfidence = linkConfidence;
    }

    public Date getSuggestedDueDate() {
        return suggestedDueDate;
    }

    public void setSuggestedDueDate(Date suggestedDueDate) {
        this.suggestedDueDate = suggestedDueDate;
    }

    public Integer getSuggestedDeadlineDays() {
        return suggestedDeadlineDays;
    }

    public void setSuggestedDeadlineDays(Integer suggestedDeadlineDays) {
        this.suggestedDeadlineDays = suggestedDeadlineDays;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getTreatedAt() {
        return treatedAt;
    }

    public void setTreatedAt(Date treatedAt) {
        this.treatedAt = treatedAt;
    }
}