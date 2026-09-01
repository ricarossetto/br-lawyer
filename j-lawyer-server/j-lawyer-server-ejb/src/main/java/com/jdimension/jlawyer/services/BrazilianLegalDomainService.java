/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumberValidator;
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.persistence.*;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementação EJB para gerenciamento do domínio jurídico brasileiro (OAB, Processos, Tribunais e TPU).
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class BrazilianLegalDomainService implements BrazilianLegalDomainServiceLocal, BrazilianLegalDomainServiceRemote {

    private static final Logger log = Logger.getLogger(BrazilianLegalDomainService.class.getName());

    @PersistenceContext(unitName = "j-lawyer-server-entitiesPU")
    private EntityManager em;

    // --- INSCRIÇÕES OAB ---

    @Override
    public List<LawyerRegistrationDTO> getLawyerRegistrations(String contactId) throws Exception {
        if (contactId == null || contactId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<BrLawyerRegistration> query = em.createNamedQuery("BrLawyerRegistration.findByContactId", BrLawyerRegistration.class);
        query.setParameter("contactId", contactId);
        List<BrLawyerRegistration> entities = query.getResultList();
        List<LawyerRegistrationDTO> dtos = new ArrayList<>(entities.size());
        for (BrLawyerRegistration entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    @Override
    public LawyerRegistrationDTO saveLawyerRegistration(LawyerRegistrationDTO dto) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da inscrição OAB não podem ser nulos");
        }
        if (dto.getOabNumber() == null || dto.getOabNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Número da OAB é obrigatório");
        }
        if (dto.getOabUf() == null || dto.getOabUf().trim().length() != 2) {
            throw new IllegalArgumentException("UF da OAB deve ter exatamente 2 caracteres");
        }

        BrLawyerRegistration entity;
        if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
            entity = em.find(BrLawyerRegistration.class, dto.getId());
            if (entity == null) {
                entity = new BrLawyerRegistration(dto.getId());
            }
        } else {
            entity = new BrLawyerRegistration(UUID.randomUUID().toString());
        }

        entity.setContactId(dto.getContactId());
        entity.setOabNumber(dto.getOabNumber().trim());
        entity.setOabUf(dto.getOabUf().trim().toUpperCase());
        entity.setOabType(dto.getOabType() != null ? dto.getOabType().trim().toUpperCase() : BrLawyerRegistration.TYPE_PRINCIPAL);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus().trim().toUpperCase() : BrLawyerRegistration.STATUS_ATIVO);
        entity.setNotice(dto.getNotice());
        entity.setIssuanceDate(dto.getIssuanceDate());

        if (dto.getId() == null || dto.getId().trim().isEmpty()) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
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

    // --- DETALHES PROCESSUAIS (CASES) ---

    @Override
    public BrazilianCaseDetailsDTO getCaseDetails(String caseId) throws Exception {
        if (caseId == null || caseId.trim().isEmpty()) {
            return null;
        }
        ArchiveFileBean legalCase = em.find(ArchiveFileBean.class, caseId);
        if (legalCase == null) {
            return null;
        }
        BrazilianCaseDetailsDTO dto = toDTO(legalCase);
        dto.setNormalizedSubjects(getCaseTpuSubjects(caseId));
        return dto;
    }

    @Override
    public BrazilianCaseDetailsDTO saveCaseDetails(BrazilianCaseDetailsDTO dto) throws Exception {
        if (dto == null || dto.getCaseId() == null || dto.getCaseId().trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador do processo (caseId) é obrigatório");
        }
        ArchiveFileBean legalCase = em.find(ArchiveFileBean.class, dto.getCaseId());
        if (legalCase == null) {
            throw new IllegalArgumentException("Processo não localizado para o ID: " + dto.getCaseId());
        }

        // Validação e normalização de NPU/CNJ se informado
        if (dto.getCnjNumber() != null && !dto.getCnjNumber().trim().isEmpty()) {
            String clean = BrazilianDocumentValidator.unmask(dto.getCnjNumber());
            if (!CnjNumberValidator.isValid(clean)) {
                throw new IllegalArgumentException("Número CNJ/NPU inválido: " + dto.getCnjNumber());
            }
            legalCase.setCnjNumber(BrazilianDocumentValidator.formatCnj(clean));
            legalCase.setCnjNumberClean(clean);
            if (legalCase.getJusticeSegment() == null && clean.length() == 20) {
                legalCase.setJusticeSegment(Integer.parseInt(clean.substring(13, 14)));
            }
        } else {
            legalCase.setCnjNumber(null);
            legalCase.setCnjNumberClean(null);
        }

        legalCase.setCourtCode(dto.getCourtCode());
        legalCase.setJusticeSegment(dto.getJusticeSegment());
        legalCase.setJurisdictionDegree(dto.getJurisdictionDegree());
        legalCase.setCourtUnit(dto.getCourtUnit());
        legalCase.setComarca(dto.getComarca());
        legalCase.setJudicialSubsection(dto.getJudicialSubsection());
        legalCase.setTpuClassCode(dto.getTpuClassCode());
        legalCase.setTpuClassName(dto.getTpuClassName());
        legalCase.setTpuSubjectCodes(dto.getTpuSubjectCodes());
        legalCase.setTpuSubjectNames(dto.getTpuSubjectNames());
        legalCase.setSecrecyLevel(dto.getSecrecyLevel() != null ? dto.getSecrecyLevel() : false);
        legalCase.setDistributionDate(dto.getDistributionDate());
        legalCase.setCaseStatusBr(dto.getCaseStatusBr());
        legalCase.setProvenanceSystem(dto.getProvenanceSystem());

        legalCase = em.merge(legalCase);

        // Atualização dos assuntos normalizados se fornecidos
        if (dto.getNormalizedSubjects() != null) {
            setCaseTpuSubjects(dto.getCaseId(), dto.getNormalizedSubjects());
        }

        em.flush();
        BrazilianCaseDetailsDTO saved = toDTO(legalCase);
        saved.setNormalizedSubjects(getCaseTpuSubjects(dto.getCaseId()));
        return saved;
    }

    @Override
    public BrazilianCaseDetailsDTO findCaseByCnjNumber(String cnjNumber) throws Exception {
        if (cnjNumber == null || cnjNumber.trim().isEmpty()) {
            return null;
        }
        String clean = BrazilianDocumentValidator.unmask(cnjNumber);
        TypedQuery<ArchiveFileBean> query = em.createNamedQuery("ArchiveFileBean.findByCnjNumberClean", ArchiveFileBean.class);
        query.setParameter("cnjNumberClean", clean);
        List<ArchiveFileBean> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        ArchiveFileBean legalCase = results.get(0);
        BrazilianCaseDetailsDTO dto = toDTO(legalCase);
        dto.setNormalizedSubjects(getCaseTpuSubjects(legalCase.getId()));
        return dto;
    }

    // --- RELACIONAMENTO NORMALIZADO: PROCESSO ↔ ASSUNTOS TPU ---

    @Override
    public List<CaseTpuSubjectDTO> getCaseTpuSubjects(String caseId) throws Exception {
        if (caseId == null || caseId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<BrCaseTpuSubject> query = em.createNamedQuery("BrCaseTpuSubject.findByCaseId", BrCaseTpuSubject.class);
        query.setParameter("caseId", caseId);
        List<BrCaseTpuSubject> entities = query.getResultList();
        List<CaseTpuSubjectDTO> dtos = new ArrayList<>(entities.size());
        for (BrCaseTpuSubject entity : entities) {
            CaseTpuSubjectDTO dto = new CaseTpuSubjectDTO();
            dto.setId(entity.getId());
            dto.setCaseId(entity.getCaseId());
            dto.setSubjectCode(entity.getSubjectCode());
            dto.setSubjectId(entity.getSubjectId());
            dto.setSubjectName(entity.getSubjectName());
            dto.setPrimarySubject(entity.isPrimarySubject());
            dto.setProvenance(entity.getProvenance());
            dto.setCreatedAt(entity.getCreatedAt());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void setCaseTpuSubjects(String caseId, List<CaseTpuSubjectDTO> subjects) throws Exception {
        if (caseId == null || caseId.trim().isEmpty()) {
            return;
        }
        // Remove associações anteriores
        TypedQuery<BrCaseTpuSubject> existingQuery = em.createNamedQuery("BrCaseTpuSubject.findByCaseId", BrCaseTpuSubject.class);
        existingQuery.setParameter("caseId", caseId);
        List<BrCaseTpuSubject> existing = existingQuery.getResultList();
        for (BrCaseTpuSubject old : existing) {
            em.remove(old);
        }

        // Insere novas
        if (subjects != null) {
            Date now = new Date();
            for (CaseTpuSubjectDTO dto : subjects) {
                BrCaseTpuSubject entity = new BrCaseTpuSubject();
                entity.setId(UUID.randomUUID().toString());
                entity.setCaseId(caseId);
                entity.setSubjectCode(dto.getSubjectCode());
                entity.setSubjectId(dto.getSubjectId());
                entity.setSubjectName(dto.getSubjectName());
                entity.setPrimarySubject(dto.isPrimarySubject());
                entity.setProvenance(dto.getProvenance() != null ? dto.getProvenance() : "MANUAL");
                entity.setCreatedAt(now);
                em.persist(entity);
            }
        }
        em.flush();
    }

    // --- CATÁLOGO DE TRIBUNAIS ---

    @Override
    public List<JudiciaryCourtDTO> listCourts() throws Exception {
        TypedQuery<BrJudiciaryCourt> query = em.createNamedQuery("BrJudiciaryCourt.findAll", BrJudiciaryCourt.class);
        List<BrJudiciaryCourt> entities = query.getResultList();
        List<JudiciaryCourtDTO> dtos = new ArrayList<>(entities.size());
        for (BrJudiciaryCourt court : entities) {
            dtos.add(toDTO(court));
        }
        return dtos;
    }

    @Override
    public List<JudiciaryCourtDTO> listCourtsBySegment(int justiceSegment) throws Exception {
        TypedQuery<BrJudiciaryCourt> query = em.createNamedQuery("BrJudiciaryCourt.findBySegment", BrJudiciaryCourt.class);
        query.setParameter("justiceSegment", justiceSegment);
        List<BrJudiciaryCourt> entities = query.getResultList();
        List<JudiciaryCourtDTO> dtos = new ArrayList<>(entities.size());
        for (BrJudiciaryCourt court : entities) {
            dtos.add(toDTO(court));
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
        return results.isEmpty() ? null : toDTO(results.get(0));
    }

    // --- CATÁLOGO TPU ---

    @Override
    public List<TpuClassDTO> listTpuClasses() throws Exception {
        TypedQuery<BrTpuClass> query = em.createNamedQuery("BrTpuClass.findAll", BrTpuClass.class);
        List<BrTpuClass> entities = query.getResultList();
        List<TpuClassDTO> dtos = new ArrayList<>(entities.size());
        for (BrTpuClass c : entities) {
            dtos.add(toDTO(c));
        }
        return dtos;
    }

    @Override
    public List<TpuClassDTO> searchTpuClasses(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            return listTpuClasses();
        }
        String pattern = "%" + query.trim().toLowerCase() + "%";
        TypedQuery<BrTpuClass> q = em.createQuery(
                "SELECT c FROM BrTpuClass c WHERE LOWER(c.name) LIKE :pattern OR CAST(c.code AS string) LIKE :pattern ORDER BY c.name",
                BrTpuClass.class
        );
        q.setParameter("pattern", pattern);
        List<BrTpuClass> entities = q.getResultList();
        List<TpuClassDTO> dtos = new ArrayList<>(entities.size());
        for (BrTpuClass c : entities) {
            dtos.add(toDTO(c));
        }
        return dtos;
    }

    @Override
    public List<TpuSubjectDTO> listTpuSubjects() throws Exception {
        TypedQuery<BrTpuSubject> query = em.createNamedQuery("BrTpuSubject.findAll", BrTpuSubject.class);
        List<BrTpuSubject> entities = query.getResultList();
        List<TpuSubjectDTO> dtos = new ArrayList<>(entities.size());
        for (BrTpuSubject s : entities) {
            dtos.add(toDTO(s));
        }
        return dtos;
    }

    @Override
    public List<TpuSubjectDTO> searchTpuSubjects(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            return listTpuSubjects();
        }
        String pattern = "%" + query.trim().toLowerCase() + "%";
        TypedQuery<BrTpuSubject> q = em.createQuery(
                "SELECT s FROM BrTpuSubject s WHERE LOWER(s.name) LIKE :pattern OR CAST(s.code AS string) LIKE :pattern ORDER BY s.name",
                BrTpuSubject.class
        );
        q.setParameter("pattern", pattern);
        List<BrTpuSubject> entities = q.getResultList();
        List<TpuSubjectDTO> dtos = new ArrayList<>(entities.size());
        for (BrTpuSubject s : entities) {
            dtos.add(toDTO(s));
        }
        return dtos;
    }

    // --- CONVERSORES ENTIDADE <-> DTO ---

    private LawyerRegistrationDTO toDTO(BrLawyerRegistration entity) {
        LawyerRegistrationDTO dto = new LawyerRegistrationDTO();
        dto.setId(entity.getId());
        dto.setContactId(entity.getContactId());
        dto.setOabNumber(entity.getOabNumber());
        dto.setOabUf(entity.getOabUf());
        dto.setOabType(entity.getOabType());
        dto.setStatus(entity.getStatus());
        dto.setNotice(entity.getNotice());
        dto.setIssuanceDate(entity.getIssuanceDate());
        return dto;
    }

    private BrazilianCaseDetailsDTO toDTO(ArchiveFileBean legalCase) {
        BrazilianCaseDetailsDTO dto = new BrazilianCaseDetailsDTO();
        dto.setCaseId(legalCase.getId());
        dto.setCnjNumber(legalCase.getCnjNumber());
        dto.setCnjNumberClean(legalCase.getCnjNumberClean());
        dto.setCourtCode(legalCase.getCourtCode());
        dto.setJusticeSegment(legalCase.getJusticeSegment());
        dto.setJurisdictionDegree(legalCase.getJurisdictionDegree());
        dto.setCourtUnit(legalCase.getCourtUnit());
        dto.setComarca(legalCase.getComarca());
        dto.setJudicialSubsection(legalCase.getJudicialSubsection());
        dto.setTpuClassCode(legalCase.getTpuClassCode());
        dto.setTpuClassName(legalCase.getTpuClassName());
        dto.setTpuSubjectCodes(legalCase.getTpuSubjectCodes());
        dto.setTpuSubjectNames(legalCase.getTpuSubjectNames());
        dto.setSecrecyLevel(legalCase.getSecrecyLevel());
        dto.setDistributionDate(legalCase.getDistributionDate());
        dto.setCaseStatusBr(legalCase.getCaseStatusBr());
        dto.setProvenanceSystem(legalCase.getProvenanceSystem());
        return dto;
    }

    private JudiciaryCourtDTO toDTO(BrJudiciaryCourt entity) {
        JudiciaryCourtDTO dto = new JudiciaryCourtDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setJusticeSegment(entity.getJusticeSegment());
        dto.setSegmentName(entity.getSegmentName());
        dto.setUf(entity.getUf());
        dto.setCourtNumber(entity.getCourtNumber());
        dto.setDatajudCode(entity.getDatajudCode());
        dto.setDjenCode(entity.getDjenCode());
        dto.setElectronicPortalUrl(entity.getElectronicPortalUrl());
        dto.setCourtType(entity.getCourtType());
        dto.setActive(entity.isActive());
        return dto;
    }

    private TpuClassDTO toDTO(BrTpuClass entity) {
        TpuClassDTO dto = new TpuClassDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setGlossary(entity.getGlossary());
        dto.setNature(entity.getNature());
        dto.setSource(entity.getSource());
        dto.setSourceVersion(entity.getSourceVersion());
        dto.setImportedAt(entity.getImportedAt());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setLastUpdatedAt(entity.getLastUpdatedAt());
        dto.setChecksum(entity.getChecksum());
        dto.setActive(entity.isActive());
        return dto;
    }

    private TpuSubjectDTO toDTO(BrTpuSubject entity) {
        TpuSubjectDTO dto = new TpuSubjectDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setParentCode(entity.getParentCode());
        dto.setGlossary(entity.getGlossary());
        dto.setSource(entity.getSource());
        dto.setSourceVersion(entity.getSourceVersion());
        dto.setImportedAt(entity.getImportedAt());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setLastUpdatedAt(entity.getLastUpdatedAt());
        dto.setChecksum(entity.getChecksum());
        dto.setActive(entity.isActive());
        return dto;
    }
}
