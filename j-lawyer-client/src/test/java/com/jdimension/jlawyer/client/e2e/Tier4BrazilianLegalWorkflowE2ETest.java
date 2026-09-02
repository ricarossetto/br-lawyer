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

import java.util.*;

import static org.junit.Assert.*;

/**
 * TIER 4: Suíte E2E de Cenários Reais de Workflow Jurídico Brasileiro.
 * Simula ciclos completos de atendimento, autuação processual, publicações DJEN/DEJT,
 * prazos do CPC e gestão de contingências em escritórios de advocacia no Brasil.
 *
 * @author BR-LAWYER Team
 */
public class Tier4BrazilianLegalWorkflowE2ETest {

    private MockWorkflowContext context;
    private Locale ptBrLocale;

    @Before
    public void setUp() {
        context = new MockWorkflowContext();
        ptBrLocale = new Locale("pt", "BR");
        Locale.setDefault(ptBrLocale);
    }

    // =========================================================================
    // WORKFLOW 1: Ação de Cobrança Cível (TJSP) - Procedimento Comum
    // =========================================================================
    @Test
    public void testWorkflow_AcaoCobrancaCivel_TJSP() throws Exception {
        BrazilianPublicationServiceRemote pubService = context.getPublicationService();
        BrazilianTaskServiceRemote taskService = context.getTaskService();
        BrazilianWorkflowDashboardServiceRemote dashService = context.getDashboardService();

        // 1. Cadastro do Cliente (Pessoa Física) e Validação de Documentos
        String clientCpf = BrazilianLegalFixtures.VALID_CPF_1;
        String clientCep = BrazilianLegalFixtures.VALID_CEP_SP;
        assertTrue("CPF do cliente deve ser válido", CpfCnpjValidator.isValidCpf(clientCpf));
        assertEquals("01001-000", clientCep);

        // 2. Autuação do Processo Cível no TJSP
        String cnjProcesso = BrazilianLegalFixtures.VALID_CNJ_TJSP;
        assertTrue(CnjNumberValidator.isValid(cnjProcesso));
        CnjNumber cnj = CnjNumberValidator.parse(cnjProcesso);
        assertEquals(8, cnj.getJusticeSegment()); // Justiça Estadual
        assertEquals(26, cnj.getCourtNumber());  // São Paulo

        BrazilianCaseDetailsDTO caseDto = new BrazilianCaseDetailsDTO("case-civel-001");
        caseDto.setCnjNumber(cnj.getFormatted());
        caseDto.setCourtCode("TJSP");
        caseDto.setCourtUnit("1ª Vara Cível do Foro Central da Comarca da Capital");
        caseDto.setTpuClassCode(BrazilianLegalFixtures.TPU_PROCEDIMENTO_COMUM.getCode());
        caseDto.setTpuClassName(BrazilianLegalFixtures.TPU_PROCEDIMENTO_COMUM.getName());

        // 3. Ingestão da Publicação do DJEN (Intimação para Apresentar Contestação)
        PublicationDetailDTO pub = new PublicationDetailDTO();
        pub.setId("pub-tjsp-contestacao");
        pub.setCnjNumber(caseDto.getCnjNumber());
        pub.setCourtCode(caseDto.getCourtCode());
        pub.setSource("DJEN");
        pub.setPublicationType("INTIMACAO");
        pub.setContent("Fica o réu intimado para contestar no prazo de 15 (quinze) dias úteis.");
        pub.setStatus("NEW");
        pub.setReadStatus("UNREAD");
        pub.setProcessId(caseDto.getCaseId());

        pubService.savePublication(pub, "djen_worker");

        // 4. Triagem Operacional: Criação de Tarefa com Prazo Fatal de 15 Dias
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date prazoFatal = cal.getTime();

        TaskDetailDTO task = new TaskDetailDTO();
        task.setTitle("Elaborar e Protocolar Contestação Cível");
        task.setCategory("PETICAO");
        task.setPriority("URGENT");
        task.setStatus("TODO");
        task.setDueDate(prazoFatal);
        task.setDueTime("18:00");
        task.setProcessId(caseDto.getCaseId());
        task.setCnjNumber(caseDto.getCnjNumber());
        task.setAssignedUser("advogado_civel");

        TaskDetailDTO savedTask = taskService.saveTask(task, "advogado_civel", true);
        assertNotNull(savedTask.getId());
        assertNotNull("Deve estar sincronizado com Frist da agenda", savedTask.getCalendarEventId());

        // 5. Atualização da Publicação para TREATED
        PublicationTreatRequestDTO treatReq = new PublicationTreatRequestDTO();
        treatReq.setUser("advogado_civel");
        treatReq.setNotes("Prazo cadastrado e atribuído ao Dr. Carlos");

        PublicationDetailDTO treatedPub = pubService.treatPublication(pub.getId(), treatReq);

        assertEquals("TREATED", treatedPub.getStatus());
        assertEquals("READ", treatedPub.getReadStatus());

        // 6. Execução e Conclusão da Tarefa
        TaskStatusChangeDTO progressReq = new TaskStatusChangeDTO();
        progressReq.setNewStatus("IN_PROGRESS");
        progressReq.setUser("advogado_civel");
        taskService.changeStatus(savedTask.getId(), progressReq);

        TaskStatusChangeDTO doneReq = new TaskStatusChangeDTO();
        doneReq.setNewStatus("DONE");
        doneReq.setUser("advogado_civel");
        TaskDetailDTO completedTask = taskService.changeStatus(savedTask.getId(), doneReq);

        assertEquals("DONE", completedTask.getStatus());
        assertNotNull(completedTask.getCompletedAt());

        // 7. Auditoria do Dashboard
        WorkflowDashboardDTO dashboard = dashService.getDashboard("advogado_civel");
        assertNotNull(dashboard);
        assertEquals(0L, dashboard.getTotalOpenTasks());
    }

