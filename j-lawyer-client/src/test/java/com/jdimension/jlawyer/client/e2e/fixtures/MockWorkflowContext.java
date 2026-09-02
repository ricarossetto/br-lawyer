/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.e2e.fixtures;

import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.services.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Contexto de simulação em memória para os serviços de backend e ambiente de execução E2E.
 * Implementa com precisão as interfaces remotas EJB do BR-LAWYER.
 *
 * @author BR-LAWYER Team
 */
public class MockWorkflowContext {

    private final Map<String, PublicationDetailDTO> publications = new ConcurrentHashMap<>();
    private final Map<String, TaskDetailDTO> tasks = new ConcurrentHashMap<>();
    private final Map<String, JudiciaryCourtDTO> courts = new ConcurrentHashMap<>();
    private final Map<Integer, TpuClassDTO> tpuClasses = new ConcurrentHashMap<>();
    private final Map<Integer, TpuSubjectDTO> tpuSubjects = new ConcurrentHashMap<>();
    private final Map<String, BrazilianCaseDetailsDTO> cases = new ConcurrentHashMap<>();
    private final Map<String, List<LawyerRegistrationDTO>> lawyerRegistrations = new ConcurrentHashMap<>();

    public MockWorkflowContext() {
        initDefaultCatalog();
    }

    private void initDefaultCatalog() {
        for (JudiciaryCourtDTO court : BrazilianLegalFixtures.SAMPLE_COURTS) {
            courts.put(court.getCode(), court);
        }
        tpuClasses.put(BrazilianLegalFixtures.TPU_PROCEDIMENTO_COMUM.getCode(), BrazilianLegalFixtures.TPU_PROCEDIMENTO_COMUM);
        tpuClasses.put(BrazilianLegalFixtures.TPU_EXECUCAO_TITULO.getCode(), BrazilianLegalFixtures.TPU_EXECUCAO_TITULO);
        tpuClasses.put(BrazilianLegalFixtures.TPU_RECLAMACAO_TRABALHISTA.getCode(), BrazilianLegalFixtures.TPU_RECLAMACAO_TRABALHISTA);
        tpuClasses.put(BrazilianLegalFixtures.TPU_EXECUCAO_FISCAL.getCode(), BrazilianLegalFixtures.TPU_EXECUCAO_FISCAL);

        tpuSubjects.put(BrazilianLegalFixtures.TPU_DANO_MORAL.getCode(), BrazilianLegalFixtures.TPU_DANO_MORAL);
        tpuSubjects.put(BrazilianLegalFixtures.TPU_INADIMPLEMENTO.getCode(), BrazilianLegalFixtures.TPU_INADIMPLEMENTO);
        tpuSubjects.put(BrazilianLegalFixtures.TPU_HORAS_EXTRAS.getCode(), BrazilianLegalFixtures.TPU_HORAS_EXTRAS);
        tpuSubjects.put(BrazilianLegalFixtures.TPU_ICMS.getCode(), BrazilianLegalFixtures.TPU_ICMS);

        // Seed initial publication
        PublicationDetailDTO p1 = BrazilianLegalFixtures.createSamplePublicationTJSP();
        publications.put(p1.getId(), p1);
    }

