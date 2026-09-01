# J-LAWYER UPSTREAM FEATURE INVENTORY & ARCHITECTURAL ATLAS

> **Projeto Upstream:** j-lawyer.org (v2.8.0 / Schema v3.6.0.8)  
> **Data do Levantamento:** 31 de Agosto de 2026  
> **Autor / Mapeador:** Subagent 1 — Upstream Cartographer (BR-LAWYER)  
> **Público-Alvo:** Engenheiros, Arquitetos e Especialistas Jurídicos da iniciativa BR-LAWYER  

---

## 1. Módulos Maven e Estrutura de Build

O projeto é estruturado como um **Maven Reactor** unificado construído sob **Java 17**, empacotando os artefatos de servidor para execução no **WildFly 26.1.3.Final** (Jakarta EE 8 / namespace `javax.*`). Dependências não disponíveis no Maven Central são supridas pelo repositório local em projeto (`maven-repo/`).

### 1.1 Matriz de Módulos Maven

| Módulo | Packaging | Descrição / Responsabilidade Arquitetural | Principais Dependências |
| :--- | :--- | :--- | :--- |
| **`j-lawyer-parent`** (raiz) | `pom` | Reactor raiz, gerencia propriedades, repositório local e `<dependencyManagement>`. | N/A |
| **`j-lawyer-server-entities`** | `jar` | Entidades JPA 2.1 (`com.jdimension.jlawyer.persistence`) e migrações Flyway SQL (`resources/db/migration`). | Hibernate 5.3.28, JPA 2.1 API |
| **`j-lawyer-server-api`** | `jar` | Interfaces remotas EJB (`*Remote.java`), DTOs de mensageria e DTOs beA (`services/bea/rest`). | `j-lawyer-server-entities` |
| **`j-lawyer-server-common`** | `jar` | Utilitários de baixo nível, abstração de VFS (`VirtualFile`: Local, SMB, SFTP, FTP), JWT, criptografia e cálculo de feriados. | JJWT, jcifs, jsch, commons-net, log4j |
| **`j-lawyer-io-common`** | `jar` | Utilitários I/O e telemetria de monitoramento de sistema. | Apache commons-io |
| **`j-lawyer-fax`** | `jar` | Integração de telefonia VoIP e fila de envio/recebimento de fax via Sipgate REST API. | Sipgate API, HTTP client |
| **`j-lawyer-cloud`** | `jar` | Integração Nextcloud/ownCloud via WebDAV (arquivos), CalDAV (calendário) e CardDAV (contatos). | Sardine WebDAV, iCal4j, Cardme |
| **`j-lawyer-invoicing`** | `jar` | Geração de faturas eletrônicas estruturadas (ZUGFeRD 2.x e XRechnung) baseado na biblioteca Mustangproject. | Mustangproject, Apache PDFBox |
| **`j-lawyer-server`** (agregador) | `pom` | Agregador dos submódulos do servidor EAR. | Submódulos EJB, WAR, IO |
| **`j-lawyer-server-ejb`** | `ejb` | Lógica de negócio corporativa, session beans Stateless e Singleton, integração LibreOffice/MS-Office/Tika, CDI Events e indexação Lucene 9.12. | Lucene 9.12, Apache Tika, POI, PDFBox, JODConverter/UNO |
| **`j-lawyer-server-war`** | `war` | Componentes web do EAR, endpoints auxiliares e rotinas de backup web. | Servlet API |
| **`j-lawyer-server-io` / `j-lawyer-io`** | `war` | Camada REST API (RESTEasy), gerador dinâmico de OpenAPI Swagger UI (`/j-lawyer-io/swagger-ui/`). | RESTEasy, Jackson, Swagger Core |
| **`j-lawyer-server-ear`** | `ear` | Empacotamento EAR final (`j-lawyer-server.ear`) para deploy no WildFly. | Empacota EJB, WARs e bibliotecas compartilhadas |
| **`j-lawyer-client`** | `jar` | Cliente desktop rico em Java Swing + FlatLaf + JavaFX WebView, comunica-se via EJB Remoting (JBoss Remoting). | FlatLaf 3.5.4, OpenJFX 17, ZXing, SunEditor, Jsoup |
| **`j-lawyer-backupmgr`** | `jar` | Utilitário desktop autônomo (JavaFX 17) para backup/restore de bancos MySQL/MariaDB e repositórios de dados. | OpenJFX, MySQL Connector |
| **`j-lawyer-web`** (Profile `-Pweb`) | `war` | Interface Web SPA moderna construída em **Angular 19** (standalone components, Signals-first, Tiptap, WOPI). | Angular 19, Tiptap Editor, Transloco |
| **`docker`** | `dir` | Definições Docker Compose para orquestração de WildFly 26 + MariaDB 10.6 em contêineres. | Imagens oficiais WildFly e MariaDB |

---

## 2. Entidades JPA (com.jdimension.jlawyer.persistence) e Schema Relacional

O schema relacional é validado na inicialização (`hibernate.hbm2ddl.auto=validate`) e evoluído por mais de **210 scripts de migração SQL** (`V1_13` até `V3_6_0_8`). Existem **76 entidades mapeadas**:

