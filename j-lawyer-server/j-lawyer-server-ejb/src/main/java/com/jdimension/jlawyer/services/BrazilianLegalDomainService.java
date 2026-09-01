/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.cnj.CnjNumber;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumberValidator;
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.persistence.*;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Implementação EJB dos serviços de domínio jurídico brasileiro:
 * Gerenciamento de inscrições OAB, detalhes processuais NPU/CNJ, catálogo canônico de tribunais e TPU.
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class BrazilianLegalDomainService implements BrazilianLegalDomainServiceRemote, BrazilianLegalDomainServiceLocal {

    private static final Logger log = Logger.getLogger(BrazilianLegalDomainService.class.getName());

    @PersistenceContext(unitName = "j-lawyer-server-ejbPU")
    private EntityManager em;

    @EJB
    private AddressBeanFacadeLocal addressFacade;

    @EJB
    private ArchiveFileBeanFacadeLocal archiveFileFacade;

    // ========================================================================
    // 1. INSCRIÇÕES OAB (Lawyer Registrations)
    // ========================================================================

    @Override
    public List<LawyerRegistrationDTO> getLawyerRegistrations(String contactId) throws Exception {
        if (contactId == null || contactId.trim().isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<BrLawyerRegistration> query = em.createNamedQuery("BrLawyerRegistration.findByContactId", BrLawyerRegistration.class);
        query.setParameter("contactId", contactId);
        List<BrLawyerRegistration> entities = query.getResultList();

        List<LawyerRegistrationDTO> dtos = new ArrayList<>();
        for (BrLawyerRegistration entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    @Override
    public LawyerRegistrationDTO saveLawyerRegistration(LawyerRegistrationDTO dto) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Registro OAB não pode ser nulo");
        }
        if (dto.getOabNumber() == null || dto.getOabUf() == null) {
            throw new IllegalArgumentException("Número e UF da OAB são obrigatórios");
        }

        BrLawyerRegistration entity;
        Date now = new Date();

        if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
            entity = em.find(BrLawyerRegistration.class, dto.getId());
            if (entity == null) {
                entity = new BrLawyerRegistration(dto.getId());
                entity.setCreationDate(now);
            }
        } else {
            entity = new BrLawyerRegistration(UUID.randomUUID().toString());
            entity.setCreationDate(now);
        }

        entity.setContactId(dto.getContactId());
        entity.setOabNumber(dto.getOabNumber().trim());
        entity.setOabUf(dto.getOabUf().trim().toUpperCase());
        entity.setOabType(dto.getOabType() != null ? dto.getOabType().trim().toUpperCase() : BrLawyerRegistration.TYPE_PRINCIPAL);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus().trim().toUpperCase() : BrLawyerRegistration.STATUS_ATIVO);
        entity.setIssuanceDate(dto.getIssuanceDate());
        entity.setSecurityCode(dto.getSecurityCode());
        entity.setNotice(dto.getNotice());
        entity.setModificationDate(now);

        em.merge(entity);
        em.flush();

        return toDTO(entity);
    }

    @Override
    public void deleteLawyerRegistration(String registrationId) throws Exception {
        if (registrationId == null || registrationId.trim().isEmpty()) {
            return;
        }
        BrLawyerRegistration entity = em.find(BrLawyerRegistration.class, registrationId);
        if (entity != null) {
            em.remove(entity);
            em.flush();
        }
    }

    // ========================================================================
    // 2. DETALHES PROCESSUAIS BRASILEIROS (Cases / Processos)
    // ========================================================================

    @Override
    public BrazilianCaseDetailsDTO getCaseDetails(String caseId) throws Exception {
        if (caseId == null || caseId.trim().isEmpty()) {
            return null;
        }

        ArchiveFileBean caseBean = archiveFileFacade.find(caseId);
        if (caseBean == null) {
            return null;
        }

        BrazilianCaseDetailsDTO dto = new BrazilianCaseDetailsDTO(caseId);
        dto.setCnjNumber(caseBean.getCnjNumber());
        dto.setCnjNumberClean(caseBean.getCnjNumberClean());
        dto.setCourtCode(caseBean.getCourtCode());
        dto.setJusticeSegment(caseBean.getJusticeSegment());
        dto.setJurisdictionDegree(caseBean.getJurisdictionDegree());
        dto.setCourtUnit(caseBean.getCourtUnit());
        dto.setComarca(caseBean.getComarca());
        dto.setJudicialSubsection(caseBean.getJudicialSubsection());
        dto.setTpuClassCode(caseBean.getTpuClassCode());
        dto.setTpuClassName(caseBean.getTpuClassName());
        dto.setTpuSubjectCodes(caseBean.getTpuSubjectCodes());
        dto.setTpuSubjectNames(caseBean.getTpuSubjectNames());
        dto.setSecrecyLevel(caseBean.getSecrecyLevel());
        dto.setDistributionDate(caseBean.getDistributionDate());
        dto.setCaseStatusBr(caseBean.getCaseStatusBr());
        dto.setProvenanceSystem(caseBean.getProvenanceSystem());

        // Enriquecer com nome do tribunal se disponível
        if (caseBean.getCourtCode() != null) {
            JudiciaryCourtDTO court = getCourtByCode(caseBean.getCourtCode());
            if (court != null) {
                dto.setCourtName(court.getName());
                dto.setSegmentName(court.getSegmentName());
            }
        }

        return dto;
    }

    @Override
    public BrazilianCaseDetailsDTO saveCaseDetails(BrazilianCaseDetailsDTO dto) throws Exception {
        if (dto == null || dto.getCaseId() == null) {
            throw new IllegalArgumentException("Dados processuais ou ID do caso não podem ser nulos");
        }

        ArchiveFileBean caseBean = archiveFileFacade.find(dto.getCaseId());
        if (caseBean == null) {
            throw new IllegalArgumentException("Processo/Caso não encontrado com ID: " + dto.getCaseId());
        }

        // Validação e normalização de NPU/CNJ se informado
        if (dto.getCnjNumber() != null && !dto.getCnjNumber().trim().isEmpty()) {
            String cnj = dto.getCnjNumber().trim();
            if (CnjNumberValidator.isValid(cnj)) {
                CnjNumber parsed = CnjNumberValidator.parse(cnj);
                caseBean.setCnjNumber(parsed.getFormatted());
                caseBean.setCnjNumberClean(parsed.getRawDigits());
                if (caseBean.getJusticeSegment() == null || caseBean.getJusticeSegment() == 0) {
                    caseBean.setJusticeSegment(parsed.getJusticeSegment());
                }
            } else {
                caseBean.setCnjNumber(cnj);
                caseBean.setCnjNumberClean(cnj.replaceAll("[^0-9]", ""));
            }
        } else {
            caseBean.setCnjNumber(null);
            caseBean.setCnjNumberClean(null);
        }

        caseBean.setCourtCode(dto.getCourtCode());
        if (dto.getJusticeSegment() != null) {
            caseBean.setJusticeSegment(dto.getJusticeSegment());
        }
        caseBean.setJurisdictionDegree(dto.getJurisdictionDegree());
        caseBean.setCourtUnit(dto.getCourtUnit());
        caseBean.setComarca(dto.getComarca());
        caseBean.setJudicialSubsection(dto.getJudicialSubsection());
        caseBean.setTpuClassCode(dto.getTpuClassCode());
        caseBean.setTpuClassName(dto.getTpuClassName());
        caseBean.setTpuSubjectCodes(dto.getTpuSubjectCodes());
        caseBean.setTpuSubjectNames(dto.getTpuSubjectNames());
        caseBean.setSecrecyLevel(dto.getSecrecyLevel());
        caseBean.setDistributionDate(dto.getDistributionDate());
        caseBean.setCaseStatusBr(dto.getCaseStatusBr());
        caseBean.setProvenanceSystem(dto.getProvenanceSystem());
        caseBean.setDateChanged(new Date());

        archiveFileFacade.edit(caseBean);
        em.flush();

        return getCaseDetails(dto.getCaseId());
    }

    @Override
    public BrazilianCaseDetailsDTO findCaseByCnjNumber(String cnjNumber) throws Exception {
        if (cnjNumber == null || cnjNumber.trim().isEmpty()) {
            return null;
        }

        String clean = cnjNumber.replaceAll("[^0-9]", "");
        TypedQuery<ArchiveFileBean> query = em.createNamedQuery("ArchiveFileBean.findByCnjNumberClean", ArchiveFileBean.class);
        query.setParameter("cnjNumberClean", clean);

        List<ArchiveFileBean> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return getCaseDetails(results.get(0).getId());
    }

    // ========================================================================
    // 3. CATÁLOGO DE TRIBUNAIS (Judiciary Courts)
    // ========================================================================

    @Override
    public List<JudiciaryCourtDTO> listCourts() throws Exception {
        TypedQuery<BrJudiciaryCourt> query = em.createNamedQuery("BrJudiciaryCourt.findAll", BrJudiciaryCourt.class);
        List<BrJudiciaryCourt> entities = query.getResultList();
        List<JudiciaryCourtDTO> dtos = new ArrayList<>();
        for (BrJudiciaryCourt c : entities) {
            dtos.add(toDTO(c));
        }
        return dtos;
    }

    @Override
    public List<JudiciaryCourtDTO> listCourtsBySegment(int justiceSegment) throws Exception {
        TypedQuery<BrJudiciaryCourt> query = em.createNamedQuery("BrJudiciaryCourt.findBySegment", BrJudiciaryCourt.class);
        query.setParameter("justiceSegment", justiceSegment);
        List<BrJudiciaryCourt> entities = query.getResultList();
        List<JudiciaryCourtDTO> dtos = new ArrayList<>();
        for (BrJudiciaryCourt c : entities) {
            dtos.add(toDTO(c));
        }
        return dtos;
    }

    @Override
    public JudiciaryCourtDTO getCourtByCode(String courtCode) throws Exception {
        if (courtCode == null || courtCode.trim().isEmpty()) {
            return null;
        }
        TypedQuery<BrJudiciaryCourt> query = em.createNamedQuery("BrJudiciaryCourt.findByCode", BrJudiciaryCourt.class);
        query.setParameter("code", courtCode.trim().toUpperCase());
        List<BrJudiciaryCourt> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return toDTO(results.get(0));
    }

    // ========================================================================
    // 4. CATÁLOGO TPU (Classes e Assuntos CNJ)
    // ========================================================================

    @Override
    public List<TpuClassDTO> listTpuClasses() throws Exception {
        TypedQuery<BrTpuClass> query = em.createNamedQuery("BrTpuClass.findAll", BrTpuClass.class);
        List<BrTpuClass> entities = query.getResultList();
        List<TpuClassDTO> dtos = new ArrayList<>();
        for (BrTpuClass c : entities) {
            dtos.add(toDTO(c));
        }
        return dtos;
    }

    @Override
    public List<TpuClassDTO> searchTpuClasses(String queryStr) throws Exception {
        if (queryStr == null || queryStr.trim().isEmpty()) {
            return listTpuClasses();
        }
        String pattern = "%" + queryStr.trim().toLowerCase() + "%";
        TypedQuery<BrTpuClass> query = em.createQuery(
            "SELECT c FROM BrTpuClass c WHERE LOWER(c.name) LIKE :p OR str(c.code) LIKE :p ORDER BY c.name",
            BrTpuClass.class
        );
        query.setParameter("p", pattern);
        query.setMaxResults(50);

        List<BrTpuClass> entities = query.getResultList();
        List<TpuClassDTO> dtos = new ArrayList<>();
        for (BrTpuClass c : entities) {
            dtos.add(toDTO(c));
        }
        return dtos;
    }

    @Override
    public List<TpuSubjectDTO> listTpuSubjects() throws Exception {
        TypedQuery<BrTpuSubject> query = em.createNamedQuery("BrTpuSubject.findAll", BrTpuSubject.class);
        List<BrTpuSubject> entities = query.getResultList();
        List<TpuSubjectDTO> dtos = new ArrayList<>();
        for (BrTpuSubject s : entities) {
            dtos.add(toDTO(s));
        }
        return dtos;
    }

    @Override
    public List<TpuSubjectDTO> searchTpuSubjects(String queryStr) throws Exception {
        if (queryStr == null || queryStr.trim().isEmpty()) {
            return listTpuSubjects();
        }
        String pattern = "%" + queryStr.trim().toLowerCase() + "%";
        TypedQuery<BrTpuSubject> query = em.createQuery(
            "SELECT s FROM BrTpuSubject s WHERE LOWER(s.name) LIKE :p OR str(s.code) LIKE :p ORDER BY s.name",
            BrTpuSubject.class
        );
        query.setParameter("p", pattern);
        query.setMaxResults(50);

        List<BrTpuSubject> entities = query.getResultList();
        List<TpuSubjectDTO> dtos = new ArrayList<>();
        for (BrTpuSubject s : entities) {
            dtos.add(toDTO(s));
        }
        return dtos;
    }

    // ========================================================================
    // MAPPERS PRIVADOS
    // ========================================================================

    private LawyerRegistrationDTO toDTO(BrLawyerRegistration e) {
        LawyerRegistrationDTO dto = new LawyerRegistrationDTO();
        dto.setId(e.getId());
        dto.setContactId(e.getContactId());
        dto.setOabNumber(e.getOabNumber());
        dto.setOabUf(e.getOabUf());
        dto.setOabType(e.getOabType());
        dto.setStatus(e.getStatus());
        dto.setIssuanceDate(e.getIssuanceDate());
        dto.setSecurityCode(e.getSecurityCode());
        dto.setNotice(e.getNotice());
        dto.setCreationDate(e.getCreationDate());
        dto.setModificationDate(e.getModificationDate());
        return dto;
    }

    private JudiciaryCourtDTO toDTO(BrJudiciaryCourt e) {
        JudiciaryCourtDTO dto = new JudiciaryCourtDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setName(e.getName());
        dto.setJusticeSegment(e.getJusticeSegment());
        dto.setSegmentName(e.getSegmentName());
        dto.setUf(e.getUf());
        dto.setCourtNumber(e.getCourtNumber());
        dto.setDatajudCode(e.getDatajudCode());
        dto.setDjenCode(e.getDjenCode());
        dto.setElectronicPortalUrl(e.getElectronicPortalUrl());
        dto.setActive(e.isActive());
        return dto;
    }

    private TpuClassDTO toDTO(BrTpuClass e) {
        TpuClassDTO dto = new TpuClassDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setName(e.getName());
        dto.setGlossary(e.getGlossary());
        dto.setNature(e.getNature());
        dto.setActive(e.isActive());
        return dto;
    }

    private TpuSubjectDTO toDTO(BrTpuSubject e) {
        TpuSubjectDTO dto = new TpuSubjectDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setName(e.getName());
        dto.setParentCode(e.getParentCode());
        dto.setGlossary(e.getGlossary());
        dto.setActive(e.isActive());
        return dto;
    }
}
