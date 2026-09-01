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
 * Suíte de testes unitários para cálculo e validação da Numeração Processual Única (NPU/CNJ)
 * segundo a Resolução CNJ nº 65/2008.
 *
 * @author BR-LAWYER Team
 */
public class CnjNumberValidatorTest {

    @Test
    public void testValidCnjNumbers() {
        // Exemplos reais de números de processos de diversos tribunais brasileiros
        String[] validNumbers = {
            "0001234-08.2023.8.26.0100", // TJSP Sintético
            "5001234-03.2024.4.03.6100", // TRF3 Sintético
            "1000123-93.2023.5.02.0001", // TRT2 Sintético
            "0000001-95.2020.1.00.0000", // STF Sintético
            "0000045-15.2021.3.00.0000"  // STJ Sintético
        };

        for (String cnj : validNumbers) {
            // Calcula o DV esperado para testar a consistência do algoritmo
            String clean = cnj.replaceAll("[^0-9]", "");
            String n7 = clean.substring(0, 7);
            int a4 = Integer.parseInt(clean.substring(9, 13));
            int j = Integer.parseInt(clean.substring(13, 14));
            int tr = Integer.parseInt(clean.substring(14, 16));
            String o4 = clean.substring(16, 20);

            int calculatedDv = CnjNumberValidator.calculateCheckDigit(n7, a4, j, tr, o4);
            int expectedDv = Integer.parseInt(clean.substring(7, 9));

            // Testa o cálculo
            assertEquals("O dígito verificador calculado deve ser consistente para: " + cnj, expectedDv, calculatedDv);

            // Testa a validação com máscara
            assertTrue("Deve validar o número com máscara: " + cnj, CnjNumberValidator.isValid(cnj));

            // Testa a validação sem máscara
            assertTrue("Deve validar o número sem máscara: " + clean, CnjNumberValidator.isValid(clean));
        }
    }

    @Test
    public void testInvalidCnjNumbers() {
        // Números com dígito verificador adulterado
        assertFalse(CnjNumberValidator.isValid("0001234-99.2023.8.26.0100"));
        assertFalse(CnjNumberValidator.isValid("5001234-00.2024.4.03.6100"));
        
        // Números com tamanho incorreto
        assertFalse(CnjNumberValidator.isValid("1234-56.2023.8.26.0100"));
        assertFalse(CnjNumberValidator.isValid("0001234-56.2023.8.26"));
        assertFalse(CnjNumberValidator.isValid(""));
        assertFalse(CnjNumberValidator.isValid(null));

        // Segmento de justiça inválido (J=0)
        assertFalse(CnjNumberValidator.isValid("0001234-56.2023.0.26.0100"));
    }

    @Test
    public void testParseAndFormat() {
        String original = "0001234-08.2023.8.26.0100";
        CnjNumber cnj = CnjNumberValidator.parse(original);

        assertNotNull(cnj);
        assertEquals("0001234", cnj.getSequentialNumber());
        assertEquals("08", cnj.getCheckDigit());
        assertEquals(2023, cnj.getYear());
        assertEquals(8, cnj.getJusticeSegment());
        assertEquals(26, cnj.getCourtNumber());
        assertEquals("0100", cnj.getOriginUnit());
        assertEquals(original, cnj.getFormatted());

        // Teste de formatação a partir de dígitos puros
        String raw = "00012340820238260100";
        assertEquals(original, CnjNumberValidator.format(raw));
    }
}
