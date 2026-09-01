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

import com.jdimension.jlawyer.domain.legal.model.PublicationOverviewDTO;
import java.util.Date;

public class RestfulPublicationOverviewV8 {

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
    private Long publicationDate;
    private Long availabilityDate;
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
    private Long suggestedDueDate;
    private Integer suggestedDeadlineDays;
    private String snippet;
    private Long createdAt;
    private Long treatedAt;

    public RestfulPublicationOverviewV8() {
    }

    public static RestfulPublicationOverviewV8 fromDTO(PublicationOverviewDTO dto) {
        if (dto == null) return null;
        RestfulPublicationOverviewV8 r = new RestfulPublicationOverviewV8();
        r.setId(dto.getId());
        r.setExternalId(dto.getExternalId());
        r.setSource(dto.getSource());
        r.setSourceType(dto.getSourceType());
        r.setCourtCode(dto.getCourtCode());
        r.setProcessId(dto.getProcessId());
        r.setCaseFileNumber(dto.getCaseFileNumber());
        r.setCaseName(dto.getCaseName());
        r.setCnjNumber(dto.getCnjNumber());
        r.setCnjNumberClean(dto.getCnjNumberClean());
        r.setPublicationDate(dto.getPublicationDate() != null ? dto.getPublicationDate().getTime() : null);
        r.setAvailabilityDate(dto.getAvailabilityDate() != null ? dto.getAvailabilityDate().getTime() : null);
        r.setPublicationType(dto.getPublicationType());
        r.setRecipient(dto.getRecipient());
        r.setLawyerName(dto.getLawyerName());
        r.setLawyerOab(dto.getLawyerOab());
        r.setStatus(dto.getStatus());
        r.setReadStatus(dto.getReadStatus());
        r.setTreatmentStatus(dto.getTreatmentStatus());
        r.setAssignedUser(dto.getAssignedUser());
        r.setLinkProvenance(dto.getLinkProvenance());
        r.setLinkConfidence(dto.getLinkConfidence());
        r.setSuggestedDueDate(dto.getSuggestedDueDate() != null ? dto.getSuggestedDueDate().getTime() : null);
        r.setSuggestedDeadlineDays(dto.getSuggestedDeadlineDays());
        r.setSnippet(dto.getSnippet());
        r.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().getTime() : null);
        r.setTreatedAt(dto.getTreatedAt() != null ? dto.getTreatedAt().getTime() : null);
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getCourtCode() { return courtCode; }
    public void setCourtCode(String courtCode) { this.courtCode = courtCode; }
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    public String getCaseFileNumber() { return caseFileNumber; }
    public void setCaseFileNumber(String caseFileNumber) { this.caseFileNumber = caseFileNumber; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public String getCnjNumber() { return cnjNumber; }
    public void setCnjNumber(String cnjNumber) { this.cnjNumber = cnjNumber; }
    public String getCnjNumberClean() { return cnjNumberClean; }
    public void setCnjNumberClean(String cnjNumberClean) { this.cnjNumberClean = cnjNumberClean; }
    public Long getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Long publicationDate) { this.publicationDate = publicationDate; }
    public Long getAvailabilityDate() { return availabilityDate; }
    public void setAvailabilityDate(Long availabilityDate) { this.availabilityDate = availabilityDate; }
    public String getPublicationType() { return publicationType; }
    public void setPublicationType(String publicationType) { this.publicationType = publicationType; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getLawyerName() { return lawyerName; }
    public void setLawyerName(String lawyerName) { this.lawyerName = lawyerName; }
    public String getLawyerOab() { return lawyerOab; }
    public void setLawyerOab(String lawyerOab) { this.lawyerOab = lawyerOab; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReadStatus() { return readStatus; }
    public void setReadStatus(String readStatus) { this.readStatus = readStatus; }
    public String getTreatmentStatus() { return treatmentStatus; }
    public void setTreatmentStatus(String treatmentStatus) { this.treatmentStatus = treatmentStatus; }
    public String getAssignedUser() { return assignedUser; }
    public void setAssignedUser(String assignedUser) { this.assignedUser = assignedUser; }
    public String getLinkProvenance() { return linkProvenance; }
    public void setLinkProvenance(String linkProvenance) { this.linkProvenance = linkProvenance; }
    public Double getLinkConfidence() { return linkConfidence; }
    public void setLinkConfidence(Double linkConfidence) { this.linkConfidence = linkConfidence; }
    public Long getSuggestedDueDate() { return suggestedDueDate; }
    public void setSuggestedDueDate(Long suggestedDueDate) { this.suggestedDueDate = suggestedDueDate; }
    public Integer getSuggestedDeadlineDays() { return suggestedDeadlineDays; }
    public void setSuggestedDeadlineDays(Integer suggestedDeadlineDays) { this.suggestedDeadlineDays = suggestedDeadlineDays; }
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getTreatedAt() { return treatedAt; }
    public void setTreatedAt(Long treatedAt) { this.treatedAt = treatedAt; }
}