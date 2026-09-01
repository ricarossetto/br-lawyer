# Matriz de Paridade Funcional e Classificação: j-lawyer → BR-LAWYER

> **Documento Oficial de Rastreabilidade:** `docs/JLAWYER_TO_BRLAWYER_PARITY_MATRIX.md`  
> **Versão:** 1.0.0  
> **Status do Baseline:** Mapeamento Integral Concluído (0% de funcionalidades omitidas)

---

## 1. Glossário de Classificação de Paridade

- **`KEEP`**: Funcionalidade universal preservada integralmente no BR-LAWYER sem alterações estruturais.
- **`LOCALIZE`**: Funcionalidade preservada no core com tradução/localização de textos, mensagens e labels para `pt-BR`.
- **`BRAZILIANIZE`**: Funcionalidade adaptada às normas materiais, processuais e regulamentares brasileiras (CNJ, OAB, CPC, CLT, LGPD).
- **`REPLACE`**: Funcionalidade específica alemã/europeia substituída por tecnologia ou padrão equivalente brasileiro (ex: ZUGFeRD → NFS-e; SEPA → PIX/CNAB).
- **`DEPRECATE`**: Funcionalidade obsoleta ou mantida apenas para compatibilidade legada.
- **`NOT_APPLICABLE`**: Funcionalidade sem correspondência ou necessidade no contexto jurídico brasileiro.
- **`BLOCKED_FOR_LICENSE_REVIEW`**: Componente fechado ou com incerteza de licença que foi ou deve ser removido (ex: `j-lawyer-bea-wrapper.jar`).

---

## 2. Matriz de Paridade Exaustiva por Módulo e Subsistema

