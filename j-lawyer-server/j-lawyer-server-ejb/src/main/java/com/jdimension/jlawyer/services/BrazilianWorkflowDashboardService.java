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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Implementação EJB para consolidação de métricas do Dashboard Operacional do Workflow Brasileiro.
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class BrazilianWorkflowDashboardService implements BrazilianWorkflowDashboardServiceLocal, BrazilianWorkflowDashboardServiceRemote {

    private static final Logger log = Logger.getLogger(BrazilianWorkflowDashboardService.class.getName());

    @PersistenceContext(unitName = "j-lawyer-server-ejbPU")
    private EntityManager em;

    @EJB
    private BrazilianPublicationServiceLocal publicationService;

    @EJB
    private BrazilianTaskServiceLocal taskService;

    @Override
    public WorkflowDashboardDTO getDashboard(String currentUser) throws Exception {
        WorkflowDashboardDTO dto = new WorkflowDashboardDTO();

        // 1. Métricas de Publicações
        try {
            TypedQuery<Long> qNew = em.createQuery("SELECT COUNT(p) FROM BrPublication p WHERE p.status = 'NOVA'", Long.class);
            dto.setTotalNewPublications(qNew.getSingleResult());

            TypedQuery<Long> qUnread = em.createQuery("SELECT COUNT(p) FROM BrPublication p WHERE p.readStatus = 'UNREAD' AND p.status != 'ARQUIVADA'", Long.class);
            dto.setTotalUnreadPublications(qUnread.getSingleResult());

            TypedQuery<Long> qUntreated = em.createQuery("SELECT COUNT(p) FROM BrPublication p WHERE p.treatmentStatus = 'NAO_TRATADA' AND p.status != 'ARQUIVADA'", Long.class);
            dto.setTotalUntreatedPublications(qUntreated.getSingleResult());
        } catch (Exception ex) {
            log.warn("Erro ao calcular métricas de publicações: " + ex.getMessage());
        }

        // 2. Datas de Referência
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date startOfToday = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        Date endOfToday = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date endOfNext7Days = cal.getTime();

        // 3. Métricas de Tarefas
        try {
            TypedQuery<Long> qOpenTasks = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED')", Long.class);
            dto.setTotalOpenTasks(qOpenTasks.getSingleResult());

            TypedQuery<Long> qOverdue = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.dueDate < :startOfToday", Long.class);
            qOverdue.setParameter("startOfToday", startOfToday);
            dto.setTotalOverdueTasks(qOverdue.getSingleResult());

            TypedQuery<Long> qToday = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.dueDate >= :startOfToday AND t.dueDate <= :endOfToday", Long.class);
            qToday.setParameter("startOfToday", startOfToday);
            qToday.setParameter("endOfToday", endOfToday);
            dto.setTotalDueTodayTasks(qToday.getSingleResult());

            TypedQuery<Long> qNext7 = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.dueDate >= :startOfToday AND t.dueDate <= :endOfNext7Days", Long.class);
            qNext7.setParameter("startOfToday", startOfToday);
            qNext7.setParameter("endOfNext7Days", endOfNext7Days);
            dto.setTotalDueNext7DaysTasks(qNext7.getSingleResult());

            if (currentUser != null && !currentUser.trim().isEmpty()) {
                TypedQuery<Long> qMy = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.assignedUser = :user", Long.class);
                qMy.setParameter("user", currentUser.trim());
                dto.setTotalMyOpenTasks(qMy.getSingleResult());
            }

            // Distribuição por Prioridade
            TypedQuery<Long> qUrgent = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.priority = 'URGENT'", Long.class);
            dto.setUrgentTasksCount(qUrgent.getSingleResult());

            TypedQuery<Long> qHigh = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.priority = 'HIGH'", Long.class);
            dto.setHighTasksCount(qHigh.getSingleResult());

            TypedQuery<Long> qNormal = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.priority = 'NORMAL'", Long.class);
            dto.setNormalTasksCount(qNormal.getSingleResult());

            TypedQuery<Long> qLow = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status NOT IN ('DONE', 'CANCELLED') AND t.priority = 'LOW'", Long.class);
            dto.setLowTasksCount(qLow.getSingleResult());

            // Distribuição por Status
            TypedQuery<Long> qTodo = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status = 'TODO'", Long.class);
            dto.setTodoCount(qTodo.getSingleResult());

            TypedQuery<Long> qProg = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status = 'IN_PROGRESS'", Long.class);
            dto.setInProgressCount(qProg.getSingleResult());

            TypedQuery<Long> qWait = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status = 'WAITING'", Long.class);
            dto.setWaitingCount(qWait.getSingleResult());

            TypedQuery<Long> qDone = em.createQuery("SELECT COUNT(t) FROM BrTask t WHERE t.status = 'DONE' AND t.completedAt >= :startOfToday", Long.class);
            qDone.setParameter("startOfToday", startOfToday);
            dto.setDoneRecentlyCount(qDone.getSingleResult());
        } catch (Exception ex) {
            log.warn("Erro ao calcular métricas de tarefas: " + ex.getMessage());
        }

        // 4. Listas de Ação Rápida
        try {
            if (publicationService != null) {
                PublicationFilterDTO pubFilter = new PublicationFilterDTO();
                pubFilter.setTreatmentStatus(BrPublication.TREATMENT_NAO_TRATADA);
                pubFilter.setPageSize(5);
                dto.setUrgentPublications(publicationService.listPublications(pubFilter));
            }

            if (taskService != null) {
                TaskFilterDTO overdueFilter = new TaskFilterDTO();
                overdueFilter.setOverdue(true);
                overdueFilter.setPageSize(5);
                dto.setUrgentOverdueTasks(taskService.listTasks(overdueFilter));

                TaskFilterDTO todayFilter = new TaskFilterDTO();
                todayFilter.setDueToday(true);
                todayFilter.setPageSize(5);
                dto.setTodayTasks(taskService.listTasks(todayFilter));
            }
        } catch (Exception ex) {
            log.warn("Erro ao carregar listas rápidas do dashboard: " + ex.getMessage());
        }

        return dto;
    }
}