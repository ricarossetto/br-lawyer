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
import java.util.Date;

/**
 * Modelo canônico de registro profissional (OAB - Cadastro Nacional dos Advogados).
 *
 * @author BR-LAWYER Team
 */
public class ProfessionalRegistrationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum RegistrationType { ADVOGADO, ESTAGIARIO, SUPLEMENTAR }
    public enum ProfessionalStatus { REGULAR, SUSPENSA, CANCELADA, LICENCIADO, NAO_ENCONTRADO, DESCONHECIDA }

    private String registrationNumber;      // Número da OAB (ex: "123456")
    private String state;                   // UF da seccional (ex: "SP")
    private RegistrationType registrationType = RegistrationType.ADVOGADO;
    private ProfessionalStatus status = ProfessionalStatus.REGULAR;
    private String fullName;                // Nome completo do advogado
    private String subSection;              // Subseção (ex: "Campinas", "Santos")
    private Date registrationDate;
    private RegistryProvenance provenance;

    public ProfessionalRegistrationResult() {
        this.provenance = new RegistryProvenance();
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public RegistrationType getRegistrationType() { return registrationType; }
    public void setRegistrationType(RegistrationType registrationType) { this.registrationType = registrationType; }

    public ProfessionalStatus getStatus() { return status; }
    public void setStatus(ProfessionalStatus status) { this.status = status; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSubSection() { return subSection; }
    public void setSubSection(String subSection) { this.subSection = subSection; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public RegistryProvenance getProvenance() { return provenance; }
    public void setProvenance(RegistryProvenance provenance) { this.provenance = provenance; }

    public String getFormattedOab() {
        return "OAB/" + (state != null ? state : "") + " " + (registrationNumber != null ? registrationNumber : "");
    }
}
