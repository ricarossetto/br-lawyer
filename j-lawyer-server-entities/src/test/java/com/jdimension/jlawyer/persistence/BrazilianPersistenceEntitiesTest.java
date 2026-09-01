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
import java.util.Date;
import static org.junit.Assert.*;

/**
 * Suíte de testes para entidades JPA do modelo de domínio jurídico brasileiro.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianPersistenceEntitiesTest {

    @Test
    public void testAddressBeanBrazilianFields() {
        AddressBean contact = new AddressBean("addr-001");
        contact.setCpf("123.456.789-00");
        contact.setCnpj("12.345.678/0001-90");
        contact.setRg("12.345.678-9");
        contact.setPersonType("PF");
        contact.setTradeName("Rossetto Advocacia & Consultoria");
        contact.setFantasyName("Rossetto Advogados");
        contact.setStateRegistration("123456789");
        contact.setMunicipalRegistration("987654321");

        assertEquals("123.456.789-00", contact.getCpf());
        assertEquals("12.345.678/0001-90", contact.getCnpj());
        assertEquals("12.345.678-9", contact.getRg());
        assertEquals("PF", contact.getPersonType());
        assertEquals("Rossetto Advocacia & Consultoria", contact.getTradeName());
        assertEquals("Rossetto Advogados", contact.getFantasyName());
        assertEquals("123456789", contact.getStateRegistration());
        assertEquals("987654321", contact.getMunicipalRegistration());
    }

    @Test
    public void testArchiveFileBeanBrazilianFields() {
        ArchiveFileBean legalCase = new ArchiveFileBean("case-001");
        legalCase.setCnjNumber("0001234-08.2023.8.26.0100");
        legalCase.setCourtCode("TJSP");
        legalCase.setJusticeSegment(8);
        legalCase.setJurisdictionDegree("G1");
        legalCase.setCourtUnit("2ª Vara Cível");
        legalCase.setComarca("São Paulo - Foro Central");
        legalCase.setTpuClassCode(7);
        legalCase.setTpuClassName("Procedimento Comum Cível");
        legalCase.setTpuSubjectCodes("10433,7780");
        legalCase.setTpuSubjectNames("Indenização por Dano Moral, Inadimplemento");
        legalCase.setSecrecyLevel(false);
        legalCase.setCaseStatusBr("EM_ANDAMENTO");
        legalCase.setDistributionDate(new Date());

        assertEquals("0001234-08.2023.8.26.0100", legalCase.getCnjNumber());
        assertEquals("00012340820238260100", legalCase.getCnjNumberClean());
        assertEquals("TJSP", legalCase.getCourtCode());
        assertEquals(Integer.valueOf(8), legalCase.getJusticeSegment());
        assertEquals("G1", legalCase.getJurisdictionDegree());
        assertEquals("2ª Vara Cível", legalCase.getCourtUnit());
        assertEquals("São Paulo - Foro Central", legalCase.getComarca());
        assertEquals(Integer.valueOf(7), legalCase.getTpuClassCode());
        assertEquals("Procedimento Comum Cível", legalCase.getTpuClassName());
        assertEquals("10433,7780", legalCase.getTpuSubjectCodes());
        assertEquals(Boolean.FALSE, legalCase.getSecrecyLevel());
        assertEquals("EM_ANDAMENTO", legalCase.getCaseStatusBr());
    }

    @Test
    public void testLawyerRegistrationEntity() {
        BrLawyerRegistration oab = new BrLawyerRegistration("oab-001", "123456", "SP");
        oab.setContactId("contact-123");
        oab.setOabType(BrLawyerRegistration.TYPE_PRINCIPAL);
        oab.setStatus(BrLawyerRegistration.STATUS_ATIVO);

        assertEquals("OAB/SP 123456", oab.getFormattedRegistration());
        assertEquals("contact-123", oab.getContactId());
        assertEquals(BrLawyerRegistration.STATUS_ATIVO, oab.getStatus());
    }

    @Test
    public void testJudiciaryCourtEntity() {
        BrJudiciaryCourt court = new BrJudiciaryCourt("court-tjsp", "TJSP", "Tribunal de Justiça de São Paulo", BrJudiciaryCourt.SEGMENT_ESTADUAL);
        court.setUf("SP");
        court.setDatajudCode("api_publica_tjsp");
        court.setDjenCode("TJSP");

        assertEquals("TJSP", court.getCode());
        assertEquals("Tribunal de Justiça de São Paulo", court.getName());
        assertEquals(8, court.getJusticeSegment());
        assertEquals("SP", court.getUf());
        assertTrue(court.isActive());
    }

    @Test
    public void testTpuEntities() {
        BrTpuClass tpuClass = new BrTpuClass("tpu-c-7", 7, "Procedimento Comum Cível");
        tpuClass.setNature("CIVEL");

        BrTpuSubject tpuSubject = new BrTpuSubject("tpu-s-10433", 10433, "Indenização por Dano Moral");
        tpuSubject.setParentCode(9518);

        assertEquals(7, tpuClass.getCode());
        assertEquals("Procedimento Comum Cível", tpuClass.getName());
        assertEquals("CIVEL", tpuClass.getNature());

        assertEquals(10433, tpuSubject.getCode());
        assertEquals("Indenização por Dano Moral", tpuSubject.getName());
        assertEquals(Integer.valueOf(9518), tpuSubject.getParentCode());
    }
}
