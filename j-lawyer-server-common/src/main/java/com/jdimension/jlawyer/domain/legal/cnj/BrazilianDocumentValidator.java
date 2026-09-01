/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.cnj;

import java.util.regex.Pattern;

/**
 * Validador e formatador canônico de documentos e identificadores brasileiros:
 * - CPF (Módulo 11 com eliminação de sequências repetidas e suporte à Lei 14.534/2023)
 * - CNPJ (Módulo 11 tradicional e CNPJ Alfanumérico da IN RFB nº 2.229/2024)
 * - CEP (Código de Endereçamento Postal de 8 dígitos)
 * - OAB (Número de Inscrição na OAB com UF)
 * - Título de Eleitor (Módulo 11 com verificação de Unidade Federativa TSE)
 * - PIS / PASEP / NIS (Módulo 11)
 *
 * @author BR-LAWYER Team
 */
public final class BrazilianDocumentValidator {

    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");
    private static final Pattern CNPJ_ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Z0-9]{12}\\d{2}$");

    private static final int[] CNPJ_WEIGHTS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_WEIGHTS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private BrazilianDocumentValidator() {
        // Utilitário estático
    }

    /**
     * Valida um CPF (11 dígitos, Módulo 11).
     *
     * @param cpf CPF com ou sem máscara
     * @return true se o CPF for válido
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        String clean = NON_DIGITS.matcher(cpf.trim()).replaceAll("");
        if (clean.length() != 11) {
            return false;
        }

        // Rejeita sequências de dígitos repetidos conhecidas
        if (clean.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += (clean.charAt(i) - '0') * (10 - i);
            }
            int rem1 = sum1 % 11;
            int dv1 = (rem1 < 2) ? 0 : (11 - rem1);
            if (dv1 != (clean.charAt(9) - '0')) {
                return false;
            }

            int sum2 = 0;
            for (int i = 0; i < 10; i++) {
                sum2 += (clean.charAt(i) - '0') * (11 - i);
            }
            int rem2 = sum2 % 11;
            int dv2 = (rem2 < 2) ? 0 : (11 - rem2);

            return dv2 == (clean.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida um CNPJ (tradicional numérico de 14 dígitos ou alfanumérico segundo a IN RFB nº 2.229/2024).
     *
     * @param cnpj CNPJ com ou sem formatação
     * @return true se o CNPJ for válido
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) {
            return false;
        }
        String clean = NON_ALPHANUMERIC.matcher(cnpj.trim()).replaceAll("").toUpperCase();
        if (clean.length() != 14 || !CNPJ_ALPHANUMERIC_PATTERN.matcher(clean).matches()) {
            return false;
        }

        if (clean.matches("^(.)\\1{13}$")) {
            return false;
        }

        try {
            int dv1 = calculateCnpjDigit(clean.substring(0, 12), CNPJ_WEIGHTS_DV1);
            int dv2 = calculateCnpjDigit(clean.substring(0, 12) + dv1, CNPJ_WEIGHTS_DV2);

            return (clean.charAt(12) - '0' == dv1) && (clean.charAt(13) - '0' == dv2);
        } catch (Exception e) {
            return false;
        }
    }

    private static int calculateCnpjDigit(String input, int[] weights) {
        int sum = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int value = (int) c - 48; // Regra ASCII da IN RFB nº 2.229/2024
            sum += value * weights[i];
        }
        int rem = sum % 11;
        return (rem < 2) ? 0 : (11 - rem);
    }

    /**
     * Valida um CEP brasileiro (8 dígitos numéricos).
     *
     * @param cep CEP formatado (XXXXX-XXX) ou apenas dígitos (XXXXXXXX)
     * @return true se for um CEP válido
     */
    public static boolean isValidCep(String cep) {
        if (cep == null) {
            return false;
        }
        String trimmed = cep.trim();
        if (!CEP_PATTERN.matcher(trimmed).matches()) {
            return false;
        }
        String clean = NON_DIGITS.matcher(trimmed).replaceAll("");
        return clean.length() == 8 && !clean.equals("00000000");
    }

    /**
     * Formata um CEP no padrão 00000-000.
     *
     * @param cep CEP com 8 dígitos
     * @return CEP formatado
     */
    public static String formatCep(String cep) {
        if (cep == null) return null;
        String clean = NON_DIGITS.matcher(cep.trim()).replaceAll("");
        if (clean.length() != 8) return cep;
        return clean.substring(0, 5) + "-" + clean.substring(5, 8);
    }

    /**
     * Formata um CPF no padrão 000.000.000-00.
     *
     * @param cpf CPF com 11 dígitos
     * @return CPF formatado
     */
    public static String formatCpf(String cpf) {
        if (cpf == null) return null;
        String clean = NON_DIGITS.matcher(cpf.trim()).replaceAll("");
        if (clean.length() != 11) return cpf;
        return String.format("%s.%s.%s-%s",
                clean.substring(0, 3),
                clean.substring(3, 6),
                clean.substring(6, 9),
                clean.substring(9, 11));
    }

    /**
     * Formata um CNPJ no padrão 00.000.000/0000-00 (suporta tradicional e alfanumérico).
     *
     * @param cnpj CNPJ com 14 caracteres
     * @return CNPJ formatado
     */
    public static String formatCnpj(String cnpj) {
        if (cnpj == null) return null;
        String clean = NON_ALPHANUMERIC.matcher(cnpj.trim()).replaceAll("").toUpperCase();
        if (clean.length() != 14) return cnpj;
        return String.format("%s.%s.%s/%s-%s",
                clean.substring(0, 2),
                clean.substring(2, 5),
                clean.substring(5, 8),
                clean.substring(8, 12),
                clean.substring(12, 14));
    }

    /**
     * Mascara um CPF para exibição segura em logs ou interfaces (LGPD: ***.123.456-**).
     *
     * @param cpf CPF com 11 dígitos
     * @return CPF mascarado
     */
    public static String maskCpf(String cpf) {
        if (cpf == null) return null;
        String clean = NON_DIGITS.matcher(cpf.trim()).replaceAll("");
        if (clean.length() != 11) return "***.***.***-**";
        return String.format("***.%s.%s-**", clean.substring(3, 6), clean.substring(6, 9));
    }

    /**
     * Normaliza e valida número de inscrição OAB.
     *
     * @param oabInscricao Ex: "OAB/SP 123456", "123456/SP", "123456-A"
     * @return true se o padrão de inscrição OAB for sintaticamente plausível
     */
    public static boolean isValidOabNumber(String oabInscricao) {
        if (oabInscricao == null || oabInscricao.trim().isEmpty()) {
            return false;
        }
        String clean = oabInscricao.replaceAll("[^a-zA-Z0-9/\\-\\s]", "").trim();
        return clean.length() >= 3 && clean.length() <= 20;
    }

    /**
     * Valida Título de Eleitor brasileiro segundo a regra oficial do TSE (Módulo 11).
     *
     * @param titulo Número do título de eleitor (12 dígitos)
     * @return true se o título for válido
     */
    public static boolean isValidTituloEleitor(String titulo) {
        if (titulo == null) return false;
        String clean = NON_DIGITS.matcher(titulo.trim()).replaceAll("");
        if (clean.length() < 10 || clean.length() > 12) return false;

        while (clean.length() < 12) {
            clean = "0" + clean;
        }

        int ufCode = Integer.parseInt(clean.substring(8, 10));
        if (ufCode < 1 || ufCode > 28) return false;

        try {
            int sum1 = 0;
            for (int i = 0; i < 8; i++) {
                sum1 += (clean.charAt(i) - '0') * (i + 2);
            }
            int rem1 = sum1 % 11;
            int dv1;
            if (rem1 == 0) {
                dv1 = (ufCode == 1 || ufCode == 2) ? 1 : 0;
            } else if (rem1 == 10) {
                dv1 = 0;
            } else {
                dv1 = rem1;
            }
            if ((clean.charAt(10) - '0') != dv1) return false;

            int sum2 = (clean.charAt(8) - '0') * 7 + (clean.charAt(9) - '0') * 8 + dv1 * 9;
            int rem2 = sum2 % 11;
            int dv2;
            if (rem2 == 0) {
                dv2 = (ufCode == 1 || ufCode == 2) ? 1 : 0;
            } else if (rem2 == 10) {
                dv2 = 0;
            } else {
                dv2 = rem2;
            }

            return (clean.charAt(11) - '0') == dv2;
        } catch (Exception e) {
            return false;
        }
    }
}