    // =========================================================================
    // WORKFLOW 2: Reclamação Trabalhista (TRT2) - Rito Ordinário
    // =========================================================================
    @Test
    public void testWorkflow_ReclamacaoTrabalhista_TRT2() throws Exception {
        BrazilianPublicationServiceRemote pubService = context.getPublicationService();
        BrazilianTaskServiceRemote taskService = context.getTaskService();

        // 1. Dados da Reclamatória (Trabalhador CPF vs Empresa Reclamada CNPJ)
        String trabalhadorCpf = BrazilianLegalFixtures.VALID_CPF_2;
        String empresaCnpj = BrazilianLegalFixtures.VALID_CNPJ_PETROBRAS;
        assertTrue(CpfCnpjValidator.isValidCpf(trabalhadorCpf));
        assertTrue(CpfCnpjValidator.isValidCnpj(empresaCnpj));

        // 2. Processo Trabalhista CNJ TRT2
        String cnjTrabalhista = BrazilianLegalFixtures.VALID_CNJ_TRT2;
        assertTrue(CnjNumberValidator.isValid(cnjTrabalhista));
        CnjNumber cnj = CnjNumberValidator.parse(cnjTrabalhista);
        assertEquals(5, cnj.getJusticeSegment()); // Justiça do Trabalho
        assertEquals(2, cnj.getCourtNumber());   // TRT 2ª Região (SP)

        // 3. Publicação DEJT Notificando Audiência Una
        PublicationDetailDTO pub = new PublicationDetailDTO();
        pub.setId("pub-trt2-audiencia");
        pub.setCnjNumber(cnjTrabalhista);
        pub.setCourtCode("TRT2");
        pub.setSource("DEJT");
        pub.setContent("Audiência designada. Comparecimento obrigatório sob pena de confissão.");
        pub.setStatus("NEW");
        pub.setReadStatus("UNREAD");
        pub.setProcessId("case-trt-202");

        pubService.savePublication(pub, "dejt_worker");

        // 4. Criação de Tarefa de Preparação de Testemunhas e Documentos
        TaskDetailDTO task = new TaskDetailDTO();
        task.setTitle("Preparar Testemunhas e Documentos para Audiência");
        task.setCategory("AUDIENCIA");
        task.setPriority("HIGH");
        task.setStatus("TODO");
        task.setProcessId("case-trt-202");
        task.setCnjNumber(cnjTrabalhista);

        TaskDetailDTO savedTask = taskService.saveTask(task, "advogado_trabalhista", true);
        assertNotNull(savedTask.getId());

        PublicationTreatRequestDTO treatReq = new PublicationTreatRequestDTO();
        treatReq.setUser("advogado_trabalhista");
        treatReq.setNotes("Audiência agendada");
        pubService.treatPublication(pub.getId(), treatReq);

        // 5. Verificação da Lista de Tarefas
        TaskFilterDTO filter = new TaskFilterDTO();
        filter.setStatus("TODO");
        filter.setPriority("HIGH");
        List<TaskOverviewDTO> tasks = taskService.listTasks(filter);
        assertTrue(tasks.stream().anyMatch(t -> savedTask.getId().equals(t.getId())));
    }

