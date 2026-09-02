/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.deadline;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * Calculadora Canônica de Prazos Processuais do Direito Brasileiro (CPC/2015 - Lei nº 13.105/2015).
 *
 * Regras implementadas:
 * 1. Art. 219 CPC: Na contagem de prazo em dias, estabelecido por lei ou pelo juiz, computar-se-ão somente os dias úteis.
 * 2. Art. 220 CPC: Suspende-se o curso do prazo processual nos dias compreendidos entre 20 de dezembro e 20 de janeiro, inclusive (Recesso Forense).
 * 3. Art. 224 CPC: Salvo disposição em contrário, os prazos serão contados excluindo o dia do começo e incluindo o dia do vencimento.
 *    - § 2º: A publicação em Diário Oficial/DJEN considera-se feita no primeiro dia útil seguinte ao da disponibilização.
 *    - § 3º: A contagem do prazo terá início no primeiro dia útil que seguir ao da publicação.
 * 4. Feriados Nacionais fixos e móveis (Carnaval, Sexta-feira Santa, Páscoa, Corpus Christi).
 *
 * Guardrails de Integridade:
 * - O cálculo é estritamente assistido e requer conferência humana obrigatória.
 * - Nunca infere automaticamente prazo fatal definitivo sem validação do advogado.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianProceduralDeadlineCalculator {

    /**
     * Calcula o prazo processual em dias úteis a partir da data de disponibilização/publicação no Diário Oficial.
     *
     * @param publicationDate Data em que a comunicação/intimação foi disponibilizada
     * @param daysCount       Quantidade de dias do prazo (ex: 15 para apelação/contestação, 5 para embargos)
     * @param customHolidays  Conjunto opcional de feriados locais ou suspensões do tribunal
     * @return Resultado detalhado do cálculo com auditoria de dias úteis, feriados e recesso
     */
    public ProceduralDeadlineResult calculateDeadline(LocalDate publicationDate, int daysCount, Set<LocalDate> customHolidays) {
        if (publicationDate == null) {
            throw new IllegalArgumentException("Data de publicação não pode ser nula");
        }
        if (daysCount <= 0) {
            throw new IllegalArgumentException("Quantidade de dias do prazo deve ser maior que zero");
        }

        Set<LocalDate> allCustomHolidays = customHolidays != null ? new HashSet<>(customHolidays) : Collections.emptySet();
        List<LocalDate> skippedHolidays = new ArrayList<>();
        List<LocalDate> skippedRecessDays = new ArrayList<>();
        List<String> notes = new ArrayList<>();

        // Passo 1: Determinar o dia da publicação oficial (1º dia útil seguinte à disponibilização)
        LocalDate officialPubDate = publicationDate;
        if (!isBusinessDay(officialPubDate, allCustomHolidays)) {
            officialPubDate = getNextBusinessDay(officialPubDate, allCustomHolidays);
            notes.add(String.format("Disponibilização em dia não útil (%s). Considera-se publicada em %s.",
                    publicationDate, officialPubDate));
        }

        // Passo 2: Determinar o dia do início da contagem (1º dia útil seguinte à publicação - Art. 224, § 3º)
        LocalDate startDate = getNextBusinessDay(officialPubDate, allCustomHolidays);
        notes.add(String.format("Início da contagem do prazo no 1º dia útil seguinte (Art. 224, § 3º CPC): %s.", startDate));

        // Passo 3: Contagem iterativa de dias úteis
        LocalDate current = startDate;
        int countedDays = 0;

        while (countedDays < daysCount) {
            if (isForensicRecess(current)) {
                skippedRecessDays.add(current);
            } else if (isHoliday(current, allCustomHolidays)) {
                skippedHolidays.add(current);
            } else if (isWeekend(current)) {
                // Final de semana comum
            } else {
                // Dia útil válido computado
                countedDays++;
                if (countedDays == daysCount) {
                    break;
                }
            }
            current = current.plusDays(1);
        }

        LocalDate deadlineDate = current;

        // Passo 4: Se o último dia cair em dia não útil/suspenso por imprevisto, prorroga para o próximo dia útil
        if (!isBusinessDay(deadlineDate, allCustomHolidays)) {
            LocalDate originalDeadline = deadlineDate;
            deadlineDate = getNextBusinessDay(deadlineDate, allCustomHolidays);
            notes.add(String.format("Vencimento recaiu em dia não útil (%s). Prorrogado para o 1º dia útil subsequente (Art. 224, § 1º CPC): %s.",
                    originalDeadline, deadlineDate));
        }

        if (!skippedRecessDays.isEmpty()) {
            notes.add(String.format("Suspensão pelo Recesso Forense (Art. 220 CPC): %d dias suspensos entre 20/dez e 20/jan.", skippedRecessDays.size()));
        }

        return new ProceduralDeadlineResult(
                publicationDate,
                startDate,
                deadlineDate,
                daysCount,
                countedDays,
                true,
                skippedHolidays,
                skippedRecessDays,
                notes
        );
    }

    /**
     * Verifica se uma data é dia útil para fins processuais (segunda a sexta, fora de feriados e fora do recesso forense).
     */
    public boolean isBusinessDay(LocalDate date, Set<LocalDate> customHolidays) {
        if (date == null || isWeekend(date) || isForensicRecess(date)) {
            return false;
        }
        return !isHoliday(date, customHolidays);
    }

    /**
     * Verifica se a data recai em final de semana (Sábado ou Domingo).
     */
    public boolean isWeekend(LocalDate date) {
        if (date == null) return false;
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    /**
     * Verifica se a data está no período de Recesso Forense do Art. 220 do CPC (20 de dezembro a 20 de janeiro, inclusive).
     */
    public boolean isForensicRecess(LocalDate date) {
        if (date == null) return false;
        Month month = date.getMonth();
        int day = date.getDayOfMonth();

        // 20 a 31 de dezembro
        if (month == Month.DECEMBER && day >= 20) {
            return true;
        }
        // 1 a 20 de janeiro
        if (month == Month.JANUARY && day <= 20) {
            return true;
        }
        return false;
    }

    /**
     * Retorna o próximo dia útil subsequente à data informada.
     */
    public LocalDate getNextBusinessDay(LocalDate date, Set<LocalDate> customHolidays) {
        LocalDate next = date.plusDays(1);
        while (!isBusinessDay(next, customHolidays)) {
            next = next.plusDays(1);
        }
        return next;
    }

    /**
     * Verifica se a data é um feriado nacional (fixo ou móvel) ou feriado customizado.
     */
    public boolean isHoliday(LocalDate date, Set<LocalDate> customHolidays) {
        if (date == null) return false;
        if (customHolidays != null && customHolidays.contains(date)) {
            return true;
        }
        return getNationalHolidays(date.getYear()).contains(date);
    }

    /**
     * Retorna o conjunto de feriados nacionais brasileiros para o ano especificado.
     */
    public Set<LocalDate> getNationalHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();

        // Feriados Nacionais Fixos (Leis Federais nº 662/1949, 6.802/1980, 10.607/2002, 14.759/2023)
        holidays.add(LocalDate.of(year, Month.JANUARY, 1));   // Confraternização Universal
        holidays.add(LocalDate.of(year, Month.APRIL, 21));     // Tiradentes
        holidays.add(LocalDate.of(year, Month.MAY, 1));        // Dia do Trabalho
        holidays.add(LocalDate.of(year, Month.SEPTEMBER, 7));  // Independência do Brasil
        holidays.add(LocalDate.of(year, Month.OCTOBER, 12));   // Nossa Senhora Aparecida
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 2));   // Finados
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 15));  // Proclamação da República
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 20));  // Dia Nacional de Zumbi e da Consciência Negra (Lei 14.759)
        holidays.add(LocalDate.of(year, Month.DECEMBER, 25));  // Natal

        // Feriados Móveis baseados no Domingo de Páscoa
        LocalDate easter = calculateEasterSunday(year);
        holidays.add(easter.minusDays(48)); // Segunda-feira de Carnaval
        holidays.add(easter.minusDays(47)); // Terça-feira de Carnaval
        holidays.add(easter.minusDays(2));  // Sexta-feira Santa (Paixão de Cristo)
        holidays.add(easter.plusDays(60));  // Corpus Christi

        return holidays;
    }

    /**
     * Algoritmo de Meeus/Jones/Butcher para cálculo do Domingo de Páscoa.
     */
    private LocalDate calculateEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}