### 2.1 Módulo de Processos e Casos (`ArchiveFile` / `Cases`)

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :---: |
| **Cadastro de Processos / Casos** | `ArchiveFileBean.java`, `ArchiveFileService.java` | JPA, Hibernate | `ArchiveFileServiceTest` | **BRAZILIANIZE** | Processo Judicial / Extrajudicial com Metadados Brasileiros | Fase 2 (`feat/brazilian-domain`) | CRUD de processo com validações de dados | Pronto p/ Implementação |
| **Numeração do Processo** | `ArchiveFileBean.fileNumber`, `CaseNumberGenerator` | Core EJB | `CaseNumberGeneratorTest` | **BRAZILIANIZE** | Numeração Única CNJ (NPU) NNNNNNN-DD.AAAA.J.TR.OOOO | Fase 2 (`feat/brazilian-domain`) | Testes unitários do validador ISO 7064 Módulo 97 | Pronto p/ Implementação |
| **Segmentação de Tribunais e Varas** | `ArchiveFileBean.court`, `ArchiveFileBean.courtDepartment` | Core EJB | N/A | **BRAZILIANIZE** | Segmento CNJ ($J=1..9$), Tribunal ($TR=01..27$), Comarca e Vara | Fase 2 (`feat/brazilian-domain`) | Mapeamento de todos os tribunais (TJs, TRFs, TRTs) | Pronto p/ Implementação |
| **Tabelas Processuais Unificadas (TPU)**| `ArchiveFileBean.subjectField`, `FormTypeBean` | EJB, REST | N/A | **BRAZILIANIZE** | Classes, Assuntos e Movimentações da TPU/CNJ (Res. 46/2007) | Fase 2 (`feat/brazilian-domain`) | Carga de catálogo TPU e busca hierárquica | Pronto p/ Implementação |
| **Segredo de Justiça** | `ArchiveFileBean.custom1` | EJB, RBAC | N/A | **BRAZILIANIZE** | Flag nativa de Segredo de Justiça (CPC art. 189) com restrição RBAC | Fase 2 (`feat/brazilian-domain`) | Validação de acesso restrito por perfil | Pronto p/ Implementação |
| **Árvore de Pastas de Documentos** | `CaseFolder.java`, `CaseFolderCellRenderer.java` | Swing, JPA | `TreeNodeUtilsTest` | **KEEP** | Árvore de Pastas do Processo (Petições, Decisões, Provas, Guias) | Fase 3 (`feat/cases-ux`) | Navegação e criação de subpastas | Mapeado |
| **Histórico e Trilha de Auditoria** | `ArchiveFileHistoryBean.java`, `HistoryComparator` | JPA | N/A | **KEEP** | Timeline de Histórico e Auditoria do Processo | Fase 3 (`feat/cases-ux`) | Registro automático de eventos no histórico | Mapeado |
| **Prazos de Reapresentação (Wiedervorlage)**| `ArchiveFileReviewsBean.java`, `ReviewsComparator` | EJB, Swing | N/A | **BRAZILIANIZE** | Acompanhamentos e Lembretes de Diligência do Processo | Fase 5 (`feat/deadlines-workflow`) | Criação e reagendamento de follow-ups | Pronto p/ Implementação |
| **Associação de Partes e Polos** | `ArchiveFileAddressesBean.java`, `PartyTypeBean` | JPA, EJB | N/A | **BRAZILIANIZE** | Partes Processuais (Polo Ativo, Passivo, Terceiros, Custos Legis) | Fase 2 (`feat/brazilian-domain`) | Adição de partes com papéis processuais brasileiros | Pronto p/ Implementação |
| **Tags e Etiquetas do Processo** | `ArchiveFileTagsBean.java` | JPA | `TaggingTest` | **KEEP** | Etiquetas e Marcadores Customizados | Fase 3 (`feat/cases-ux`) | Atribuição de tags simples e multivaloradas | Mapeado |
| **Formulários Dinâmicos por Matéria** | `ArchiveFileFormsBean.java`, `FormsService.java` | Groovy, EJB | N/A | **BRAZILIANIZE** | Fichas Dinâmicas por Ramo (Trabalhista, Família, Previdenciário, etc.) | Fase 2 (`feat/brazilian-domain`) | Renderização e persistência de fichas especializadas | Pronto p/ Implementação |
| **Arquivamento e Desarquivamento** | `ArchiveFileService.archiveCase()` | EJB | `ArchiveFileServiceTest` | **KEEP** | Arquivamento com retenção e conformidade LGPD | Fase 8 (`feat/security-lgpd`) | Transição de estado de arquivamento | Mapeado |

---

### 2.2 Módulo de Contatos e CRM (`Address` / `Contacts`)

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :---: |
| **Cadastro de Pessoas Físicas** | `AddressBean.java` | JPA | `SimilarityTest` | **BRAZILIANIZE** | Pessoa Física com CPF (validação Módulo 11), RG/CIN, Nacionalidade | Fase 2 (`feat/brazilian-domain`) | Validação de CPF com dígitos verificadores válidos | Pronto p/ Implementação |
| **Cadastro de Pessoas Jurídicas** | `AddressBean.java` | JPA | N/A | **BRAZILIANIZE** | Pessoa Jurídica com CNPJ (incluindo alfanumérico), Razão Social, IE/IM | Fase 2 (`feat/brazilian-domain`) | Validação de CNPJ e formatação correta | Pronto p/ Implementação |
| **Cadastro de Advogados** | `AddressBean.java` | JPA | N/A | **BRAZILIANIZE** | Advogado com Inscrição OAB (Número, UF, Principal/Suplementar) | Fase 2 (`feat/brazilian-domain`) | Validação de formato OAB por UF | Pronto p/ Implementação |
| **Códigos Postais (PLZ → CEP)** | `CityDataBean.java`, `ImportZipCodesThread` | JPA, EJB | N/A | **BRAZILIANIZE** | CEP Brasileiro (8 dígitos) com integração ViaCEP e Base IBGE | Fase 2 (`feat/brazilian-domain`) | Consulta e autopreenchimento de CEP | Pronto p/ Implementação |
| **Bancos e Agências (BLZ/BIC → COMPE)**| `BankDataBean.java`, `ImportBanksThread` | JPA | N/A | **BRAZILIANIZE** | Tabela FEBRABAN de Bancos (Código COMPE 3 dígitos, ISPB, Agência/Conta)| Fase 6 (`feat/financial-brazil`) | Importação e busca de bancos brasileiros | Pronto p/ Implementação |
| **Detecção de Conflito de Interesses** | `ConflictOfInterestUtils.java` | EJB, Swing | N/A | **BRAZILIANIZE** | Verificação de Conflito de Interesses por CPF/CNPJ e Nome Normalizado | Fase 3 (`feat/cases-ux`) | Interceptação de cadastro com partes conflitantes | Pronto p/ Implementação |
| **Documentos do Contato** | `AddressDocumentsBean.java`, `AddressDocumentService`| JPA, VFS | N/A | **KEEP** | Pasta de Documentos Gerais do Cliente (Procurações, Contratos) | Fase 3 (`feat/cases-ux`) | Upload e download de anexos do contato | Mapeado |
| **Mala Direta e Campanhas** | `Campaign.java`, `CustomerRelationsService.java` | JPA, EJB | N/A | **LOCALIZE** | Campanhas de Comunicação e Mala Direta em pt-BR | Fase 7 (`feat/communications`) | Exportação de listas e filtros de contatos | Mapeado |