### 2.1 Núcleo de Processos / Casos (Cases & Folders)
1. **`ArchiveFileBean`** (tabela `cases`): Entidade central. Contém `id`, `name`, `fileNumber` (número do processo/pasta), `filenumberext`, `claimNumber`, `claimValue` (valor da causa), `archived`, `notice`, `lawyer`, `assistant`, `reason`, `subjectField`, `custom1..3`, `date_created`, `date_changed`, `date_archived`, `ext_id`, referências a grupo (`Group`), pasta raiz (`CaseFolder`) e configurações de calendário.
2. **`ArchiveFileAddressesBean`** (tabela `archive_file_addresses`): Mapeamento N:N entre Processos e Contatos (`AddressBean`), vinculando o papel processual (`PartyTypeBean`) e número de referência da parte.
3. **`ArchiveFileDocumentsBean`** (tabela `archive_file_documents`): Metadados de documentos atrelados a processos: `name`, `extension`, `size`, `hash`, `version`, `locked`, `dateCreated`, `dateChanged`, `folder` (`CaseFolder`), tags, notas e status de destaque (*highlight*).
4. **`CaseFolder`** (tabela `case_folders`): Estrutura hierárquica de pastas e subpastas de documentos dentro de um processo.
5. **`CaseFolderSettings`** (tabela `case_folder_settings`): Configurações de sincronização de pastas de processos com serviços de nuvem (Nextcloud).
6. **`CaseSyncSettings`** (tabela `case_sync_settings`): Regras de sincronização granular de processos com repositórios remotos.
7. **`DocumentFolder`** / **`DocumentFolderTemplate`**: Modelos pré-configurados de estruturas de pastas para novos processos.
8. **`DocumentNameTemplate`** (tabela `document_name_templates`): Expressões de nomenclatura automatizada de arquivos com variáveis dinâmicas.
9. **`DocumentTagRule`** / **`DocumentTagRuleCondition`**: Motor de regras condicionais para etiquetagem automática de documentos.
10. **`ArchiveFileHistoryBean`** (tabela `archive_file_history`): Trilha de auditoria cronológica de alterações em processos.
11. **`ArchiveFileReviewsBean`** (tabela `archive_file_reviews`): Controle de revisões periódicas e prazos de reapresentação (*Wiedervorlage*).
12. **`ArchiveFileFormsBean`** / **`ArchiveFileFormEntriesBean`**: Armazenamento de formulários dinâmicos de dados do processo e pares chave-valor.
13. **`FormTypeBean`** / **`FormTypeArtefactBean`**: Definições de tipos de formulários (ex: Direito de Família, Trânsito, Locação) e artefatos Groovy/JSON.
14. **`ArchiveFileTagsBean`**, **`DocumentTagsBean`**, **`AddressTagsBean`**: Sistema universal de tags polimórficas (simples e multivaloradas/listas).

### 2.2 CRM e Contatos (Addresses & Directory)
15. **`AddressBean`** (tabela `addresses`): Cadastro completo de pessoas físicas, jurídicas, tribunais, juízes, advogados e seguradoras. Campos: nome, prenomes, tratamento (*salutation/title*), endereços, contatos telefônicos/móveis, emails múltiplos, dados SEPA/bancários, Leitweg-ID (faturamento público), segurados vinculados (*insurants*), histórico e notas.
16. **`AddressDocumentsBean`** (tabela `address_documents`): Repositório de documentos diretamente vinculados a um contato (procurações gerais, contratos sociais, documentos pessoais).
17. **`BankDataBean`** (tabela `bank_data`): Cadastro de agências bancárias, códigos de compensação bancária (*BLZ*) e códigos BIC/SWIFT.
18. **`CityDataBean`** (tabela `city_data`): Base de dados de códigos postais (CEP/PLZ), cidades e estados.
19. **`PartyTypeBean`** (tabela `party_types`): Catálogo de papéis processuais (Cliente, Parte Contrária, Advogado Contrário, Tribunal, Testemunha, Perito, Notário, Seguradora).
20. **`Campaign`** / **`CampaignAddress`**: Módulo de CRM para campanhas de mala direta e comunicações em massa.

### 2.3 Financeiro, Honorários, Faturamento e Execução (Finance & Ledger)
21. **`Invoice`** (tabela `invoices`): Faturas de honorários e despesas. Armazena número da fatura, status (rascunho, emitida, paga, cancelada), tipo (`InvoiceType`), totais bruto/líquido, alíquotas de imposto, moeda, comprador/ordem de compra, remetente, dados de pagamento e referência ZUGFeRD.
22. **`InvoicePosition`** (tabela `invoice_positions`): Itens individuais da fatura (honorário por hora, honorário fixo, reembolso de custas, despesas postais), com quantidade, valor unitário e alíquota de imposto.
23. **`InvoicePool`** / **`InvoicePoolAccess`**: Círculos de numeração e controle de séries de faturamento independentes por advogado ou filial.
24. **`InvoiceType`** (tabela `invoice_types`): Classificação de faturas (Honorários Iniciais, Fatura Intermediária, Fatura Final, Nota de Crédito).
25. **`InvoicePositionTemplate`**: Modelos de itens de cobrança pré-configurados.
26. **`Timesheet`** (tabela `timesheets`): Folhas de apontamento de horas (*timesheets*) associadas a processos e advogados.
27. **`TimesheetPosition`** (tabela `timesheet_positions`): Lançamentos detalhados de horas (data, tempo gasto em minutos/horas, taxa horária, usuário, descrição, status de faturado).
28. **`TimesheetPositionTemplate`** / **`TimesheetAllowedPositionTpl`**: Catálogo de atividades tarifadas padronizadas.
29. **`CaseAccountEntry`** (tabela `case_account_entries`): Conta-corrente do processo (livro caixa de custas, depósitos judiciais, valores de terceiros / *Fremdgeld* e reembolsos).
30. **`Payment`** (tabela `payments`): Registro de pagamentos recebidos/efetuados via extratos bancários.
31. **`PaymentAllocation`** / **`PaymentSplitProposal`**: Alocação e rateio de pagamentos parciais entre faturas, custas e principal.
32. **`BankStatementsCSVConfig`** (tabela `config_bankstatement`): Esquemas customizados de importação e parsing de extratos bancários CSV (MT940/CAMT/CSV).
33. **`ClaimLedger`** / **`ClaimLedgerEntry`**: Livro de cálculo de execução de dívidas e cobranças judiciais (*Forderungskonto*).
34. **`ClaimComponent`** / **`ClaimComponentType`**: Componentes da dívida (Principal, Juros Moratórios, Custas de Execução, Cláusula Penal).
35. **`BaseInterest`** / **`InterestRule`** / **`InterestType`**: Tabelas de taxas de juros legais e regras de cálculo temporal de juros sobre o principal.

