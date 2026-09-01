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
 * Entidade JPA para Classes Processuais das Tabelas Processuais Unificadas (TPU) do CNJ.
 * Catálogo versionado, dinâmico e importável (sem hardcode).
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_tpu_classes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrTpuClass.findAll", query = "SELECT c FROM BrTpuClass c ORDER BY c.name"),
    @NamedQuery(name = "BrTpuClass.findById", query = "SELECT c FROM BrTpuClass c WHERE c.id = :id"),
    @NamedQuery(name = "BrTpuClass.findByCode", query = "SELECT c FROM BrTpuClass c WHERE c.code = :code"),
    @NamedQuery(name = "BrTpuClass.findByNature", query = "SELECT c FROM BrTpuClass c WHERE c.nature = :nature ORDER BY c.name"),
    @NamedQuery(name = "BrTpuClass.findByVersion", query = "SELECT c FROM BrTpuClass c WHERE c.sourceVersion = :sourceVersion ORDER BY c.name")
})
public class BrTpuClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "code", unique = true)
    private int code;

    @Basic(optional = false)
    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "glossary", columnDefinition = "TEXT")
    private String glossary;

    @Column(name = "nature", length = 50)
    private String nature;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "source_version", length = 50)
    private String sourceVersion;

    @Column(name = "imported_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date importedAt;

    @Column(name = "valid_from")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "valid_to")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;

    @Column(name = "last_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt;

    @Column(name = "checksum", length = 64)
    private String checksum;

    @Basic(optional = false)
    @Column(name = "active")
    private boolean active;

    public BrTpuClass() {
    }

    public BrTpuClass(String id) {
        this.id = id;
    }

    public BrTpuClass(String id, int code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGlossary() {
        return glossary;
    }

    public void setGlossary(String glossary) {
        this.glossary = glossary;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public Date getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Date importedAt) {
        this.importedAt = importedAt;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BrTpuClass)) {
            return false;
        }
        BrTpuClass other = (BrTpuClass) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrTpuClass[code=" + code + ", name=" + name + "]";
    }
}
