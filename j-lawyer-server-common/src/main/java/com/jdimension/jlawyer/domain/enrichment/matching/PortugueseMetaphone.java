/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.matching;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Codificador fonético canônico para a língua portuguesa (Metaphone-PT).
 * Utilizado para deduplicação, cruzamento de partes e detecção de conflitos
 * de interesse com tolerância a variações ortográficas (ex: Souza/Sousa, Luiz/Luís, Teresa/Theresa).
 *
 * @author BR-LAWYER Team
 */
public final class PortugueseMetaphone {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NON_ALPHA = Pattern.compile("[^A-Z]");

    private PortugueseMetaphone() {
        // Utilitário estático
    }

    /**
     * Codifica uma palavra ou nome em sua representação fonética do português.
     *
     * @param text Nome ou texto
     * @return Chave fonética simplificada
     */
    public static String encode(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // 1. Normalização e desacentuação
        String nfd = Normalizer.normalize(text, Normalizer.Form.NFD);
        String clean = NON_ALPHA.matcher(DIACRITICS.matcher(nfd).replaceAll("").toUpperCase(Locale.ROOT)).replaceAll("");
        if (clean.isEmpty()) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        int len = clean.length();

        for (int i = 0; i < len; i++) {
            char c = clean.charAt(i);
            char next = (i + 1 < len) ? clean.charAt(i + 1) : '\0';
            char prev = (i > 0) ? clean.charAt(i - 1) : '\0';

            switch (c) {
                case 'A': case 'E': case 'I': case 'O': case 'U':
                    if (i == 0) out.append(c);
                    break;
                case 'B': case 'P':
                    out.append('P');
                    break;
                case 'C':
                    if (next == 'H') {
                        out.append('X');
                        i++;
                    } else if (next == 'E' || next == 'I') {
                        out.append('S');
                    } else {
                        out.append('K');
                    }
                    break;
                case 'D': case 'T':
                    out.append('T');
                    break;
                case 'F': case 'V':
                    out.append('F');
                    break;
                case 'G':
                    if (next == 'E' || next == 'I') {
                        out.append('J');
                    } else {
                        out.append('K');
                    }
                    break;
                case 'H':
                    // Silencioso isolado
                    break;
                case 'J':
                    out.append('J');
                    break;
                case 'K': case 'Q':
                    out.append('K');
                    break;
                case 'L':
                    if (next == 'H') {
                        out.append('1'); // Som LH
                        i++;
                    } else if (i == len - 1 || !isVowel(next)) {
                        out.append('U'); // L em fim de sílaba
                    } else {
                        out.append('L');
                    }
                    break;
                case 'M': case 'N':
                    if (next == 'H') {
                        out.append('3'); // Som NH
                        i++;
                    } else {
                        out.append('M');
                    }
                    break;
                case 'R':
                    out.append('R');
                    if (next == 'R') i++;
                    break;
                case 'S':
                    if (next == 'S') {
                        out.append('S');
                        i++;
                    } else if (isVowel(prev) && isVowel(next)) {
                        out.append('Z'); // S intervocálico
                    } else {
                        out.append('S');
                    }
                    break;
                case 'X': case 'Z':
                    out.append('S');
                    break;
                case 'W':
                    out.append(i == 0 ? 'V' : 'U');
                    break;
                case 'Y':
                    if (i == 0) out.append('I');
                    break;
                default:
                    break;
            }
        }

        return out.toString();
    }

    /**
     * Codifica todas as palavras de um nome composto.
     *
     * @param fullName Nome completo (ex: "João da Silva Souza")
     * @return Chave fonética composta (ex: "J SLF SS")
     */
    public static String encodePhrase(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] words = fullName.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            String enc = encode(w);
            if (!enc.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(enc);
            }
        }
        return sb.toString();
    }

    private static boolean isVowel(char c) {
        return c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U';
    }
}