### 2.4 Calendário, Prazos e Agendamentos
36. **`CalendarSetup`** / **`CalendarAccess`**: Configurações de calendários de usuários, agendas compartilhadas e permissões CalDAV.
37. **`CalendarEntryTemplate`**: Modelos de compromissos frequentes (Audiências, Perícias, Prazos Recursais, Despachos).
38. **`EventTypes`**: Tipos de eventos com código de cores e comportamentos de alerta (Prazos Fatais, Audiências, Lembretes).

### 2.5 Comunicação, Mensageria e Correio Eletrônico
39. **`MailboxSetup`** / **`MailboxAccess`**: Configurações de contas de e-mail (IMAP, SMTP, MS Exchange com autenticação OAuth2 / Graph API, pastas de quarentena e pastas de scanner).
40. **`InstantMessage`** / **`InstantMessageMention`**: Chat interno corporativo entre membros do escritório, com menções diretas (`@usuario`) e vínculos a processos.
41. **`FaxQueueBean`** (tabela `fax_queue`): Fila de despacho e recebimento de fax digital (VoIP Sipgate).
42. **`EpostQueueBean`** (tabela `epost_queue`): Fila de integração postal física e eletrônica (Deutsche Post E-POST).
43. **`IntegrationHook`** / **`IntegrationHookLog`**: Registro de Webhooks de entrada/saída e auditoria de chamadas HTTP externas.

### 2.6 Inteligência Artificial ("Ingo")
44. **`AssistantConfig`** (tabela `assistant_configs`): Servidores de IA configurados (provedor, endpoint REST, chaves de API criptografadas, timeouts e modelos).
45. **`AssistantPrompt`** (tabela `assistant_prompts`): Biblioteca de system prompts, instruções operacionais e templates de prompt para tarefas especializadas.
46. **`AssistantReplacement`** (tabela `assistant_replacements`): Regras de substituição de texto e sanitização de prompts.

### 2.7 Segurança, Usuários e Administração
47. **`AppUserBean`** (tabela `users`): Usuários do sistema, login, hash de senha criptográfica, caminhos de nuvem, ramal VoIP, status e perfis.
48. **`AppRoleBean`** (tabela `roles`): Papéis de controle de acesso RBAC (`adminRole`, `loginRole`, `sysAdminRole`, `aiAgentRole`, `lawyerRole`, `assistantRole`).
49. **`Group`** / **`GroupMembership`** / **`ArchiveFileGroupsBean`**: Grupos de trabalho e isolamento de processos por equipes.
50. **`AppOptionGroupBean`** / **`ServerSettingsBean`**: Configurações globais do sistema e tabelas de parâmetros.
51. **`MappingTable`** / **`MappingEntry`**: Tabelas de mapeamento e normalização (títulos de cortesia, gêneros, codificações externas).
52. **`TransactionLog`**: Registro de transações críticas para auditoria e conformidade.

---

## 3. Serviços de Negócio EJB (Stateless & Singleton)

A camada EJB no módulo `j-lawyer-server-ejb` encapsula as transações JTA e regras de negócio, expondo interfaces `*Remote` (para o cliente Swing) e `*Local` (para chamadas inter-bean e REST):

### 3.1 Session Beans Singleton (`@Singleton` / `@Startup`)
1. **`ContainerLifecycleBean`**: Inicialização e encerramento do container, verificação de consistência de índices Lucene e bootstrap do ambiente.
2. **`ScheduledTasksService`**: Agendador de tarefas periódicas via `@Schedule` (alerta de faturas vencidas às 06:01, envio diário de pauta às 05:01, resumo semanal aos domingos, polling Dropscan a cada 15 min e limpeza de sessões beA a cada 15 min).
3. **`SingletonService`**: Gerenciador de estado global da aplicação e cache de configurações de sistema.
4. **`CalendarSyncService`**: Orquestrador central de sincronização de calendários em background com servidores CalDAV/Nextcloud.
5. **`ContactSyncService`**: Orquestrador de sincronização de contatos em background com servidores CardDAV/Nextcloud.
6. **`PdfPreviewGenerationService`**: Fila assíncrona de processamento e renderização de thumbnails e previews de documentos.
7. **`BeaSessionRegistry`**: Gerenciador de sessões e tokens criptográficos da caixa postal do advogado alemão (beA).
8. **`CustomHooksService`**: Despachante assíncrono de eventos e disparador de Webhooks HTTP.

