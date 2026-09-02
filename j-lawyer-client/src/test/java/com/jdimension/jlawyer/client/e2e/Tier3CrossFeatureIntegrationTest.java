/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.e2e;

import com.jdimension.jlawyer.client.e2e.fixtures.BrazilianLegalFixtures;
import com.jdimension.jlawyer.client.e2e.fixtures.MockWorkflowContext;
import com.jdimension.jlawyer.domain.legal.cnj.*;
import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.services.*;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static org.junit.Assert.*;

/**
 * TIER 3: Suíte de Integração Cruzada de Funcionalidades (Pairwise & Multi-Feature Interactions).
 * Valida a interoperabilidade entre validações de domínio, formulários UI, publicações, tarefas e agenda.
 *
 * @author BR-LAWYER Team
 */
public class Tier3CrossFeatureIntegrationTest {

    private MockWorkflowContext context;
    private Locale ptBrLocale;

    @Before
    public void setUp() {
        context = new MockWorkflowContext();
        ptBrLocale = new Locale("pt", "BR");
        Locale.setDefault(ptBrLocale);
    }

    // =========================================================================
    // Scenario 1: CNJ Case Creation + CPF/CNPJ Parties + Case Linking
    // =========================================================================
    @Test
    public void testCrossFeature_CaseCreationWithPartiesAndCnj() {
        // 1. Validação e Parse do CNJ TJSP
        String cnjString = BrazilianLegalFixtures.VALID_CNJ_TJSP;
        assertTrue("CNJ deve ser válido", CnjNumberValidator.isValid(cnjString));
        CnjNumber cnj = CnjNumberValidator.parse(cnjString);
        assertNotNull(cnj);

        // 2. Criação do DTO do Processo
        BrazilianCaseDetailsDTO caseDto = new BrazilianCaseDetailsDTO("case-tjsp-001");
        caseDto.setCnjNumber(cnj.getFormatted());
        caseDto.setCourtCode("TJSP");
        caseDto.setJusticeSegment(cnj.getJusticeSegment());
        caseDto.setCourtUnit("1ª Vara Cível Central");
        caseDto.setTpuClassCode(BrazilianLegalFixtures.TPU_PROCEDIMENTO_COMUM.getCode());
        caseDto.setTpuClassName(BrazilianLegalFixtures.TPU_PROCEDIMENTO_COMUM.getName());

        // 3. Validação das Partes (Autor = Pessoa Física com CPF, Réu = Pessoa Jurídica com CNPJ)
        String autorCpf = BrazilianLegalFixtures.VALID_CPF_1;
        String reuCnpj = BrazilianLegalFixtures.VALID_CNPJ_PETROBRAS;
        assertTrue("CPF do autor deve ser válido", CpfCnpjValidator.isValidCpf(autorCpf));
        assertTrue("CNPJ do réu deve ser válido", CpfCnpjValidator.isValidCnpj(reuCnpj));

        // 4. Assuntos TPU Normalizados
        List<CaseTpuSubjectDTO> subjects = new ArrayList<>();
        subjects.add(new CaseTpuSubjectDTO("case-tjsp-001", BrazilianLegalFixtures.TPU_DANO_MORAL.getCode(),
            BrazilianLegalFixtures.TPU_DANO_MORAL.getName(), true));
        caseDto.setNormalizedSubjects(subjects);

        // 5. Verificações de Consistência Cruzada
        assertEquals("00012340820238260100", caseDto.getCnjNumberClean());
        assertEquals("TJSP", caseDto.getCourtCode());
        assertEquals(1, caseDto.getNormalizedSubjects().size());
        assertTrue(caseDto.getNormalizedSubjects().get(0).isPrimarySubject());
    }

