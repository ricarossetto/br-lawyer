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
 * Capacidades e suportes providos por um adaptador/provedor de enriquecimento.
 *
 * @author BR-LAWYER Team
 */
public class ProviderCapabilities implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean supportsCpf;
    private boolean supportsCnpj;
    private boolean supportsQsa;
    private boolean supportsAddress;
    private boolean supportsCnae;
    private boolean supportsCorporateStatus;
    private boolean supportsProfessionalRegistration;
    private boolean supportsBanking;
    private boolean requiresCredentials;
    private boolean selfHostable;
    private boolean officialGovSource;

    public ProviderCapabilities() {}

    public boolean isSupportsCpf() { return supportsCpf; }
    public void setSupportsCpf(boolean supportsCpf) { this.supportsCpf = supportsCpf; }

    public boolean isSupportsCnpj() { return supportsCnpj; }
    public void setSupportsCnpj(boolean supportsCnpj) { this.supportsCnpj = supportsCnpj; }

    public boolean isSupportsQsa() { return supportsQsa; }
    public void setSupportsQsa(boolean supportsQsa) { this.supportsQsa = supportsQsa; }

    public boolean isSupportsAddress() { return supportsAddress; }
    public void setSupportsAddress(boolean supportsAddress) { this.supportsAddress = supportsAddress; }

    public boolean isSupportsCnae() { return supportsCnae; }
    public void setSupportsCnae(boolean supportsCnae) { this.supportsCnae = supportsCnae; }

    public boolean isSupportsCorporateStatus() { return supportsCorporateStatus; }
    public void setSupportsCorporateStatus(boolean supportsCorporateStatus) { this.supportsCorporateStatus = supportsCorporateStatus; }

    public boolean isSupportsProfessionalRegistration() { return supportsProfessionalRegistration; }
    public void setSupportsProfessionalRegistration(boolean supportsProfessionalRegistration) { this.supportsProfessionalRegistration = supportsProfessionalRegistration; }

    public boolean isSupportsBanking() { return supportsBanking; }
    public void setSupportsBanking(boolean supportsBanking) { this.supportsBanking = supportsBanking; }

    public boolean isRequiresCredentials() { return requiresCredentials; }
    public void setRequiresCredentials(boolean requiresCredentials) { this.requiresCredentials = requiresCredentials; }

    public boolean isSelfHostable() { return selfHostable; }
    public void setSelfHostable(boolean selfHostable) { this.selfHostable = selfHostable; }

    public boolean isOfficialGovSource() { return officialGovSource; }
    public void setOfficialGovSource(boolean officialGovSource) { this.officialGovSource = officialGovSource; }
}
