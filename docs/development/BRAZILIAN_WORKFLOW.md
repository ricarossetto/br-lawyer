# Brazilian Legal Workflow (Publicações, Triagem, Tarefas & Prazos)

## 1. Visão Geral

Este módulo implementa o **Workflow Operacional Brasileiro** no BR-LAWYER, permitindo a gestão ponta a ponta da rotina de escritórios e departamentos jurídicos no Brasil:

```
+-----------------------------------------------------------------------------------+
|                            FLUXO OPERACIONAL BRASILEIRO                           |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [Publicações / Intimações]                                                       |
|             │                                                                     |
|             ▼                                                                     |
|  [Deduplicação Determinística SHA-256]                                            |
|             │                                                                     |
|             ▼                                                                     |
|  [Auto-vinculação por CNJ Limpo (20 dígitos)] ──► [Vinculação Manual se Pendente] |
|             │                                                                     |
|             ▼                                                                     |
|  [Triagem & Leitura Humana (Human-in-the-Loop)]                                   |
|             │                                                                     |
|             ├──► [Dispensar / Arquivar com Motivo]                                |
|             │                                                                     |
|             └──► [Tratar Publicação]                                              |
|                       │                                                           |
|                       ▼                                                           |
|            [Criação de Tarefa Legal]                                              |
|            - Título, Categoria, Prioridade, Responsável                           |
|            - Prazo Fatal (Data e Hora)                                            |
|            - Checklist e Comentários                                              |
|                       │                                                           |
|                       ▼                                                           |
|            [Sincronização com Calendário j-lawyer]                                |
|            - Criação em `case_events` (EVENTTYPE_RESPITE = 20 / Frist)            |
|            - Acompanhamento unificado e lembretes                                 |
|                       │                                                           |
|                       ▼                                                           |
|            [Execução & Conclusão]                                                 |
|            - Status: TODO -> IN_PROGRESS -> WAITING -> DONE                       |
|            - Conclusão da tarefa fecha automaticamente o prazo no calendário      |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 2. Princípios de Arquitetura e Regras de Negócio

1. **Human-in-the-Loop Obrigatório**:
   - Abertura ou visualização interna de uma intimação **não gera ciência tácita nem simula protocolo no tribunal**.
   - O status de leitura interna (`read_status = UNREAD / READ`) é estritamente operacional para a equipe jurídica.
2. **Deduplicação Determinística**:
   - Cada publicação gera um hash SHA-256 calculado sobre: `content | court_code | publication_date | lawyer_name`.
   - Evita duplicatas provenientes de múltiplos diários ou reimportações.
3. **Auto-Vinculação a Processos**:
   - Ao receber uma publicação, o sistema extrai os dígitos numéricos do número CNJ (`cnj_number_clean`, 20 dígitos) e busca no índice `ArchiveFileBean.findByCnjNumberClean`.
   - Se encontrado, vincula com procedência `AUTO_CNJ` e confiança `1.0`.
4. **Sincronização Unificada com o Calendário do j-lawyer**:
   - Não foram criados sistemas paralelos de prazo. Tarefas com `dueDate` e `processId` criam automaticamente registros em `ArchiveFileReviewsBean` (`case_events`) sob o tipo `EVENTTYPE_RESPITE = 20` (Frist / Prazo fatal).
   - Ao concluir a tarefa (`DONE`), o registro de prazo associado é marcado como `done = true`.

---

## 3. Modelo de Dados e Migrações (Flyway `V3_6_0_11`)

| Tabela | Descrição |
|---|---|
| `br_publications` | Armazena publicações, intimações, teores, metadados do tribunal, status de leitura e tratamento. |
| `br_tasks` | Tarefas jurídicas com prioridade, categoria, responsável, prazo fatal e vínculo com processo e calendário. |
| `br_task_comments` | Histórico cronológico de discussões e apontamentos da tarefa. |
| `br_task_checklist_items` | Itens acionáveis de checklist com ordenação e status de conclusão. |
| `br_publication_events` | Log imutável de auditoria de todos os eventos do ciclo de vida da publicação e tarefas. |

---

## 4. Camadas da Aplicação

### 4.1. JPA Entities (`j-lawyer-server-entities`)
- `BrPublication.java`
- `BrTask.java`
- `BrTaskComment.java`
- `BrTaskChecklistItem.java`
- `BrPublicationEvent.java`

### 4.2. DTOs & Interfaces EJB (`j-lawyer-server-api`)
- `com.jdimension.jlawyer.domain.legal.model.*`: 15 DTOs cobrindo paginação, filtros, requisições de tratamento, kanban e dashboards.
- `com.jdimension.jlawyer.services.BrazilianPublicationServiceLocal` / `Remote`
- `com.jdimension.jlawyer.services.BrazilianTaskServiceLocal` / `Remote`
- `com.jdimension.jlawyer.services.BrazilianWorkflowDashboardServiceLocal` / `Remote`

### 4.3. Business Logic Stateless EJBs (`j-lawyer-server-ejb`)
- `BrazilianPublicationService.java`
- `BrazilianTaskService.java`
- `BrazilianWorkflowDashboardService.java`

### 4.4. REST API v8 Endpoints (`j-lawyer-io`)
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/v8/publications` | Listagem filtrada e paginada de publicações |
| `POST` | `/v8/publications` | Ingestão / criação de publicação com deduplicação |
| `GET` | `/v8/publications/{id}` | Detalhes completos e histórico de eventos |
| `PUT` | `/v8/publications/{id}/read` | Marcar como lida / não lida |
| `PUT` | `/v8/publications/{id}/link` | Vincular a processo do j-lawyer |
| `POST` | `/v8/publications/{id}/treat` | Tratar publicação e opcionalmente criar tarefa |
| `POST` | `/v8/publications/{id}/archive` | Dispensar / arquivar publicação com justificativa |
| `GET` | `/v8/tasks` | Listagem filtrada e paginada de tarefas jurídicas |
| `POST` | `/v8/tasks` | Criar nova tarefa com sincronização de prazo |
| `GET` | `/v8/tasks/{id}` | Detalhes da tarefa, checklist e comentários |
| `PUT` | `/v8/tasks/{id}/status` | Transição de status (`TODO`, `IN_PROGRESS`, `WAITING`, `DONE`, `CANCELLED`) |
| `GET` | `/v8/tasks/kanban` | Quadro Kanban estruturado por colunas de status |
| `POST` | `/v8/tasks/{id}/comments` | Adicionar comentário |
| `POST` | `/v8/tasks/{id}/checklist` | Adicionar item de checklist |
| `PUT` | `/v8/tasks/{id}/checklist/{itemId}` | Alternar item de checklist |
| `GET` | `/v8/workflow/dashboard` | Métricas e contadores em tempo real para o dashboard |

### 4.5. Swing UI (`j-lawyer-client`)
- `BrazilianWorkflowPanel.java`: Painel principal com 3 abas (Publicações Inbox, Tarefas Judiciais, Dashboard Operacional com cards de métricas em tempo real).
- `PublicationDetailDialog.java`: Visualização completa do teor judicial, notas internas e acionador de tratamento.
- `TaskEditDialog.java`: Criação/edição de tarefas e prazos com opção de sincronização com o calendário oficial.
- `BrazilianWorkflowFrame.java`: Janela de topo para acesso direto ao módulo.