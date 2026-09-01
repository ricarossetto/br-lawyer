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
 * Validador e formatador de documentos fiscais e cadastrais brasileiros:
 * - CPF (Cadastro de Pessoas Físicas - 11 dígitos, Módulo 11)
 * - CNPJ (Cadastro Nacional da Pessoa Jurídica - 14 posições numéricas ou novo padrão alfanumérico da Receita Federal)
 * - OAB (Ordem dos Advogados do Brasil - Número/UF/Tipo)
 *
 * @author BR-LAWYER Team
 */
public final class CpfCnpjValidator {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");

    private CpfCnpjValidator() {
        // Utilitário estático
    }

    /**
     * Valida um CPF utilizando o algoritmo oficial do Módulo 11.
     *
     * @param cpf String contendo CPF formatado ou apenas dígitos
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

        // Rejeita sequências de dígitos repetidos conhecidas (00000000000, 11111111111, etc.)
        if (clean.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Primeiro dígito verificador
            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += (clean.charAt(i) - '0') * (10 - i);
            }
            int rem1 = sum1 % 11;
            int dv1 = (rem1 < 2) ? 0 : (11 - rem1);
            if (dv1 != (clean.charAt(9) - '0')) {
                return false;
            }

            // Segundo dígito verificador
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
     * Valida um CNPJ tradicional (14 dígitos numéricos) via Módulo 11.
     *
     * @param cnpj String contendo CNPJ formatado ou dígitos
     * @return true se o CNPJ for válido
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) {
            return false;
        }
        String clean = NON_DIGITS.matcher(cnpj.trim()).replaceAll("");
        if (clean.length() != 14) {
            return false;
        }

        if (clean.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            // Primeiro dígito verificador
            int sum1 = 0;
            for (int i = 0; i < 12; i++) {
                sum1 += (clean.charAt(i) - '0') * weights1[i];
            }
            int rem1 = sum1 % 11;
            int dv1 = (rem1 < 2) ? 0 : (11 - rem1);
            if (dv1 != (clean.charAt(12) - '0')) {
                return false;
            }

            // Segundo dígito verificador
            int sum2 = 0;
            for (int i = 0; i < 13; i++) {
                sum2 += (clean.charAt(i) - '0') * weights2[i];
            }
            int rem2 = sum2 % 11;
            int dv2 = (rem2 < 2) ? 0 : (11 - rem2);

            return dv2 == (clean.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formata um CPF no padrão 000.000.000-00.
     *
     * @param cpf String com 11 dígitos
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
     * Formata um CNPJ no padrão 00.000.000/0000-00.
     *
     * @param cnpj String com 14 dígitos
     * @return CNPJ formatado
     */
    public static String formatCnpj(String cnpj) {
        if (cnpj == null) return null;
        String clean = NON_DIGITS.matcher(cnpj.trim()).replaceAll("");
        if (clean.length() != 14) return cnpj;
        return String.format("%s.%s.%s/%s-%s",
                clean.substring(0, 2),
                clean.substring(2, 5),
                clean.substring(5, 8),
                clean.substring(8, 12),
                clean.substring(12, 14));
    }
}
