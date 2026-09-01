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

        assertEquals("50012345620244036100", caseDto.getCnjNumberClean());
        assertEquals("TRF3", caseDto.getCourtCode());
        assertEquals("case-999", caseDto.getCaseId());
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
        assertEquals("TRF3 - Tribunal Regional Federal da 3ª Região", courtDto.toString());
        assertTrue(courtDto.isActive());
    }

    @Test
    public void testTpuClassDto() {
        TpuClassDTO classDto = new TpuClassDTO(7, "Procedimento Comum Cível");
        assertEquals("7 - Procedimento Comum Cível", classDto.getFormatted());
        assertEquals(7, classDto.getCode());
    }

    @Test
    public void testTpuSubjectDto() {
        TpuSubjectDTO subjectDto = new TpuSubjectDTO(10433, "Indenização por Dano Moral");
        assertEquals("10433 - Indenização por Dano Moral", subjectDto.getFormatted());
        assertEquals(10433, subjectDto.getCode());
    }
}
