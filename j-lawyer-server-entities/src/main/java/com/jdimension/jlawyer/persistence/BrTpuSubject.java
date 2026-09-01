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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidade JPA para Assuntos Processuais das Tabelas Processuais Unificadas (TPU) do CNJ.
 * Permite múltiplos assuntos por processo com hierarquia pai/filho.
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_tpu_subjects")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrTpuSubject.findAll", query = "SELECT s FROM BrTpuSubject s ORDER BY s.name"),
    @NamedQuery(name = "BrTpuSubject.findById", query = "SELECT s FROM BrTpuSubject s WHERE s.id = :id"),
    @NamedQuery(name = "BrTpuSubject.findByCode", query = "SELECT s FROM BrTpuSubject s WHERE s.code = :code"),
    @NamedQuery(name = "BrTpuSubject.findByParentCode", query = "SELECT s FROM BrTpuSubject s WHERE s.parentCode = :parentCode ORDER BY s.name")
})
public class BrTpuSubject implements Serializable {

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

    @Column(name = "parent_code")
    private Integer parentCode;

    @Column(name = "glossary", columnDefinition = "TEXT")
    private String glossary;

    @Basic(optional = false)
    @Column(name = "active")
    private boolean active;

    public BrTpuSubject() {
    }

    public BrTpuSubject(String id) {
        this.id = id;
    }

    public BrTpuSubject(String id, int code, String name) {
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

    public Integer getParentCode() {
        return parentCode;
    }

    public void setParentCode(Integer parentCode) {
        this.parentCode = parentCode;
    }

    public String getGlossary() {
        return glossary;
    }

    public void setGlossary(String glossary) {
        this.glossary = glossary;
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
        if (!(object instanceof BrTpuSubject)) {
            return false;
        }
        BrTpuSubject other = (BrTpuSubject) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrTpuSubject[code=" + code + ", name=" + name + "]";
    }
}
