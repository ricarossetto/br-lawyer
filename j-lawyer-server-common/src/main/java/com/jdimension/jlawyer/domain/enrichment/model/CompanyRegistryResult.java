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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Modelo canônico de dados cadastrais empresariais brasileiros (Pessoa Jurídica / CNPJ).
 *
 * @author BR-LAWYER Team
 */
public class CompanyRegistryResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum CorporateStatus { ATIVA, BAIXADA, INAPTA, SUSPENSA, NULA, DESCONHECIDA }
    public enum EstablishmentType { MATRIZ, FILIAL }

    private String cnpj;                        // CNPJ formatado ou alfanumérico
    private String cleanCnpj;                   // Apenas caracteres alfanuméricos
    private String legalName;                   // Razão Social
    private String tradeName;                   // Nome Fantasia
    private CorporateStatus status = CorporateStatus.ATIVA;
    private String statusDescription;           // Descrição da situação
    private Date statusDate;                    // Data da situação cadastral
    private String statusReason;                // Motivo da situação cadastral
    private Date openingDate;                   // Data de abertura/início de atividade
    private EstablishmentType establishmentType = EstablishmentType.MATRIZ;
    private String legalNatureCode;             // Código da natureza jurídica (ex: "2062", "2321")
    private String legalNatureDescription;      // Descrição (ex: "Sociedade Empresária Limitada")
    private String companySize;                 // Porte (ME, EPP, DEMAIS)
    private BigDecimal capitalSocial;           // Capital social
    private boolean simplesOptant;              // Optante pelo Simples Nacional
    private Date simplesOptionDate;
    private Date simplesExclusionDate;
    private boolean meiopting;                  // Optante pelo SIMEI / MEI
    private String mainCnaeCode;                // CNAE fiscal principal (ex: "6911701")
    private String mainCnaeDescription;         // Descrição do CNAE principal (ex: "Serviços advocatícios")
    private List<CnaeEntry> secondaryCnaes = new ArrayList<>();
    private AddressResult address;              // Endereço completo com código IBGE
    private List<String> phones = new ArrayList<>();
    private List<String> emails = new ArrayList<>();
    private String specialStatus;               // Situação especial perante o fisco
    private List<CompanyMemberResult> members = new ArrayList<>(); // Quadro Societário (QSA)
    private RegistryProvenance provenance;

    public static class CnaeEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private String code;
        private String description;

        public CnaeEntry() {}
        public CnaeEntry(String code, String description) {
            this.code = code;
            this.description = description;
        }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public CompanyRegistryResult() {
        this.address = new AddressResult();
        this.provenance = new RegistryProvenance();
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getCleanCnpj() { return cleanCnpj; }
    public void setCleanCnpj(String cleanCnpj) { this.cleanCnpj = cleanCnpj; }

    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }

    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }

    public CorporateStatus getStatus() { return status; }
    public void setStatus(CorporateStatus status) { this.status = status; }

    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }

    public Date getStatusDate() { return statusDate; }
    public void setStatusDate(Date statusDate) { this.statusDate = statusDate; }

    public String getStatusReason() { return statusReason; }
    public void setStatusReason(String statusReason) { this.statusReason = statusReason; }

    public Date getOpeningDate() { return openingDate; }
    public void setOpeningDate(Date openingDate) { this.openingDate = openingDate; }

    public EstablishmentType getEstablishmentType() { return establishmentType; }
    public void setEstablishmentType(EstablishmentType establishmentType) { this.establishmentType = establishmentType; }

    public String getLegalNatureCode() { return legalNatureCode; }
    public void setLegalNatureCode(String legalNatureCode) { this.legalNatureCode = legalNatureCode; }

    public String getLegalNatureDescription() { return legalNatureDescription; }
    public void setLegalNatureDescription(String legalNatureDescription) { this.legalNatureDescription = legalNatureDescription; }

    public String getCompanySize() { return companySize; }
    public void setCompanySize(String companySize) { this.companySize = companySize; }

    public BigDecimal getCapitalSocial() { return capitalSocial; }
    public void setCapitalSocial(BigDecimal capitalSocial) { this.capitalSocial = capitalSocial; }

    public boolean isSimplesOptant() { return simplesOptant; }
    public void setSimplesOptant(boolean simplesOptant) { this.simplesOptant = simplesOptant; }

    public Date getSimplesOptionDate() { return simplesOptionDate; }
    public void setSimplesOptionDate(Date simplesOptionDate) { this.simplesOptionDate = simplesOptionDate; }

    public Date getSimplesExclusionDate() { return simplesExclusionDate; }
    public void setSimplesExclusionDate(Date simplesExclusionDate) { this.simplesExclusionDate = simplesExclusionDate; }

    public boolean isMeiopting() { return meiopting; }
    public void setMeiopting(boolean meiopting) { this.meiopting = meiopting; }

    public String getMainCnaeCode() { return mainCnaeCode; }
    public void setMainCnaeCode(String mainCnaeCode) { this.mainCnaeCode = mainCnaeCode; }

    public String getMainCnaeDescription() { return mainCnaeDescription; }
    public void setMainCnaeDescription(String mainCnaeDescription) { this.mainCnaeDescription = mainCnaeDescription; }

    public List<CnaeEntry> getSecondaryCnaes() { return secondaryCnaes; }
    public void setSecondaryCnaes(List<CnaeEntry> secondaryCnaes) { this.secondaryCnaes = secondaryCnaes; }

    public AddressResult getAddress() { return address; }
    public void setAddress(AddressResult address) { this.address = address; }

    public List<String> getPhones() { return phones; }
    public void setPhones(List<String> phones) { this.phones = phones; }

    public List<String> getEmails() { return emails; }
    public void setEmails(List<String> emails) { this.emails = emails; }

    public String getSpecialStatus() { return specialStatus; }
    public void setSpecialStatus(String specialStatus) { this.specialStatus = specialStatus; }

    public List<CompanyMemberResult> getMembers() { return members; }
    public void setMembers(List<CompanyMemberResult> members) { this.members = members; }

    public RegistryProvenance getProvenance() { return provenance; }
    public void setProvenance(RegistryProvenance provenance) { this.provenance = provenance; }
}
