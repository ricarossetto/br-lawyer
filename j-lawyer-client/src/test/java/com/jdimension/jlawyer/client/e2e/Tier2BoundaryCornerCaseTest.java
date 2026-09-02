/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.e2e;

import com.jdimension.jlawyer.client.e2e.fixtures.BrazilianLegalFixtures;
import com.jdimension.jlawyer.client.e2e.fixtures.MockWorkflowContext;
import com.jdimension.jlawyer.domain.legal.cnj.*;
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.client.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * TIER 2: Suíte de Casos de Borda, Condições Limítrofes e Entradas Adversárias.
 * Valida robustez matemática, limites de dados, campos nulos/vazios e integridade de erros.
 *
 * @author BR-LAWYER Team
 */
public class Tier2BoundaryCornerCaseTest {

    private MockWorkflowContext context;
    private Locale ptBrLocale;

    @Before
    public void setUp() {
        context = new MockWorkflowContext();
        ptBrLocale = new Locale("pt", "BR");
        Locale.setDefault(ptBrLocale);
    }

    // =========================================================================
    // 1. CNJ NPU Boundary Cases (Resolução 65/2008)
    // =========================================================================
    @Test
    public void testCnj_BoundaryZeroPaddedSequential() {
        assertTrue(CnjNumberValidator.isValid(BrazilianLegalFixtures.VALID_CNJ_STF));
        CnjNumber parsed = CnjNumberValidator.parse(BrazilianLegalFixtures.VALID_CNJ_STF);
        assertNotNull(parsed);
        assertEquals("0000001", parsed.getSequentialNumber());
        assertEquals("95", parsed.getCheckDigit());
        assertEquals(2020, parsed.getYear());
        assertEquals(1, parsed.getJusticeSegment());
    }

    @Test
    public void testCnj_BoundaryMaxSequential() {
        int dv = CnjNumberValidator.calculateCheckDigit("9999999", 2024, 8, 26, "0100");
        assertTrue("DV calculado deve estar entre 1 e 97", dv >= 1 && dv <= 97);
        String cnjMax = String.format("9999999-%02d.2024.8.26.0100", dv);
        assertTrue(CnjNumberValidator.isValid(cnjMax));
    }

    @Test
    public void testCnj_InvalidJusticeSegments() {
        assertFalse(CnjNumberValidator.isValid("0001234-08.2023.0.26.0100"));
        assertFalse(CnjNumberValidator.isValid("0001234-08.2023.10.26.0100"));
    }

