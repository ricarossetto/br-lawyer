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
 * DTO consolidado com os metadados do processo judicial brasileiro.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianCaseDetailsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String caseId;
    private String cnjNumber;
    private String cnjNumberClean;
    private String courtCode;
    private String courtName;
    private Integer justiceSegment;
    private String segmentName;
    private String jurisdictionDegree;
    private String courtUnit;
    private String comarca;
    private String judicialSubsection;
    private Integer tpuClassCode;
    private String tpuClassName;
    private String tpuSubjectCodes;
    private String tpuSubjectNames;
    private Boolean secrecyLevel;
    private Date distributionDate;
    private String caseStatusBr;
    private String provenanceSystem;

    public BrazilianCaseDetailsDTO() {
    }

    public BrazilianCaseDetailsDTO(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCnjNumber() {
        return cnjNumber;
    }

    public void setCnjNumber(String cnjNumber) {
        this.cnjNumber = cnjNumber;
        if (cnjNumber != null) {
            this.cnjNumberClean = cnjNumber.replaceAll("[^0-9]", "");
        } else {
            this.cnjNumberClean = null;
        }
    }

    public String getCnjNumberClean() {
        return cnjNumberClean;
    }

    public void setCnjNumberClean(String cnjNumberClean) {
        this.cnjNumberClean = cnjNumberClean;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public Integer getJusticeSegment() {
        return justiceSegment;
    }

    public void setJusticeSegment(Integer justiceSegment) {
        this.justiceSegment = justiceSegment;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getJurisdictionDegree() {
        return jurisdictionDegree;
    }

    public void setJurisdictionDegree(String jurisdictionDegree) {
        this.jurisdictionDegree = jurisdictionDegree;
    }

    public String getCourtUnit() {
        return courtUnit;
    }

    public void setCourtUnit(String courtUnit) {
        this.courtUnit = courtUnit;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca;
    }

    public String getJudicialSubsection() {
        return judicialSubsection;
    }

    public void setJudicialSubsection(String judicialSubsection) {
        this.judicialSubsection = judicialSubsection;
    }

    public Integer getTpuClassCode() {
        return tpuClassCode;
    }

    public void setTpuClassCode(Integer tpuClassCode) {
        this.tpuClassCode = tpuClassCode;
    }

    public String getTpuClassName() {
        return tpuClassName;
    }

    public void setTpuClassName(String tpuClassName) {
        this.tpuClassName = tpuClassName;
    }

    public String getTpuSubjectCodes() {
        return tpuSubjectCodes;
    }

    public void setTpuSubjectCodes(String tpuSubjectCodes) {
        this.tpuSubjectCodes = tpuSubjectCodes;
    }

    public String getTpuSubjectNames() {
        return tpuSubjectNames;
    }

    public void setTpuSubjectNames(String tpuSubjectNames) {
        this.tpuSubjectNames = tpuSubjectNames;
    }

    public Boolean getSecrecyLevel() {
        return secrecyLevel;
    }

    public void setSecrecyLevel(Boolean secrecyLevel) {
        this.secrecyLevel = secrecyLevel;
    }

    public Date getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(Date distributionDate) {
        this.distributionDate = distributionDate;
    }

    public String getCaseStatusBr() {
        return caseStatusBr;
    }

    public void setCaseStatusBr(String caseStatusBr) {
        this.caseStatusBr = caseStatusBr;
    }

    public String getProvenanceSystem() {
        return provenanceSystem;
    }

    public void setProvenanceSystem(String provenanceSystem) {
        this.provenanceSystem = provenanceSystem;
    }
}
