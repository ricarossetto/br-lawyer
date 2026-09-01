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
 * DTO para solicitação de vinculação de uma publicação a um processo existente.
 *
 * @author BR-LAWYER Team
 */
public class PublicationLinkRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String processId;
    private String provenance = "MANUAL"; // MANUAL, AUTO_CNJ, IMPORT, API
    private Double confidence = 1.0;
    private String user;

    public PublicationLinkRequestDTO() {
    }

    public PublicationLinkRequestDTO(String processId, String provenance, String user) {
        this.processId = processId;
        this.provenance = provenance;
        this.user = user;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}