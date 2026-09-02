/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.deadline;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Resultado do cálculo de prazo processual sob o Código de Processo Civil (CPC/2015).
 *
 * Conforme o guardrail do ATRIUM / BR-LAWYER:
 * - O prazo calculado é sempre uma estimativa assistida.
 * - Exige conferência e confirmação humana antes de se tornar prazo fatal definitivo.
 *
 * @author BR-LAWYER Team
 */
public final class ProceduralDeadlineResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDate publicationDate;
    private final LocalDate startDate;
    private final LocalDate deadlineDate;
    private final int requestedDays;
    private final int elapsedBusinessDays;
    private final boolean isBusinessDays;
    private final List<LocalDate> skippedHolidays;
    private final List<LocalDate> skippedRecessDays;
    private final List<String> notes;
    private final boolean requiresHumanConfirmation;

    public ProceduralDeadlineResult(LocalDate publicationDate,
                                    LocalDate startDate,
                                    LocalDate deadlineDate,
                                    int requestedDays,
                                    int elapsedBusinessDays,
                                    boolean isBusinessDays,
                                    List<LocalDate> skippedHolidays,
                                    List<LocalDate> skippedRecessDays,
                                    List<String> notes) {
        this.publicationDate = publicationDate;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.requestedDays = requestedDays;
        this.elapsedBusinessDays = elapsedBusinessDays;
        this.isBusinessDays = isBusinessDays;
        this.skippedHolidays = skippedHolidays != null ? Collections.unmodifiableList(skippedHolidays) : Collections.emptyList();
        this.skippedRecessDays = skippedRecessDays != null ? Collections.unmodifiableList(skippedRecessDays) : Collections.emptyList();
        this.notes = notes != null ? Collections.unmodifiableList(notes) : Collections.emptyList();
        this.requiresHumanConfirmation = true; // Guardrail obrigatório
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public int getRequestedDays() {
        return requestedDays;
    }

    public int getElapsedBusinessDays() {
        return elapsedBusinessDays;
    }

    public boolean isBusinessDays() {
        return isBusinessDays;
    }

    public List<LocalDate> getSkippedHolidays() {
        return skippedHolidays;
    }

    public List<LocalDate> getSkippedRecessDays() {
        return skippedRecessDays;
    }

    public List<String> getNotes() {
        return notes;
    }

    public boolean isRequiresHumanConfirmation() {
        return requiresHumanConfirmation;
    }

    @Override
    public String toString() {
        return "ProceduralDeadlineResult{" +
                "publicationDate=" + publicationDate +
                ", startDate=" + startDate +
                ", deadlineDate=" + deadlineDate +
                ", requestedDays=" + requestedDays +
                ", isBusinessDays=" + isBusinessDays +
                ", skippedHolidays=" + skippedHolidays.size() +
                ", skippedRecessDays=" + skippedRecessDays.size() +
                ", requiresHumanConfirmation=" + requiresHumanConfirmation +
                '}';
    }
}
