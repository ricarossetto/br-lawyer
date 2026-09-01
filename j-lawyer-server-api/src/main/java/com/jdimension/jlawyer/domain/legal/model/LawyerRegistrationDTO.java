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
 * DTO para transferência e persistência de inscrições OAB de profissionais jurídicos.
 *
 * @author BR-LAWYER Team
 */
public class LawyerRegistrationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String contactId;
    private String oabNumber;
    private String oabUf;
    private String oabType;
    private String status;
    private String issuanceDate;
    private String securityCode;
    private String notice;
    private Date creationDate;
    private Date modificationDate;

    public LawyerRegistrationDTO() {
    }

    public LawyerRegistrationDTO(String oabNumber, String oabUf, String oabType) {
        this.oabNumber = oabNumber;
        this.oabUf = oabUf;
        this.oabType = oabType;
        this.status = "ATIVO";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getOabNumber() {
        return oabNumber;
    }

    public void setOabNumber(String oabNumber) {
        this.oabNumber = oabNumber;
    }

    public String getOabUf() {
        return oabUf;
    }

    public void setOabUf(String oabUf) {
        this.oabUf = oabUf;
    }

    public String getOabType() {
        return oabType;
    }

    public void setOabType(String oabType) {
        this.oabType = oabType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getFormattedRegistration() {
        if (oabNumber == null || oabUf == null) {
            return "";
        }
        return "OAB/" + oabUf.toUpperCase() + " " + oabNumber + (oabType != null ? " (" + oabType + ")" : "");
    }

    @Override
    public String toString() {
        return getFormattedRegistration();
    }
}
