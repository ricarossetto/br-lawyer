/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.deadline;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Testes Unitários Canônicos da Calculadora de Prazos Processuais do CPC/2015.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianProceduralDeadlineCalculatorTest {

    private BrazilianProceduralDeadlineCalculator calculator;

    @Before
    public void setUp() {
        calculator = new BrazilianProceduralDeadlineCalculator();
    }

    @Test
    public void testStandard15DayBusinessDaysCalculation() {
        // Terça-feira, 01/08/2023 (Disponibilização)
        LocalDate publicationDate = LocalDate.of(2023, Month.AUGUST, 1);
        
        // 1º dia útil seguinte é Quarta-feira 02/08/2023 (Início da contagem)
        // 15 dias úteis sem feriados federais em agosto:
        // Semana 1: 02(Qua), 03(Qui), 04(Sex) -> 3 dias
        // Semana 2: 07(Seg) a 11(Sex) -> 5 dias (total 8)
        // Semana 3: 14(Seg) a 18(Sex) -> 5 dias (total 13)
        // Semana 4: 21(Seg), 22(Ter) -> 2 dias (total 15)
        // Vencimento esperado: Terça-feira, 22/08/2023
        
        ProceduralDeadlineResult result = calculator.calculateDeadline(publicationDate, 15, Collections.emptySet());

        assertNotNull(result);
        assertEquals(LocalDate.of(2023, Month.AUGUST, 1), result.getPublicationDate());
        assertEquals(LocalDate.of(2023, Month.AUGUST, 2), result.getStartDate());
        assertEquals(LocalDate.of(2023, Month.AUGUST, 22), result.getDeadlineDate());
        assertEquals(15, result.getRequestedDays());
        assertEquals(15, result.getElapsedBusinessDays());
        assertTrue(result.isBusinessDays());
        assertTrue("Guardrail: confirmação humana sempre obrigatória", result.isRequiresHumanConfirmation());
    }

    @Test
    public void testPublicationOnFridayStartsOnTuesday() {
        // Sexta-feira, 04/08/2023 (Disponibilização)
        LocalDate pubDate = LocalDate.of(2023, Month.AUGUST, 4);

        // Publicação considerada em 04/08 (dia útil).
        // 1º dia útil seguinte é Segunda-feira 07/08/2023 (Início da contagem - Art. 224, § 3º)
        ProceduralDeadlineResult result = calculator.calculateDeadline(pubDate, 5, Collections.emptySet());

        assertEquals(LocalDate.of(2023, Month.AUGUST, 7), result.getStartDate());
        // 5 dias úteis: 07(Seg), 08(Ter), 09(Qua), 10(Qui), 11(Sex)
        assertEquals(LocalDate.of(2023, Month.AUGUST, 11), result.getDeadlineDate());
    }

    @Test
    public void testArt220CpcForensicRecessSuspension() {
        // Intimação disponibilizada em 15/12/2026 (Terça-feira)
        // Início: 16/12/2026 (Quarta-feira)
        // Dias antes do recesso: 16(Qua), 17(Qui), 18(Sex), 19(Sáb - fds) -> 3 dias úteis computados (16, 17, 18).
        // 20/12/2026 a 20/01/2027: Suspenso pelo Recesso Forense (Art. 220 CPC).
        // Reinício: 21/01/2027 (Quinta-feira)
        // Faltam 12 dias para completar 15 dias:
        // Jan 2027: 21(Qui), 22(Sex) -> 2 dias (total 5)
        // 25(Seg) a 29(Sex) -> 5 dias (total 10)
        // Fev 2027: 01(Seg) a 05(Sex) -> 5 dias (total 15)
        // Vencimento esperado: Sexta-feira, 05/02/2027

        LocalDate pubDate = LocalDate.of(2026, Month.DECEMBER, 15);
        ProceduralDeadlineResult result = calculator.calculateDeadline(pubDate, 15, Collections.emptySet());

        assertNotNull(result);
        assertEquals(LocalDate.of(2026, Month.DECEMBER, 16), result.getStartDate());
        assertEquals(LocalDate.of(2027, Month.FEBRUARY, 5), result.getDeadlineDate());
        assertFalse("Deve registrar dias do recesso suspensos", result.getSkippedRecessDays().isEmpty());
    }

    @Test
    public void testNationalHolidaysSkipped() {
        // Ano 2024: 15 de Novembro (Proclamação da República - Sexta) e 20 de Novembro (Zumbi dos Palmares - Quarta)
        // Publicação em 13/11/2024 (Quarta) -> Início 14/11/2024 (Quinta)
        // Prazo de 5 dias úteis:
        // 14/11 (Qui) -> Dia 1
        // 15/11 (Sex - Feriado Proclamação) -> Pula
        // 16/11 (Sáb) / 17/11 (Dom) -> Pula
        // 18/11 (Seg) -> Dia 2
        // 19/11 (Ter) -> Dia 3
        // 20/11 (Qua - Feriado Zumbi Lei 14.759) -> Pula
        // 21/11 (Qui) -> Dia 4
        // 22/11 (Sex) -> Dia 5
        // Vencimento esperado: 22/11/2024

        LocalDate pubDate = LocalDate.of(2024, Month.NOVEMBER, 13);
        ProceduralDeadlineResult result = calculator.calculateDeadline(pubDate, 5, Collections.emptySet());

        assertEquals(LocalDate.of(2024, Month.NOVEMBER, 14), result.getStartDate());
        assertEquals(LocalDate.of(2024, Month.NOVEMBER, 22), result.getDeadlineDate());
        assertTrue(result.getSkippedHolidays().contains(LocalDate.of(2024, Month.NOVEMBER, 15)));
        assertTrue(result.getSkippedHolidays().contains(LocalDate.of(2024, Month.NOVEMBER, 20)));
    }

    @Test
    public void testCustomTribunalSuspension() {
        // Terça-feira, 01/08/2023 -> Prazo de 3 dias
        // Suspensão customizada pelo Tribunal no dia 03/08/2023
        Set<LocalDate> customHolidays = new HashSet<>();
        customHolidays.add(LocalDate.of(2023, Month.AUGUST, 3));

        LocalDate pubDate = LocalDate.of(2023, Month.AUGUST, 1);
        ProceduralDeadlineResult result = calculator.calculateDeadline(pubDate, 3, customHolidays);

        // Início: 02/08 (Dia 1)
        // 03/08: Suspenso pelo tribunal (Pula)
        // 04/08: (Dia 2)
        // 07/08 (Seg): (Dia 3)
        // Vencimento esperado: Segunda-feira 07/08/2023
        assertEquals(LocalDate.of(2023, Month.AUGUST, 7), result.getDeadlineDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDateThrowsException() {
        calculator.calculateDeadline(null, 15, Collections.emptySet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroDaysThrowsException() {
        calculator.calculateDeadline(LocalDate.now(), 0, Collections.emptySet());
    }
}