    public BrazilianPublicationServiceRemote getPublicationService() {
        return new BrazilianPublicationServiceRemote() {
            @Override
            public PublicationDetailDTO getPublication(String id) {
                return publications.get(id);
            }

            @Override
            public PublicationDetailDTO savePublication(PublicationDetailDTO dto, String actor) {
                if (dto.getId() == null || dto.getId().trim().isEmpty()) {
                    dto.setId(UUID.randomUUID().toString());
                    dto.setCreatedAt(new Date());
                }
                dto.setUpdatedAt(new Date());
                publications.put(dto.getId(), dto);
                return dto;
            }

            @Override
            public List<PublicationOverviewDTO> listPublications(PublicationFilterDTO filter) {
                return publications.values().stream()
                    .filter(p -> filter == null || filter.getStatus() == null || "ALL".equalsIgnoreCase(filter.getStatus()) || filter.getStatus().equalsIgnoreCase(p.getStatus()))
                    .filter(p -> filter == null || filter.getReadStatus() == null || "ALL".equalsIgnoreCase(filter.getReadStatus()) || filter.getReadStatus().equalsIgnoreCase(p.getReadStatus()))
                    .filter(p -> filter == null || filter.getSearchText() == null || filter.getSearchText().trim().isEmpty() ||
                        (p.getContent() != null && p.getContent().toLowerCase().contains(filter.getSearchText().toLowerCase())) ||
                        (p.getCnjNumber() != null && p.getCnjNumber().contains(filter.getSearchText())))
                    .map(p -> {
                        PublicationOverviewDTO ov = new PublicationOverviewDTO();
                        ov.setId(p.getId());
                        ov.setCnjNumber(p.getCnjNumber());
                        ov.setCourtCode(p.getCourtCode());
                        ov.setPublicationDate(p.getPublicationDate());
                        ov.setSource(p.getSource());
                        ov.setStatus(p.getStatus());
                        ov.setReadStatus(p.getReadStatus());
                        ov.setProcessId(p.getProcessId());
                        return ov;
                    })
                    .collect(Collectors.toList());
            }

            @Override
            public long countPublications(PublicationFilterDTO filter) {
                return listPublications(filter).size();
            }

            @Override
            public PublicationDetailDTO markRead(String publicationId, boolean read, String user) {
                PublicationDetailDTO pub = publications.get(publicationId);
                if (pub != null) {
                    pub.setReadStatus(read ? "READ" : "UNREAD");
                    if (read) pub.setReadAt(new Date());
                }
                return pub;
            }

            @Override
            public PublicationDetailDTO linkToCase(String publicationId, PublicationLinkRequestDTO request) {
                PublicationDetailDTO pub = publications.get(publicationId);
                if (pub != null && request != null) {
                    pub.setProcessId(request.getProcessId());
                }
                return pub;
            }

            @Override
            public PublicationDetailDTO unlinkFromCase(String publicationId, String user) {
                PublicationDetailDTO pub = publications.get(publicationId);
                if (pub != null) {
                    pub.setProcessId(null);
                }
                return pub;
            }

            @Override
            public PublicationDetailDTO treatPublication(String publicationId, PublicationTreatRequestDTO request) {
                PublicationDetailDTO pub = publications.get(publicationId);
                if (pub != null && request != null) {
                    pub.setStatus("TREATED");
                    pub.setTreatmentStatus("TREATED");
                    pub.setReadStatus("READ");
                    pub.setTreatedAt(new Date());
                    pub.setTreatedBy(request.getUser());
                    pub.setNotes(request.getNotes());
                }
                return pub;
            }

            @Override
            public PublicationDetailDTO archivePublication(String publicationId, String user, String reason) {
                PublicationDetailDTO pub = publications.get(publicationId);
                if (pub != null) {
                    pub.setStatus("ARCHIVED");
                    pub.setArchivedAt(new Date());
                    pub.setArchivedBy(user);
                    pub.setNotes(reason);
                }
                return pub;
            }

            @Override
            public PublicationDetailDTO assignPublication(String publicationId, String assignedUser, String actor) {
                PublicationDetailDTO pub = publications.get(publicationId);
                if (pub != null) {
                    pub.setAssignedUser(assignedUser);
                }
                return pub;
            }

            @Override
            public PublicationDetailDTO deduplicateAndIngest(PublicationDetailDTO dto, String actor) {
                return savePublication(dto, actor);
            }

            @Override
            public List<PublicationEventDTO> getPublicationHistory(String publicationId) {
                PublicationDetailDTO pub = publications.get(publicationId);
                return pub != null ? pub.getEvents() : Collections.emptyList();
            }

            @Override
            public void deletePublication(String publicationId, String user) {
                publications.remove(publicationId);
            }
        };
    }