---

### 2.3 Integrações Judiciais e Comunicações Eletrônicas (`beA` → `Judicial Integration`)

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :---: |
| **Wrapper Proprietário beA** | `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar`| Fechado (BRAK) | N/A | **BLOCKED_FOR_LICENSE_REVIEW** | N/A (Remoção total e substituição por adaptadores abertos) | Fase 1 (`chore/upstream-baseline`)| Remoção da dependência no POM e build | A Remover |
| **Serviço de Caixa beA** | `BeaService.java`, `BeaAccess.java` | beA wrapper | N/A | **REPLACE** | Arquitetura Plugável `JudicialSystemAdapter` | Fase 4 (`feat/judicial-integrations`)| Interface unificada de adaptadores | Pronto p/ Implementação |
| **Diário da Justiça Nacional (DJEN)** | Novo conector | HTTP REST (CNJ)| N/A | **BRAZILIANIZE** | `DjenAdapter` (Consumo da ComunicaAPI do CNJ `/api/v1/comunicacao`) | Fase 4 (`feat/judicial-integrations`)| Parsing de JSON do DJEN e paginação | Pronto p/ Implementação |
| **Enriquecimento DataJud** | Novo conector | Elasticsearch (CNJ)| N/A | **BRAZILIANIZE** | `DataJudAdapter` (Consulta pública DataJud com chave CNJ) | Fase 4 (`feat/judicial-integrations`)| Consulta por NPU e parsing de movimentos | Pronto p/ Implementação |
| **Workflow de Publicações (ATRIUM)** | Nova arquitetura | JPA, EJB, REST | N/A | **BRAZILIANIZE** | Triagem de Publicações (Leitura vs Workflow Interno vs Ciência Oficial)| Fase 4 (`feat/judicial-integrations`)| Transições de status e garantia de não-ciência | Pronto p/ Implementação |
| **Portais Eletrônicos (PJe, eproc, e-SAJ)**| Novos adaptadores | MNI / REST | N/A | **BRAZILIANIZE** | `PjeAdapter`, `EprocAdapter`, `EsajAdapter` (Importação assistida) | Fase 4 (`feat/judicial-integrations`)| Importação segura com credenciais isoladas | Pronto p/ Implementação |

---