    // =========================================================================
    // WORKFLOW 3: Execução Fiscal Federal (TRF3)
    // =========================================================================
    @Test
    public void testWorkflow_ExecucaoFiscal_TRF3() throws Exception {
        BrazilianPublicationServiceRemote pubService = context.getPublicationService();
        BrazilianTaskServiceRemote taskService = context.getTaskService();

        // 1. Processo de Execução Fiscal Federal TRF3
        String cnjFiscal = BrazilianLegalFixtures.VALID_CNJ_TRF3;
        assertTrue(CnjNumberValidator.isValid(cnjFiscal));
        CnjNumber cnj = CnjNumberValidator.parse(cnjFiscal);
        assertEquals(4, cnj.getJusticeSegment()); // Justiça Federal
        assertEquals(3, cnj.getCourtNumber());   // TRF 3ª Região

        // 2. Ingestão de Publicação para Embargos à Execução (+30 dias)
        PublicationDetailDTO pub = new PublicationDetailDTO();
        pub.setId("pub-trf3-fiscal");
        pub.setCnjNumber(cnjFiscal);
        pub.setCourtCode("TRF3");
        pub.setSource("DJEN");
        pub.setContent("Citação em Execução Fiscal - Prazo 30 dias para Embargos");
        pub.setStatus("NEW");
        pub.setReadStatus("UNREAD");
        pub.setProcessId("case-trf3-303");

        pubService.savePublication(pub, "djen_importer");

        // 3. Criação de Tarefa de Embargos à Execução
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);

        TaskDetailDTO task = new TaskDetailDTO();
        task.setTitle("Elaborar Embargos à Execução Fiscal");
        task.setCategory("PETICAO");
        task.setPriority("URGENT");
        task.setStatus("TODO");
        task.setDueDate(cal.getTime());
        task.setProcessId("case-trf3-303");
        task.setCnjNumber(cnjFiscal);

        TaskDetailDTO savedTask = taskService.saveTask(task, "advogado_tributarista", true);
        assertNotNull(savedTask.getId());

        PublicationTreatRequestDTO treatReq = new PublicationTreatRequestDTO();
        treatReq.setUser("advogado_tributarista");
        treatReq.setNotes("Embargos em elaboração");
        pubService.treatPublication(pub.getId(), treatReq);

        assertEquals("TREATED", pubService.getPublication(pub.getId()).getStatus());
    }

    // =========================================================================
    // WORKFLOW 4: Procedimento Recursal no STJ (Recurso Especial)
    // =========================================================================
    @Test
    public void testWorkflow_RecursoEspecial_STJ() throws Exception {
        BrazilianLegalDomainServiceRemote domainService = context.getLegalDomainService();

        // 1. Validação do CNJ no STJ (Segmento 3 = Tribunais Superiores / STJ)
        String cnjStj = BrazilianLegalFixtures.VALID_CNJ_STJ;
        assertTrue(CnjNumberValidator.isValid(cnjStj));
        CnjNumber cnj = CnjNumberValidator.parse(cnjStj);
        assertEquals(3, cnj.getJusticeSegment()); // STJ
        assertEquals(0, cnj.getCourtNumber());   // Tribunal Superior

        // 2. Validação da Habilitação OAB Nacional / Suplementar
        LawyerRegistrationDTO oab = new LawyerRegistrationDTO("456789", "RJ", "PRINCIPAL");
        LawyerRegistrationDTO saved = domainService.saveLawyerRegistration(oab);
        assertEquals("ATIVO", saved.getStatus());
        assertEquals("OAB/RJ 456789 (PRINCIPAL)", saved.getFormattedRegistration());
    }
}