### 3.2 Session Beans Stateless (`@Stateless`)
| Serviço EJB | Interface Remota | Interface Local | Responsabilidades de Negócio |
| :--- | :--- | :--- | :--- |
| **`ArchiveFileService`** | `ArchiveFileServiceRemote` | `ArchiveFileServiceLocal` | CRUD de processos, associação de partes, controle de histórico, revisões, árvore de pastas de documentos e permissões de grupos. |
| **`AddressService`** | `AddressServiceRemote` | `AddressServiceLocal` | Gestão de contatos, busca avançada, verificação de similaridade, importação de bancos de dados de CEP/PLZ e prevenção de conflito de interesses. |
| **`AddressDocumentService`**| `AddressDocumentServiceRemote`| `AddressDocumentServiceLocal`| Gestão de arquivos e procurações atrelados diretamente a contatos. |
| **`CalendarService`** | `CalendarServiceRemote` | `CalendarServiceLocal` | Prazos, audiências, compromissos, cálculo de feriados e conflitos de horários. |
| **`InvoiceService`** | `InvoiceServiceRemote` | `InvoiceServiceLocal` | Emissão de faturas, cálculo de impostos, controle de numeração (pools), status de liquidação e geração de boletos/GiroCode. |
| **`TimesheetService`** | `TimesheetServiceRemote` | `TimesheetServiceLocal` | Apontamento de horas, associação a processos e conversão de lançamentos em itens de fatura. |
| **`ClaimLedgerService`** | `ClaimLedgerServiceRemote` | `ClaimLedgerServiceLocal` | Cálculo de execução de títulos e dívidas, juros legais (*Basiszinssatz*) e imputação de pagamentos. |
| **`PaymentService`** | `PaymentServiceRemote` | `PaymentServiceLocal` | Gestão de pagamentos, importação de extratos bancários e algoritmo de rateio inteligente. |
| **`EmailService`** | `EmailServiceRemote` | `EmailServiceLocal` | Integração IMAP/SMTP/Exchange, parsing de mensagens MIME, extração de anexos e arquivamento de e-mails diretamente na pasta do processo (.eml). |
| **`SearchService`** | `SearchServiceRemote` | `SearchServiceLocal` | Indexação e busca textual de alto desempenho via Apache Lucene 9.12 sobre processos e documentos extraídos com Apache Tika. |
| **`SecurityService`** | `SecurityServiceRemote` | `SecurityServiceLocal` | Autenticação, autorização RBAC, emissão de JWT, gerenciamento de credenciais e hash criptográfico. |
| **`SystemManagement`** | `SystemManagementRemote` | `SystemManagementLocal` | Resolução de placeholders dinâmicos de modelos, perfis de escritório, configuração de servidores de e-mail e parâmetros globais. |
| **`FormsService`** | `FormsServiceRemote` | `FormsServiceLocal` | Gerenciamento de formulários dinâmicos de processos (*Falldatenblätter*) e extração de valores para preenchimento documental. |
| **`CustomerRelationsService`**| `CustomerRelationsServiceRemote`| `CustomerRelationsServiceLocal`| Campanhas de mala direta e exportação segmentada de listas de contatos. |
| **`IntegrationService`** | `IntegrationServiceRemote` | `IntegrationServiceLocal` | Configurações de conexões de IA Ingo, prompts customizados e substituições de texto. |
| **`DropscanService`** | `DropscanServiceRemote` | `DropscanServiceLocal` | Polling e download automatizado de cartas digitalizadas pela empresa Dropscan. |
| **`MessagingService`** | `MessagingServiceRemote` | `MessagingServiceLocal` | Mensagens instantâneas internas, notificações push de desktop e menções. |
| **`ReportService`** | `ReportServiceRemote` | `ReportServiceLocal` | Geração de relatórios operacionais, estatísticas de faturamento e gráficos. |
| **`VoipService`** | `VoipServiceRemote` | `VoipServiceLocal` | Discagem de chamadas (*click-to-call*), histórico telefônico e envio de fax via Sipgate. |
| **`BeaService`** | `BeaServiceRemote` | `BeaServiceLocal` | Comunicação com o Tribunal alemão via caixa postal beA. |
| **`DocUtilityService`** | `DocUtilityServiceRemote` | `DocUtilityServiceLocal` | Utilitários de conversão e manipulação de documentos. |
| **`DataBucketLoader`** | `DataBucketLoaderRemote` | `DataBucketLoaderLocal` | Carga de buckets de dados para exportação e importação. |

---

## 4. Rotas e Versões da API REST (org.jlawyer.io.rest)

A API REST do j-lawyer é servida em `/j-lawyer-io/rest/` com autenticação HTTP Basic ou JWT Bearer. É dividida em versões históricas imutáveis (`v1` a `v7`) e a versão atual consolidada (`v8` + endpoints especializados):

### 4.1 Panorama das Versões v1 a v7
- **v1**: Endpoints iniciais para Processos (`/v1/cases`), Contatos (`/v1/contacts`), Formulários (`/v1/forms`) e Segurança (`/v1/security`).
- **v2 / v3**: Aprimoramentos na manipulação de documentos e paginação de contatos.
- **v4**: Introdução da API de Calendário (`/v4/calendar`) para sincronização móvel.
- **v5 / v6**: Inclusão de DataBuckets (`/v6/databuckets`), Templates de documentos (`/v6/templates`) e autenticação expandida.
- **v7**: Versão consolidada do ciclo anterior, incluindo Administração (`/v7/administration`), Configurações globais (`/v7/configuration`), E-mails (`/v7/email`), Faturas (`/v7/invoices`), Mensagens Instantâneas (`/v7/messaging`), Relatórios (`/v7/reports`) e Webhooks (`/v7/webhooks`).