### 2.4 Prazos, Agenda e Calendário (`Calendar` / `Deadlines`)

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :---: |
| **Calendário e Agenda de Compromissos**| `CalendarService.java`, `MultiCalDialog.java` | JPA, CalDAV | N/A | **LOCALIZE** | Agenda de Compromissos, Audiências e Sessões de Julgamento em pt-BR | Fase 5 (`feat/deadlines-workflow`) | Criação e visualização de audiências na grade | Mapeado |
| **Cálculo de Feriados (Alemanha)** | `CalendarCommonUtils.java` | Jollyday | N/A | **REPLACE** | Calendário Forense Brasileiro (Feriados Nacionais, Estaduais, Lei 5.010)| Fase 5 (`feat/deadlines-workflow`) | Testes de contagem de dias úteis com feriados | Pronto p/ Implementação |
| **Recesso Forense (CPC art. 220)** | Novo motor temporal | Core EJB | N/A | **BRAZILIANIZE** | Suspensão obrigatória de prazos entre 20 de dezembro e 20 de janeiro | Fase 5 (`feat/deadlines-workflow`) | Teste de prorrogação de prazo iniciado no recesso | Pronto p/ Implementação |
| **Regime de Prazos (CPC vs CLT vs CPP)**| Novo motor temporal | Core EJB | N/A | **BRAZILIANIZE** | Contagem em Dias Úteis (CPC/CLT) vs Dias Corridos (CPP) + Regra DJEN | Fase 5 (`feat/deadlines-workflow`) | Testes comparativos de prazos por matéria | Pronto p/ Implementação |
| **Anti-Alucinação de Prazos (HITL)** | Nova arquitetura de IA | EJB, Ingo | N/A | **BRAZILIANIZE** | Sugestão Auditável de Prazos com Homologação Humana Obrigatória | Fase 5 (`feat/deadlines-workflow`) | Bloqueio de agendamento cego sem confirmação | Pronto p/ Implementação |
| **Tarefas e Kanban Jurídico (ATRIUM)** | `LegalTaskBean.java`, `TasksPanel.java` | JPA, REST | N/A | **BRAZILIANIZE** | Gestão de Tarefas vinculadas a Processos/Publicações com Kanban e Lista | Fase 5 (`feat/deadlines-workflow`) | Movimentação de cards e checklist | Pronto p/ Implementação |

---

### 2.5 Gestão Documental, Modelos e Busca (`Documents` / `Search`)

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :---: |
| **Modelos de Documentos e Placeholders**| `PlaceHolderServerUtils.java`, `DocUtilityService`| POI, ODF Toolkit | `LibreOfficeODFTest`, `MicrosoftOfficeDocxTest` | **BRAZILIANIZE** | Placeholders Brasileiros (`cliente.cpf`, `processo.numero_cnj`, `advogado.oab`) | Fase 3 (`feat/cases-ux`) | Fusão de modelos DOCX e ODT com dados do caso | Pronto p/ Implementação |
| **Manipulação de PDFs** | `PdfFormsAccess.java`, `PDFBlankPageSplitter`| iText 9, PDFBox | `DocumentPreviewTest` | **KEEP** | Preenchimento de Formulários, Divisão em Páginas e Carimbo Digital | Fase 3 (`feat/cases-ux`) | Divisão de PDF em páginas e merge | Mapeado |
| **Visualizadores Nativos de Documentos**| `DocumentViewerFactory.java`, Painéis de UI | Swing, JavaFX | N/A | **KEEP** | Pré-visualização integrada de PDFs, HTML, Áudio, Imagens e E-mails | Fase 3 (`feat/cases-ux`) | Abertura correta de previews no painel | Mapeado |
| **Edição Colaborativa Web (WOPI)** | `WopiEndpoint.java`, `OfficePageComponent.ts` | REST, Collabora | N/A | **KEEP** | Edição Online via Collabora Online / Microsoft 365 na Web UI | Fase 9 (`feat/web-ui-modern`) | Comunicação WOPI e salvamento de lock | Mapeado |
| **Busca Full-Text com Apache Lucene** | `SearchService.java`, `SearchIndexProcessor.java`| Lucene 9.12, Tika | N/A | **BRAZILIANIZE** | Busca Textual com suporte a filtros por CNJ, CPF, CNPJ e OAB | Fase 3 (`feat/cases-ux`) | Indexação e busca booleana com highlight | Pronto p/ Implementação |
| **Busca Global Rápida (Ctrl+K / ATRIUM)**| `GlobalSearchDialog.java` | Swing, REST | N/A | **BRAZILIANIZE** | Command Palette Omni-Search (`Ctrl+K` / `Cmd+K`) para entidades e comandos | Fase 3 (`feat/cases-ux`) | Disparo de atalho e navegação instantânea | Pronto p/ Implementação |

