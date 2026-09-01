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
 * Modelo canônico de dados cadastrais de Pessoa Física brasileira (CPF).
 *
 * @author BR-LAWYER Team
 */
public class PersonRegistryResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum PersonStatus { REGULAR, PENDENTE_REGULARIZACAO, SUSPENSA, CANCELADA_MULTIPLICIDADE, TITULAR_FALECIDO, NULA, DESCONHECIDA }

    private String cpf;                 // CPF formatado (000.000.000-00)
    private String cleanCpf;            // Apenas 11 dígitos
    private String fullName;            // Nome civil completo
    private String socialName;          // Nome social oficial (Decreto 8.727/2016)
    private Date birthDate;             // Data de nascimento
    private PersonStatus status = PersonStatus.REGULAR;
    private String statusDescription;
    private Date statusDate;
    private String motherName;
    private String nationality;
    private String gender;
    private boolean residentAbroad;
    private RegistryProvenance provenance;

    public PersonRegistryResult() {
        this.provenance = new RegistryProvenance();
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCleanCpf() { return cleanCpf; }
    public void setCleanCpf(String cleanCpf) { this.cleanCpf = cleanCpf; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSocialName() { return socialName; }
    public void setSocialName(String socialName) { this.socialName = socialName; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public PersonStatus getStatus() { return status; }
    public void setStatus(PersonStatus status) { this.status = status; }

    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }

    public Date getStatusDate() { return statusDate; }
    public void setStatusDate(Date statusDate) { this.statusDate = statusDate; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public boolean isResidentAbroad() { return residentAbroad; }
    public void setResidentAbroad(boolean residentAbroad) { this.residentAbroad = residentAbroad; }

    public RegistryProvenance getProvenance() { return provenance; }
    public void setProvenance(RegistryProvenance provenance) { this.provenance = provenance; }
}