### 4.2 Arquitetura da Versão Atual (`v8` + WOPI)
| Endpoint v8 | Path Base | Métodos | Permissões | Descrição Funcional |
| :--- | :--- | :--- | :--- | :--- |
| **`AssistantEndpointV8`** | `/v8/assistant` | GET, POST, PUT, DELETE | `adminRole`, `loginRole` | Configuração do assistente Ingo: CRUD de servidores de IA, prompts do sistema e regras de substituição de texto. |
| **`AuthenticationEndpointV8`**| `/v8/auth` | POST | `PermitAll` | Autenticação de usuários, validação de credenciais e emissão de tokens JWT para o Web Client. |
| **`CasesEndpointV8`** | `/v8/cases` | GET, POST, PUT, DELETE | `loginRole` | Gerenciamento de processos, árvore de pastas, envio/download de documentos, histórico e partes. |
| **`ContactsEndpointV8`** | `/v8/contacts` | GET, POST, PUT, DELETE | `loginRole` | Gestão de contatos, busca textual, detalhes de pessoas e empresas. |
| **`CalendarEndpointV8`** | `/v8/calendar` | GET, POST, PUT, DELETE | `loginRole` | Consulta de pauta, criação de compromissos e prazos com lembretes. |
| **`TimesheetsEndpointV8`** | `/v8/timesheets` | GET, POST, PUT, DELETE | `loginRole` | Apontamento de horas, listagem de timesheets abertos e lançamento de tarefas tarifadas. |
| **`PaymentsEndpointV8`** | `/v8/payments` | GET, POST, PUT, DELETE | `loginRole` | Gestão de pagamentos, importação de extratos bancários e alocações em faturas. |
| **`SearchEndpointV8`** | `/v8/search` | GET, POST | `loginRole` | Busca textual full-text em todo o repositório de processos e documentos via Lucene. |
| **`DocumentsBinEndpointV8`**| `/v8/documentsbin` | GET, POST, DELETE | `loginRole` | Lixeira de documentos excluídos, permitindo restauração ou expurgo definitivo. |
| **`DropscanEndpointV8`** | `/v8/dropscan` | GET, POST | `loginRole` | Integração com caixas postais digitais Dropscan. |
| **`ProfileEndpointV8`** | `/v8/profile` | GET, PUT | `loginRole` | Consulta e atualização do perfil do usuário logado e preferências de visualização. |
| **`OfficeEndpointV8`** | `/v8/office` | GET, POST | `loginRole` | Sessões de edição colaborativa online de documentos via WOPI. |
| **`WopiEndpoint`** | `/wopi/files/{id}` | GET, POST | `PermitAll` (token WOPI) | Implementação do protocolo padrão **WOPI (Web Application Open Platform Interface)** para integração com Collabora Online e Microsoft 365 / Office Online. |
| **`BeaEndpointV8`** | `/v8/bea` | GET, POST, PUT | `loginRole` | API REST para envio e recebimento de mensagens judiciais via beA. |

---

## 5. Interface Desktop Swing (`j-lawyer-client`)

O cliente desktop é construído em Java Swing utilizando a biblioteca **FlatLaf 3.5.4** para temas modernos (Claro/Escuro/Inter/Roboto) e arquivos de design `.form` integrados ao NetBeans GUI Builder.

### 5.1 Telas Principais e Painéis Centrais
- **`JKanzleiGUI`**: Janela principal da aplicação. Contém a barra de menus do sistema, barra de status global, barra de módulos lateral (`ModuleBarPanel`), área de trabalho dinâmica (`DesktopPanel`) e gerenciador de abas de processos abertos.
- **`DesktopPanel`**: Dashboard modular do advogado, configurável em grade (`DesktopLayoutManager`, `DesktopGridLayoutDialog`), contendo widgets em tempo real:
  - *Últimos Processos Alterados* (`LastChangedEntryPanelTransparent`)
  - *Prazos e Reapresentações Vencendo* (`ReviewDueEntryPanelTransparent`)
  - *Faturas em Aberto* (`InvoicesOpenPanel`)
  - *Itens com Etiquetas / Tags* (`TaggedEntryPanelTransparent`)
  - *Notificações e Lembretes de Prazos* (`ReminderNotificationDialog`)
  - *Busca Global Instantânea* (`GlobalSearchDialog`)
- **`ArchiveFilePanel`**: Painel mestre de processos (mais de 580 KB de código). Apresenta:
  - Aba de Dados Gerais (Número, Descrição, Advogado responsável, Assistente, Valor da causa, Notas).
  - Aba de Partes Envolvidas (`PartiesPanel`, `InvolvedPartyEntryPanel`).
  - Aba de Documentos com visualizador em árvore de pastas (`CaseFolderCellRenderer`), controle de versões, bloqueio de arquivos e painel de pré-visualização integrado.
  - Aba de Histórico e Auditoria (`ArchiveFileHistoryTableModel`).
  - Aba de Prazos e Compromissos (`NewEventPanel`).
  - Aba de Formulários Dinâmicos (`FormPluginsPanel`).
  - Aba de Conta-Corrente do Processo (`CaseAccountEntryPanel`).
  - Aba de Execução de Dívidas / Forderungskonto (`ClaimLedgerDialog`).