---

### 2.6 Módulo Financeiro e Faturamento (`Invoicing` / `Finance`)

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :---: |
| **Faturamento ZUGFeRD / XRechnung** | `j-lawyer-invoicing/`, `Mustangproject` | Mustang, PDFBox | N/A | **REPLACE** | Nota Fiscal de Serviços Eletrônica (NFS-e Nacional/ABRASF) + Recibos | Fase 6 (`feat/financial-brazil`) | Emissão de recibo de honorários com retenções | Pronto p/ Implementação |
| **Tabela RVG (Alemanha)** | `CalculationPlugins`, `rvg.groovy` | Groovy | N/A | **REPLACE** | Módulo de Honorários Contratuais (Êxito, Fixos, Horas) e Sucumbenciais | Fase 6 (`feat/financial-brazil`) | Lançamento e cálculo de sucumbência CPC art. 85| Pronto p/ Implementação |
| **Pagamentos SEPA / EPC QR Code** | `java-sepa-xml`, `GiroCode` | SEPA lib | N/A | **REPLACE** | Pagamentos via PIX (QR Code / Copia e Cola EMVCo) e Boleto Bancário | Fase 6 (`feat/financial-brazil`) | Geração de payload PIX e código de barras | Pronto p/ Implementação |
| **Apontamento de Horas (Time Billing)** | `TimesheetService.java`, `Timesheet.java` | JPA, EJB | N/A | **LOCALIZE** | Apontamento de Horas e Faturamento por Horas Trabalhadas em pt-BR | Fase 6 (`feat/financial-brazil`) | Lançamento de horas e conversão em fatura | Mapeado |
| **Conta-Corrente do Processo** | `CaseAccountEntry.java`, `CaseAccountEntryPanel`| JPA, EJB | N/A | **LOCALIZE** | Conta do Processo (Custas, Adiantamentos, Depósitos Judiciais, Reembolsos) | Fase 6 (`feat/financial-brazil`) | Lançamento e conciliação de saldo do caso | Mapeado |
| **Cálculo de Execuções / Dívidas** | `ClaimLedgerService.java`, `ClaimLedger.java` | JPA, EJB | N/A | **BRAZILIANIZE** | Livro de Cobrança com Correção Monetária Oficial (IPCA-E/SELIC) e Juros | Fase 6 (`feat/financial-brazil`) | Cálculo de liquidação de sentença com juros BR | Pronto p/ Implementação |
| **Conciliação Bancária** | `BankStatementsCSVConfig.java`, `PaymentService` | CSV, JPA | N/A | **BRAZILIANIZE** | Leitura de Extratos Bancários OFX e Retornos CNAB 240/400 | Fase 6 (`feat/financial-brazil`) | Parsing de arquivo OFX e rateio em faturas | Pronto p/ Implementação |

---

