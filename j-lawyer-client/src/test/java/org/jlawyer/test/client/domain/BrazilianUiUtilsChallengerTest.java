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
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumber;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumberValidator;
import com.jdimension.jlawyer.domain.legal.cnj.CpfCnpjValidator;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Empirical Challenger Stress-Test Suite for Brazilian Legal Domain UI formatters and validators.
 */
public class BrazilianUiUtilsChallengerTest {

    // =========================================================================
    // 1. CNJ / NPU STRESS TESTS
    // =========================================================================

    @Test
    public void testAllCanonicalCnjFixtures() {
        for (String validCnj : BrazilianLegalFixtures.ALL_VALID_CNJ) {
            String clean = validCnj.replaceAll("[^0-9]", "");
            assertTrue("CNJ should be valid: " + validCnj, CnjNumberValidator.isValid(clean));
            assertTrue("CNJ formatted should be valid: " + validCnj, CnjNumberValidator.isValid(validCnj));

            JTextField field = new JTextField();
            AtomicReference<CnjNumber> ref = new AtomicReference<>();
            BrazilianUiUtils.installCnjFormatter(field, ref::set);

            field.setText(clean);
            assertEquals(validCnj, field.getText());
            assertNull("Outline should be null for valid CNJ: " + validCnj, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
            assertNotNull("Callback should have fired for: " + validCnj, ref.get());
            assertEquals(clean.substring(0, 7), ref.get().getSequentialNumber());
        }
    }

    @Test
    public void testCnjCheckDigitCalculationEdgeCases() {
        // Test various courts and segments
        // STF (J=1, TR=00): 0000001-95.2020.1.00.0000
        int dvStf = CnjNumberValidator.calculateCheckDigit("0000001", 2020, 1, 0, "0000");
        assertEquals(95, dvStf);

        // STJ (J=3, TR=00): 0000045-15.2021.3.00.0000
        int dvStj = CnjNumberValidator.calculateCheckDigit("0000045", 2021, 3, 0, "0000");
        assertEquals(15, dvStj);

        // TJSP (J=8, TR=26): 0001234-08.2023.8.26.0100
        int dvTjsp = CnjNumberValidator.calculateCheckDigit("0001234", 2023, 8, 26, "0100");
        assertEquals(8, dvTjsp);

        // TRF3 (J=4, TR=03): 5001234-03.2024.4.03.6100
        int dvTrf3 = CnjNumberValidator.calculateCheckDigit("5001234", 2024, 4, 3, "6100");
        assertEquals(3, dvTrf3);
    }

    @Test
    public void testCnjInvalidCheckDigitsNeverTriggerCallback() {
        String[] invalidCnjs = {
            BrazilianLegalFixtures.INVALID_CNJ_BAD_DV_1,
            BrazilianLegalFixtures.INVALID_CNJ_BAD_DV_2,
            "0001234-00.2023.8.26.0100",
            "0001234-98.2023.8.26.0100",
            "5001234-99.2024.4.03.6100"
        };

        for (String invalid : invalidCnjs) {
            String clean = invalid.replaceAll("[^0-9]", "");
            assertFalse("Should be invalid: " + invalid, CnjNumberValidator.isValid(clean));

            JTextField field = new JTextField();
            AtomicBoolean called = new AtomicBoolean(false);
            BrazilianUiUtils.installCnjFormatter(field, cnj -> called.set(true));

            field.setText(clean);
            assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
            assertFalse("Callback must NEVER fire for invalid CNJ: " + invalid, called.get());
            assertNotNull(field.getToolTipText());
            assertTrue(field.getToolTipText().toLowerCase().contains("inválido") || field.getToolTipText().toLowerCase().contains("incompleto"));
        }
    }

    @Test
    public void testCnjIncompleteNumbersFocusTransitions() {
        JTextField field = new JTextField();
        AtomicBoolean called = new AtomicBoolean(false);
        BrazilianUiUtils.installCnjFormatter(field, cnj -> called.set(true));

        // Incomplete while typing (no focus lost yet) -> outline is null
        field.setText("0001234");
        assertEquals("0001234", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertFalse(called.get());

        field.setText("0001234082023");
        assertEquals("0001234-08.2023", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertFalse(called.get());

        // Simulate focus lost on incomplete field -> outline becomes error
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusLost(new FocusEvent(field, FocusEvent.FOCUS_LOST));
        }
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(field.getToolTipText().contains("incompleto"));

        // Simulate focus gained on field -> outline returns to null
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusGained(new FocusEvent(field, FocusEvent.FOCUS_GAINED));
        }
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Complete the 20 digits with valid CNJ
        field.setText(BrazilianLegalFixtures.VALID_CNJ_TJSP_CLEAN);
        assertEquals(BrazilianLegalFixtures.VALID_CNJ_TJSP, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(called.get());
    }

    @Test
    public void testCnjNonNumericSanitizationAndLengthCapping() {
        JTextField field = new JTextField();
        AtomicReference<CnjNumber> ref = new AtomicReference<>();
        BrazilianUiUtils.installCnjFormatter(field, ref::set);

        // Input with letters, dashes, dots, spaces, special chars
        field.setText("Proc. Nº 0001234 - 08 . 2023 . 8 . 26 . 0100 (Urgente!)");
        assertEquals(BrazilianLegalFixtures.VALID_CNJ_TJSP, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertNotNull(ref.get());

        // Extra digits beyond 20 should be truncated
        field.setText(BrazilianLegalFixtures.VALID_CNJ_TJSP_CLEAN + "99999");
        assertEquals(BrazilianLegalFixtures.VALID_CNJ_TJSP, field.getText());
    }

    @Test
    public void testCnjEmptyAndWhitespaceHandling() {
        JTextField field = new JTextField();
        AtomicBoolean called = new AtomicBoolean(false);
        BrazilianUiUtils.installCnjFormatter(field, cnj -> called.set(true));

        field.setText("");
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertNull(field.getToolTipText());
        assertFalse(called.get());

        // Focus lost on empty field should remain clean (no error outline)
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusLost(new FocusEvent(field, FocusEvent.FOCUS_LOST));
        }
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertNull(field.getToolTipText());
    }

    // =========================================================================
    // 2. CPF STRESS TESTS
    // =========================================================================

    @Test
    public void testAllValidCpfs() {
        for (String cpf : BrazilianLegalFixtures.ALL_VALID_CPF) {
            String clean = cpf.replaceAll("[^0-9]", "");
            assertTrue("CPF should be valid: " + cpf, BrazilianDocumentValidator.isValidCpf(clean));
            assertTrue("CPF formatted should be valid: " + cpf, BrazilianDocumentValidator.isValidCpf(cpf));
            assertTrue("CpfCnpjValidator should validate: " + cpf, CpfCnpjValidator.isValidCpf(cpf));

            JTextField field = new JTextField();
            BrazilianUiUtils.installCpfFormatter(field);
            field.setText(clean);
            assertEquals(cpf, field.getText());
            assertNull("Outline should be null for valid CPF: " + cpf, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        }
    }

    @Test
    public void testCpfAllRepeatedDigitsRejected() {
        // 000.000.000-00 through 999.999.999-99 must ALL be rejected
        for (int d = 0; d <= 9; d++) {
            String repeatedClean = String.valueOf(d).repeat(11);
            String formatted = String.format("%d%d%d.%d%d%d.%d%d%d-%d%d", d, d, d, d, d, d, d, d, d, d, d);

            assertFalse("Repeated CPF digits must be rejected: " + repeatedClean, BrazilianDocumentValidator.isValidCpf(repeatedClean));
            assertFalse("Repeated CPF digits must be rejected: " + repeatedClean, CpfCnpjValidator.isValidCpf(repeatedClean));

            JTextField field = new JTextField();
            BrazilianUiUtils.installCpfFormatter(field);
            field.setText(repeatedClean);
            assertEquals(formatted, field.getText());
            assertEquals("Repeated CPF must trigger error outline: " + repeatedClean,
                    BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
            assertTrue(field.getToolTipText().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCpfIncompleteAndFocusTransitions() {
        JTextField field = new JTextField();
        BrazilianUiUtils.installCpfFormatter(field);

        // Typing incomplete CPF
        field.setText("11144477");
        assertEquals("111.444.77", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Focus lost on incomplete CPF
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusLost(new FocusEvent(field, FocusEvent.FOCUS_LOST));
        }
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(field.getToolTipText().contains("incompleto"));

        // Focus gained
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusGained(new FocusEvent(field, FocusEvent.FOCUS_GAINED));
        }
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Finish valid CPF
        field.setText(BrazilianLegalFixtures.VALID_CPF_1_CLEAN);
        assertEquals(BrazilianLegalFixtures.VALID_CPF_1, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }

    @Test
    public void testCpfNonNumericAndCapping() {
        JTextField field = new JTextField();
        BrazilianUiUtils.installCpfFormatter(field);

        field.setText("CPF: 111.444.777-35 (titular)");
        assertEquals(BrazilianLegalFixtures.VALID_CPF_1, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        field.setText(BrazilianLegalFixtures.VALID_CPF_1_CLEAN + "9999");
        assertEquals(BrazilianLegalFixtures.VALID_CPF_1, field.getText());
    }

    // =========================================================================
    // 3. CNPJ STRESS TESTS (Traditional & Alphanumeric IN RFB 2229/2024)
    // =========================================================================

    @Test
    public void testAllValidTraditionalCnpjs() {
        for (String cnpj : BrazilianLegalFixtures.ALL_VALID_CNPJ) {
            String clean = cnpj.replaceAll("[^0-9]", "");
            assertTrue("CNPJ should be valid: " + cnpj, BrazilianDocumentValidator.isValidCnpj(clean));
            assertTrue("CNPJ formatted should be valid: " + cnpj, BrazilianDocumentValidator.isValidCnpj(cnpj));

            JTextField field = new JTextField();
            BrazilianUiUtils.installCnpjFormatter(field);
            field.setText(clean);
            assertEquals(cnpj, field.getText());
            assertNull("Outline should be null for valid CNPJ: " + cnpj, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        }
    }

    @Test
    public void testAlphanumericCnpjInRfb2229() {
        // Valid alphanumeric CNPJ: 12ABC34501DE35
        String alphaCnpj = "12ABC34501DE35";
        assertTrue("Alphanumeric CNPJ must be valid: " + alphaCnpj, BrazilianDocumentValidator.isValidCnpj(alphaCnpj));
        assertEquals("12.ABC.345/01DE-35", BrazilianDocumentValidator.formatCnpj(alphaCnpj));

        JTextField field = new JTextField();
        BrazilianUiUtils.installCnpjFormatter(field);

        field.setText(alphaCnpj);
        assertEquals("12.ABC.345/01DE-35", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertEquals("CNPJ Válido", field.getToolTipText());

        // Lowercase input should be automatically uppercased and formatted
        field.setText("12abc34501de35");
        assertEquals("12.ABC.345/01DE-35", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Invalid alphanumeric CNPJ with bad DV
        field.setText("12ABC34501DE99");
        assertEquals("12.ABC.345/01DE-99", field.getText());
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(field.getToolTipText() != null && field.getToolTipText().toLowerCase().contains("inv"));
    }

    @Test
    public void testCnpjRepeatedCharactersRejected() {
        String repeatedNumeric = "00000000000000";
        assertFalse(BrazilianDocumentValidator.isValidCnpj(repeatedNumeric));

        String repeatedAlpha = "AAAAAAAAAAAAAA";
        assertFalse(BrazilianDocumentValidator.isValidCnpj(repeatedAlpha));

        JTextField field = new JTextField();
        BrazilianUiUtils.installCnpjFormatter(field);
        field.setText(repeatedAlpha);
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }

    @Test
    public void testCnpjIncompleteAndFocusTransitions() {
        JTextField field = new JTextField();
        BrazilianUiUtils.installCnpjFormatter(field);

        field.setText("330001670001");
        assertEquals("33.000.167/0001", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Focus lost on incomplete CNPJ
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusLost(new FocusEvent(field, FocusEvent.FOCUS_LOST));
        }
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(field.getToolTipText().contains("incompleto"));

        // Focus gained
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusGained(new FocusEvent(field, FocusEvent.FOCUS_GAINED));
        }
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Complete valid CNPJ
        field.setText("33000167000101");
        assertEquals(BrazilianLegalFixtures.VALID_CNPJ_PETROBRAS, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }

    // =========================================================================
    // 4. DYNAMIC CPF/CNPJ FORMATTER STRESS TESTS
    // =========================================================================

    @Test
    public void testDynamicCpfCnpjSwitching() {
        JTextField field = new JTextField();
        BrazilianUiUtils.installCpfOrCnpjFormatter(field);

        // 11 digits CPF
        field.setText(BrazilianLegalFixtures.VALID_CPF_2_CLEAN);
        assertEquals(BrazilianLegalFixtures.VALID_CPF_2, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // 14 chars CNPJ
        field.setText("33000167000101");
        assertEquals(BrazilianLegalFixtures.VALID_CNPJ_PETROBRAS, field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // 14 chars Alphanumeric CNPJ
        field.setText("12ABC34501DE35");
        assertEquals("12.ABC.345/01DE-35", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Focus lost on incomplete
        field.setText("12345");
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusLost(new FocusEvent(field, FocusEvent.FOCUS_LOST));
        }
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
    }

    // =========================================================================
    // 5. CEP STRESS TESTS & CALLBACKS
    // =========================================================================

    @Test
    public void testCepMaskAndCallbacks() {
        JTextField field = new JTextField();
        AtomicInteger callCount = new AtomicInteger(0);

        BrazilianUiUtils.installCepFormatter(field, callCount::incrementAndGet);

        // Typing partial CEP
        field.setText("01001");
        assertEquals("01001", field.getText());
        assertEquals(0, callCount.get());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Focus lost on incomplete CEP
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusLost(new FocusEvent(field, FocusEvent.FOCUS_LOST));
        }
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(field.getToolTipText().contains("incompleto"));
        assertEquals(0, callCount.get());

        // Focus gained
        for (FocusListener fl : field.getFocusListeners()) {
            fl.focusGained(new FocusEvent(field, FocusEvent.FOCUS_GAINED));
        }
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));

        // Full valid CEP
        field.setText("01001000");
        assertEquals("01001-000", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertEquals(1, callCount.get());

        // Zero CEP 00000-000 is invalid
        field.setText("00000000");
        assertEquals("00000-000", field.getText());
        assertEquals(BrazilianUiUtils.OUTLINE_ERROR, field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertEquals(1, callCount.get()); // must NOT increment
    }

    @Test
    public void testCepExtraCharactersAndSanitization() {
        JTextField field = new JTextField();
        AtomicBoolean completed = new AtomicBoolean(false);
        BrazilianUiUtils.installCepFormatter(field, () -> completed.set(true));

        field.setText("CEP 20040-002 RJ");
        assertEquals("20040-002", field.getText());
        assertNull(field.getClientProperty(BrazilianUiUtils.OUTLINE_KEY));
        assertTrue(completed.get());

        field.setText("200400029999");
        assertEquals("20040-002", field.getText());
    }
}