- **`AddressPanel`**: Painel de CRM e contatos, com suporte a pessoas físicas, jurídicas, autoridades, validação de endereços, múltiplos telefones e e-mails, e verificação em tempo real de conflitos de interesses.
- **`EmailInboxPanel` / `SendEmailFrame`**: Cliente de e-mail completo embutido, com suporte a IMAP IDLE, pastas sincronizadas, editor HTML rico (SunEditor), criptografia S/MIME e arquivamento de e-mails diretamente na pasta do processo.
- **`ScannerPanel`**: Painel de integração com scanners de mesa (SANE/TWAIN) e monitoramento de pastas de digitalização de rede com divisão automática de PDFs em páginas em branco (`PDFBlankPageSplitter`).
- **`MessagingCenterPanel` / `PopoutMessenger`**: Chat corporativo flutuante ou acoplado, com histórico, suporte a hashtags e menções de processos.
- **`ReportingPanel`**: Painel de Business Intelligence e relatórios com gráficos dinâmicos de barras e tabelas exportáveis.

### 5.2 Galeria de Visualizadores de Documentos (`DocumentViewerFactory`)
O cliente desktop dispõe de múltiplos visualizadores nativos para inspeção imediata sem necessidade de abrir aplicativos externos:
- `PdfImageScrollingPanel` / `PdfImagePanel`: Renderizador nativo de PDF com paginação contínua e zoom.
- `JavaFxBrowserPanel` / `HtmlPanel`: Visualizador HTML e editor SunEditor embutido.
- `MarkdownPanel`: Visualizador Markdown com syntax highlighting.
- `GifJpegPngImageWithTextPanel`: Visualizador de imagens com camada OCR de texto pesquisável.
- `SoundplayerPanel` / `WaveformPanel`: Reprodutor de ditados jurídicos e áudios com visualização de ondas sonoras.
- `OutlookMessagePanel` & `EmailPanel`: Visualizadores nativos de arquivos `.msg` e `.eml`.
- `XRechnungPanel` & `XjustizPanel`: Visualizadores especializados para faturas eletrônicas estruturadas e processos eletrônicos em XML.

---

## 6. Cliente Web SPA (`j-lawyer-web`)

O cliente web é uma SPA moderna desenvolvida em **Angular 19** com suporte a componentes standalone e gerenciamento reativo baseado em **Angular Signals**.

### 6.1 Arquitetura do Frontend Web
- **Estrutura de Rotas:**
  - `/login`: Autenticação JWT (`LoginComponent`).
  - `/desktop`: Dashboard web (`DesktopComponent`).
  - `/cases` e `/cases/:id`: Módulo de processos com deep linking direto para casos (`AktenComponent`).
  - `/contacts` e `/contacts/:id`: Módulo de contatos (`KontakteComponent`).
  - `/calendar`: Pauta e agenda interativa (`KalenderComponent`).
  - `/communication`: E-mails e comunicações (`EmailComponent`).
  - `/documents`: Repositório de documentos gerais (`DokumenteComponent`).
  - `/reporting`: Relatórios web (`ReportingComponent`).
  - `/trash`: Lixeira de documentos (`PapierkorbComponent`).
  - `/scans`: Gestão de digitalizações (`ScansComponent`).
  - `/settings`: Configurações em abas (Geral, Administração com `roleGuard['adminRole']` e Sistema com `roleGuard['sysAdminRole']`).
  - `/office/:id`: Editor de documentos em tela cheia via integração **WOPI / Collabora Online / MS 365** (`OfficePageComponent`).
  - `/htmledit/:id`: Editor de texto rico standalone baseado no **Tiptap Editor** (`HtmlEditorPageComponent`).
- **Segurança da Cadeia de Suprimentos:** O projeto Angular utiliza um pipeline npm estritamente auditado e endurecido (`.npmrc` com `ignore-scripts` e empacotamento offline para reproduzibilidade Maven).

---

## 7. Capacidades Documentais, OCR e Busca Textual

1. **Templates e Placeholders:**
   - Suporte completo a modelos OpenDocument (`.odt`), Microsoft Word (`.docx`) e formulários PDF interativos.
   - O mecanismo `PlaceHolderServerUtils` resolve centenas de placeholders dinâmicos:
     - Dados do escritório: `{{PROFIL_FIRMA}}`, `{{PROFIL_STRASSE}}`, `{{PROFIL_IBAN}}`, etc.
     - Dados do processo: `{{AKTE_AZ}}`, `{{AKTE_WEGEN}}`, `{{AKTE_KURZRUBRUM}}`, `{{AKTE_SW}}`, etc.
     - Dados das partes dinâmicas por papel: `{{M_NAME}}` (Mandante), `{{G_NAME}}` (Gegner/Contrário), `{{GERICHT_NAME}}` (Tribunal), etc.
     - Tabelas de cálculo e faturas: `{{TABELLE_1}}`, `{{RECHNUNG_TABELLE}}`.
     - Texto gerado por IA: `{{INGO_TEXT}}`.
2. **Processamento Office:**
   - **LibreOffice / JODConverter:** `LibreOfficeAccess.java` executa conversões headless entre DOCX/ODT e exportação para PDF de alta fidelidade via ponte UNO.
   - **Apache POI:** `MicrosoftOfficeAccess.java` realiza manipulação direta de arquivos `.docx` via OpenXML.
   - **PDFBox & iText:** Preenchimento de campos de formulários PDF (`PdfFormsAccess.java`), divisão por páginas em branco (`PDFBlankPageSplitter`) e aplicação de carimbos digitais (`PdfStampConfigurationDialog`).
