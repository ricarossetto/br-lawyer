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

/**
 * DTO para representação de tribunais do Poder Judiciário Brasileiro.
 *
 * @author BR-LAWYER Team
 */
public class JudiciaryCourtDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private int justiceSegment;
    private String segmentName;
    private String uf;
    private int courtNumber;
    private String datajudCode;
    private String djenCode;
    private String electronicPortalUrl;
    private String courtType;
    private boolean active;

    public JudiciaryCourtDTO() {
    }

    public JudiciaryCourtDTO(String code, String name, int justiceSegment, String uf) {
        this.code = code;
        this.name = name;
        this.justiceSegment = justiceSegment;
        this.uf = uf;
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

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
