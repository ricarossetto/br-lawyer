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
 * Entidade JPA para Publicações e Intimações Judiciais Brasileiras.
 * Suporta ciclo de vida completo: NOVA -> EM_ANALISE -> TRATADA / ARQUIVADA.
 * Mantém distinção entre estado de leitura (UNREAD/READ) e estado de tratamento (NAO_TRATADA/TRATADA/DISPENSADA).
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_publications")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrPublication.findAll", query = "SELECT p FROM BrPublication p ORDER BY p.publicationDate DESC, p.createdAt DESC"),
    @NamedQuery(name = "BrPublication.findById", query = "SELECT p FROM BrPublication p WHERE p.id = :id"),
    @NamedQuery(name = "BrPublication.findByStatus", query = "SELECT p FROM BrPublication p WHERE p.status = :status ORDER BY p.publicationDate DESC"),
    @NamedQuery(name = "BrPublication.findByReadStatus", query = "SELECT p FROM BrPublication p WHERE p.readStatus = :readStatus ORDER BY p.publicationDate DESC"),
    @NamedQuery(name = "BrPublication.findByTreatmentStatus", query = "SELECT p FROM BrPublication p WHERE p.treatmentStatus = :treatmentStatus ORDER BY p.publicationDate DESC"),
    @NamedQuery(name = "BrPublication.findByProcessId", query = "SELECT p FROM BrPublication p WHERE p.processId = :processId ORDER BY p.publicationDate DESC"),
    @NamedQuery(name = "BrPublication.findByCnjNumberClean", query = "SELECT p FROM BrPublication p WHERE p.cnjNumberClean = :cnjNumberClean ORDER BY p.publicationDate DESC"),
    @NamedQuery(name = "BrPublication.findByFingerprint", query = "SELECT p FROM BrPublication p WHERE p.fingerprint = :fingerprint"),
    @NamedQuery(name = "BrPublication.findByExternalIdAndSource", query = "SELECT p FROM BrPublication p WHERE p.externalId = :externalId AND p.source = :source"),
    @NamedQuery(name = "BrPublication.countNewUntreated", query = "SELECT COUNT(p) FROM BrPublication p WHERE p.treatmentStatus = 'NAO_TRATADA' AND p.status != 'ARQUIVADA'"),
    @NamedQuery(name = "BrPublication.countUnread", query = "SELECT COUNT(p) FROM BrPublication p WHERE p.readStatus = 'UNREAD' AND p.status != 'ARQUIVADA'")
})
public class BrPublication implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_NOVA = "NOVA";
    public static final String STATUS_EM_ANALISE = "EM_ANALISE";
    public static final String STATUS_TRATADA = "TRATADA";
    public static final String STATUS_ARQUIVADA = "ARQUIVADA";

    public static final String READ_UNREAD = "UNREAD";
    public static final String READ_READ = "READ";

    public static final String TREATMENT_NAO_TRATADA = "NAO_TRATADA";
    public static final String TREATMENT_TRATADA = "TRATADA";
    public static final String TREATMENT_DISPENSADA = "DISPENSADA";

    public static final String PROVENANCE_AUTO_CNJ = "AUTO_CNJ";
    public static final String PROVENANCE_MANUAL = "MANUAL";
    public static final String PROVENANCE_IMPORT = "IMPORT";
    public static final String PROVENANCE_API = "API";

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "source", length = 100)
    private String source = "MANUAL";

    @Column(name = "source_type", length = 50)
    private String sourceType = "DIARIO_OFICIAL";

    @Column(name = "court_code", length = 20)
    private String courtCode;

    @Column(name = "process_id", length = 250)
    private String processId;

    @Column(name = "cnj_number", length = 25)
    private String cnjNumber;

    @Column(name = "cnj_number_clean", length = 20)
    private String cnjNumberClean;

    @Column(name = "publication_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationDate;

    @Column(name = "availability_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date availabilityDate;

    @Lob
    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "raw_content")
    private String rawContent;

    @Column(name = "publication_type", length = 50)
    private String publicationType = "INTIMACAO";

    @Column(name = "recipient", length = 255)
    private String recipient;

    @Column(name = "lawyer_name", length = 255)
    private String lawyerName;

    @Column(name = "lawyer_oab", length = 30)
    private String lawyerOab;

    @Basic(optional = false)
    @Column(name = "status", length = 30)
    private String status = STATUS_NOVA;

    @Basic(optional = false)
    @Column(name = "read_status", length = 20)
    private String readStatus = READ_UNREAD;

    @Basic(optional = false)
    @Column(name = "treatment_status", length = 30)
    private String treatmentStatus = TREATMENT_NAO_TRATADA;

    @Column(name = "assigned_user", length = 100)
    private String assignedUser;

    @Column(name = "link_provenance", length = 50)
    private String linkProvenance = PROVENANCE_MANUAL;

    @Column(name = "link_confidence")
    private Double linkConfidence = 0.0;

    @Column(name = "suggested_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date suggestedDueDate;

    @Column(name = "suggested_deadline_days")
    private Integer suggestedDeadlineDays = 0;

    @Column(name = "suggestion_source", length = 100)
    private String suggestionSource;

    @Column(name = "suggestion_confidence")
    private Double suggestionConfidence = 0.0;

    @Column(name = "fingerprint", length = 64)
    private String fingerprint;

    @Column(name = "provenance", length = 100)
    private String provenance = "MANUAL";

    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Basic(optional = false)
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "read_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readAt;

    @Column(name = "treated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date treatedAt;

    @Column(name = "treated_by", length = 100)
    private String treatedBy;

    @Column(name = "archived_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedAt;

    @Column(name = "archived_by", length = 100)
    private String archivedBy;

    @Lob
    @Column(name = "notes")
    private String notes;

    public BrPublication() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public BrPublication(String id) {
        this.id = id;
        this.createdAt = new Date();
        this.updatedAt = new Date();
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

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BrPublication)) {
            return false;
        }
        BrPublication other = (BrPublication) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrPublication[id=" + id + ", cnj=" + cnjNumber + ", status=" + status + ", treatment=" + treatmentStatus + "]";
    }
}