3. **Extração de Texto & OCR:**
   - `TikaConfigurator.java`: Utiliza o Apache Tika para extrair texto de qualquer formato binário (DOC, DOCX, ODT, RTF, PDF, EML, MSG, XLS, TXT).
4. **Motor de Busca Textual (Apache Lucene 9.12):**
   - Indexação assíncrona orientada a eventos (`SearchIndexProcessor.java`).
   - Busca booleana ponderada com realce de termos (*highlighting*) cobrindo o conteúdo integral de todos os documentos do acervo.

---

## 8. CRM, Contatos e Prevenção de Conflito de Interesses

- **Modelo de Dados do Contato:** O `AddressBean` comporta múltiplos endereços físicos, múltiplos e-mails tipificados (comercial, pessoal, faturamento), contas bancárias, identificadores fiscais e conexões familiares.
- **Detecção de Conflito de Interesses:**
  - Implementado em `ConflictOfInterestUtils.java`.
  - Ao vincular um contato a um novo processo com determinado papel (ex: Réu/Parte Contrária), o algoritmo pesquisa todo o histórico de processos da base.
  - Se o contato já tiver sido representado pelo escritório em outro processo como Autor/Cliente, o sistema intercepta a ação e exibe o `ConflictOfInterestDialog`, exibindo a lista de processos conflitantes e impedindo a atuação contrária inadvertida.

---

## 9. Módulo Financeiro, Faturamento e Execução de Créditos

1. **Faturamento Eletrônico (ZUGFeRD & XRechnung):**
   - Integração com **Mustangproject** (`j-lawyer-invoicing`) para gerar faturas em PDF/A-3 com XML embutido em conformidade com as diretivas europeias de faturamento eletrônico.
   - Geração de QR Code padrão europeu (EPC QR Code / GiroCode) para pagamento bancário instantâneo.
2. **Apontamento de Horas (Timesheets):**
   - Registro de horas por processo, advogado e atividade tarifada.
   - Faturamento com um clique: conversão automática de timesheets aprovados em itens de faturas (`TimesheetBillingDialog`).
3. **Conta-Corrente do Processo (Case Account):**
   - Controle estrito de depósitos judiciais, adiantamento de despesas e valores de terceiros (*Fremdgeld*), com segregação contábil.
4. **Execução de Dívidas / Forderungskonto (`ClaimLedger`):**
   - Sistema contábil de liquidação de sentenças e execuções de títulos.
   - Cálculo automático de juros moratórios sobre o principal segundo a taxa legal (*Basiszinssatz*), amortização de pagamentos parciais (primeiro custas, depois juros, depois principal) e extrato de saldo devedor atualizado.
5. **Conciliação Bancária:**
   - Importação de extratos bancários em CSV com motor de regras configurável (`BankStatementsCSVConfig`).
   - Algoritmo de rateio e reconciliação automática de faturas pagas (`PaymentSplitCalculator`).

---

## 10. Inteligência Artificial: Assistente "Ingo" e Ferramentas

O j-lawyer possui um ecossistema avançado de IA operacional integrado ao cliente desktop e pronto para extensão via MCP (Model Context Protocol).

### 10.1 Arquitetura do Assistente Ingo
- **Backends Suportados:** Conecta-se a LLMs locais (Ollama, vLLM) ou remotos (OpenAI, Anthropic, Mistral) através de configurações gerenciadas no `AssistantConfig`.
- **Modos de Operação no Desktop:**
  - *Chat Interativo sobre o Processo* (`AssistantChatDialog`): Permite conversar com o processo, fazer perguntas sobre os documentos da pasta e solicitar minutas.
  - *Extração de Dados* (`AssistantExtractDialog`): Extração estruturada de entidades a partir de contratos ou petições digitalizadas.
  - *Visão Computacional* (`AssistantVisionDialog`): Análise de imagens e plantas anexadas.
  - *Geração Documental* (`AssistantGenerateDialog`): Criação de peças a partir de modelos injetando o conteúdo gerado via placeholder `{{INGO_TEXT}}`.

### 10.2 Catálogo de 66 Ferramentas (Tool Calling em `ToolRegistry.java`)
O assistente opera através de um registro central de ferramentas com níveis de risco (`RISK_LOW`, `RISK_MEDIUM`, `RISK_HIGH`), controle de permissão RBAC (`aiAgentRole`) e diálogos de aprovação prévia do usuário (`ToolApprovalDialog`):

1. **Gestão de Processos:** `search_cases`, `get_case`, `get_case_by_id`, `create_case`, `update_case`, `get_history_for_case`, `get_parties_for_case`, `add_party_to_case`, `list_case_folders`, `create_case_folder`, `list_folder_templates`, `apply_folder_template`, `list_case_tags`, `set_case_tag`.
2. **Gestão de Documentos:** `list_case_documents`, `list_case_documents_by_date`, `search_case_documents`, `get_document_text`, `get_document_content`, `rename_document`, `delete_document`, `move_document_to_folder`, `move_document_to_case`, `list_document_tags`, `set_document_tag`, `search_templates`, `list_letter_heads`, `create_document_from_template`, `create_note`.
3. **CRM e Contatos:** `search_contacts`, `create_contact`, `create_or_get_contact`, `update_contact`, `list_contact_tags`, `set_contact_tag`.
4. **Agenda e Calendário:** `list_calendars`, `get_events_for_case`, `get_all_open_events`, `get_all_open_events_between_dates`, `list_event_types`, `find_free_slots`, `create_event`, `update_event`.
5. **Financeiro e Horas:** `get_all_open_invoices`, `search_invoices`, `search_invoices_by_date`, `list_invoice_pools`, `create_invoice`, `create_invoice_position`, `get_all_open_timesheets`, `get_open_timesheets_for_case`, `get_timesheet_positions`, `create_timesheet_position`.
6. **E-mails e Mensagens:** `list_mailboxes`, `search_emails`, `get_email`, `save_email_to_case`, `search_instant_messages`, `create_instant_message`.
7. **Formulários e Sistema:** `list_form_types`, `create_case_form`, `list_users`, `get_my_groups`, `get_current_date_time`, `web_search`, `fetch_url`.

