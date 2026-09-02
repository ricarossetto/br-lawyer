/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package org.jlawyer.test.client.domain;

import com.jdimension.jlawyer.client.e2e.fixtures.BrazilianLegalFixtures;
import com.jdimension.jlawyer.client.utils.BrazilianUiUtils;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumber;
import org.junit.Test;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Testes unitários para BrazilianUiUtils (máscaras, validações FlatLaf outline e callbacks).
 */
public class BrazilianUiUtilsTest {

    @Test
    public void testCnjMask() {
        assertEquals("", BrazilianUiUtils.applyCnjMask(""));
        assertEquals("0001234", BrazilianUiUtils.applyCnjMask("0001234"));
        assertEquals("0001234-08", BrazilianUiUtils.applyCnjMask("000123408"));
        assertEquals("0001234-08.2023", BrazilianUiUtils.applyCnjMask("0001234082023"));
        assertEquals("0001234-08.2023.8", BrazilianUiUtils.applyCnjMask("00012340820238"));
        assertEquals("0001234-08.2023.8.26", BrazilianUiUtils.applyCnjMask("0001234082023826"));
        assertEquals("0001234-08.2023.8.26.0100", BrazilianUiUtils.applyCnjMask("00012340820238260100"));
    }

    @Test
    public void testInstallCnjFormatterValid() {
        JTextField field = new JTextField();
        AtomicReference<CnjNumber> captured = new AtomicReference<>();

        BrazilianUiUtils.installCnjFormatter(field, captured::set);

        // NPU válido de fixture: 5001234-03.2024.4.03.6100
        field.setText(BrazilianLegalFixtures.VALID_CNJ_TRF3_CLEAN);

        assertEquals(BrazilianLegalFixtures.VALID_CNJ_TRF3, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertNotNull(captured.get());
        assertEquals("5001234", captured.get().getSequentialNumber());
        assertEquals("03", captured.get().getCheckDigit());
        assertEquals(2024, captured.get().getYear());
        assertEquals(4, captured.get().getJusticeSegment());
        assertEquals(3, captured.get().getCourtNumber());
    }

    @Test
    public void testInstallCnjFormatterInvalidDv() {
        JTextField field = new JTextField();
        AtomicReference<CnjNumber> captured = new AtomicReference<>();

        BrazilianUiUtils.installCnjFormatter(field, captured::set);

        // 0001234-99.2023.8.26.0100 (DV 99 inválido)
        field.setText("00012349920238260100");

        assertEquals("0001234-99.2023.8.26.0100", field.getText());
        assertEquals("error", field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertNull(captured.get());
        assertTrue(field.getToolTipText().contains("inválido"));
    }

    @Test
    public void testCpfFormatter() {
        assertEquals("111.444.777-35", BrazilianUiUtils.applyCpfMask("11144477735"));

        JTextField field = new JTextField();
        BrazilianUiUtils.installCpfFormatter(field);

        // CPF válido com DV correto: 11144477735
        field.setText(BrazilianLegalFixtures.VALID_CPF_1_CLEAN);
        assertEquals(BrazilianLegalFixtures.VALID_CPF_1, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // CPF com dígitos repetidos inválido: 11111111111
        field.setText(BrazilianLegalFixtures.INVALID_CPF_REPEATED_1);
        assertEquals("111.111.111-11", field.getText());
        assertEquals("error", field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }

    @Test
    public void testCnpjFormatter() {
        JTextField field = new JTextField();
        BrazilianUiUtils.installCnpjFormatter(field);

        // CNPJ tradicional válido: 33.000.167/0001-01
        field.setText("33000167000101");
        assertEquals(BrazilianLegalFixtures.VALID_CNPJ_PETROBRAS, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // CNPJ alfanumérico válido: 12ABC34501DE35
        field.setText("12ABC34501DE35");
        assertEquals("12.ABC.345/01DE-35", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // CNPJ com DV incorreto
        field.setText(BrazilianLegalFixtures.INVALID_CNPJ_BAD_DV);
        assertEquals(BrazilianLegalFixtures.INVALID_CNPJ_BAD_DV, field.getText());
        assertEquals("error", field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }

    @Test
    public void testCepFormatter() {
        assertEquals("01001-000", BrazilianUiUtils.applyCepMask("01001000"));

        JTextField field = new JTextField();
        AtomicBoolean completed = new AtomicBoolean(false);

        BrazilianUiUtils.installCepFormatter(field, () -> completed.set(true));

        field.setText(BrazilianLegalFixtures.VALID_CEP_SP_CLEAN);
        assertEquals(BrazilianLegalFixtures.VALID_CEP_SP, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(completed.get());
    }

    @Test
    public void testDynamicCpfCnpjFormatter() {
        JTextField field = new JTextField();
        BrazilianUiUtils.installCpfOrCnpjFormatter(field);

        // Digitando CPF (11 dígitos)
        field.setText(BrazilianLegalFixtures.VALID_CPF_1_CLEAN);
        assertEquals(BrazilianLegalFixtures.VALID_CPF_1, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Digitando CNPJ (14 dígitos)
        field.setText("33000167000101");
        assertEquals(BrazilianLegalFixtures.VALID_CNPJ_PETROBRAS, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }
}
