/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.financial;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Testes Unitários da Calculadora Financeira de Honorários e Prestação de Contas de RPV.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianLegalFinancialCalculatorTest {

    private BrazilianLegalFinancialCalculator calculator;

    @Before
    public void setUp() {
        calculator = new BrazilianLegalFinancialCalculator();
    }

    @Test
    public void testStandardRpvCalculation30Percent() {
        // RPV de R$ 45.000,00 com 30% de honorários contratuais
        BigDecimal gross = new BigDecimal("45000.00");
        BigDecimal rate = new BigDecimal("0.30");

        RpvCalculationResult result = calculator.calculateRpvStatement(
                gross,
                rate,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                RpvCalculationResult.FinancialStatus.LIQUIDADO
        );

        assertNotNull(result);
        assertEquals(new BigDecimal("45000.00"), result.getGrossAmount());
        assertEquals(new BigDecimal("13500.00"), result.getContractualFeeAmount());
        assertEquals(new BigDecimal("13500.00"), result.getTotalOfficeFees());
        assertEquals(new BigDecimal("31500.00"), result.getNetClientAmount());
        assertEquals(RpvCalculationResult.FinancialStatus.LIQUIDADO, result.getStatus());
    }

    @Test
    public void testRpvWithSuccumbingFeesAndExpenses() {
        // RPV R$ 100.000,00 com 20% contratuais + R$ 5.000,00 sucumbenciais + R$ 1.500,00 custas reembolsáveis + R$ 2.000,00 IRRF
        BigDecimal gross = new BigDecimal("100000.00");
        BigDecimal rate = new BigDecimal("0.20");
        BigDecimal succumbing = new BigDecimal("5000.00");
        BigDecimal tax = new BigDecimal("2000.00");
        BigDecimal expenses = new BigDecimal("1500.00");

        RpvCalculationResult result = calculator.calculateRpvStatement(
                gross,
                rate,
                succumbing,
                tax,
                expenses,
                RpvCalculationResult.FinancialStatus.REPASSADO
        );

        assertNotNull(result);
        assertEquals(new BigDecimal("100000.00"), result.getGrossAmount());
        assertEquals(new BigDecimal("20000.00"), result.getContractualFeeAmount());
        assertEquals(new BigDecimal("25000.00"), result.getTotalOfficeFees()); // 20k + 5k
        // Líquido Cliente = 100.000 - 20.000 (contratuais) - 2.000 (IRRF) - 1.500 (despesas) = 76.500,00
        assertEquals(new BigDecimal("76500.00"), result.getNetClientAmount());
        assertEquals(RpvCalculationResult.FinancialStatus.REPASSADO, result.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullGrossAmountThrowsException() {
        calculator.calculateRpvStatement(null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
    }
}