---

## 11. Tarefas em Segundo Plano e Utilitário de Backup

1. **Rotinas Agendadas (`ScheduledTasksService`):**
   - Monitoramento diário de faturas a vencer.
   - Envio automático por e-mail da pauta diária de audiências e prazos de cada advogado.
   - Limpeza periódica de sessões inativas e tokens expirados.
   - Sincronização periódica com caixas postais Dropscan.
2. **Gerenciador de Backup (`j-lawyer-backupmgr`):**
   - Aplicação desktop autônoma JavaFX 17.
   - Realiza dump consistente do banco de dados MySQL/MariaDB e cópia sincronizada dos volumes de arquivos binários (`j-lawyer-data`), gerando arquivos de backup compactados e criptografados.
   - Utilitário `RestoreExecutor.java` realiza a recuperação completa do ambiente em caso de desastre.

---

## 12. Inventário da Suíte de Testes Automatizados

O repositório possui suítes de testes unitários e de integração baseados em JUnit distribuídos nos módulos:

| Módulo | Classes de Teste | Escopo / Cobertura |
| :--- | :--- | :--- |
| **`j-lawyer-server-ejb`** | `ArchiveFileServiceTest`, `CaseNumberGeneratorTest`, `EmailServiceTest`, `InstantMessagingUtilTest`, `InvoiceNumberGeneratorTest`, `LibreOfficeODFTest`, `MicrosoftOfficeDocxTest`, `SystemManagementTest`, `EmailMimeStructureTest` | Testes de geração de numeração, regras de negócio de processos, fusão de documentos ODF/DOCX, parsing de mensagens MIME e resolução de placeholders. |
| **`j-lawyer-server-common`**| `FtpTest`, `LocalFileTest`, `SftpTest`, `SmbTest`, `JwtServiceTest`, `PasswordsUtilTest`, `SimilarityTest`, `TreeNodeUtilsTest`, `WrappedSMimeTest` | Testes de abstração de armazenamento VFS, emissão/validação de JWT, hash de senhas, cálculo de similaridade fonética de contatos e criptografia S/MIME. |
| **`j-lawyer-client`** | `DocumentPreviewTest`, `MailTest`, `FileUtilsTest`, `StoredOrderUtilsTest`, `SystemUtilsTest`, `VersionUtilsTest`, `TaggingTest` | Testes de carregamento de previews, tratamento de arquivos locais, tags e parsing de versões. |
| **`j-lawyer-backupmgr`** | `RestoreExecutorTest` | Validação de restauração de dumps de banco de dados e integridade de arquivos restaurados. |
| **`j-lawyer-fax`** | `SipgateNeoVoiceTest`, `SipUtilsTest`, `SipgateApiTest` | Testes de comunicação com a API REST da Sipgate para telefonia e fax. |
| **`j-lawyer-server-war`** | `BackupExecutorTest`, `FileNameEncodingTest`, `SyncTest` | Testes de sincronização de arquivos e codificação de nomes no backup. |
| **`j-lawyer-cloud`** | `NextcloudTest`, `GoogleTest` | Testes de integração WebDAV/CalDAV com Nextcloud. |

---

## 13. Conclusão e Recomendações para a Adaptação Brasileira (BR-LAWYER)

O inventário revela uma plataforma extremamente madura, robusta e modular. Para a localização brasileira no projeto **BR-LAWYER**, os pontos focais de substituição/adaptação identificados são:

1. **Substituição do beA:** Substituição da camada `BeaService` / `BeaEndpointV8` pelo conector MNI (Modelo Nacional de Interoperabilidade) do CNJ / PJe / e-SAJ / Projudi / Eproc.
2. **Substituição do ZUGFeRD/XRechnung:** Adaptação da camada `j-lawyer-invoicing` para emissão de NFS-e padrão nacional (ABRASF/Padrão Nacional da Receita) e PIX Copia e Cola / QR Code dinâmico no lugar do EPC GiroCode.
3. **Adaptação do Cálculo de Execuções (`ClaimLedger`):** Substituição do *Basiszinssatz* alemão pela correção monetária oficial do Brasil (IPCA-E, INPC, SELIC) e regras de juros moratórios do Código Civil / CPC.
4. **Expansão de Campos de Contatos (`AddressBean`):** Suporte nativo a CPF/CNPJ, Inscrição Estadual, OAB (UF + Número) e validação de CEP via ViaCEP / Correios.
5. **Aproveitamento do Ingo e Expansão MCP:** O subsistema de 66 ferramentas em `ToolRegistry.java` e a arquitetura de IA são 100% reutilizáveis e compatíveis para conexão direta com modelos em língua portuguesa e servidores MCP.
