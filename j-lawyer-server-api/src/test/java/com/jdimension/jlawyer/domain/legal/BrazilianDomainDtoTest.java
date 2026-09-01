/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal;

import com.jdimension.jlawyer.domain.legal.model.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Suíte de testes unitários para DTOs do domínio jurídico brasileiro.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianDomainDtoTest {

    @Test
    public void testCaseDetailsDto() {
        BrazilianCaseDetailsDTO caseDto = new BrazilianCaseDetailsDTO("case-999");
        caseDto.setCnjNumber("5001234-56.2024.4.03.6100");
        caseDto.setCourtCode("TRF3");
        caseDto.setJusticeSegment(4);
        caseDto.setCourtUnit("1ª Vara Cível Federal");
        caseDto.setTpuClassCode(1116);
        caseDto.setTpuClassName("Execução de Título Extrajudicial");

        List<CaseTpuSubjectDTO> subjects = new ArrayList<>();
        subjects.add(new CaseTpuSubjectDTO("case-999", 10433, "Indenização por Dano Moral", true));
        subjects.add(new CaseTpuSubjectDTO("case-999", 7780, "Inadimplemento", false));
        caseDto.setNormalizedSubjects(subjects);

        assertEquals("50012345620244036100", caseDto.getCnjNumberClean());
        assertEquals("TRF3", caseDto.getCourtCode());
        assertEquals("case-999", caseDto.getCaseId());
        assertEquals(2, caseDto.getNormalizedSubjects().size());
        assertTrue(caseDto.getNormalizedSubjects().get(0).isPrimarySubject());
        assertFalse(caseDto.getNormalizedSubjects().get(1).isPrimarySubject());
    }

    @Test
    public void testLawyerRegistrationDto() {
        LawyerRegistrationDTO oabDto = new LawyerRegistrationDTO("456789", "RJ", "SUPLEMENTAR");
        assertEquals("OAB/RJ 456789 (SUPLEMENTAR)", oabDto.getFormattedRegistration());
        assertEquals("ATIVO", oabDto.getStatus());
    }

    @Test
    public void testJudiciaryCourtDto() {
        JudiciaryCourtDTO courtDto = new JudiciaryCourtDTO("TRF3", "Tribunal Regional Federal da 3ª Região", 4, "SP");
        courtDto.setCourtType("TRIBUNAL_REGIONAL_FEDERAL");
        assertEquals("TRF3 - Tribunal Regional Federal da 3ª Região", courtDto.toString());
        assertEquals("TRIBUNAL_REGIONAL_FEDERAL", courtDto.getCourtType());
        assertTrue(courtDto.isActive());
    }

    @Test
    public void testTpuClassDto() {
        TpuClassDTO classDto = new TpuClassDTO(7, "Procedimento Comum Cível");
        classDto.setSource("CNJ_TPU");
        classDto.setSourceVersion("2026.1");
        assertEquals("7 - Procedimento Comum Cível", classDto.getFormatted());
        assertEquals(7, classDto.getCode());
        assertEquals("CNJ_TPU", classDto.getSource());
        assertEquals("2026.1", classDto.getSourceVersion());
    }

    @Test
    public void testTpuSubjectDto() {
        TpuSubjectDTO subjectDto = new TpuSubjectDTO(10433, "Indenização por Dano Moral");
        subjectDto.setSource("CNJ_TPU");
        subjectDto.setSourceVersion("2026.1");
        assertEquals("10433 - Indenização por Dano Moral", subjectDto.getFormatted());
        assertEquals(10433, subjectDto.getCode());
        assertEquals("CNJ_TPU", subjectDto.getSource());
    }

    @Test
    public void testCaseTpuSubjectDto() {
        CaseTpuSubjectDTO dto = new CaseTpuSubjectDTO("case-100", 10433, "Indenização por Dano Moral", true);
        assertEquals("10433 - Indenização por Dano Moral (Principal)", dto.toString());
        assertEquals("case-100", dto.getCaseId());
        assertEquals(10433, dto.getSubjectCode());
        assertTrue(dto.isPrimarySubject());
    }
}