    public BrazilianTaskServiceRemote getTaskService() {
        return new BrazilianTaskServiceRemote() {
            @Override
            public TaskDetailDTO getTask(String id) {
                return tasks.get(id);
            }

            @Override
            public TaskDetailDTO saveTask(TaskDetailDTO dto, String user, boolean syncCalendar) {
                if (dto.getId() == null || dto.getId().trim().isEmpty()) {
                    dto.setId(UUID.randomUUID().toString());
                    dto.setCreatedAt(new Date());
                    dto.setCreatedBy(user);
                }
                dto.setUpdatedAt(new Date());
                if (syncCalendar) {
                    dto.setCalendarEventId("frist-9999");
                }
                tasks.put(dto.getId(), dto);
                return dto;
            }

            @Override
            public List<TaskOverviewDTO> listTasks(TaskFilterDTO filter) {
                return tasks.values().stream()
                    .filter(t -> filter == null || filter.getStatus() == null || "ALL".equalsIgnoreCase(filter.getStatus()) || filter.getStatus().equalsIgnoreCase(t.getStatus()))
                    .filter(t -> filter == null || filter.getPriority() == null || "ALL".equalsIgnoreCase(filter.getPriority()) || filter.getPriority().equalsIgnoreCase(t.getPriority()))
                    .map(t -> {
                        TaskOverviewDTO ov = new TaskOverviewDTO();
                        ov.setId(t.getId());
                        ov.setTitle(t.getTitle());
                        ov.setPriority(t.getPriority());
                        ov.setStatus(t.getStatus());
                        ov.setDueDate(t.getDueDate());
                        ov.setDueTime(t.getDueTime());
                        ov.setAssignedUser(t.getAssignedUser());
                        ov.setProcessId(t.getProcessId());
                        ov.setCnjNumber(t.getCnjNumber());
                        ov.setCalendarEventId(t.getCalendarEventId());
                        ov.setOverdue(t.isOverdue());
                        ov.setDueToday(t.isDueToday());
                        return ov;
                    })
                    .collect(Collectors.toList());
            }

            @Override
            public long countTasks(TaskFilterDTO filter) {
                return listTasks(filter).size();
            }

            @Override
            public TaskDetailDTO changeStatus(String taskId, TaskStatusChangeDTO changeRequest) {
                TaskDetailDTO task = tasks.get(taskId);
                if (task != null && changeRequest != null) {
                    task.setStatus(changeRequest.getNewStatus());
                    if ("DONE".equalsIgnoreCase(changeRequest.getNewStatus())) {
                        task.setCompletedAt(new Date());
                        task.setCompletedBy(changeRequest.getUser());
                    }
                    task.setUpdatedAt(new Date());
                }
                return task;
            }

            @Override
            public TaskDetailDTO assignTask(String taskId, String assignedUser, String actor) {
                TaskDetailDTO task = tasks.get(taskId);
                if (task != null) {
                    task.setAssignedUser(assignedUser);
                    task.setUpdatedAt(new Date());
                }
                return task;
            }

            @Override
            public TaskCommentDTO addComment(String taskId, String userName, String commentText) {
                TaskCommentDTO c = new TaskCommentDTO();
                c.setId(UUID.randomUUID().toString());
                c.setTaskId(taskId);
                c.setUserName(userName);
                c.setCommentText(commentText);
                c.setCreatedAt(new Date());
                return c;
            }

            @Override
            public List<TaskCommentDTO> getComments(String taskId) {
                TaskDetailDTO task = tasks.get(taskId);
                return task != null ? task.getComments() : Collections.emptyList();
            }

            @Override
            public TaskChecklistItemDTO addChecklistItem(String taskId, String title, int order) {
                TaskChecklistItemDTO item = new TaskChecklistItemDTO();
                item.setId(UUID.randomUUID().toString());
                item.setTaskId(taskId);
                item.setTitle(title);
                item.setItemOrder(order);
                item.setDone(false);
                return item;
            }

            @Override
            public TaskChecklistItemDTO toggleChecklistItem(String checklistItemId, boolean done, String user) {
                TaskChecklistItemDTO item = new TaskChecklistItemDTO();
                item.setId(checklistItemId);
                item.setDone(done);
                return item;
            }

            @Override
            public void deleteChecklistItem(String checklistItemId) {
                // mock delete
            }

            @Override
            public KanbanBoardDTO getKanbanBoard(String assignedUser, String processId) {
                return new KanbanBoardDTO();
            }

            @Override
            public void deleteTask(String taskId, String user) {
                tasks.remove(taskId);
            }
        };
    }

