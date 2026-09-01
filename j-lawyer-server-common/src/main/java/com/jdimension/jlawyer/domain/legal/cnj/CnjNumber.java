/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.cnj;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa a Numeração Processual Única (NPU) do Poder Judiciário Brasileiro,
 * conforme estabelecido pela Resolução CNJ nº 65/2008.
 *
 * Formato padrão: NNNNNNN-DD.AAAA.J.TR.OOOO
 * Onde:
 * - NNNNNNN (7 dígitos): Número sequencial do processo no ano e unidade de origem
 * - DD (2 dígitos): Dígito verificador (ISO 7064 Módulo 97 Base 10)
 * - AAAA (4 dígitos): Ano do ajuizamento da ação
 * - J (1 dígito): Segmento de Justiça (1=STF, 2=CNJ, 3=STJ, 4=Federal, 5=Trabalho, 6=Eleitoral, 7=Militar União, 8=Estadual, 9=Militar Estadual)
 * - TR (2 dígitos): Tribunal do respectivo segmento de justiça
 * - OOOO (4 dígitos): Unidade de origem do processo (Comarca / Vara)
 *
 * @author BR-LAWYER Team
 */
public final class CnjNumber implements Serializable, Comparable<CnjNumber> {

    private static final long serialVersionUID = 1L;

    private final String sequentialNumber; // NNNNNNN (7 dígitos)
    private final String checkDigit;       // DD (2 dígitos)
    private final int year;                // AAAA (4 dígitos)
    private final int justiceSegment;      // J (1 dígito)
    private final int courtNumber;         // TR (2 dígitos)
    private final String originUnit;       // OOOO (4 dígitos)
    private final String rawDigits;        // 20 dígitos numéricos
    private final String formatted;        // NNNNNNN-DD.AAAA.J.TR.OOOO

    public CnjNumber(String sequentialNumber, String checkDigit, int year, int justiceSegment, int courtNumber, String originUnit) {
        this.sequentialNumber = String.format("%07d", Long.parseLong(sequentialNumber));
        this.checkDigit = String.format("%02d", Integer.parseInt(checkDigit));
        this.year = year;
        this.justiceSegment = justiceSegment;
        this.courtNumber = courtNumber;
        this.originUnit = String.format("%04d", Integer.parseInt(originUnit));
        
        this.rawDigits = this.sequentialNumber + this.checkDigit + this.year + this.justiceSegment + String.format("%02d", this.courtNumber) + this.originUnit;
        this.formatted = String.format("%s-%s.%04d.%d.%02d.%s", this.sequentialNumber, this.checkDigit, this.year, this.justiceSegment, this.courtNumber, this.originUnit);
    }

    public String getSequentialNumber() {
        return sequentialNumber;
    }

    public String getCheckDigit() {
        return checkDigit;
    }

    public int getYear() {
        return year;
    }

    public int getJusticeSegment() {
        return justiceSegment;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public String getOriginUnit() {
        return originUnit;
    }

    public String getRawDigits() {
        return rawDigits;
    }

    public String getFormatted() {
        return formatted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CnjNumber cnjNumber = (CnjNumber) o;
        return rawDigits.equals(cnjNumber.rawDigits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawDigits);
    }

    @Override
    public String toString() {
        return formatted;
    }

    @Override
    public int compareTo(CnjNumber other) {
        return this.rawDigits.compareTo(other.rawDigits);
    }
}