    @Test
    public void testCnj_NullEmptyWhitespaceInputs() {
        assertFalse(CnjNumberValidator.isValid(null));
        assertFalse(CnjNumberValidator.isValid(""));
        assertFalse(CnjNumberValidator.isValid("   "));
        assertFalse(CnjNumberValidator.isValid("\t\n"));
        try {
            CnjNumberValidator.parse(null);
            fail("parse(null) deve lançar IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
        try {
            CnjNumberValidator.parse("");
            fail("parse('') deve lançar IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
        try {
            CnjNumberValidator.parse("invalid-cnj");
            fail("parse('invalid-cnj') deve lançar IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testCnj_BoundaryCheckDigit98WrapAround() {
        int dv = CnjNumberValidator.calculateCheckDigit("0000000", 2024, 1, 0, "0000");
        assertTrue(dv >= 1 && dv <= 97);
    }

    // =========================================================================
    // 2. CPF & CNPJ Boundary Cases
    // =========================================================================
    @Test
    public void testCpf_AllRepeatedDigitsRejected() {
        for (int d = 0; d <= 9; d++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 11; i++) sb.append(d);
            String raw = sb.toString();
            assertFalse("CPF de dígitos repetidos deve ser rejeitado: " + raw, CpfCnpjValidator.isValidCpf(raw));
        }
    }

    @Test
    public void testCpf_BoundaryLengthVariations() {
        assertFalse(CpfCnpjValidator.isValidCpf("1"));
        assertFalse(CpfCnpjValidator.isValidCpf("1234567890"));
        assertFalse(CpfCnpjValidator.isValidCpf("123456789012"));
        assertNull(CpfCnpjValidator.formatCpf(null));
        assertEquals("", CpfCnpjValidator.formatCpf(""));
        assertEquals("123", CpfCnpjValidator.formatCpf("123"));
    }

    @Test
    public void testCnpj_AllRepeatedDigitsRejected() {
        for (int d = 0; d <= 9; d++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 14; i++) sb.append(d);
            String raw = sb.toString();
            assertFalse("CNPJ de dígitos repetidos deve ser rejeitado: " + raw, CpfCnpjValidator.isValidCnpj(raw));
        }
    }

    @Test
    public void testCnpj_BoundaryLengthVariations() {
        assertFalse(CpfCnpjValidator.isValidCnpj("123"));
        assertFalse(CpfCnpjValidator.isValidCnpj("1234567800019"));
        assertFalse(CpfCnpjValidator.isValidCnpj("123456780001999"));
        assertNull(CpfCnpjValidator.formatCnpj(null));
        assertEquals("", CpfCnpjValidator.formatCnpj(""));
    }

    @Test
    public void testCpfCnpj_SpecialCharactersAndInjection() {
        assertFalse(CpfCnpjValidator.isValidCpf("111.444.777-35' OR '1'='1"));
        assertFalse(CpfCnpjValidator.isValidCnpj("00.000.000/0001-91' OR 1=1; --"));
        assertFalse(CpfCnpjValidator.isValidCpf("111.444.777-XX"));
        assertFalse(CpfCnpjValidator.isValidCnpj("SELECT * FROM USERS;"));
    }

    // =========================================================================
    // 3. Date & Deadline Boundary Cases
    // =========================================================================
    @Test
    public void testDate_LeapYearHandling() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", ptBrLocale);
        sdf.setLenient(false);

        try {
            Date leapDate = sdf.parse("29/02/2024");
            assertNotNull(leapDate);
        } catch (Exception e) {
            fail("2024 é bissexto, 29/02 deve ser válido: " + e.getMessage());
        }

        try {
            sdf.parse("29/02/2023");
            fail("29/02/2023 deveria lançar ParseException");
        } catch (Exception expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testDate_YearEndRollover() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.DECEMBER, 31, 23, 59, 59);
        Date endOfYear = cal.getTime();

        cal.add(Calendar.SECOND, 1);
        Date startOfNextYear = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", ptBrLocale);
        assertEquals("31/12/2026 23:59:59", sdf.format(endOfYear));
        assertEquals("01/01/2027 00:00:00", sdf.format(startOfNextYear));
    }

    @Test
    public void testDate_NullDateUtilsHandling() {
        assertFalse(DateUtils.isToday(null));
        assertFalse(DateUtils.overlapsWithRange(null, null, -1, 1));
        assertNull(DateUtils.parseDate("invalid-date-string"));
    }

    // =========================================================================
    // 4. Currency Formatting Boundary Cases
    // =========================================================================
    @Test
    public void testCurrency_ZeroAndExtremeValues() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(ptBrLocale);
        DecimalFormat df = new DecimalFormat("R$ #,##0.00", symbols);

        assertEquals("R$ 0,00", df.format(0.0));
        assertEquals("R$ 0,01", df.format(0.01));
        assertEquals("R$ 999.999.999,99", df.format(999999999.99));
        assertEquals("-R$ 500,00", df.format(-500.00));
    }

    // =========================================================================
    // 5. Workflow Pagination & Boundary Querying
    // =========================================================================
    @Test
    public void testWorkflow_TaskStatusHandling() throws Exception {
        TaskDetailDTO task = new TaskDetailDTO();
        task.setTitle("Tarefa com Status Desconhecido");
        task.setStatus("UNKNOWN_STATUS");
        TaskDetailDTO saved = context.getTaskService().saveTask(task, "tester", false);
        assertNotNull(saved);
        assertEquals("UNKNOWN_STATUS", saved.getStatus());
    }

    @Test
    public void testWorkflow_DeleteTaskGracefully() throws Exception {
        context.getTaskService().deleteTask("non-existent-id-999", "admin");
        assertTrue(true);
    }

    @Test
    public void testWorkflow_ExtremeSearchQuery() throws Exception {
        PublicationFilterDTO filter = new PublicationFilterDTO();
        filter.setSearchText("TERMO_COMPLETAMENTE_INEXISTENTE_XYZ_999");
        List<PublicationOverviewDTO> result = context.getPublicationService().listPublications(filter);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
