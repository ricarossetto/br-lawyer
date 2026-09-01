/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.cnj;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validador e utilitário de cálculo da Numeração Processual Única (NPU) do CNJ.
 * Implementa rigorosamente o algoritmo ISO 7064 Módulo 97 Base 10 da Resolução CNJ nº 65/2008.
 *
 * @author BR-LAWYER Team
 */
public final class CnjNumberValidator {

    private static final Pattern CNJ_FORMATTED_PATTERN = Pattern.compile("^(\\d{7})-(\\d{2})\\.(\\d{4})\\.(\\d)\\.(\\d{2})\\.(\\d{4})$");
    private static final Pattern CNJ_DIGITS_ONLY_PATTERN = Pattern.compile("^(\\d{7})(\\d{2})(\\d{4})(\\d)(\\d{2})(\\d{4})$");
    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");
    private static final BigInteger NINETY_SEVEN = BigInteger.valueOf(97);

    private CnjNumberValidator() {
        // Utilitário estático
    }

    /**
     * Valida se uma string representa um número de processo CNJ válido.
     * Aceita formatos com ou sem máscara:
     * - "0001234-56.2023.8.26.0100"
     * - "00012345620238260100"
     *
     * @param cnjString String contendo o número do processo
     * @return true se o formato e o dígito verificador forem matematicamente válidos
     */
    public static boolean isValid(String cnjString) {
        if (cnjString == null) {
            return false;
        }
        String clean = NON_DIGITS.matcher(cnjString.trim()).replaceAll("");
        if (clean.length() != 20) {
            return false;
        }

        try {
            String nnnnnnn = clean.substring(0, 7);
            String dd = clean.substring(7, 9);
            String aaaa = clean.substring(9, 13);
            String j = clean.substring(13, 14);
            String tr = clean.substring(14, 16);
            String oooo = clean.substring(16, 20);

            int jVal = Integer.parseInt(j);
            if (jVal < 1 || jVal > 9) {
                return false;
            }

            int expectedDv = calculateCheckDigit(nnnnnnn, Integer.parseInt(aaaa), jVal, Integer.parseInt(tr), oooo);
            int actualDv = Integer.parseInt(dd);

            return expectedDv == actualDv;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calcula o dígito verificador DD (2 dígitos) para os componentes processuais.
     * Fórmula oficial:
     * R1 = NNNNNNN % 97
     * R2 = (R1 * 10000 + AAAA) % 97
     * R3 = (R2 * 1000 + J * 100 + TR) % 97
     * R4 = (R3 * 1000000 + OOOO * 100) % 97
     * DD = 98 - (R4 % 97)
     * Se DD == 98, DD = 01 (ajustado para módulo 97)
     *
     * @param sequential 7 dígitos sequenciais (NNNNNNN)
     * @param year Ano com 4 dígitos (AAAA)
     * @param justice Segmento de Justiça (1 a 9)
     * @param court Tribunal (TR, 0 a 99)
     * @param origin Unidade de origem (OOOO, 4 dígitos)
     * @return Inteiro entre 1 e 97 com o dígito verificador
     */
    public static int calculateCheckDigit(String sequential, int year, int justice, int court, String origin) {
        long n7 = Long.parseLong(sequential);
        long o4 = Long.parseLong(origin);

        long r1 = n7 % 97;
        long r2 = (r1 * 10000L + year) % 97;
        long r3 = (r2 * 1000L + justice * 100L + court) % 97;
        long r4 = (r3 * 1000000L + o4 * 100L) % 97;

        int dv = (int) (98 - r4);
        if (dv == 98) {
            dv = 1;
        }
        return dv;
    }

    /**
     * Faz o parse de uma string CNJ e retorna o objeto estruturado {@link CnjNumber}.
     *
     * @param cnjString String com ou sem máscara
     * @return Objeto {@link CnjNumber}
     * @throws IllegalArgumentException se o número for inválido
     */
    public static CnjNumber parse(String cnjString) {
        if (!isValid(cnjString)) {
            throw new IllegalArgumentException("Número de processo CNJ inválido: " + cnjString);
        }
        String clean = NON_DIGITS.matcher(cnjString.trim()).replaceAll("");
        String nnnnnnn = clean.substring(0, 7);
        String dd = clean.substring(7, 9);
        String aaaa = clean.substring(9, 13);
        String j = clean.substring(13, 14);
        String tr = clean.substring(14, 16);
        String oooo = clean.substring(16, 20);

        return new CnjNumber(nnnnnnn, dd, Integer.parseInt(aaaa), Integer.parseInt(j), Integer.parseInt(tr), oooo);
    }

    /**
     * Formata uma string de 20 dígitos no formato canônico NNNNNNN-DD.AAAA.J.TR.OOOO.
     *
     * @param unformatted String com dígitos
     * @return String formatada
     */
    public static String format(String unformatted) {
        if (unformatted == null) {
            return null;
        }
        String clean = NON_DIGITS.matcher(unformatted.trim()).replaceAll("");
        if (clean.length() != 20) {
            return unformatted; // Retorna original se não tiver os 20 dígitos
        }
        return String.format("%s-%s.%s.%s.%s.%s",
                clean.substring(0, 7),
                clean.substring(7, 9),
                clean.substring(9, 13),
                clean.substring(13, 14),
                clean.substring(14, 16),
                clean.substring(16, 20));
    }
}
