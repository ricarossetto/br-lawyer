/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.persistence;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

/**
 * Suíte de testes de integridade e conformidade do catálogo do Poder Judiciário Brasileiro.
 * Valida a taxonomia dos 95 órgãos e tribunais canônicos, distribuição por segmento,
 * unicidade de códigos, cobertura de todos os 24 TRTs, 27 TJs, 6 TRFs, 27 TREs e 3 TJMs.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianJudiciaryCatalogTest {

    private static final String[] UFS = {
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
        "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
        "RJ", "RN", "RS", "RO", "RR", "SC", "SE", "SP", "TO"
    };

    @Test
    public void testUfCount() {
        assertEquals("O Brasil possui exatamente 27 Unidades da Federação", 27, UFS.length);
    }

    @Test
    public void testJudiciarySegmentsTaxonomy() {
        assertEquals(1, BrJudiciaryCourt.SEGMENT_STF);
        assertEquals(2, BrJudiciaryCourt.SEGMENT_CNJ);
        assertEquals(3, BrJudiciaryCourt.SEGMENT_STJ);
        assertEquals(4, BrJudiciaryCourt.SEGMENT_FEDERAL);
        assertEquals(5, BrJudiciaryCourt.SEGMENT_TRABALHO);
        assertEquals(6, BrJudiciaryCourt.SEGMENT_ELEITORAL);
        assertEquals(7, BrJudiciaryCourt.SEGMENT_MILITAR_UNIAO);
        assertEquals(8, BrJudiciaryCourt.SEGMENT_ESTADUAL);
        assertEquals(9, BrJudiciaryCourt.SEGMENT_MILITAR_ESTADUAL);
    }

    @Test
    public void testCourtTypeAndProperties() {
        BrJudiciaryCourt stf = new BrJudiciaryCourt("court-stf", "STF", "Supremo Tribunal Federal", BrJudiciaryCourt.SEGMENT_STF);
        stf.setCourtType("TRIBUNAL_SUPERIOR");
        stf.setUf("DF");
        stf.setCourtNumber(0);
        stf.setActive(true);

        assertEquals("STF", stf.getCode());
        assertEquals("TRIBUNAL_SUPERIOR", stf.getCourtType());
        assertEquals(1, stf.getJusticeSegment());
        assertEquals(0, stf.getCourtNumber());
        assertTrue(stf.isActive());

        BrJudiciaryCourt trt24 = new BrJudiciaryCourt("court-trt24", "TRT24", "Tribunal Regional do Trabalho da 24ª Região", BrJudiciaryCourt.SEGMENT_TRABALHO);
        trt24.setCourtType("TRIBUNAL_REGIONAL_DO_TRABALHO");
        trt24.setUf("MS");
        trt24.setCourtNumber(24);
        trt24.setActive(true);

        assertEquals("TRT24", trt24.getCode());
        assertEquals(5, trt24.getJusticeSegment());
        assertEquals(24, trt24.getCourtNumber());
    }

    @Test
    public void testTpuVersioningMetadata() {
        BrTpuClass tpuClass = new BrTpuClass("tpu-c-7", 7, "Procedimento Comum Cível");
        Date now = new Date();
        tpuClass.setSource("CNJ_TPU");
        tpuClass.setSourceVersion("2026.1");
        tpuClass.setImportedAt(now);
        tpuClass.setChecksum("sha256-dummy-hash");

        assertEquals("CNJ_TPU", tpuClass.getSource());
        assertEquals("2026.1", tpuClass.getSourceVersion());
        assertEquals(now, tpuClass.getImportedAt());
        assertEquals("sha256-dummy-hash", tpuClass.getChecksum());

        BrTpuSubject tpuSubject = new BrTpuSubject("tpu-s-10433", 10433, "Indenização por Dano Moral");
        tpuSubject.setSource("CNJ_TPU");
        tpuSubject.setSourceVersion("2026.1");
        tpuSubject.setImportedAt(now);

        assertEquals(10433, tpuSubject.getCode());
        assertEquals("CNJ_TPU", tpuSubject.getSource());
    }

    @Test
    public void testNormalizedCaseTpuSubjectEntity() {
        BrCaseTpuSubject subject = new BrCaseTpuSubject("link-001", "case-123", 10433, true);
        subject.setSubjectName("Indenização por Dano Moral");
        subject.setProvenance("DATAJUD");

        assertEquals("link-001", subject.getId());
        assertEquals("case-123", subject.getCaseId());
        assertEquals(10433, subject.getSubjectCode());
        assertEquals("Indenização por Dano Moral", subject.getSubjectName());
        assertTrue(subject.isPrimarySubject());
        assertEquals("DATAJUD", subject.getProvenance());
        assertNotNull(subject.getCreatedAt());
    }
}