    public BrazilianWorkflowDashboardServiceRemote getDashboardService() {
        return new BrazilianWorkflowDashboardServiceRemote() {
            @Override
            public WorkflowDashboardDTO getDashboard(String currentUser) {
                WorkflowDashboardDTO dto = new WorkflowDashboardDTO();
                dto.setTotalNewPublications(publications.values().stream().filter(p -> "NEW".equalsIgnoreCase(p.getStatus())).count());
                dto.setTotalUnreadPublications(publications.values().stream().filter(p -> "UNREAD".equalsIgnoreCase(p.getReadStatus())).count());
                dto.setTotalUntreatedPublications(publications.values().stream().filter(p -> !"TREATED".equalsIgnoreCase(p.getStatus()) && !"ARCHIVED".equalsIgnoreCase(p.getStatus())).count());
                dto.setTotalOpenTasks(tasks.values().stream().filter(t -> !"DONE".equalsIgnoreCase(t.getStatus()) && !"CANCELLED".equalsIgnoreCase(t.getStatus())).count());
                dto.setTotalOverdueTasks(tasks.values().stream().filter(TaskDetailDTO::isOverdue).count());
                dto.setTotalDueTodayTasks(tasks.values().stream().filter(TaskDetailDTO::isDueToday).count());
                return dto;
            }
        };
    }

    public BrazilianLegalDomainServiceRemote getLegalDomainService() {
        return new BrazilianLegalDomainServiceRemote() {
            @Override
            public List<LawyerRegistrationDTO> getLawyerRegistrations(String contactId) {
                return lawyerRegistrations.getOrDefault(contactId, Collections.emptyList());
            }

            @Override
            public LawyerRegistrationDTO saveLawyerRegistration(LawyerRegistrationDTO registration) {
                if (registration.getId() == null) {
                    registration.setId(UUID.randomUUID().toString());
                }
                registration.setStatus("ATIVO");
                return registration;
            }

            @Override
            public void deleteLawyerRegistration(String registrationId) {
                // mock delete
            }

            @Override
            public BrazilianCaseDetailsDTO getCaseDetails(String caseId) {
                return cases.computeIfAbsent(caseId, id -> {
                    BrazilianCaseDetailsDTO dto = new BrazilianCaseDetailsDTO(id);
                    dto.setCnjNumber(BrazilianLegalFixtures.VALID_CNJ_TJSP);
                    dto.setCourtCode("TJSP");
                    dto.setJusticeSegment(8);
                    return dto;
                });
            }

            @Override
            public BrazilianCaseDetailsDTO saveCaseDetails(BrazilianCaseDetailsDTO details) {
                if (details != null && details.getCaseId() != null) {
                    cases.put(details.getCaseId(), details);
                }
                return details;
            }

            @Override
            public BrazilianCaseDetailsDTO findCaseByCnjNumber(String cnjNumber) {
                return cases.values().stream()
                    .filter(c -> cnjNumber != null && cnjNumber.equals(c.getCnjNumber()))
                    .findFirst()
                    .orElse(null);
            }

            @Override
            public List<CaseTpuSubjectDTO> getCaseTpuSubjects(String caseId) {
                BrazilianCaseDetailsDTO c = cases.get(caseId);
                return c != null ? c.getNormalizedSubjects() : Collections.emptyList();
            }

            @Override
            public void setCaseTpuSubjects(String caseId, List<CaseTpuSubjectDTO> subjects) {
                BrazilianCaseDetailsDTO c = getCaseDetails(caseId);
                if (c != null) {
                    c.setNormalizedSubjects(subjects);
                }
            }

            @Override
            public List<JudiciaryCourtDTO> listCourts() {
                return new ArrayList<>(courts.values());
            }

            @Override
            public List<JudiciaryCourtDTO> listCourtsBySegment(int justiceSegment) {
                return courts.values().stream()
                    .filter(c -> c.getJusticeSegment() == justiceSegment)
                    .collect(Collectors.toList());
            }

            @Override
            public JudiciaryCourtDTO getCourtByCode(String courtCode) {
                return courts.get(courtCode);
            }

            @Override
            public List<TpuClassDTO> listTpuClasses() {
                return new ArrayList<>(tpuClasses.values());
            }

            @Override
            public List<TpuClassDTO> searchTpuClasses(String query) {
                return tpuClasses.values().stream()
                    .filter(c -> query == null || c.getName().toLowerCase().contains(query.toLowerCase()) || String.valueOf(c.getCode()).contains(query))
                    .collect(Collectors.toList());
            }

            @Override
            public List<TpuSubjectDTO> listTpuSubjects() {
                return new ArrayList<>(tpuSubjects.values());
            }

            @Override
            public List<TpuSubjectDTO> searchTpuSubjects(String query) {
                return tpuSubjects.values().stream()
                    .filter(s -> query == null || s.getName().toLowerCase().contains(query.toLowerCase()) || String.valueOf(s.getCode()).contains(query))
                    .collect(Collectors.toList());
            }
        };
    }
}
