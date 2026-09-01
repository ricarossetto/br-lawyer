/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.model;

import java.io.Serializable;

/**
 * Representação de instituição bancária / participante do SPB/PIX do BACEN.
 *
 * @author BR-LAWYER Team
 */
public class BankingInstitutionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ispb;            // ISPB de 8 dígitos (ex: "00000000")
    private String compeCode;       // Código COMPE de 3 dígitos (ex: "001", "237", "341")
    private String shortName;       // Nome reduzido / comercial (ex: "BCO DO BRASIL S.A.")
    private String fullName;        // Razão social completa
    private boolean pixParticipant; // Indicador de participante do PIX
    private String type;            // Tipo de instituição

    public BankingInstitutionResult() {}

    public BankingInstitutionResult(String ispb, String compeCode, String shortName, String fullName, boolean pixParticipant) {
        this.ispb = ispb;
        this.compeCode = compeCode;
        this.shortName = shortName;
        this.fullName = fullName;
        this.pixParticipant = pixParticipant;
    }

    public String getIspb() { return ispb; }
    public void setIspb(String ispb) { this.ispb = ispb; }

    public String getCompeCode() { return compeCode; }
    public void setCompeCode(String compeCode) { this.compeCode = compeCode; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public boolean isPixParticipant() { return pixParticipant; }
    public void setPixParticipant(boolean pixParticipant) { this.pixParticipant = pixParticipant; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDisplayLabel() {
        if (compeCode != null && !compeCode.isEmpty()) {
            return compeCode + " - " + (shortName != null ? shortName : fullName);
        }
        return (shortName != null ? shortName : fullName) + " (ISPB: " + ispb + ")";
    }
}
