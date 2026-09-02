/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.discovery;

import com.jdimension.jlawyer.domain.legal.cnj.CnjNumberValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser de Publicações e Intimações do Diário da Justiça Eletrônico Nacional (DJEN).
 *
 * Responsável por extrair de forma robusta e explicável:
 * - Número CNJ (padrão de 20 dígitos)
 * - Inscrições OAB mencionadas no texto ou metadados
 * - Destinatários e partes
 * - Data de disponibilização e Tribunal
 *
 * @author BR-LAWYER Team
 */
public class DjenPublicationParser {

    private static final Pattern CNJ_PATTERN = Pattern.compile("\\b(\\d{7})[-.]?(\\d{2})\\.?(\\d{4})\\.?(\\d{1})\\.?(\\d{2})\\.?(\\d{4})\\b");
    private static final Pattern OAB_PATTERN = Pattern.compile("(?i)\\bOAB[\\s/:]*([A-Z]{2})[\\s/.-]*(\\d{1,8})[\\s/.-]*([A-Z]?)\\b|\\b([A-Z]{2})[\\s/.-]*(\\d{1,8})[\\s/.-]*([A-Z]?)\\s*[/\\-]?\\s*OAB\\b");

    public DjenParsedPublication parse(String publicationId, String rawText, String tribunal, String disponibilizacaoDate, List<String> recipients) {
        if (rawText == null) {
            rawText = "";
        }

        // 1. Extrair Número CNJ
        String extractedCnj = null;
        Matcher cnjMatcher = CNJ_PATTERN.matcher(rawText);
        if (cnjMatcher.find()) {
            String seq = cnjMatcher.group(1);
            String dig = cnjMatcher.group(2);
            String ano = cnjMatcher.group(3);
            String seg = cnjMatcher.group(4);
            String tri = cnjMatcher.group(5);
            String ori = cnjMatcher.group(6);
            String clean = seq + dig + ano + seg + tri + ori;
            if (CnjNumberValidator.isValid(clean)) {
                extractedCnj = CnjNumberValidator.format(clean);
            }
        }

        // 2. Extrair OABs
        List<String> extractedOabs = new ArrayList<>();
        Matcher oabMatcher = OAB_PATTERN.matcher(rawText);
        while (oabMatcher.find()) {
            String uf = oabMatcher.group(1) != null ? oabMatcher.group(1) : oabMatcher.group(4);
            String num = oabMatcher.group(2) != null ? oabMatcher.group(2) : oabMatcher.group(5);
            String suf = oabMatcher.group(3) != null ? oabMatcher.group(3) : (oabMatcher.group(6) != null ? oabMatcher.group(6) : "");
            if (uf != null && num != null) {
                String formattedOab = "OAB/" + uf.toUpperCase() + " " + num + (suf.isEmpty() ? "" : "-" + suf.toUpperCase());
                if (!extractedOabs.contains(formattedOab)) {
                    extractedOabs.add(formattedOab);
                }
            }
        }

        return new DjenParsedPublication(
                publicationId,
                extractedCnj,
                tribunal,
                disponibilizacaoDate,
                rawText,
                recipients != null ? recipients : Collections.emptyList(),
                extractedOabs
        );
    }

    public static final class DjenParsedPublication implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String publicationId;
        private final String cnjNumber;
        private final String tribunal;
        private final String disponibilizacaoDate;
        private final String text;
        private final List<String> recipients;
        private final List<String> extractedOabs;

        public DjenParsedPublication(String publicationId, String cnjNumber, String tribunal,
                                     String disponibilizacaoDate, String text,
                                     List<String> recipients, List<String> extractedOabs) {
            this.publicationId = publicationId;
            this.cnjNumber = cnjNumber;
            this.tribunal = tribunal;
            this.disponibilizacaoDate = disponibilizacaoDate;
            this.text = text;
            this.recipients = Collections.unmodifiableList(new ArrayList<>(recipients));
            this.extractedOabs = Collections.unmodifiableList(new ArrayList<>(extractedOabs));
        }

        public String getPublicationId() { return publicationId; }
        public String getCnjNumber() { return cnjNumber; }
        public String getTribunal() { return tribunal; }
        public String getDisponibilizacaoDate() { return disponibilizacaoDate; }
        public String getText() { return text; }
        public List<String> getRecipients() { return recipients; }
        public List<String> getExtractedOabs() { return extractedOabs; }
    }
}
