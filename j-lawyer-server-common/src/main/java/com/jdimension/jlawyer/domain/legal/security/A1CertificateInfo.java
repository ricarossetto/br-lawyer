/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.security;

import java.io.Serializable;
import java.util.Date;

/**
 * Metadados higienizados de um Certificado Digital ICP-Brasil A1 (PKCS#12).
 *
 * Guardrail de Segurança:
 * - Contém exclusivamente metadados públicos do certificado.
 * - NUNCA contém chaves privadas, senhas ou bytes confidenciais.
 *
 * @author BR-LAWYER Team
 */
public final class A1CertificateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String alias;
    private final String subjectDn;
    private final String commonName;
    private final String issuerDn;
    private final String serialNumber;
    private final String sha256Fingerprint;
    private final Date notBefore;
    private final Date notAfter;
    private final String documentNumber; // CPF ou CNPJ extraído dos OIDs da ICP-Brasil
    private final boolean isExpired;
    private final boolean isActive;

    public A1CertificateInfo(String alias,
                             String subjectDn,
                             String commonName,
                             String issuerDn,
                             String serialNumber,
                             String sha256Fingerprint,
                             Date notBefore,
                             Date notAfter,
                             String documentNumber) {
        this.alias = alias;
        this.subjectDn = subjectDn;
        this.commonName = commonName;
        this.issuerDn = issuerDn;
        this.serialNumber = serialNumber;
        this.sha256Fingerprint = sha256Fingerprint;
        this.notBefore = notBefore != null ? new Date(notBefore.getTime()) : null;
        this.notAfter = notAfter != null ? new Date(notAfter.getTime()) : null;
        this.documentNumber = documentNumber;

        Date now = new Date();
        this.isExpired = notAfter != null && now.after(notAfter);
        this.isActive = notBefore != null && notAfter != null && now.after(notBefore) && now.before(notAfter);
    }

    public String getAlias() { return alias; }
    public String getSubjectDn() { return subjectDn; }
    public String getCommonName() { return commonName; }
    public String getIssuerDn() { return issuerDn; }
    public String getSerialNumber() { return serialNumber; }
    public String getSha256Fingerprint() { return sha256Fingerprint; }
    public Date getNotBefore() { return notBefore != null ? new Date(notBefore.getTime()) : null; }
    public Date getNotAfter() { return notAfter != null ? new Date(notAfter.getTime()) : null; }
    public String getDocumentNumber() { return documentNumber; }
    public boolean isExpired() { return isExpired; }
    public boolean isActive() { return isActive; }

    @Override
    public String toString() {
        return "A1CertificateInfo{" +
                "commonName='" + commonName + '\'' +
                ", issuerDn='" + issuerDn + '\'' +
                ", notAfter=" + notAfter +
                ", isExpired=" + isExpired +
                ", isActive=" + isActive +
                '}';
    }
}