    // =========================================================================
    // Scenario 2: Publication Ingestion + Auto CNJ Match + Triage Task Creation
    // =========================================================================
    @Test
    public void testCrossFeature_PublicationIngestionAndTaskCreationFlow() throws Exception {
        BrazilianPublicationServiceRemote pubService = context.getPublicationService();
        BrazilianTaskServiceRemote taskService = context.getTaskService();

        // 1. Ingestão de Publicação DJEN
        PublicationDetailDTO newPub = new PublicationDetailDTO();
        newPub.setId("pub-djen-999");
        newPub.setCnjNumber(BrazilianLegalFixtures.VALID_CNJ_TJSP);
        newPub.setCourtCode("TJSP");
        newPub.setSource("DJEN");
        newPub.setContent("Intimação das partes para manifestação em 15 dias.");
        newPub.setStatus("NEW");
        newPub.setReadStatus("UNREAD");
        newPub.setProcessId("case-tjsp-001");

        pubService.savePublication(newPub, "djen_importer");

        // 2. Consulta de Publicações Pendentes
        PublicationFilterDTO filter = new PublicationFilterDTO();
        filter.setStatus("NEW");
        List<PublicationOverviewDTO> untreated = pubService.listPublications(filter);
        assertTrue(untreated.stream().anyMatch(p -> "pub-djen-999".equals(p.getId())));

        // 3. Criação de Tarefa vinculada com Prazo Fatal (+15 dias)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date dueDate = cal.getTime();

        TaskDetailDTO task = new TaskDetailDTO();
        task.setTitle("Manifestação sobre Decisão Interlocutória");
        task.setCategory("PETICAO");
        task.setPriority("HIGH");
        task.setStatus("TODO");
        task.setDueDate(dueDate);
        task.setDueTime("18:00");
        task.setProcessId(newPub.getProcessId());
        task.setCnjNumber(newPub.getCnjNumber());
        task.setAssignedUser("advogado_responsavel");

        TaskDetailDTO savedTask = taskService.saveTask(task, "advogado_responsavel", true);
        assertNotNull(savedTask.getId());
        assertNotNull("Deve possuir CalendarEventId vinculado à agenda oficial", savedTask.getCalendarEventId());

        // 4. Conclusão da Triagem da Publicação
        PublicationTreatRequestDTO treatReq = new PublicationTreatRequestDTO();
        treatReq.setUser("advogado_responsavel");
        treatReq.setNotes("Tarefa criada para cumprimento");

        PublicationDetailDTO treated = pubService.treatPublication(newPub.getId(), treatReq);

        assertEquals("TREATED", treated.getStatus());
        assertEquals("READ", treated.getReadStatus());

        // 5. Verificação da Lista de Tarefas
        TaskFilterDTO taskFilter = new TaskFilterDTO();
        taskFilter.setStatus("TODO");
        List<TaskOverviewDTO> openTasks = taskService.listTasks(taskFilter);
        assertTrue(openTasks.stream().anyMatch(t -> savedTask.getId().equals(t.getId())));
    }

    // =========================================================================
    // Scenario 3: Dashboard KPI Aggregation + Read State Propagation
    // =========================================================================
    @Test
    public void testCrossFeature_DashboardKpiPropagation() throws Exception {
        BrazilianPublicationServiceRemote pubService = context.getPublicationService();
        BrazilianWorkflowDashboardServiceRemote dashService = context.getDashboardService();

        // 1. Estado Inicial do Dashboard
        WorkflowDashboardDTO initialDash = dashService.getDashboard("admin");
        long initialNew = initialDash.getTotalNewPublications();
        assertTrue(initialNew >= 1);

        // 2. Ingestão de mais 2 publicações
        PublicationDetailDTO pub2 = BrazilianLegalFixtures.createSamplePublicationTRT2();
        pub2.setId("pub-trt2-batch2");
        pubService.savePublication(pub2, "sync_worker");

        WorkflowDashboardDTO updatedDash = dashService.getDashboard("admin");
        assertEquals(initialNew + 1, updatedDash.getTotalNewPublications());

        // 3. Tratamento de publicação
        PublicationTreatRequestDTO treatReq = new PublicationTreatRequestDTO();
        treatReq.setUser("admin");
        treatReq.setNotes("Ok");
        pubService.treatPublication("pub-trt2-batch2", treatReq);

        WorkflowDashboardDTO afterTriageDash = dashService.getDashboard("admin");
        assertEquals(initialNew, afterTriageDash.getTotalNewPublications());
    }

    // =========================================================================
    // Scenario 4: Brazilian Currency Formatting + Financial Claim Calculation
    // =========================================================================
    @Test
    public void testCrossFeature_FinancialClaimCalculationAndFormatting() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(ptBrLocale);
        DecimalFormat currencyFmt = new DecimalFormat("R$ #,##0.00", symbols);

        double principal = 100000.00;
        double honorarios = principal * 0.20; // 20% de honorários sucumbenciais
        double custas = 1542.50;
        double totalClaim = principal + honorarios + custas;

        assertEquals("R$ 100.000,00", currencyFmt.format(principal));
        assertEquals("R$ 20.000,00", currencyFmt.format(honorarios));
        assertEquals("R$ 1.542,50", currencyFmt.format(custas));
        assertEquals("R$ 121.542,50", currencyFmt.format(totalClaim));
    }

    // =========================================================================
    // Scenario 5: OAB Multiple Registration + Court Jurisdiction Validation
    // =========================================================================
    @Test
    public void testCrossFeature_LawyerOabAndCourtJurisdiction() throws Exception {
        BrazilianLegalDomainServiceRemote domainService = context.getLegalDomainService();

        // 1. Cadastro e Validação de OAB
        LawyerRegistrationDTO oabSp = new LawyerRegistrationDTO("123456", "SP", "PRINCIPAL");
        LawyerRegistrationDTO savedOab = domainService.saveLawyerRegistration(oabSp);
        assertNotNull(savedOab);
        assertEquals("OAB/SP 123456 (PRINCIPAL)", savedOab.getFormattedRegistration());
        assertEquals("ATIVO", savedOab.getStatus());

        // 2. Listagem de Tribunais compatíveis em SP (Estadual e Federal)
        List<JudiciaryCourtDTO> spCourts = domainService.listCourts();
        assertTrue(spCourts.stream().anyMatch(c -> "TJSP".equals(c.getCode())));
        assertTrue(spCourts.stream().anyMatch(c -> "TRF3".equals(c.getCode())));
    }
}
