/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.security;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * Validador e Extrator em Memória de Certificados Digitais ICP-Brasil A1 (PKCS#12 / .pfx / .p12).
 *
 * Princípios de Segurança (ATRIUM / BR-LAWYER):
 * 1. O processamento é realizado estritamente em memória local volátil.
 * 2. As senhas em char[] são zeradas imediatamente após o carregamento (wipe).
 * 3. Chaves privadas e senhas NUNCA são salvas em log, auditoria ou banco de dados.
 *
 * @author BR-LAWYER Team
 */
public class A1CertificateValidator {

    /**
     * Inspeciona e valida um arquivo PKCS#12 em memória sem persistir a chave privada ou a senha.
     *
     * @param pfxBytes Bytes do arquivo .pfx / .p12
     * @param password Senha do certificado
     * @return Informações públicas e sanitizadas do certificado
     * @throws Exception se a senha estiver incorreta ou o formato for inválido
     */
    public A1CertificateInfo validateAndExtractInfo(byte[] pfxBytes, char[] password) throws Exception {
        if (pfxBytes == null || pfxBytes.length == 0) {
            throw new IllegalArgumentException("Bytes do certificado não podem ser nulos ou vazios");
        }
        if (password == null) {
            throw new IllegalArgumentException("Senha do certificado não pode ser nula");
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (ByteArrayInputStream bais = new ByteArrayInputStream(pfxBytes)) {
                keyStore.load(bais, password);
            }

            Enumeration<String> aliases = keyStore.aliases();
            String targetAlias = null;
            X509Certificate x509 = null;

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    Certificate cert = keyStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        targetAlias = alias;
                        x509 = (X509Certificate) cert;
                        break;
                    }
                }
            }

            if (x509 == null) {
                throw new IllegalStateException("Nenhum certificado X509 com chave privada foi localizado no arquivo PKCS#12");
            }

            // Extrair metadados públicos
            String subjectDn = x509.getSubjectDN().getName();
            String commonName = extractCommonName(subjectDn);
            String issuerDn = x509.getIssuerDN().getName();
            String serialNumber = x509.getSerialNumber().toString(16).toUpperCase();
            String fingerprint = calculateSha256Fingerprint(x509.getEncoded());
            String documentNumber = extractIcpBrasilDocument(x509, subjectDn);

            return new A1CertificateInfo(
                    targetAlias,
                    subjectDn,
                    commonName,
                    issuerDn,
                    serialNumber,
                    fingerprint,
                    x509.getNotBefore(),
                    x509.getNotAfter(),
                    documentNumber
            );
        } finally {
            // Memory Wipe: Limpeza imediata da senha da memória
            Arrays.fill(password, '\0');
        }
    }

    private String extractCommonName(String subjectDn) {
        if (subjectDn == null) return "";
        for (String part : subjectDn.split(",")) {
            String trimmed = part.trim();
            if (trimmed.startsWith("CN=") || trimmed.startsWith("cn=")) {
                return trimmed.substring(3).trim();
            }
        }
        return subjectDn;
    }

    private String calculateSha256Fingerprint(byte[] certEncoded) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(certEncoded);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private String extractIcpBrasilDocument(X509Certificate cert, String subjectDn) {
        // Tenta extrair CPF ou CNPJ do Common Name (padrão ICP-Brasil: NOME:CPF ou EMPRESA:CNPJ)
        if (subjectDn != null) {
            String[] parts = subjectDn.split(":");
            if (parts.length >= 2) {
                String potentialDoc = parts[parts.length - 1].replaceAll("[^0-9]", "");
                if (potentialDoc.length() == 11 || potentialDoc.length() == 14) {
                    return potentialDoc;
                }
            }
        }
        return null;
    }
}
