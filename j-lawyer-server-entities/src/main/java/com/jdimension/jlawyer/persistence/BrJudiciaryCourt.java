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
 * Entidade JPA para catálogo canônico de tribunais do Poder Judiciário Brasileiro.
 * Mapeia os 91 tribunais (Superiores, Federais, Trabalhistas, Eleitorais, Militares e Estaduais).
 *
 * @author BR-LAWYER Team
 */
@Entity
@Table(name = "br_judiciary_courts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BrJudiciaryCourt.findAll", query = "SELECT c FROM BrJudiciaryCourt c ORDER BY c.justiceSegment, c.code"),
    @NamedQuery(name = "BrJudiciaryCourt.findById", query = "SELECT c FROM BrJudiciaryCourt c WHERE c.id = :id"),
    @NamedQuery(name = "BrJudiciaryCourt.findByCode", query = "SELECT c FROM BrJudiciaryCourt c WHERE c.code = :code"),
    @NamedQuery(name = "BrJudiciaryCourt.findBySegment", query = "SELECT c FROM BrJudiciaryCourt c WHERE c.justiceSegment = :justiceSegment ORDER BY c.code"),
    @NamedQuery(name = "BrJudiciaryCourt.findByUf", query = "SELECT c FROM BrJudiciaryCourt c WHERE c.uf = :uf ORDER BY c.code")
})
public class BrJudiciaryCourt implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SEGMENT_STF = 1;
    public static final int SEGMENT_CNJ = 2;
    public static final int SEGMENT_STJ = 3;
    public static final int SEGMENT_FEDERAL = 4;
    public static final int SEGMENT_TRABALHO = 5;
    public static final int SEGMENT_ELEITORAL = 6;
    public static final int SEGMENT_MILITAR_UNIAO = 7;
    public static final int SEGMENT_ESTADUAL = 8;
    public static final int SEGMENT_MILITAR_ESTADUAL = 9;

    @Id
    @Basic(optional = false)
    @Column(name = "id", length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "code", length = 20, unique = true)
    private String code;

    @Basic(optional = false)
    @Column(name = "name", length = 150)
    private String name;

    @Basic(optional = false)
    @Column(name = "justice_segment")
    private int justiceSegment;

    @Column(name = "segment_name", length = 50)
    private String segmentName;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "court_number")
    private int courtNumber;

    @Column(name = "datajud_code", length = 50)
    private String datajudCode;

    @Column(name = "djen_code", length = 50)
    private String djenCode;

    @Column(name = "electronic_portal_url", length = 255)
    private String electronicPortalUrl;

    @Basic(optional = false)
    @Column(name = "active")
    private boolean active;

    public BrJudiciaryCourt() {
    }

    public BrJudiciaryCourt(String id) {
        this.id = id;
    }

    public BrJudiciaryCourt(String id, String code, String name, int justiceSegment) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.justiceSegment = justiceSegment;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJusticeSegment() {
        return justiceSegment;
    }

    public void setJusticeSegment(int justiceSegment) {
        this.justiceSegment = justiceSegment;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(int courtNumber) {
        this.courtNumber = courtNumber;
    }

    public String getDatajudCode() {
        return datajudCode;
    }

    public void setDatajudCode(String datajudCode) {
        this.datajudCode = datajudCode;
    }

    public String getDjenCode() {
        return djenCode;
    }

    public void setDjenCode(String djenCode) {
        this.djenCode = djenCode;
    }

    public String getElectronicPortalUrl() {
        return electronicPortalUrl;
    }

    public void setElectronicPortalUrl(String electronicPortalUrl) {
        this.electronicPortalUrl = electronicPortalUrl;
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
        if (!(object instanceof BrJudiciaryCourt)) {
            return false;
        }
        BrJudiciaryCourt other = (BrJudiciaryCourt) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "BrJudiciaryCourt[code=" + code + ", name=" + name + "]";
    }
}
