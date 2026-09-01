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
 * Representação canônica de integrante do Quadro de Sócios e Administradores (QSA).
 *
 * @author BR-LAWYER Team
 */
public class CompanyMemberResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MemberType { PESSOA_FISICA, PESSOA_JURIDICA, ESTRANGEIRO }

    private String name;
    private String identifier;          // CPF ou CNPJ mascarado (***123456**) ou completo se autorizado
    private MemberType memberType = MemberType.PESSOA_FISICA;
    private String qualificationCode;   // Ex: "49" (Sócio-Administrador), "05" (Administrador), "10" (Diretor)
    private String qualificationDescription; // Descrição textual da qualificação
    private String ageGroup;            // Faixa etária (ex: "ENTRE_41_A_50")
    private Date entryDate;             // Data de entrada na sociedade
    private Double capitalPercentage;   // % de participação societária
    private String legalRepresentativeName;
    private String legalRepresentativeIdentifier;
    private String legalRepresentativeQualification;
    private String country;
    private boolean selectedForImport = true; // Flag auxiliar de seleção na UI

    public CompanyMemberResult() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public MemberType getMemberType() { return memberType; }
    public void setMemberType(MemberType memberType) { this.memberType = memberType; }

    public String getQualificationCode() { return qualificationCode; }
    public void setQualificationCode(String qualificationCode) { this.qualificationCode = qualificationCode; }

    public String getQualificationDescription() { return qualificationDescription; }
    public void setQualificationDescription(String qualificationDescription) { this.qualificationDescription = qualificationDescription; }

    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }

    public Date getEntryDate() { return entryDate; }
    public void setEntryDate(Date entryDate) { this.entryDate = entryDate; }

    public Double getCapitalPercentage() { return capitalPercentage; }
    public void setCapitalPercentage(Double capitalPercentage) { this.capitalPercentage = capitalPercentage; }

    public String getLegalRepresentativeName() { return legalRepresentativeName; }
    public void setLegalRepresentativeName(String legalRepresentativeName) { this.legalRepresentativeName = legalRepresentativeName; }

    public String getLegalRepresentativeIdentifier() { return legalRepresentativeIdentifier; }
    public void setLegalRepresentativeIdentifier(String legalRepresentativeIdentifier) { this.legalRepresentativeIdentifier = legalRepresentativeIdentifier; }

    public String getLegalRepresentativeQualification() { return legalRepresentativeQualification; }
    public void setLegalRepresentativeQualification(String legalRepresentativeQualification) { this.legalRepresentativeQualification = legalRepresentativeQualification; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public boolean isSelectedForImport() { return selectedForImport; }
    public void setSelectedForImport(boolean selectedForImport) { this.selectedForImport = selectedForImport; }
}
