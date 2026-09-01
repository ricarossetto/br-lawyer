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
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.persistence.*;
import org.jboss.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Implementação EJB para Gestão e Triagem de Publicações e Intimações Judiciais Brasileiras.
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class BrazilianPublicationService implements BrazilianPublicationServiceLocal, BrazilianPublicationServiceRemote {

    private static final Logger log = Logger.getLogger(BrazilianPublicationService.class.getName());

    @PersistenceContext(unitName = "j-lawyer-server-ejbPU")
    private EntityManager em;

    @Resource
    private SessionContext sessionContext;

    @EJB
    private BrazilianTaskServiceLocal taskService;

    public String resolveActor(String actor) {
        if (actor != null && !actor.trim().isEmpty() && !"CURRENT_USER".equalsIgnoreCase(actor.trim())) {
            return actor.trim();
        }
        try {
            if (sessionContext != null && sessionContext.getCallerPrincipal() != null) {
                String caller = sessionContext.getCallerPrincipal().getName();
                if (caller != null && !caller.trim().isEmpty() && !"anonymous".equalsIgnoreCase(caller.trim())) {
                    return caller.trim();
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return "system";
    }

    @Override
    public PublicationDetailDTO getPublication(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        BrPublication pub = em.find(BrPublication.class, id);
        if (pub == null) {
            return null;
        }
        PublicationDetailDTO dto = toDetailDTO(pub);
        dto.setEvents(getPublicationHistory(id));
        if (taskService != null) {
            TaskFilterDTO taskFilter = new TaskFilterDTO();
            // Fetch tasks linked to this publication
            List<TaskOverviewDTO> tasks = getTasksByPublicationId(id);
            dto.setLinkedTasks(tasks);
        }
        return dto;
    }

    @Override
    public PublicationDetailDTO savePublication(PublicationDetailDTO dto, String actor) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da publicação não podem ser nulos");
        }

        BrPublication entity;
        boolean isNew = false;
        if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
            entity = em.find(BrPublication.class, dto.getId());
            if (entity == null) {
                entity = new BrPublication(dto.getId());
                isNew = true;
            }
        } else {
            entity = new BrPublication(UUID.randomUUID().toString());
            isNew = true;
        }

        copyProperties(dto, entity);
        entity.setUpdatedAt(new Date());

        // Normalização de CNJ se informado
        if (dto.getCnjNumber() != null && !dto.getCnjNumber().trim().isEmpty()) {
            String clean = BrazilianDocumentValidator.unmask(dto.getCnjNumber());
            entity.setCnjNumberClean(clean);
            entity.setCnjNumber(BrazilianDocumentValidator.formatCnj(clean));
        }

        // Cálculo determinístico de fingerprint
        if (entity.getFingerprint() == null || entity.getFingerprint().trim().isEmpty()) {
            entity.setFingerprint(computeFingerprint(entity));
        }

        // Tentativa de auto-vinculação se o processo ainda não estiver vinculado e houver CNJ
        if ((entity.getProcessId() == null || entity.getProcessId().trim().isEmpty()) 
                && entity.getCnjNumberClean() != null && !entity.getCnjNumberClean().trim().isEmpty()) {
            autoLinkByCnj(entity);
        }

        if (isNew) {
            em.persist(entity);
            logAuditEvent(entity.getId(), null, entity.getProcessId(), BrPublicationEvent.EVENT_RECEIVED, 
                    actor != null ? actor : "SYSTEM", "Publicação recebida no sistema (fonte: " + entity.getSource() + ")");
        } else {
            entity = em.merge(entity);
        }
        em.flush();

        return getPublication(entity.getId());
    }

    @Override
    public PublicationDetailDTO deduplicateAndIngest(PublicationDetailDTO dto, String actor) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da publicação não podem ser nulos");
        }

        // 1. Busca por ID externo + Fonte
        if (dto.getExternalId() != null && !dto.getExternalId().trim().isEmpty() 
                && dto.getSource() != null && !dto.getSource().trim().isEmpty()) {
            TypedQuery<BrPublication> q = em.createNamedQuery("BrPublication.findByExternalIdAndSource", BrPublication.class);
            q.setParameter("externalId", dto.getExternalId().trim());
            q.setParameter("source", dto.getSource().trim());
            List<BrPublication> existing = q.getResultList();
            if (!existing.isEmpty()) {
                BrPublication pub = existing.get(0);
                log.infof("Publicação duplicada detectada por external_id (%s/%s). Retornando registro existente: %s", 
                        dto.getSource(), dto.getExternalId(), pub.getId());
                return getPublication(pub.getId());
            }
        }

        // 2. Busca por Fingerprint SHA-256
        String cleanCnj = (dto.getCnjNumber() != null) ? BrazilianDocumentValidator.unmask(dto.getCnjNumber()) : "";
        String computedFp = computeFingerprint(dto.getSource(), dto.getExternalId(), cleanCnj, dto.getPublicationDate(), dto.getContent());
        TypedQuery<BrPublication> fpQuery = em.createNamedQuery("BrPublication.findByFingerprint", BrPublication.class);
        fpQuery.setParameter("fingerprint", computedFp);
        List<BrPublication> fpExisting = fpQuery.getResultList();
        if (!fpExisting.isEmpty()) {
            BrPublication pub = fpExisting.get(0);
            log.infof("Publicação duplicada detectada por hash fingerprint SHA-256 (%s). Retornando registro existente: %s", 
                    computedFp, pub.getId());
            return getPublication(pub.getId());
        }

        // 3. Se não existe, persiste novo registro
        dto.setFingerprint(computedFp);
        return savePublication(dto, actor);
    }

    @Override
    public List<PublicationOverviewDTO> listPublications(PublicationFilterDTO filter) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BrPublication> cq = cb.createQuery(BrPublication.class);
        Root<BrPublication> root = cq.from(BrPublication.class);

        List<Predicate> predicates = buildPredicates(filter, cb, root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.desc(root.get("publicationDate")), cb.desc(root.get("createdAt")));

        TypedQuery<BrPublication> query = em.createQuery(cq);
        if (filter != null && filter.getPageSize() > 0) {
            query.setFirstResult(Math.max(0, filter.getPage()) * filter.getPageSize());
            query.setMaxResults(filter.getPageSize());
        }

        List<BrPublication> entities = query.getResultList();
        List<PublicationOverviewDTO> dtos = new ArrayList<>(entities.size());
        for (BrPublication pub : entities) {
            dtos.add(toOverviewDTO(pub));
        }
        return dtos;
    }

    @Override
    public long countPublications(PublicationFilterDTO filter) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<BrPublication> root = cq.from(BrPublication.class);

        List<Predicate> predicates = buildPredicates(filter, cb, root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public PublicationDetailDTO markRead(String publicationId, boolean read, String user) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return null;
        }
        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub == null) {
            return null;
        }

        String newReadStatus = read ? BrPublication.READ_READ : BrPublication.READ_UNREAD;
        if (!newReadStatus.equals(pub.getReadStatus())) {
            pub.setReadStatus(newReadStatus);
            pub.setReadAt(read ? new Date() : null);
            pub.setUpdatedAt(new Date());
            pub = em.merge(pub);
            em.flush();

            String eventType = read ? BrPublicationEvent.EVENT_READ : BrPublicationEvent.EVENT_UNREAD;
            logAuditEvent(pub.getId(), null, pub.getProcessId(), eventType, 
                    user != null ? user : "CURRENT_USER", read ? "Publicação marcada como lida" : "Publicação marcada como não lida");
        }

        return getPublication(pub.getId());
    }

    @Override
    public PublicationDetailDTO linkToCase(String publicationId, PublicationLinkRequestDTO request) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return null;
        }
        if (request == null || request.getProcessId() == null || request.getProcessId().trim().isEmpty()) {
            throw new IllegalArgumentException("Processo de destino é obrigatório para vinculação");
        }

        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub == null) {
            return null;
        }

        ArchiveFileBean legalCase = em.find(ArchiveFileBean.class, request.getProcessId());
        if (legalCase == null) {
            throw new IllegalArgumentException("Processo não encontrado para o ID: " + request.getProcessId());
        }

        pub.setProcessId(legalCase.getId());
        pub.setLinkProvenance(request.getProvenance() != null ? request.getProvenance() : BrPublication.PROVENANCE_MANUAL);
        pub.setLinkConfidence(request.getConfidence() != null ? request.getConfidence() : 1.0);
        pub.setUpdatedAt(new Date());

        if (BrPublication.STATUS_NOVA.equals(pub.getStatus())) {
            pub.setStatus(BrPublication.STATUS_EM_ANALISE);
        }

        pub = em.merge(pub);
        em.flush();

        logAuditEvent(pub.getId(), null, legalCase.getId(), BrPublicationEvent.EVENT_LINKED, 
                request.getUser() != null ? request.getUser() : "CURRENT_USER", 
                "Publicação vinculada ao processo " + legalCase.getFileNumber() + " (" + legalCase.getName() + ") - Proveniência: " + pub.getLinkProvenance());

        return getPublication(pub.getId());
    }

    @Override
    public PublicationDetailDTO unlinkFromCase(String publicationId, String user) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return null;
        }
        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub == null) {
            return null;
        }

        String oldProcessId = pub.getProcessId();
        pub.setProcessId(null);
        pub.setLinkProvenance(BrPublication.PROVENANCE_MANUAL);
        pub.setLinkConfidence(0.0);
        pub.setUpdatedAt(new Date());
        pub = em.merge(pub);
        em.flush();

        logAuditEvent(pub.getId(), null, oldProcessId, BrPublicationEvent.EVENT_UNLINKED, 
                user != null ? user : "CURRENT_USER", "Publicação desvinculada do processo");

        return getPublication(pub.getId());
    }

    @Override
    public PublicationDetailDTO treatPublication(String publicationId, PublicationTreatRequestDTO request) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return null;
        }
        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub == null) {
            return null;
        }

        pub.setStatus(BrPublication.STATUS_TRATADA);
        pub.setTreatmentStatus(BrPublication.TREATMENT_TRATADA);
        pub.setReadStatus(BrPublication.READ_READ);
        if (pub.getReadAt() == null) {
            pub.setReadAt(new Date());
        }
        pub.setTreatedAt(new Date());
        pub.setTreatedBy(request != null && request.getUser() != null ? request.getUser() : "CURRENT_USER");
        if (request != null && request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            pub.setNotes(request.getNotes().trim());
        }
        pub.setUpdatedAt(new Date());
        pub = em.merge(pub);
        em.flush();

        String actor = pub.getTreatedBy();
        logAuditEvent(pub.getId(), null, pub.getProcessId(), BrPublicationEvent.EVENT_TREATED, 
                actor, "Publicação tratada juridicamente" + (pub.getNotes() != null ? ": " + pub.getNotes() : ""));

        // Se solicitado, cria tarefa jurídica associada e sincroniza prazo com o calendário
        if (request != null && request.isCreateFollowUpTask() && taskService != null) {
            TaskDetailDTO task = new TaskDetailDTO();
            task.setTitle(request.getTaskTitle() != null && !request.getTaskTitle().trim().isEmpty() 
                    ? request.getTaskTitle() : "Cumprimento de publicação: " + (pub.getCourtCode() != null ? pub.getCourtCode() : "Intimação"));
            task.setDescription(request.getTaskDescription() != null ? request.getTaskDescription() : pub.getContent());
            task.setProcessId(pub.getProcessId());
            task.setPublicationId(pub.getId());
            task.setCategory(request.getTaskCategory() != null ? request.getTaskCategory() : BrTask.CATEGORY_CUMPRIMENTO_PRAZO);
            task.setPriority(request.getTaskPriority() != null ? request.getTaskPriority() : BrTask.PRIORITY_HIGH);
            task.setAssignedUser(request.getTaskAssignedUser() != null ? request.getTaskAssignedUser() : pub.getAssignedUser());
            task.setDueDate(request.getTaskDueDate() != null ? request.getTaskDueDate() : pub.getSuggestedDueDate());
            task.setDueTime(request.getTaskDueTime());
            task.setStatus(BrTask.STATUS_TODO);

            TaskDetailDTO createdTask = taskService.saveTask(task, actor, request.isSyncWithCalendar());
            logAuditEvent(pub.getId(), createdTask.getId(), pub.getProcessId(), BrPublicationEvent.EVENT_TASK_CREATED, 
                    actor, "Tarefa '" + createdTask.getTitle() + "' criada a partir da publicação (Prazo: " + createdTask.getDueDate() + ")");
        }

        return getPublication(pub.getId());
    }

    @Override
    public PublicationDetailDTO archivePublication(String publicationId, String user, String reason) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return null;
        }
        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub == null) {
            return null;
        }

        pub.setStatus(BrPublication.STATUS_ARQUIVADA);
        pub.setTreatmentStatus(BrPublication.TREATMENT_DISPENSADA);
        pub.setArchivedAt(new Date());
        pub.setArchivedBy(user != null ? user : "CURRENT_USER");
        if (reason != null && !reason.trim().isEmpty()) {
            pub.setNotes((pub.getNotes() != null ? pub.getNotes() + " | " : "") + "Arquivada: " + reason.trim());
        }
        pub.setUpdatedAt(new Date());
        pub = em.merge(pub);
        em.flush();

        logAuditEvent(pub.getId(), null, pub.getProcessId(), BrPublicationEvent.EVENT_ARCHIVED, 
                pub.getArchivedBy(), "Publicação arquivada / dispensada" + (reason != null ? ": " + reason : ""));

        return getPublication(pub.getId());
    }

    @Override
    public PublicationDetailDTO assignPublication(String publicationId, String assignedUser, String actor) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return null;
        }
        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub == null) {
            return null;
        }

        pub.setAssignedUser(assignedUser);
        pub.setUpdatedAt(new Date());
        pub = em.merge(pub);
        em.flush();

        logAuditEvent(pub.getId(), null, pub.getProcessId(), BrPublicationEvent.EVENT_ASSIGNED, 
                actor != null ? actor : "CURRENT_USER", "Responsável alterado para: " + (assignedUser != null ? assignedUser : "Nenhum"));

        return getPublication(pub.getId());
    }

    @Override
    public List<PublicationEventDTO> getPublicationHistory(String publicationId) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<BrPublicationEvent> query = em.createNamedQuery("BrPublicationEvent.findByPublicationId", BrPublicationEvent.class);
        query.setParameter("publicationId", publicationId);
        List<BrPublicationEvent> events = query.getResultList();
        List<PublicationEventDTO> dtos = new ArrayList<>(events.size());
        for (BrPublicationEvent e : events) {
            PublicationEventDTO dto = new PublicationEventDTO();
            dto.setId(e.getId());
            dto.setPublicationId(e.getPublicationId());
            dto.setTaskId(e.getTaskId());
            dto.setProcessId(e.getProcessId());
            dto.setEventType(e.getEventType());
            dto.setActor(e.getActor());
            dto.setDetails(e.getDetails());
            dto.setCreatedAt(e.getCreatedAt());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void deletePublication(String publicationId, String user) throws Exception {
        if (publicationId == null || publicationId.trim().isEmpty()) {
            return;
        }
        BrPublication pub = em.find(BrPublication.class, publicationId);
        if (pub != null) {
            em.remove(pub);
            em.flush();
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private void autoLinkByCnj(BrPublication entity) {
        try {
            TypedQuery<ArchiveFileBean> q = em.createNamedQuery("ArchiveFileBean.findByCnjNumberClean", ArchiveFileBean.class);
            q.setParameter("cnjNumberClean", entity.getCnjNumberClean());
            List<ArchiveFileBean> matches = q.getResultList();
            if (matches.size() == 1) {
                ArchiveFileBean match = matches.get(0);
                entity.setProcessId(match.getId());
                entity.setLinkProvenance(BrPublication.PROVENANCE_AUTO_CNJ);
                entity.setLinkConfidence(1.0);
                log.infof("Publicação %s vinculada automaticamente por CNJ exato ao processo %s (%s)", 
                        entity.getId(), match.getFileNumber(), match.getName());
            } else if (matches.size() > 1) {
                // Ambíguo: não vincula automaticamente, mas marca baixa confiança para revisão humana
                entity.setLinkConfidence(0.5);
                log.warnf("Múltiplos processos encontrados para o CNJ %s. Vinculação automática ignorada para revisão humana.", 
                        entity.getCnjNumberClean());
            }
        } catch (Exception ex) {
            log.warn("Erro ao tentar auto-vinculação por CNJ: " + ex.getMessage());
        }
    }

    private List<TaskOverviewDTO> getTasksByPublicationId(String publicationId) {
        try {
            TypedQuery<BrTask> q = em.createNamedQuery("BrTask.findByPublicationId", BrTask.class);
            q.setParameter("publicationId", publicationId);
            List<BrTask> tasks = q.getResultList();
            List<TaskOverviewDTO> dtos = new ArrayList<>(tasks.size());
            for (BrTask t : tasks) {
                TaskOverviewDTO o = new TaskOverviewDTO();
                o.setId(t.getId());
                o.setTitle(t.getTitle());
                o.setProcessId(t.getProcessId());
                o.setPublicationId(t.getPublicationId());
                o.setAssignedUser(t.getAssignedUser());
                o.setStatus(t.getStatus());
                o.setPriority(t.getPriority());
                o.setDueDate(t.getDueDate());
                o.setDueTime(t.getDueTime());
                o.setCategory(t.getCategory());
                o.setCreatedAt(t.getCreatedAt());
                dtos.add(o);
            }
            return dtos;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void logAuditEvent(String publicationId, String taskId, String processId, String eventType, String actor, String details) {
        try {
            BrPublicationEvent event = new BrPublicationEvent();
            event.setId(UUID.randomUUID().toString());
            event.setPublicationId(publicationId);
            event.setTaskId(taskId);
            event.setProcessId(processId);
            event.setEventType(eventType);
            event.setActor(resolveActor(actor));
            event.setDetails(details);
            event.setCreatedAt(new Date());
            em.persist(event);
        } catch (Exception ex) {
            log.warn("Falha ao registrar evento de auditoria: " + ex.getMessage());
        }
    }

    private String computeFingerprint(BrPublication p) {
        return computeFingerprint(p.getSource(), p.getExternalId(), p.getCnjNumberClean(), p.getPublicationDate(), p.getContent());
    }

    private String computeFingerprint(String source, String externalId, String cleanCnj, Date pubDate, String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(source != null ? source.trim().toLowerCase() : "").append("|");
            sb.append(externalId != null ? externalId.trim() : "").append("|");
            sb.append(cleanCnj != null ? cleanCnj.trim() : "").append("|");
            sb.append(pubDate != null ? pubDate.getTime() : "").append("|");
            sb.append(content != null ? content.trim() : "");

            byte[] hash = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    private List<Predicate> buildPredicates(PublicationFilterDTO filter, CriteriaBuilder cb, Root<BrPublication> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter == null) {
            return predicates;
        }

        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getStatus())) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus().trim()));
        }
        if (filter.getReadStatus() != null && !filter.getReadStatus().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getReadStatus())) {
            predicates.add(cb.equal(root.get("readStatus"), filter.getReadStatus().trim()));
        }
        if (filter.getTreatmentStatus() != null && !filter.getTreatmentStatus().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getTreatmentStatus())) {
            predicates.add(cb.equal(root.get("treatmentStatus"), filter.getTreatmentStatus().trim()));
        }
        if (filter.getCourtCode() != null && !filter.getCourtCode().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("courtCode"), filter.getCourtCode().trim().toUpperCase()));
        }
        if (filter.getProcessId() != null && !filter.getProcessId().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("processId"), filter.getProcessId().trim()));
        }
        if (filter.getCnjNumber() != null && !filter.getCnjNumber().trim().isEmpty()) {
            String clean = BrazilianDocumentValidator.unmask(filter.getCnjNumber());
            predicates.add(cb.equal(root.get("cnjNumberClean"), clean));
        }
        if (filter.getAssignedUser() != null && !filter.getAssignedUser().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("assignedUser"), filter.getAssignedUser().trim()));
        }
        if (filter.getLawyerOab() != null && !filter.getLawyerOab().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("lawyerOab")), "%" + filter.getLawyerOab().trim().toLowerCase() + "%"));
        }
        if (filter.getFromDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("publicationDate"), filter.getToDate()));
        }
        if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
            String pattern = "%" + filter.getSearchText().trim().toLowerCase() + "%";
            Predicate contentMatch = cb.like(cb.lower(root.get("content")), pattern);
            Predicate recipientMatch = cb.like(cb.lower(root.get("recipient")), pattern);
            Predicate lawyerMatch = cb.like(cb.lower(root.get("lawyerName")), pattern);
            predicates.add(cb.or(contentMatch, recipientMatch, lawyerMatch));
        }

        return predicates;
    }

    private void copyProperties(PublicationDetailDTO dto, BrPublication entity) {
        entity.setExternalId(dto.getExternalId());
        entity.setSource(dto.getSource() != null ? dto.getSource() : "MANUAL");
        entity.setSourceType(dto.getSourceType() != null ? dto.getSourceType() : "DIARIO_OFICIAL");
        entity.setCourtCode(dto.getCourtCode());
        entity.setProcessId(dto.getProcessId());
        entity.setPublicationDate(dto.getPublicationDate() != null ? dto.getPublicationDate() : new Date());
        entity.setAvailabilityDate(dto.getAvailabilityDate());
        entity.setContent(dto.getContent());
        entity.setRawContent(dto.getRawContent() != null ? dto.getRawContent() : dto.getContent());
        entity.setPublicationType(dto.getPublicationType() != null ? dto.getPublicationType() : "INTIMACAO");
        entity.setRecipient(dto.getRecipient());
        entity.setLawyerName(dto.getLawyerName());
        entity.setLawyerOab(dto.getLawyerOab());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getReadStatus() != null) entity.setReadStatus(dto.getReadStatus());
        if (dto.getTreatmentStatus() != null) entity.setTreatmentStatus(dto.getTreatmentStatus());
        entity.setAssignedUser(dto.getAssignedUser());
        if (dto.getLinkProvenance() != null) entity.setLinkProvenance(dto.getLinkProvenance());
        if (dto.getLinkConfidence() != null) entity.setLinkConfidence(dto.getLinkConfidence());
        entity.setSuggestedDueDate(dto.getSuggestedDueDate());
        entity.setSuggestedDeadlineDays(dto.getSuggestedDeadlineDays());
        entity.setSuggestionSource(dto.getSuggestionSource());
        entity.setSuggestionConfidence(dto.getSuggestionConfidence());
        if (dto.getProvenance() != null) entity.setProvenance(dto.getProvenance());
        entity.setNotes(dto.getNotes());
    }

    private PublicationOverviewDTO toOverviewDTO(BrPublication p) {
        PublicationOverviewDTO dto = new PublicationOverviewDTO();
        dto.setId(p.getId());
        dto.setExternalId(p.getExternalId());
        dto.setSource(p.getSource());
        dto.setSourceType(p.getSourceType());
        dto.setCourtCode(p.getCourtCode());
        dto.setProcessId(p.getProcessId());
        dto.setCnjNumber(p.getCnjNumber());
        dto.setCnjNumberClean(p.getCnjNumberClean());
        dto.setPublicationDate(p.getPublicationDate());
        dto.setAvailabilityDate(p.getAvailabilityDate());
        dto.setPublicationType(p.getPublicationType());
        dto.setRecipient(p.getRecipient());
        dto.setLawyerName(p.getLawyerName());
        dto.setLawyerOab(p.getLawyerOab());
        dto.setStatus(p.getStatus());
        dto.setReadStatus(p.getReadStatus());
        dto.setTreatmentStatus(p.getTreatmentStatus());
        dto.setAssignedUser(p.getAssignedUser());
        dto.setLinkProvenance(p.getLinkProvenance());
        dto.setLinkConfidence(p.getLinkConfidence());
        dto.setSuggestedDueDate(p.getSuggestedDueDate());
        dto.setSuggestedDeadlineDays(p.getSuggestedDeadlineDays());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setTreatedAt(p.getTreatedAt());

        if (p.getContent() != null) {
            String cleanText = p.getContent().replaceAll("\\s+", " ").trim();
            dto.setSnippet(cleanText.length() > 200 ? cleanText.substring(0, 197) + "..." : cleanText);
        }

        // Informações da pasta do processo se vinculado
        if (p.getProcessId() != null && !p.getProcessId().trim().isEmpty()) {
            ArchiveFileBean c = em.find(ArchiveFileBean.class, p.getProcessId());
            if (c != null) {
                dto.setCaseFileNumber(c.getFileNumber());
                dto.setCaseName(c.getName());
            }
        }

        return dto;
    }

    private PublicationDetailDTO toDetailDTO(BrPublication p) {
        PublicationDetailDTO dto = new PublicationDetailDTO();
        dto.setId(p.getId());
        dto.setExternalId(p.getExternalId());
        dto.setSource(p.getSource());
        dto.setSourceType(p.getSourceType());
        dto.setCourtCode(p.getCourtCode());
        dto.setProcessId(p.getProcessId());
        dto.setCnjNumber(p.getCnjNumber());
        dto.setCnjNumberClean(p.getCnjNumberClean());
        dto.setPublicationDate(p.getPublicationDate());
        dto.setAvailabilityDate(p.getAvailabilityDate());
        dto.setContent(p.getContent());
        dto.setRawContent(p.getRawContent());
        dto.setPublicationType(p.getPublicationType());
        dto.setRecipient(p.getRecipient());
        dto.setLawyerName(p.getLawyerName());
        dto.setLawyerOab(p.getLawyerOab());
        dto.setStatus(p.getStatus());
        dto.setReadStatus(p.getReadStatus());
        dto.setTreatmentStatus(p.getTreatmentStatus());
        dto.setAssignedUser(p.getAssignedUser());
        dto.setLinkProvenance(p.getLinkProvenance());
        dto.setLinkConfidence(p.getLinkConfidence());
        dto.setSuggestedDueDate(p.getSuggestedDueDate());
        dto.setSuggestedDeadlineDays(p.getSuggestedDeadlineDays());
        dto.setSuggestionSource(p.getSuggestionSource());
        dto.setSuggestionConfidence(p.getSuggestionConfidence());
        dto.setFingerprint(p.getFingerprint());
        dto.setProvenance(p.getProvenance());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        dto.setReadAt(p.getReadAt());
        dto.setTreatedAt(p.getTreatedAt());
        dto.setTreatedBy(p.getTreatedBy());
        dto.setArchivedAt(p.getArchivedAt());
        dto.setArchivedBy(p.getArchivedBy());
        dto.setNotes(p.getNotes());

        if (p.getProcessId() != null && !p.getProcessId().trim().isEmpty()) {
            ArchiveFileBean c = em.find(ArchiveFileBean.class, p.getProcessId());
            if (c != null) {
                dto.setCaseFileNumber(c.getFileNumber());
                dto.setCaseName(c.getName());
            }
        }

        return dto;
    }
}