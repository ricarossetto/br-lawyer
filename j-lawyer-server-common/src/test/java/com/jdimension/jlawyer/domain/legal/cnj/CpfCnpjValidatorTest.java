/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.cnj;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes unitários para validação de CPF e CNPJ.
 *
 * @author BR-LAWYER Team
 */
public class CpfCnpjValidatorTest {

    @Test
    public void testValidCpf() {
        // Fixtures sintéticas válidas segundo o algoritmo do Módulo 11
        // (Exemplos gerados matematicamente para teste)
        String[] validCpfs = {
            "111.444.777-35",
            "529.982.247-25",
            "123.456.789-09"
        };

        for (String cpf : validCpfs) {
            String raw = cpf.replaceAll("[^0-9]", "");
            assertTrue("CPF com máscara deve ser válido: " + cpf, CpfCnpjValidator.isValidCpf(cpf));
            assertTrue("CPF sem máscara deve ser válido: " + raw, CpfCnpjValidator.isValidCpf(raw));
            assertEquals("Formatação deve coincidir", cpf, CpfCnpjValidator.formatCpf(raw));
        }
    }

    @Test
    public void testInvalidCpf() {
        // Sequências repetidas
        assertFalse(CpfCnpjValidator.isValidCpf("111.111.111-11"));
        assertFalse(CpfCnpjValidator.isValidCpf("000.000.000-00"));

        // Dígitos verificadores incorretos
        assertFalse(CpfCnpjValidator.isValidCpf("123.456.789-00"));
        assertFalse(CpfCnpjValidator.isValidCpf("529.982.247-99"));

        // Tamanho incorreto
        assertFalse(CpfCnpjValidator.isValidCpf("123.456.789"));
        assertFalse(CpfCnpjValidator.isValidCpf(""));
        assertFalse(CpfCnpjValidator.isValidCpf(null));
    }

    @Test
    public void testValidCnpj() {
        // Fixtures sintéticas de CNPJ válidos
        String[] validCnpjs = {
            "00.000.000/0001-91", // Banco do Brasil
            "33.000.167/0001-01", // Petrobras
            "04.524.238/0001-77"
        };

        for (String cnpj : validCnpjs) {
            String raw = cnpj.replaceAll("[^0-9]", "");
            assertTrue("CNPJ com máscara deve ser válido: " + cnpj, CpfCnpjValidator.isValidCnpj(cnpj));
            assertTrue("CNPJ sem máscara deve ser válido: " + raw, CpfCnpjValidator.isValidCnpj(raw));
            assertEquals("Formatação deve coincidir", cnpj, CpfCnpjValidator.formatCnpj(raw));
        }
    }

    @Test
    public void testInvalidCnpj() {
        // Sequências repetidas
        assertFalse(CpfCnpjValidator.isValidCnpj("11.111.111/1111-11"));
        assertFalse(CpfCnpjValidator.isValidCnpj("00.000.000/0000-00"));

        // Dígito incorreto
        assertFalse(CpfCnpjValidator.isValidCnpj("00.000.000/0001-00"));
        assertFalse(CpfCnpjValidator.isValidCnpj("33.000.167/0001-99"));

        // Tamanho incorreto
        assertFalse(CpfCnpjValidator.isValidCnpj("33.000.167"));
        assertFalse(CpfCnpjValidator.isValidCnpj(""));
        assertFalse(CpfCnpjValidator.isValidCnpj(null));
    }
}
