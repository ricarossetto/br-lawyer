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
 * Entidade JPA para registro profissional de advogados junto à OAB (Ordem dos Advogados do Brasil).
 * Permite múltiplas inscrições por profissional (Principal, Suplementar, Estagiário, Consultor).
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_lawyer_registrations")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrLawyerRegistration.findAll", query = "SELECT r FROM BrLawyerRegistration r"),
    @NamedQuery(name = "BrLawyerRegistration.findById", query = "SELECT r FROM BrLawyerRegistration r WHERE r.id = :id"),
    @NamedQuery(name = "BrLawyerRegistration.findByContactId", query = "SELECT r FROM BrLawyerRegistration r WHERE r.contactId = :contactId"),
    @NamedQuery(name = "BrLawyerRegistration.findByOabNumberAndUf", query = "SELECT r FROM BrLawyerRegistration r WHERE r.oabNumber = :oabNumber AND r.oabUf = :oabUf")
})
public class BrLawyerRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_PRINCIPAL = "PRINCIPAL";
    public static final String TYPE_SUPLEMENTAR = "SUPLEMENTAR";
    public static final String TYPE_ESTAGIARIO = "ESTAGIARIO";
    public static final String TYPE_CONSULTOR = "CONSULTOR";

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_INATIVO = "INATIVO";
    public static final String STATUS_SUSPENSO = "SUSPENSO";
    public static final String STATUS_CANCELADO = "CANCELADO";

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "contact_id", length = 36)
    private String contactId;

    @Basic(optional = false)
    @Column(name = "oab_number", length = 20)
    private String oabNumber;

    @Basic(optional = false)
    @Column(name = "oab_uf", length = 2)
    private String oabUf;

    @Column(name = "oab_type", length = 20)
    private String oabType;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "issuance_date", length = 10)
    private String issuanceDate;

    @Column(name = "security_code", length = 50)
    private String securityCode;

    @Column(name = "notice", columnDefinition = "TEXT")
    private String notice;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;

    public BrLawyerRegistration() {
    }

    public BrLawyerRegistration(String id) {
        this.id = id;
    }

    public BrLawyerRegistration(String id, String oabNumber, String oabUf) {
        this.id = id;
        this.oabNumber = oabNumber;
        this.oabUf = oabUf;
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
        return "OAB/" + oabUf.toUpperCase() + " " + oabNumber;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BrLawyerRegistration)) {
            return false;
        }
        BrLawyerRegistration other = (BrLawyerRegistration) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrLawyerRegistration[id=" + id + ", oab=" + getFormattedRegistration() + "]";
    }
}
