/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.persistence.*;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Implementação EJB para Gestão de Tarefas Jurídicas, Prazos e Integração com Calendário do j-lawyer.
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class BrazilianTaskService implements BrazilianTaskServiceLocal, BrazilianTaskServiceRemote {

    private static final Logger log = Logger.getLogger(BrazilianTaskService.class.getName());

    @PersistenceContext(unitName = "j-lawyer-server-ejbPU")
    private EntityManager em;

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

    @Resource
    private SessionContext sessionContext;

    @Override
    public TaskDetailDTO getTask(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        BrTask task = em.find(BrTask.class, id);
        if (task == null) {
            return null;
        }
        return toDetailDTO(task);
    }

    @Override
    public TaskDetailDTO saveTask(TaskDetailDTO dto, String user, boolean syncCalendar) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da tarefa não podem ser nulos");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Título da tarefa é obrigatório");
        }

        BrTask entity;
        boolean isNew = false;
        if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
            entity = em.find(BrTask.class, dto.getId());
            if (entity == null) {
                entity = new BrTask(dto.getId());
                isNew = true;
            }
        } else {
            entity = new BrTask(UUID.randomUUID().toString());
            isNew = true;
        }

        entity.setTitle(dto.getTitle().trim());
        entity.setDescription(dto.getDescription());
        entity.setProcessId(dto.getProcessId());
        entity.setPublicationId(dto.getPublicationId());
        entity.setAssignedUser(dto.getAssignedUser() != null && !"CURRENT_USER".equalsIgnoreCase(dto.getAssignedUser().trim()) ? dto.getAssignedUser().trim() : resolveActor(user));
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus().trim().toUpperCase());
        if (dto.getPriority() != null) entity.setPriority(dto.getPriority().trim().toUpperCase());
        entity.setDueDate(dto.getDueDate());
        entity.setDueTime(dto.getDueTime());
        entity.setEstimatedMinutes(dto.getEstimatedMinutes() != null ? dto.getEstimatedMinutes() : 0);
        entity.setActualMinutes(dto.getActualMinutes() != null ? dto.getActualMinutes() : 0);
        entity.setCategory(dto.getCategory() != null ? dto.getCategory().trim().toUpperCase() : BrTask.CATEGORY_ANALISE);
        entity.setNotes(dto.getNotes());
        entity.setUpdatedAt(new Date());

        if (isNew) {
            entity.setCreatedBy(user != null ? user : "CURRENT_USER");
            entity.setCreatedAt(new Date());
        }

        // Se o status for concluído, marca timestamp de conclusão
        if (BrTask.STATUS_DONE.equals(entity.getStatus()) && entity.getCompletedAt() == null) {
            entity.setCompletedAt(new Date());
            entity.setCompletedBy(user != null ? user : "CURRENT_USER");
        } else if (!BrTask.STATUS_DONE.equals(entity.getStatus())) {
            entity.setCompletedAt(null);
            entity.setCompletedBy(null);
        }

        // --- INTEGRAÇÃO COM O CALENDÁRIO / PRAZOS DO J-LAWYER ---
        if (syncCalendar && entity.getProcessId() != null && !entity.getProcessId().trim().isEmpty() && entity.getDueDate() != null) {
            syncWithCaseEvents(entity, user);
        }

        if (isNew) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();

        return getTask(entity.getId());
    }

    @Override
    public List<TaskOverviewDTO> listTasks(TaskFilterDTO filter) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BrTask> cq = cb.createQuery(BrTask.class);
        Root<BrTask> root = cq.from(BrTask.class);

        List<Predicate> predicates = buildPredicates(filter, cb, root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.asc(root.get("dueDate")), cb.desc(root.get("priority")), cb.desc(root.get("createdAt")));

        TypedQuery<BrTask> query = em.createQuery(cq);
        if (filter != null && filter.getPageSize() > 0) {
            query.setFirstResult(Math.max(0, filter.getPage()) * filter.getPageSize());
            query.setMaxResults(filter.getPageSize());
        }

        List<BrTask> entities = query.getResultList();
        List<TaskOverviewDTO> dtos = new ArrayList<>(entities.size());
        for (BrTask task : entities) {
            dtos.add(toOverviewDTO(task));
        }
        return dtos;
    }

    @Override
    public long countTasks(TaskFilterDTO filter) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<BrTask> root = cq.from(BrTask.class);

        List<Predicate> predicates = buildPredicates(filter, cb, root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public TaskDetailDTO changeStatus(String taskId, TaskStatusChangeDTO changeRequest) throws Exception {
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }
        BrTask task = em.find(BrTask.class, taskId);
        if (task == null) {
            return null;
        }

        String oldStatus = task.getStatus();
        String newStatus = (changeRequest != null && changeRequest.getNewStatus() != null) 
                ? changeRequest.getNewStatus().trim().toUpperCase() : BrTask.STATUS_TODO;

        task.setStatus(newStatus);
        task.setUpdatedAt(new Date());

        String user = (changeRequest != null && changeRequest.getUser() != null) ? changeRequest.getUser() : "CURRENT_USER";

        if (BrTask.STATUS_DONE.equals(newStatus)) {
            task.setCompletedAt(new Date());
            task.setCompletedBy(user);
        } else {
            task.setCompletedAt(null);
            task.setCompletedBy(null);
        }

        if (changeRequest != null && changeRequest.getActualMinutes() != null && changeRequest.getActualMinutes() > 0) {
            task.setActualMinutes(changeRequest.getActualMinutes());
        }

        // Sincroniza estado de conclusão no evento de calendário correspondente
        if (task.getCalendarEventId() != null && !task.getCalendarEventId().trim().isEmpty()) {
            ArchiveFileReviewsBean review = em.find(ArchiveFileReviewsBean.class, task.getCalendarEventId());
            if (review != null) {
                review.setDone(BrTask.STATUS_DONE.equals(newStatus));
                em.merge(review);
            }
        }

        // Adiciona comentário de histórico se houver texto
        if (changeRequest != null && changeRequest.getComment() != null && !changeRequest.getComment().trim().isEmpty()) {
            addComment(task.getId(), user, "Status alterado de " + oldStatus + " para " + newStatus + ": " + changeRequest.getComment().trim());
        }

        task = em.merge(task);
        em.flush();

        return getTask(task.getId());
    }

    @Override
    public TaskDetailDTO assignTask(String taskId, String assignedUser, String actor) throws Exception {
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }
        BrTask task = em.find(BrTask.class, taskId);
        if (task == null) {
            return null;
        }

        task.setAssignedUser(assignedUser);
        task.setUpdatedAt(new Date());

        // Atualiza responsável no calendário correspondente
        if (task.getCalendarEventId() != null && !task.getCalendarEventId().trim().isEmpty()) {
            ArchiveFileReviewsBean review = em.find(ArchiveFileReviewsBean.class, task.getCalendarEventId());
            if (review != null) {
                review.setAssignee(assignedUser);
                em.merge(review);
            }
        }

        task = em.merge(task);
        em.flush();

        return getTask(task.getId());
    }

    @Override
    public TaskCommentDTO addComment(String taskId, String userName, String commentText) throws Exception {
        if (taskId == null || taskId.trim().isEmpty() || commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da tarefa e texto do comentário são obrigatórios");
        }
        BrTask task = em.find(BrTask.class, taskId);
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não encontrada para o ID: " + taskId);
        }

        BrTaskComment comment = new BrTaskComment(UUID.randomUUID().toString(), task, 
                userName != null ? userName : "CURRENT_USER", commentText.trim());
        em.persist(comment);

        task.setUpdatedAt(new Date());
        em.merge(task);
        em.flush();

        return new TaskCommentDTO(comment.getId(), taskId, comment.getUserName(), comment.getCommentText(), comment.getCreatedAt());
    }

    @Override
    public List<TaskCommentDTO> getComments(String taskId) throws Exception {
        if (taskId == null || taskId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<BrTaskComment> query = em.createNamedQuery("BrTaskComment.findByTaskId", BrTaskComment.class);
        query.setParameter("taskId", taskId);
        List<BrTaskComment> entities = query.getResultList();
        List<TaskCommentDTO> dtos = new ArrayList<>(entities.size());
        for (BrTaskComment c : entities) {
            dtos.add(new TaskCommentDTO(c.getId(), taskId, c.getUserName(), c.getCommentText(), c.getCreatedAt()));
        }
        return dtos;
    }

    @Override
    public TaskChecklistItemDTO addChecklistItem(String taskId, String title, int order) throws Exception {
        if (taskId == null || taskId.trim().isEmpty() || title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da tarefa e título do item são obrigatórios");
        }
        BrTask task = em.find(BrTask.class, taskId);
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não encontrada para o ID: " + taskId);
        }

        BrTaskChecklistItem item = new BrTaskChecklistItem(UUID.randomUUID().toString(), task, title.trim(), order);
        em.persist(item);

        task.setUpdatedAt(new Date());
        em.merge(task);
        em.flush();

        return new TaskChecklistItemDTO(item.getId(), taskId, item.getTitle(), item.isDone(), item.getItemOrder());
    }

    @Override
    public TaskChecklistItemDTO toggleChecklistItem(String checklistItemId, boolean done, String user) throws Exception {
        if (checklistItemId == null || checklistItemId.trim().isEmpty()) {
            return null;
        }
        BrTaskChecklistItem item = em.find(BrTaskChecklistItem.class, checklistItemId);
        if (item == null) {
            return null;
        }

        item.setDone(done);
        item.setCompletedAt(done ? new Date() : null);
        item.setCompletedBy(done ? (user != null ? user : "CURRENT_USER") : null);

        if (item.getTask() != null) {
            item.getTask().setUpdatedAt(new Date());
            em.merge(item.getTask());
        }

        item = em.merge(item);
        em.flush();

        TaskChecklistItemDTO dto = new TaskChecklistItemDTO(item.getId(), 
                item.getTask() != null ? item.getTask().getId() : null, item.getTitle(), item.isDone(), item.getItemOrder());
        dto.setCompletedAt(item.getCompletedAt());
        dto.setCompletedBy(item.getCompletedBy());
        return dto;
    }

    @Override
    public void deleteChecklistItem(String checklistItemId) throws Exception {
        if (checklistItemId == null || checklistItemId.trim().isEmpty()) {
            return;
        }
        BrTaskChecklistItem item = em.find(BrTaskChecklistItem.class, checklistItemId);
        if (item != null) {
            if (item.getTask() != null) {
                item.getTask().setUpdatedAt(new Date());
                em.merge(item.getTask());
            }
            em.remove(item);
            em.flush();
        }
    }

    @Override
    public KanbanBoardDTO getKanbanBoard(String assignedUser, String processId) throws Exception {
        KanbanBoardDTO board = new KanbanBoardDTO();

        KanbanColumnDTO colTodo = new KanbanColumnDTO(BrTask.STATUS_TODO, "A Fazer");
        KanbanColumnDTO colInProgress = new KanbanColumnDTO(BrTask.STATUS_IN_PROGRESS, "Em Andamento");
        KanbanColumnDTO colWaiting = new KanbanColumnDTO(BrTask.STATUS_WAITING, "Aguardando");
        KanbanColumnDTO colDone = new KanbanColumnDTO(BrTask.STATUS_DONE, "Concluído");

        TaskFilterDTO filter = new TaskFilterDTO();
        filter.setAssignedUser(assignedUser);
        filter.setProcessId(processId);
        filter.setPageSize(200); // Até 200 itens por consulta Kanban

        List<TaskOverviewDTO> allTasks = listTasks(filter);
        List<TaskOverviewDTO> todoList = new ArrayList<>();
        List<TaskOverviewDTO> inProgressList = new ArrayList<>();
        List<TaskOverviewDTO> waitingList = new ArrayList<>();
        List<TaskOverviewDTO> doneList = new ArrayList<>();

        for (TaskOverviewDTO t : allTasks) {
            if (BrTask.STATUS_TODO.equalsIgnoreCase(t.getStatus())) {
                todoList.add(t);
            } else if (BrTask.STATUS_IN_PROGRESS.equalsIgnoreCase(t.getStatus())) {
                inProgressList.add(t);
            } else if (BrTask.STATUS_WAITING.equalsIgnoreCase(t.getStatus())) {
                waitingList.add(t);
            } else if (BrTask.STATUS_DONE.equalsIgnoreCase(t.getStatus())) {
                doneList.add(t);
            }
        }

        colTodo.setTasks(todoList);
        colInProgress.setTasks(inProgressList);
        colWaiting.setTasks(waitingList);
        colDone.setTasks(doneList);

        board.setColumns(Arrays.asList(colTodo, colInProgress, colWaiting, colDone));
        board.setTotalTasks(todoList.size() + inProgressList.size() + waitingList.size() + doneList.size());

        return board;
    }

    @Override
    public void deleteTask(String taskId, String user) throws Exception {
        if (taskId == null || taskId.trim().isEmpty()) {
            return;
        }
        BrTask task = em.find(BrTask.class, taskId);
        if (task != null) {
            // Remove evento de calendário associado se existir
            if (task.getCalendarEventId() != null && !task.getCalendarEventId().trim().isEmpty()) {
                ArchiveFileReviewsBean review = em.find(ArchiveFileReviewsBean.class, task.getCalendarEventId());
                if (review != null) {
                    em.remove(review);
                }
            }
            em.remove(task);
            em.flush();
        }
    }

    // --- MÉTODOS AUXILIARES E SINCRONIZAÇÃO DE CALENDÁRIO ---

    private void syncWithCaseEvents(BrTask task, String user) {
        try {
            ArchiveFileBean caseBean = em.find(ArchiveFileBean.class, task.getProcessId());
            if (caseBean == null) {
                return;
            }

            ArchiveFileReviewsBean review = null;
            if (task.getCalendarEventId() != null && !task.getCalendarEventId().trim().isEmpty()) {
                review = em.find(ArchiveFileReviewsBean.class, task.getCalendarEventId());
            }

            boolean isNewReview = (review == null);
            if (isNewReview) {
                review = new ArchiveFileReviewsBean(UUID.randomUUID().toString());
                review.setEventType(EventTypes.EVENTTYPE_RESPITE); // Frist / Prazo Fatal
                review.setCreatedBy(user != null ? user : task.getCreatedBy());
            }

            review.setSummary(task.getTitle());
            review.setDescription(task.getDescription());
            review.setBeginDate(task.getDueDate());
            review.setArchiveFileKey(caseBean);
            review.setAssignee(task.getAssignedUser());
            review.setDone(BrTask.STATUS_DONE.equals(task.getStatus()));

            if (isNewReview) {
                em.persist(review);
                task.setCalendarEventId(review.getId());
            } else {
                em.merge(review);
            }
        } catch (Exception ex) {
            log.warn("Erro ao sincronizar tarefa jurídica com o calendário do j-lawyer: " + ex.getMessage());
        }
    }

    private List<Predicate> buildPredicates(TaskFilterDTO filter, CriteriaBuilder cb, Root<BrTask> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter == null) {
            return predicates;
        }

        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getStatus())) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus().trim().toUpperCase()));
        }
        if (filter.getAssignedUser() != null && !filter.getAssignedUser().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("assignedUser"), filter.getAssignedUser().trim()));
        }
        if (filter.getProcessId() != null && !filter.getProcessId().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("processId"), filter.getProcessId().trim()));
        }
        if (filter.getPriority() != null && !filter.getPriority().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getPriority())) {
            predicates.add(cb.equal(root.get("priority"), filter.getPriority().trim().toUpperCase()));
        }
        if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getCategory())) {
            predicates.add(cb.equal(root.get("category"), filter.getCategory().trim().toUpperCase()));
        }

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfToday = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endOfToday = cal.getTime();

        if (Boolean.TRUE.equals(filter.getOverdue())) {
            predicates.add(cb.not(root.get("status").in(BrTask.STATUS_DONE, BrTask.STATUS_CANCELLED)));
            predicates.add(cb.lessThan(root.get("dueDate"), startOfToday));
        }
        if (Boolean.TRUE.equals(filter.getDueToday())) {
            predicates.add(cb.not(root.get("status").in(BrTask.STATUS_DONE, BrTask.STATUS_CANCELLED)));
            predicates.add(cb.between(root.get("dueDate"), startOfToday, endOfToday));
        }
        if (filter.getFromDueDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), filter.getFromDueDate()));
        }
        if (filter.getToDueDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), filter.getToDueDate()));
        }
        if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
            String pattern = "%" + filter.getSearchText().trim().toLowerCase() + "%";
            Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
            Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
            predicates.add(cb.or(titleMatch, descMatch));
        }

        return predicates;
    }

    private TaskOverviewDTO toOverviewDTO(BrTask t) {
        TaskOverviewDTO dto = new TaskOverviewDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setProcessId(t.getProcessId());
        dto.setPublicationId(t.getPublicationId());
        dto.setCalendarEventId(t.getCalendarEventId());
        dto.setAssignedUser(t.getAssignedUser());
        dto.setCreatedBy(t.getCreatedBy());
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());
        dto.setDueDate(t.getDueDate());
        dto.setDueTime(t.getDueTime());
        dto.setCategory(t.getCategory());
        dto.setEstimatedMinutes(t.getEstimatedMinutes());
        dto.setActualMinutes(t.getActualMinutes());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setCompletedAt(t.getCompletedAt());

        // Flags calculadas
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date startOfToday = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        Date endOfToday = cal.getTime();

        if (t.getDueDate() != null && !BrTask.STATUS_DONE.equals(t.getStatus()) && !BrTask.STATUS_CANCELLED.equals(t.getStatus())) {
            dto.setOverdue(t.getDueDate().before(startOfToday));
            dto.setDueToday(t.getDueDate().after(startOfToday) && t.getDueDate().before(endOfToday));
        }

        // Informações da pasta do processo
        if (t.getProcessId() != null && !t.getProcessId().trim().isEmpty()) {
            ArchiveFileBean c = em.find(ArchiveFileBean.class, t.getProcessId());
            if (c != null) {
                dto.setCaseFileNumber(c.getFileNumber());
                dto.setCaseName(c.getName());
                dto.setCnjNumber(c.getCnjNumber());
            }
        }

        if (t.getChecklistItems() != null) {
            dto.setChecklistTotalCount(t.getChecklistItems().size());
            int done = 0;
            for (BrTaskChecklistItem item : t.getChecklistItems()) {
                if (item.isDone()) done++;
            }
            dto.setChecklistDoneCount(done);
        }

        if (t.getComments() != null) {
            dto.setCommentCount(t.getComments().size());
        }

        return dto;
    }

    private TaskDetailDTO toDetailDTO(BrTask t) {
        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setProcessId(t.getProcessId());
        dto.setPublicationId(t.getPublicationId());
        dto.setCalendarEventId(t.getCalendarEventId());
        dto.setAssignedUser(t.getAssignedUser());
        dto.setCreatedBy(t.getCreatedBy());
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());
        dto.setDueDate(t.getDueDate());
        dto.setDueTime(t.getDueTime());
        dto.setCompletedAt(t.getCompletedAt());
        dto.setCompletedBy(t.getCompletedBy());
        dto.setEstimatedMinutes(t.getEstimatedMinutes());
        dto.setActualMinutes(t.getActualMinutes());
        dto.setCategory(t.getCategory());
        dto.setNotes(t.getNotes());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date startOfToday = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        Date endOfToday = cal.getTime();

        if (t.getDueDate() != null && !BrTask.STATUS_DONE.equals(t.getStatus()) && !BrTask.STATUS_CANCELLED.equals(t.getStatus())) {
            dto.setOverdue(t.getDueDate().before(startOfToday));
            dto.setDueToday(t.getDueDate().after(startOfToday) && t.getDueDate().before(endOfToday));
        }

        if (t.getProcessId() != null && !t.getProcessId().trim().isEmpty()) {
            ArchiveFileBean c = em.find(ArchiveFileBean.class, t.getProcessId());
            if (c != null) {
                dto.setCaseFileNumber(c.getFileNumber());
                dto.setCaseName(c.getName());
                dto.setCnjNumber(c.getCnjNumber());
            }
        }

        // Checklist e Comentários
        List<BrTaskChecklistItem> items = t.getChecklistItems();
        if (items != null) {
            List<TaskChecklistItemDTO> itemDtos = new ArrayList<>(items.size());
            for (BrTaskChecklistItem i : items) {
                TaskChecklistItemDTO itemDto = new TaskChecklistItemDTO(i.getId(), t.getId(), i.getTitle(), i.isDone(), i.getItemOrder());
                itemDto.setCompletedAt(i.getCompletedAt());
                itemDto.setCompletedBy(i.getCompletedBy());
                itemDtos.add(itemDto);
            }
            dto.setChecklistItems(itemDtos);
        }

        List<BrTaskComment> comments = t.getComments();
        if (comments != null) {
            List<TaskCommentDTO> commentDtos = new ArrayList<>(comments.size());
            for (BrTaskComment c : comments) {
                commentDtos.add(new TaskCommentDTO(c.getId(), t.getId(), c.getUserName(), c.getCommentText(), c.getCreatedAt()));
            }
            dto.setComments(commentDtos);
        }

        return dto;
    }
}