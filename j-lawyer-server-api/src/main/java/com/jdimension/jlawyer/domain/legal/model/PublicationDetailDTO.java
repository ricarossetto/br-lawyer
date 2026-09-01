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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO com todos os detalhes de uma publicação/intimação judicial.
 *
 * @author BR-LAWYER Team
 */
public class PublicationDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String externalId;
    private String source;
    private String sourceType;
    private String courtCode;
    private String processId;
    private String caseFileNumber;
    private String caseName;
    private String cnjNumber;
    private String cnjNumberClean;
    private Date publicationDate;
    private Date availabilityDate;
    private String content;
    private String rawContent;
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
    private String suggestionSource;
    private Double suggestionConfidence;
    private String fingerprint;
    private String provenance;
    private Date createdAt;
    private Date updatedAt;
    private Date readAt;
    private Date treatedAt;
    private String treatedBy;
    private Date archivedAt;
    private String archivedBy;
    private String notes;

    private List<PublicationEventDTO> events = new ArrayList<>();
    private List<TaskOverviewDTO> linkedTasks = new ArrayList<>();

    public PublicationDetailDTO() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
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

    public String getSuggestionSource() {
        return suggestionSource;
    }

    public void setSuggestionSource(String suggestionSource) {
        this.suggestionSource = suggestionSource;
    }

    public Double getSuggestionConfidence() {
        return suggestionConfidence;
    }

    public void setSuggestionConfidence(Double suggestionConfidence) {
        this.suggestionConfidence = suggestionConfidence;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public Date getTreatedAt() {
        return treatedAt;
    }

    public void setTreatedAt(Date treatedAt) {
        this.treatedAt = treatedAt;
    }

    public String getTreatedBy() {
        return treatedBy;
    }

    public void setTreatedBy(String treatedBy) {
        this.treatedBy = treatedBy;
    }

    public Date getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Date archivedAt) {
        this.archivedAt = archivedAt;
    }

    public String getArchivedBy() {
        return archivedBy;
    }

    public void setArchivedBy(String archivedBy) {
        this.archivedBy = archivedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PublicationEventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<PublicationEventDTO> events) {
        this.events = events;
    }

    public List<TaskOverviewDTO> getLinkedTasks() {
        return linkedTasks;
    }

    public void setLinkedTasks(List<TaskOverviewDTO> linkedTasks) {
        this.linkedTasks = linkedTasks;
    }
}