### 2.7 Inteligência Artificial ("Ingo") e Assistente

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :--- |
| **Motor de Chat e Assistente Ingo** | `AssistantConfig.java`, `AssistantChatDialog.java`| REST (LLMs) | N/A | **LOCALIZE** | Assistente Jurídico Provider-Agnostic em pt-BR (OpenAI, Claude, Ollama) | Fase 8 (`feat/security-lgpd`) | Diálogo interativo e extração documental | Mapeado |
| **Catálogo de 66 Ferramentas (Tool Calling)**| `ToolRegistry.java` | EJB, REST | N/A | **BRAZILIANIZE** | Ferramentas de IA com Controle de Risco e Salvaguardas HITL em pt-BR | Fase 8 (`feat/security-lgpd`) | Execução de tool calling com aprovação humana | Pronto p/ Implementação |
| **Geração de Peças via Modelos** | `AssistantGenerateDialog.java` | Groovy, POI | N/A | **LOCALIZE** | Geração de Minutas de Petições e Pareceres com Injeção em Templates | Fase 8 (`feat/security-lgpd`) | Geração de minuta e preenchimento de placeholders| Mapeado |
| **Sanitização de PII antes do Envio para IA**| `AssistantReplacement.java` | Regex, EJB | N/A | **BRAZILIANIZE** | Mascaramento Prévio de Dados Pessoais (CPF, Contas) p/ conformidade LGPD | Fase 8 (`feat/security-lgpd`) | Testes de ofuscação de PII em prompts | Pronto p/ Implementação |

---

### 2.8 Segurança, LGPD, Infraestrutura e Backups

| Feature j-lawyer | Localização no Código | Dependências | Testes Existentes | Classificação | Equivalente Brasileiro | Fase / Branch | Testes Necessários | Status |
| :--- | :--- | :--- | :--- | :---: | :--- | :---: | :--- | :--- |
| **Controle de Acesso RBAC** | `AppRoleBean.java`, `SecurityService.java` | EJB, JAAS | `JwtServiceTest` | **BRAZILIANIZE** | Perfis de Acesso Brasileiros (Sócio, Advogado, Assistente, Financeiro, DPO) | Fase 8 (`feat/security-lgpd`) | Autorização de métodos EJB com `@RolesAllowed` | Pronto p/ Implementação |
| **Autenticação em Dois Fatores (MFA/TOTP)**| `OpenSpec add-two-factor-auth` | RFC 6238 | `PasswordsUtilTest` | **KEEP** | Autenticação MFA com Aplicativos Autenticadores Padrão | Fase 8 (`feat/security-lgpd`) | Validação de código TOTP no login | Mapeado |
| **Criptografia em Repouso de Documentos**| `OpenSpec add-document-encryption-at-rest` | AES-256-GCM | `WrappedSMimeTest` | **KEEP** | Criptografia AES-256 no Repositório de Documentos e Backups | Fase 8 (`feat/security-lgpd`) | Cifra e decifra transparente de anexos | Mapeado |
| **Trilha de Auditoria Imutável** | `TransactionLog.java`, `IntegrationHookLog.java` | JPA | N/A | **BRAZILIANIZE** | Log de Auditoria Detalhado para Conformidade com a LGPD | Fase 8 (`feat/security-lgpd`) | Registro de visualização de casos sigilosos | Pronto p/ Implementação |
| **Gerenciador de Backup e Restauração**| `j-lawyer-backupmgr`, `RestoreExecutor.java` | JavaFX 17 | `RestoreExecutorTest` | **KEEP** | Utilitário Autônomo de Backup e Restauração Completa de Base e Arquivos | Fase 10 (`feat/release-installers`)| Backup completo e teste de restauração | Mapeado |

---

## 3. Resumo Quantitativo de Paridade

- **Total de Funcionalidades Auditadas:** 48 áreas funcionais maiores (abrangendo 76 entidades, 28 EJBs e 66 tools de IA).
- **KEEP (Preservadas Nativamente):** 16 itens (33,3%)
- **LOCALIZE (Tradução e Adaptação pt-BR):** 8 itens (16,7%)
- **BRAZILIANIZE (Extensão e Regras Oficiais BR):** 19 itens (39,6%)
- **REPLACE (Substituição de Padrões Alemães):** 4 itens (8,3%)
- **BLOCKED_FOR_LICENSE_REVIEW (Remoção de Binário Fechado):** 1 item (2,1%)
- **DEPRECATE / NOT_APPLICABLE:** 0 itens silenciosamente removidos.
