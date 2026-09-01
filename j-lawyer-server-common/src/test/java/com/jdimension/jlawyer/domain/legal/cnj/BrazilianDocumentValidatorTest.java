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
 * Testes unitários abrangentes para o validador canônico brasileiro.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianDocumentValidatorTest {

    @Test
    public void testValidCpf() {
        // CPFs sintéticos válidos com dígitos verificadores corretos
        assertTrue(BrazilianDocumentValidator.isValidCpf("52998224725"));
        assertTrue(BrazilianDocumentValidator.isValidCpf("529.982.247-25"));
        assertTrue(BrazilianDocumentValidator.isValidCpf("11144477735"));
        assertTrue(BrazilianDocumentValidator.isValidCpf("111.444.777-35"));

        // Rejeições de inválidos
        assertFalse(BrazilianDocumentValidator.isValidCpf(null));
        assertFalse(BrazilianDocumentValidator.isValidCpf(""));
        assertFalse(BrazilianDocumentValidator.isValidCpf("12345678900"));
        assertFalse(BrazilianDocumentValidator.isValidCpf("00000000000"));
        assertFalse(BrazilianDocumentValidator.isValidCpf("11111111111"));
        assertFalse(BrazilianDocumentValidator.isValidCpf("22222222222"));
        assertFalse(BrazilianDocumentValidator.isValidCpf("99999999999"));
        assertFalse(BrazilianDocumentValidator.isValidCpf("123"));
    }

    @Test
    public void testValidCnpjTraditional() {
        // CNPJs numéricos tradicionais válidos
        assertTrue(BrazilianDocumentValidator.isValidCnpj("00000000000191")); // Banco do Brasil
        assertTrue(BrazilianDocumentValidator.isValidCnpj("00.000.000/0001-91"));
        assertTrue(BrazilianDocumentValidator.isValidCnpj("33000167000101")); // Petrobras
        assertTrue(BrazilianDocumentValidator.isValidCnpj("33.000.167/0001-01"));

        // Rejeições de inválidos
        assertFalse(BrazilianDocumentValidator.isValidCnpj(null));
        assertFalse(BrazilianDocumentValidator.isValidCnpj(""));
        assertFalse(BrazilianDocumentValidator.isValidCnpj("00000000000000"));
        assertFalse(BrazilianDocumentValidator.isValidCnpj("11111111111111"));
        assertFalse(BrazilianDocumentValidator.isValidCnpj("12345678000100"));
    }

    @Test
    public void testValidCnpjAlphanumeric() {
        // Teste do novo padrão alfanumérico da IN RFB nº 2.229/2024
        assertTrue(BrazilianDocumentValidator.isValidCnpj("12ABC34501DE35"));
        assertTrue(BrazilianDocumentValidator.isValidCnpj("12.ABC.345/01DE-35"));

        assertFalse(BrazilianDocumentValidator.isValidCnpj("12ABC34501DE00"));
    }

    @Test
    public void testValidCep() {
        assertTrue(BrazilianDocumentValidator.isValidCep("01310-100"));
        assertTrue(BrazilianDocumentValidator.isValidCep("01310100"));
        assertTrue(BrazilianDocumentValidator.isValidCep("70040-906"));
        assertTrue(BrazilianDocumentValidator.isValidCep("70040906"));

        assertFalse(BrazilianDocumentValidator.isValidCep(null));
        assertFalse(BrazilianDocumentValidator.isValidCep(""));
        assertFalse(BrazilianDocumentValidator.isValidCep("00000000"));
        assertFalse(BrazilianDocumentValidator.isValidCep("12345"));
        assertFalse(BrazilianDocumentValidator.isValidCep("01310-10A"));
    }

    @Test
    public void testFormattersAndMasking() {
        assertEquals("529.982.247-25", BrazilianDocumentValidator.formatCpf("52998224725"));
        assertEquals("00.000.000/0001-91", BrazilianDocumentValidator.formatCnpj("00000000000191"));
        assertEquals("01310-100", BrazilianDocumentValidator.formatCep("01310100"));
        assertEquals("***.982.247-**", BrazilianDocumentValidator.maskCpf("52998224725"));
    }

    @Test
    public void testValidTituloEleitor() {
        assertTrue(BrazilianDocumentValidator.isValidTituloEleitor("004324010132"));
        assertFalse(BrazilianDocumentValidator.isValidTituloEleitor("004324010150"));
        assertFalse(BrazilianDocumentValidator.isValidTituloEleitor(null));
    }
}
