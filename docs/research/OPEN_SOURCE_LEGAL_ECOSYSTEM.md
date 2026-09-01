# OPEN SOURCE LEGAL ECOSYSTEM RESEARCH & BENCHMARK REPORT
## Pesquisa Arquitetural, Funcional e Regulatória do Ecossistema LegalTech Open Source para o BR-LAWYER

> **Documento de Governança Técnica & Pesquisa:** `docs/research/OPEN_SOURCE_LEGAL_ECOSYSTEM.md`  
> **Iniciativa:** BR-LAWYER (Evolução Soberana do j-lawyer.org para o Brasil)  
> **Modo:** EXCLUSIVAMENTE READ-ONLY RESEARCH  
> **Data de Conclusão:** 31 de Agosto de 2026  
> **Status:** APROVADO PARA REFERÊNCIA DE ENGENHARIA  

---

## 1. Sumário Executivo e Princípios Cardeais de Governança

### 1.1 Premissa Fundamental
O **BR-LAWYER** é concebido como um **fork evolutivo sustentável do j-lawyer.org**. Esta pesquisa tem por escopo exclusivo mapear e investigar projetos *open source* maduros e de excelência técnica no ecossistema global e brasileiro, identificando conceitos, algoritmos, padrões de UX, schemas de dados e integrações que possam acelerar o desenvolvimento do produto nacional.

### 1.2 Regra de Ouro Arquitetural: Não Criar uma Colagem de Aplicações
O BR-LAWYER **não** deve ser descaracterizado ou fragmentado em uma colagem de dezenas de microsserviços heterogêneos desconexos. Recomenda-se integração ou porte apenas quando o componente externo oferecer solução materialmente superior, validada e inexistente no core j-lawyer.

A governança técnica segue estritamente a seguinte **Hierarquia de Preferência**:
$$\text{Reutilizar Core j-lawyer} > \text{Adaptar Conceito Externo} > \text{Integrar Serviço/Sidecar} > \text{Copiar Código} > \text{Reescrever Funcionalidade Madura}$$

### 1.3 Matriz de Compatibilidade Upstream e Não-Atrito de Merge
Qualquer evolução derivada desta pesquisa deve obedecer aos 6 Princípios Cardeais de `docs/UPSTREAM_COMPATIBILITY_GUIDELINES.md`:
1. **Aditividade Estrita:** Recursos específicos do Brasil residem em pacotes e classes novos (`com.jdimension.jlawyer.domain.legal.*`, `com.jdimension.jlawyer.integration.brazil.*`).
2. **Sem Refatorações Cosméticas:** Proibido reformatar código original upstream.
3. **Estabilidade de Contratos:** Nenhuma assinatura pública ou protegida de EJB/JPA herdada é alterada ou removida.
4. **Localização via ResourceBundles:** Strings em Português exclusivamente em arquivos `_pt_BR.properties`.
5. **Padrão Adapter & Feature Flags:** Desacoplamento de módulos locais exclusivos (substituição do `beA` pelo `JudicialSystemAdapter`).
6. **Evolução Não-Destrutiva de JPA:** Tabelas novas utilizam prefixo `br_` ou colunas anuláveis em tabelas existentes.

---

## 2. Panorama Comparativo Global dos 10 Projetos Avaliados

| # | Projeto / Repositório | Licença | Stack Principal | Maturidade | Papel no Ecossistema BR-LAWYER |
| :-: | :--- | :--- | :--- | :-: | :--- |
| **1** | **STELLA** (`stella/stella`) | Apache 2.0 | FastAPI, Bun/Elysia, Rust/WASM, Postgres | Alta | **Referência de AI Legal Workspace, MCP e Anonimização WASM** |
| **2** | **DOCASSEMBLE** (`jhpyle/docassemble`) | MIT | Python, Flask, Celery, docxtpl, Redis, Postgres | Altíssima | **Referência de Guided Interviews e DOCX Templating Avançado** |
| **3** | **NANOJUD** (`lucmolero/nanojud`) | MIT | Python 3.10+, Pydantic v2, HTTPX, MCP SDK | Média/Ativa | **Blueprint para o `JudicialSystemAdapter` (DJEN, DataJud, Resiliência)** |
| **4** | **KIMAI** (`kimai/kimai`) | AGPLv3 | PHP 8.2+, Symfony, Doctrine, Tailwind | Altíssima | **Port de Algoritmos de Precificação, Cascata de Taxas e Budgets** |
| **5** | **OPENSIGN** (`OpenSignLabs/OpenSign`) | AGPLv3 | Node.js, Express, Parse Server, React, Mongo | Alta | **Integração Sidecar para Assinatura Eletrônica de Clientes & Contratos** |
| **6** | **PAPERLESS-NGX** (`paperless-ngx/...`) | GPLv3 | Python/Django, Celery, Tesseract, OCRmyPDF | Altíssima | **Sidecar de OCR/PDF-A e Port de Classificador de Metadados ML (TF-IDF)** |
| **7** | **MAYAN EDMS** (`mayan-edms/...`) | Apache 2.0 | Python/Django, Celery, PostgreSQL | Alta | **Port de Conceitos de Ciclo de Vida Documental (FSM) e Schemas Dinâmicos** |
| **8** | **OPENSPECTER** (`akashshrx/...`) | AGPLv3 | Next.js, TypeScript, Express, Supabase | Média | **Port de Conceito de Matter-Scoped AI e Tabular Reviews** |
| **9** | **LAWLINK** (`lawflow-boop/LawLink`) | MIT | Next.js, TypeScript, Prisma, PostgreSQL | Média | **Port de Algoritmo de Conflict Check e Fluxo de Client Intake** |
| **10** | **OFICIAL CNJ / PJE / PDPJ** | N/A (Oficial) | Java/Spring, Kubernetes, Keycloak, REST/SOAP | Governamental | **Padrão Normativo Obrigatório (DataJud v2, DJEN, MNI, PJeOffice)** |

---

## 3. Investigação Técnica Profunda por Projeto

### 3.1 PROJETO 1: STELLA (`stella/stella`)
* **URL:** `https://github.com/stella/stella` (Portal: `stll.app`)
* **Licença:** Apache License 2.0
* **Stack:** Python 3.10+ (FastAPI), Bun (Elysia), PostgreSQL (`pgvector`), Rust (WebAssembly), OpenWebUI.
* **Maturidade:** Alta; arquitetura desenhada para escala de bancas *Magic Circle*, com suíte de testes de regressão adversarial sobre roundtrips de documentos OOXML.

#### Análise Funcional e Arquitetural:
1. **Matter Workspace:** O caso (*Matter*) é o contêiner universal de isolamento de contexto. Documentos, notas, tarefas e sessões de chat com agentes de IA herdam a fronteira do Matter, garantindo ausência de vazamento de dados (*cross-matter data leakage*).
2. **Edição Cirúrgica de DOCX (*Surgical DOCX Editing*):** Em vez de converter `.docx` para HTML simples (o que destrói sumários, cabeçalhos, rodapés e estilos corporativos), o Stella manipula diretamente a árvore OpenXML. A IA sugere alterações pontuais em parágrafos específicos mantendo o arquivo binário intacto, com suporte a *tracked changes* e comentários.
3. **Tabular Review com Citações Fundamentadas (*Grounded Citations*):** Permite extrair dados estruturados em lote de múltiplos documentos (ex: Due Diligence de 100 contratos de locação). Cada célula da tabela contém ponteiros de offset de caracteres; ao clicar no dado, o documento é aberto no trecho exato que comprova a resposta, eliminando alucinações.
4. **Legal AI Agent & Skills:** Agente orientado a ferramentas (*Tool Calling*) com sistema declarativo de habilidades (*skills*) versionadas em arquivos de configuração.
5. **Anonimização e Redação de PII (`stella/anonymize`):** Módulo compilado em Rust/WASM que roda no cliente antes do despacho do prompt para a API do LLM, detectando e mascarando nomes, CPFs, CNPJs, dados bancários e informações sensíveis, com tela de revisão e homologação humana (*Interactive Redaction Preview*).
6. **Model Context Protocol (MCP):** O Stella atua como servidor MCP completo. Clientes de IA externos (Claude Desktop, IDEs ou agentes autônomos) conectam-se ao Stella para consultar casos, listar documentos e invocar pesquisas forenses.
7. **Source & Registry Adapters:** Conectores com tipagem estrita para entidades corporativas e bases jurídicas (Companies House, SEC EDGAR; equivalente ao DataJud e Receita Federal no Brasil).

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer possui a pasta do processo (`ArchiveFileBean`) e o assistente "Ingo" (REST v2 básico), mas não possui edição cirúrgica de DOCX no browser, MCP, revisão tabular nem motor de anonimização client-side.
* **Recomendações:**
  * `ADOPT_CODE`: Adotar o módulo WASM `stella/anonymize` no frontend Angular para conformidade estrita com a LGPD antes de qualquer chamada a LLMs.
  * `PORT_CONCEPT`: Implementar o padrão **BR-LAWYER MCP Server** no backend Java para interoperabilidade com Claude/OpenAI/Gemini.
  * `PORT_ALGORITHM`: Portar a lógica de manipulação cirúrgica de parágrafos OpenXML no editor de documentos.

---

### 3.2 PROJETO 2: DOCASSEMBLE (`jhpyle/docassemble`)
* **URL:** `https://github.com/jhpyle/docassemble`
* **Licença:** MIT License
* **Stack:** Python 3.10+, Flask, Celery, Redis, PostgreSQL, `python-docx-template` (`docxtpl`), LibreOffice, `pdftk`, `pdfjam`.
* **Maturidade:** Altíssima (10+ anos, referência mundial em Legal Automation, mantido por Jonathan Pyle e Suffolk University LIT Lab).

#### Análise Funcional e Arquitetural:
1. **Guided Interviews & Backward Chaining:** Docassemble não opera com formulários estáticos ou árvores de decisão manuais rígidas. Ele utiliza um **Grafo Direcionado Acíclico (DAG) com Encadeamento Regressivo (*Backward Chaining*)**. O desenvolvedor define uma meta obrigatória (`mandatory: True`, ex: "gerar contrato social"). Se a variável `socio_administrador.cpf` for necessária e não estiver preenchida, o motor busca recursivamente o bloco que define essa variável e renderiza a pergunta para o usuário.
2. **Document Assembly Engine:** Utiliza `python-docx-template`, inserindo tags **Jinja2** dentro de modelos Microsoft Word `.docx`. Permite condicionais (`{% if %}`), loops de repetição de linhas de tabela (`{% tr for item in items %}`), interpolação com filtros monetários e concordância gramatical, inclusão de subdocumentos e carimbos de assinatura gráfica.
3. **Manipulação de PDF:** Converte DOCX para PDF via LibreOffice headless e utiliza `pdftk`/`pdfjam`/`pikepdf` para costurar anexos (documentos de identidade, comprovantes de residência) com numeração contínua de páginas (*Bates numbering*).
4. **Questionários Reutilizáveis & Intake:** Permite que clientes preencham dados externamente via links seguros, populando automaticamente o banco de dados.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer utiliza substituição simples de texto em strings (`MicrosoftOfficeAccess.java` / `replaceInBodyElements`) e formulários estáticos (`ArchiveFileFormsBean`). Não suporta loops, condicionais ricas em DOCX nem entrevistas dinâmicas orientadas a metas.
* **Viabilidade de Equivalente Nativo:** Recomenda-se **não** incorporar o monólito Python do Docassemble ao build padrão do WildFly. Em seu lugar, deve-se implementar um equivalente nativo em Java:
  * `PORT_CONCEPT` / `ADOPT_CODE`: Adotar a biblioteca Java **`docx-stamper`** (baseada em Spring Expression Language / SpEL) no `j-lawyer-server-ejb`. Isso confere ao BR-LAWYER poder idêntico ao Jinja2 (`{{#if}}`, `{{#repeat}}`, `{{variable}}`) sem adicionar dependências externas ou runtime Python.
  * `PORT_CONCEPT`: Implementar o Wizard de Guided Interviews no `j-lawyer-web` (Angular 19) orientado por JSON Schemas reativos.
  * `INTEGRATE_SIDECAR`: Oferecer o Docassemble oficial como contêiner sidecar opcional para bancas que já possuam acervo legado de questionários em YAML.

---

### 3.3 PROJETO 3: NANOJUD (`lucmolero/nanojud`)
* **URL:** `https://github.com/lucmolero/nanojud`
* **Licença:** MIT License
* **Stack:** Python 3.10+, Pydantic v2, HTTPX, Typer, MCP SDK.
* **Maturidade:** Média / Ativa; toolkit especializado em extração ética e estruturada de dados do Judiciário brasileiro.

#### Análise Funcional e Arquitetural:
1. **Conector DJEN Oficial:** Cliente assíncrono para a API REST da PDPJ (`comunicaapi.pje.jus.br/api/v1/comunicacao`), permitindo busca por OAB/UF, número de processo e período, com extração estruturada de despachos, decisões e prazos.
2. **Conector DataJud (CNJ):** Wrapper para o cluster Elasticsearch da API Pública do CNJ (`https://api-publica.datajud.cnj.jus.br`), consultando metadados completos de processos por NPU, classe TPU e tribunal com paginação eficiente via cursor `search_after`.
3. **Contratos de Dados Normalizados:** Schemas Pydantic canônicos (`ProcessoUnificado`, `ParteUnificada`, `MovimentacaoUnificada`, `PublicacaoUnificada`) que desacoplam as particularidades dos diferentes tribunais.
4. **Envelope de Proveniência (*Data Provenance*):** Rastreabilidade formal com hash SHA-256 do payload bruto, timestamp UTC, sistema de origem e score de confiança do dado.
5. **Modelo de Resiliência & Rate Limiting:** Implementação de algoritmo *Token Bucket* para evitar HTTP 429, *Exponential Backoff* com jitter para erros 5xx e *Circuit Breaker* por tribunal para evitar congelamento de threads.
6. **Deduplicação Determinística:** Hash de normalização textual (`NPU + TipoAto + DataDisponibilizacao + HashTextoLimpo`) que impede a duplicação de intimações e andamentos repetidos entre DJEN e sistemas locais.
7. **Servidor MCP Forense:** Expõe tools judiciais brasileiras para agentes de IA consultarem andamentos e publicações em linguagem natural.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer possui a infraestrutura do `beA` (tribunais alemães), baseada em EJB e wrappers proprietários BRAK.
* **Recomendações:**
  * `PORT_ALGORITHM` & `INTEGRATE_API`: A arquitetura do Nanojud é o blueprint perfeito para o **`JudicialSystemAdapter`** do BR-LAWYER. Toda a lógica de consumo do DJEN, DataJud, cálculo de prazos do art. 224 do CPC e deduplicação deve ser implementada em Java puro nos serviços EJB (`DjenAdapter.java`, `DataJudAdapter.java`).
  * `REJECT` (Core): Descartar web scrapers frágeis de eSAJ no núcleo da aplicação Java. Manter eventuais scrapers isolados como sidecars opcionais.

---

### 3.4 PROJETO 4: KIMAI (`kimai/kimai`)
* **URL:** `https://github.com/kimai/kimai`
* **Licença:** AGPLv3
* **Stack:** PHP 8.2+, Symfony 6/7, Doctrine ORM, MySQL/MariaDB, Tailwind/Tabler.
* **Maturidade:** Altíssima (15+ anos, padrão ouro em Time Tracking e Billing open source).

#### Análise Funcional e Arquitetural:
1. **Hierarquia de Taxas (*Rate Engine* em 5 Níveis):** Resolução algorítmica de preço por hora em cascata:
   $$\text{Taxa Efetiva} = \text{Coalesce}(\text{Taxa Atividade}, \text{Taxa Processo}, \text{Taxa Cliente}, \text{Taxa Usuário}, \text{Taxa Global})$$
2. **DRE Horário (Custo Interno vs. Preço Cobrado):** Armazena para cada apontamento tanto o custo horário do colaborador (*internal rate*) quanto o valor faturável ao cliente (*billable rate*), viabilizando relatórios de margem líquida por processo.
3. **Orçamentos Flexíveis & Retainers Mensais:** Suporte a orçamentos em horas ou em valor monetário por processo ou por cliente, com renovação mensal automática (*Monthly Reset Retainers* - modelo padrão de assessoria jurídica contínua no Brasil).
4. **Trava de Períodos Contábeis (*Lockdown Periods*):** Impede que colaboradores alterem ou lancem horas retroativas em meses fiscais já fechados ou faturados.
5. **Multi-Timer & Live Tracking:** Cronômetro dinâmico na web com alternância rápida entre múltiplos processos ativos.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer possui as entidades `Timesheet` e `TimesheetPosition`, mas com taxas estáticas digitadas manualmente, sem herança hierárquica automática, sem distinção de custo interno e sem travas de fechamento contábil.
* **Recomendações:**
  * `PORT_ALGORITHM`: Portar o algoritmo de cascata de preços em 5 níveis para o EJB `TimesheetService` e implementar a classe `RateResolverService.java`.
  * `PORT_CONCEPT`: Adicionar campos `internalRate`, `isBillable` e regras de *Lockdown Period* na entidade JPA `TimesheetPosition`.
  * `REJECT`: Rejeitar a stack PHP/Symfony; todas as regras devem ser portadas para Java 17 / Jakarta EE.

---

### 3.5 PROJETO 5: OPENSIGN (`OpenSignLabs/OpenSign`)
* **URL:** `https://github.com/OpenSignLabs/OpenSign`
* **Licença:** AGPL-3.0 (Servidor) / MIT (React SDK `@opensign/react`)
* **Stack:** Node.js, Express, Parse Server, React, Vite, MongoDB, `pdf-lib`.
* **Maturidade:** Alta; alternativa open source consolidada ao DocuSign/Adobe Sign.

#### Análise Funcional e Arquitetural:
1. **Workflow de Envelopes & Múltiplos Signatários:** Criação de envelopes com roteamento de assinantes sequencial ou paralelo, posicionamento visual drag-and-drop de campos de assinatura, rubrica e data no PDF.
2. **Trilha de Auditoria Criptográfica (*Audit Trail*):** Registro inviolável de visualização, IP, User-Agent, carimbo de tempo UTC, validação de OTP por e-mail e hash SHA-256 do documento original e final.
3. **Certificado de Conclusão (*Certificate of Completion*):** Apensado automaticamente como última página do PDF com a trilha completa de auditoria.
4. **Webhooks com HMAC-SHA256:** Notificação de eventos (`document.completed`, `document.viewed`) assinados criptograficamente.
5. **Distinção Jurídica no Brasil (Lei 14.063/2020 vs. ICP-Brasil):**
   * **Assinatura Eletrônica Simples/Avançada (OpenSign):** 100% válida e eficaz para contratos de honorários, procurações *ad judicia* extrajudiciais, termos de sigilo (NDAs), acordos e onboarding de clientes.
   * **Assinatura Qualificada ICP-Brasil (PAdES / Tokens A1 e A3):** Exclusiva para peticionamento eletrônico em tribunais (PJe, eSAJ, Projudi). O OpenSign **não** substitui o assinador ICP-Brasil.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** Inexistente.
* **Recomendações:**
  * `INTEGRATE_SIDECAR` & `INTEGRATE_API`: Disponibilizar o OpenSign como contêiner Docker sidecar pré-configurado no `docker-compose.yaml` para assinaturas de contratos de clientes.
  * `PORT_ALGORITHM`: Implementar receptor de Webhook no `j-lawyer-server-io` para baixar o PDF assinado e anexá-lo automaticamente ao processo (`ArchiveFileDocumentsBean`).
  * `PORT_ALGORITHM` (Nativo): Criar módulo nativo em Java puro com BouncyCastle + Apache PDFBox para assinaturas ICP-Brasil (PAdES) de petições judiciais.

---

### 3.6 PROJETO 6: PAPERLESS-NGX (`paperless-ngx/paperless-ngx`)
* **URL:** `https://github.com/paperless-ngx/paperless-ngx`
* **Licença:** GPLv3
* **Stack:** Python 3.10+ (Django), Celery, Redis, PostgreSQL/SQLite, Tesseract OCR, OCRmyPDF, `scikit-learn`, Angular.
* **Maturidade:** Altíssima (comunidade massiva, referência global em gestão e OCR de documentos).

#### Análise Funcional e Arquitetural:
1. **Pipeline OCRmyPDF Industrial:** Processamento assíncrono com `unpaper` (deskew, rotação automática de páginas em 90°/180°/270°, remoção de bordas escuras) e Tesseract multilíngue, convertendo imagens digitalizadas em arquivos ISO **PDF/A-1b / PDF/A-2b** com camada de texto pesquisável embutida.
2. **Classificador de Machine Learning Local (TF-IDF):** Utiliza `scikit-learn` com `TfidfVectorizer` e redes neurais MLP para aprender os padrões dos documentos do escritório e prever automaticamente: Correspondente (Remetente), Tipo de Documento e Tags, com zero custo de API e execução em milissegundos.
3. **Inbox Geral de Documentos:** Diretório monitorado e consumo via e-mail para triagem centralizada antes da atribuição aos processos.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer possui motor de busca full-text com **Apache Lucene 9.12.0** nativo e extração via Apache Tika. No entanto, o j-lawyer não faz pré-processamento de imagem (deskew), não gera PDF/A pesquisável no storage e não possui autoclassificador local com ML.
* **Recomendações:**
  * `INTEGRATE_SIDECAR`: Empacotar um microserviço Docker leve baseado no pipeline OCRmyPDF para normalização de PDFs e OCR assíncrono.
  * `PORT_ALGORITHM`: Portar o algoritmo de autoclassificação TF-IDF para a JVM utilizando o módulo nativo **`lucene-classification`** do Apache Lucene ou bibliotecas como Tribuo/Smile, eliminando custos de LLM para tarefas rotineiras de classificação de peças.
  * Preservar o motor Apache Lucene 9.12 como mecanismo primário de indexação textual.

---

### 3.7 PROJETO 7: MAYAN EDMS (`mayan-edms/mayan-edms`)
* **URL:** `https://gitlab.com/mayan-edms/mayan-edms`
* **Licença:** Apache License 2.0
* **Stack:** Python 3.10+ (Django), Celery (multi-queue), PostgreSQL.
* **Maturidade:** Alta (12+ anos, voltado a compliance estrito e gestão documental governamental).

#### Análise Funcional e Arquitetural:
1. **Document Lifecycle FSM (Máquina de Estados Finitos):** Esteira formal de ciclo de vida documental (*Rascunho $\rightarrow$ Em Revisão $\rightarrow$ Aprovado $\rightarrow$ Assinado $\rightarrow$ Protocolado $\rightarrow$ Arquivado*).
2. **Schemas de Metadados Dinâmicos por Tipo de Documento:** Definição de metadados tipados e validados para cada tipo de documento (ex: Contrato Social tem CNPJ e Sócios; Matrícula de Imóvel tem Cartório e Livro).
3. **ACLs e Permissões em Nível de Objeto:** Permite restringir a visibilidade de documentos específicos dentro de um processo (ex: segregação de documentos de honorários ou sigilo judicial).
4. **Versionamento Criptográfico Imutável:** Cada revisão mantém o hash SHA-256 e o binário físico original invioláveis para auditoria forense.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer possui formulários dinâmicos no nível do processo, mas não no nível de documentos individuais, e seu controle de acesso a documentos é herdado integralmente da pasta do caso.
* **Recomendações:**
  * `PORT_CONCEPT`: Modelar o enum `DocumentState` no EJB `DocumentServiceLocal` para suportar a esteira de revisão de peças jurídicas (*Minuta $\rightarrow$ Revisão do Sócio $\rightarrow$ Assinatura*).
  * `PORT_CONCEPT`: Adicionar campo de confidencialidade/restrição de papel no `ArchiveFileDocumentsBean`.
  * `REJECT`: Rejeitar a stack monolítica pesada do Mayan.

---

### 3.8 PROJETO 8: OPENSPECTER (`akashshrx/OpenSpecter`)
* **URL:** `https://github.com/akashshrx/OpenSpecter` (Baseado no Mike OSS / `willchen96/mike`)
* **Licença:** AGPL-3.0
* **Stack:** TypeScript, Next.js, Express, Supabase (PostgreSQL com RLS), Cloudflare R2 / S3.
* **Maturidade:** Média; clone open source de workspaces jurídicos de elite (Harvey AI / Legora).

#### Análise Funcional e Arquitetural:
1. **Matter-Scoped AI:** Aplicação estrita de Row Level Security (RLS) no PostgreSQL, garantindo que consultas e embeddings de IA fiquem circunscritos ao projeto ativo.
2. **Workflows Jurídicos Reutilizáveis:** Biblioteca de prompts estruturados e modelos de extração tabular compartilháveis entre os membros da banca (ex: Checklist de Petição Inicial, Due Diligence de M&A, Revisão de Cláusulas Contratuais).
3. **Context Minimization:** Técnicas de chunking semântico e filtragem de tokens para reduzir o envio de dados desnecessários a LLMs, diminuindo custos e latência.
4. **UX Grayscale Minimalista:** Interface focada em produtividade em split-view (documento à esquerda, análise à direita) com citações interativas.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O assistente "Ingo" possui ações pré-configuradas em banco (`AssistantPrompt`), mas sem conceito de workflows compartilhados ou exportação de matrizes tabulares.
* **Recomendações:**
  * `PORT_CONCEPT`: Implementar a entidade `LegalWorkflowBean` no `j-lawyer-server-entities` para permitir que bancas criem e compartilhem rotinas de análise de peças.
  * `PORT_ALGORITHM`: Adotar as diretrizes de minimização de contexto no pipeline do assistente de IA.
  * `USE_AS_REFERENCE`: Adotar os padrões de design grayscale e split-view no `j-lawyer-web` (Angular).

---

### 3.9 PROJETO 9: LAWLINK (`lawflow-boop/LawLink`)
* **URL:** `https://github.com/lawflow-boop/LawLink`
* **Licença:** MIT License
* **Stack:** TypeScript, Next.js, Prisma ORM, PostgreSQL, shadcn/ui, Docker.
* **Maturidade:** Média / Ativa; focado na operação diária de escritórios independentes e pequenas bancas.

#### Análise Funcional e Arquitetural:
1. **Motor de Conflict Check (Conflito de Interesses):** Busca cruzada automatizada ao cadastrar novos clientes ou partes contrárias contra todo o histórico do escritório (clientes ativos, inativos, partes adversas anteriores, sócios e testemunhas), com workflow formal de homologação e justificativa de liberação por um sócio.
2. **Client Intake & Auto-Conversion:** Triagem de novos contatos e potenciais clientes com qualificação preliminar e transição com 1 clique para caso formal (*Matter*), transferindo automaticamente contatos e documentos.
3. **Checklist de Encerramento e Arquivamento:** Procedimento padronizado de arquivamento com verificação de pendências financeiras, devolução de documentos e exportação integral do dossiê em ZIP.

#### Avaliação para o BR-LAWYER:
* **Equivalente no j-lawyer:** O j-lawyer possui apenas verificação de colisão de compromissos na agenda (`Kollisionen`), mas **não** possui motor de conflito de interesses ético/jurídico nem fluxo de intake.
* **Recomendações:**
  * `PORT_ALGORITHM` & `PORT_CONCEPT`: Implementar o EJB `ConflictCheckService.java` no BR-LAWYER, realizando busca cruzada fonética e aproximada (Levenshtein) no banco de dados e no índice Lucene.
  * `PORT_CONCEPT`: Criar o módulo de Client Intake com conversão direta para `ArchiveFileBean`.

---

### 3.10 PROJETO 10: ECOSSISTEMA OFICIAL CNJ / PJE / PDPJ-BR / GIT.JUS
* **Origem:** Conselho Nacional de Justiça (CNJ), PDPJ-Br, Plataforma Digital do Poder Judiciário.
* **Licença:** Domínio Público / Software Público Brasileiro / Referência Oficial Normativa.
* **Stack:** Java, Spring Boot, Keycloak, Kubernetes, RabbitMQ, Elasticsearch, MinIO, WebSockets.
* **Maturidade:** Padrão Governamental Oficial Mandatório (Resoluções CNJ nº 185/2013, 335/2020, 455/2022).

#### Análise dos Padrões Oficiais:
1. **Modelo Nacional de Interoperabilidade (MNI):**
   * *MNI 2.2.2 (SOAP/WSDL):* Operações `consultarProcesso`, `entregarManifestacaoProcessual`, `consultarAvisosPendentes`, `consultarTeorComunicacao`.
   * *MNI 3.0 / PDPJ (REST/JSON):* APIs OpenAPI 3.0 com autenticação OAuth2 via Keycloak CNJ.
2. **DataJud API Pública v2 (Elasticsearch):** Endpoint `POST /api_publica_{tribunal}/_search` com autenticação por chave pública e paginação via `search_after`.
3. **DJEN (ComunicaAPI):** Endpoint público `GET /api/v1/comunicacao` sem necessidade de token, fonte oficial de intimações judiciais no Brasil.
4. **Padrões de Assinatura Digital ICP-Brasil:**
   * Padrão **PAdES** (DOC-ICP-15) com carimbo do tempo para peças em PDF.
   * Suporte a certificados tipo **A1** (arquivo `.pfx`/`.p12`) e **A3** (hardware criptográfico via PKCS#11).
   * **Protocolo WebSocket PJeOffice / PJeOffice Pro:** Comunicação local na porta `127.0.0.1:8800` para acionamento transparente de tokens físicos nos navegadores sem necessidade de plugins legados.
5. **Arquitetura PDPJ-Br & SSO Keycloak:** Barramento nacional de microsserviços integrando os 91 tribunais do país.

#### Avaliação para o BR-LAWYER:
* **Recomendações:**
  * `INTEGRATE_API`: Adotar os contratos oficiais de DataJud e DJEN como serviços nativos em Java no backend WildFly.
  * `INTEGRATE_API`: Implementar cliente WebSocket para o protocolo PJeOffice (porta 8800) na interface web Angular.
  * `PORT_ALGORITHM`: Implementar assinador PAdES nativo em Java (BouncyCastle) para certificados A1.
  * `USE_AS_REFERENCE`: Adotar a taxonomia das Tabelas Processuais Unificadas (TPU) do CNJ para classificação de classes, assuntos e movimentos.

---

## 4. Arquitetura Integrada do BR-LAWYER (Visão Alvo)

A arquitetura resultante consolida os melhores conceitos e algoritmos pesquisados, mantendo a solidez do monólito modular Java/Jakarta EE do j-lawyer e desacoplando integrações pesadas em contêineres sidecar:

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   BR-LAWYER WEB CLIENT                                      │
│                Angular 19 (Signals, Standalone, Tailwind, Split-Pane Workspace)             │
│   ├── Guided Interviews Wizard (Docassemble Concept)                                        │
│   ├── PII Anonymizer WASM Engine (Stella Adopted Code)                                      │
│   ├── Tabular Review & Grounded Citations (OpenSpecter / Stella Concept)                    │
│   ├── Conflict Check & Client Intake UI (LawLink Concept)                                   │
│   └── PJeOffice WebSocket Client (CNJ Official 127.0.0.1:8800 Protocol)                     │
└──────────────────────────────────────────────┬──────────────────────────────────────────────┘
                                               │ HTTPS / EJB Remoting / REST v8
┌──────────────────────────────────────────────▼──────────────────────────────────────────────┐
│                               BR-LAWYER SERVER (WildFly 26)                                 │
│                                                                                             │
│   ┌───────────────────────────┐ ┌───────────────────────────┐ ┌─────────────────────────┐   │
│   │   JudicialSystemAdapter   │ │   Document Automation     │ │   Rate & Billing Engine │   │
│   │   (Nanojud / CNJ API)     │ │   (docx-stamper / POI)    │ │   (Kimai Rate Cascade)  │   │
│   │ • DjenAdapter (Comunica)  │ │ • SpEL / Jinja Expressions│ │ • 5-Level Rate Resolver │   │
│   │ • DataJudAdapter (Elastic)│ │ • FSM Lifecycle (Mayan)   │ │ • Monthly Retainers     │   │
│   │ • Token Bucket / CircuitBr│ │ • Metadata Schemas        │ │ • Lockdown Periods      │   │
│   │ • Deduplication Engine    │ │ • BouncyCastle ICP-Brasil │ │ • Internal Cost vs Bill │   │
│   └───────────────────────────┘ └───────────────────────────┘ └─────────────────────────┘   │
│                                                                                             │
│   ┌───────────────────────────┐ ┌───────────────────────────┐ ┌─────────────────────────┐   │
│   │   Ingo AI & MCP Server    │ │   Conflict Check Service  │ │   Embedded Search Engine│   │
│   │   (Stella / OpenSpecter)  │ │   (LawLink Algorithm)     │ │   (Apache Lucene 9.12)  │   │
│   │ • BR-LAWYER MCP Server    │ │ • Cross-Party Fuzzy Search│ │ • Full-Text Indexed     │   │
│   │ • Matter-Scoped Context   │ │ • Ethics Compliance Log   │ │ • TF-IDF ML Classifier  │   │
│   │ • Reusable Workflows      │ │ • Blocking Conflict Hit   │ │   (Paperless Concept)   │   │
│   └───────────────────────────┘ └───────────────────────────┘ └─────────────────────────┘   │
└──────────────────────────────┬───────────────────────────────┬──────────────────────────────┘
                               │ REST / Webhooks               │ Docker Internal Net
┌──────────────────────────────▼──────────────┐ ┌──────────────▼──────────────────────────────┐
│       OPENSIGN SIDECAR (Opcional)           │ │        OCRmyPDF SIDECAR (Opcional)          │
│   Node.js / Express / MongoDB / React       │ │   Python / Tesseract / Unpaper / Ghostscript│
│ • Assinatura Eletrônica de Clientes         │ │ • Normalização PDF/A-1b / PDF/A-2b          │
│ • Trilha de Auditoria & Certificado         │ │ • Deskew & Limpeza de Scans                 │
└─────────────────────────────────────────────┘ └─────────────────────────────────────────────┘
```

---

## 5. TOP 10 Funcionalidades Externas para Incorporação Prioritária

Abaixo apresentamos a seleção final e justificada das **10 funcionalidades de maior valor material** a serem incorporadas ao BR-LAWYER:

| Ranking | Funcionalidade & Origem | Classificação | Justificativa de Engenharia e Impacto no BR-LAWYER |
| :-: | :--- | :--- | :--- |
| **1** | **Conectores DJEN & DataJud no `JudicialSystemAdapter`** *(Nanojud / CNJ Oficial)* | `INTEGRATE_API` / `PORT_ALGORITHM` | **Prioridade Máxima:** Substitui integralmente o módulo legado alemão `beA`. Garante captura automática de intimações de advogados (DJEN) e enriquecimento de metadados processuais de 91 tribunais via DataJud/Elasticsearch em Java puro com resiliência e deduplicação. |
| **2** | **Anonimização e Redação de Dados PII em WASM** *(Stella `stella/anonymize`)* | `ADOPT_CODE` | Executa mascaramento de CPFs, CNPJs, nomes e contas bancárias diretamente no navegador do usuário antes de despachar prompts para LLMs externos. Garante conformidade total com a LGPD e o sigilo da OAB com zero custo de licença (Apache 2.0). |
| **3** | **Motor de Verificação de Conflito de Interesses (*Conflict Check*)** *(LawLink)* | `PORT_ALGORITHM` / `PORT_CONCEPT` | Algoritmo de busca cruzada e fonética contra todo o acervo de clientes, partes adversas, sócios e testemunhas antes de aceitar um caso. Suprime uma lacuna crítica de governança ética e compliance em médias e grandes bancas. |
| **4** | **Motor de Templates DOCX Avançado (`docx-stamper` / Jinja2)** *(Docassemble)* | `PORT_CONCEPT` / `ADOPT_CODE` | Substitui a substituição primitiva de strings do j-lawyer por um motor com loops (`{% for %}`), condicionais (`{% if %}`) e expressões ricas dentro de arquivos Word `.docx` nativos na JVM, permitindo geração automatizada de contratos e petições complexas. |
| **5** | **Cascata Hierárquica de Taxas e Orçamentos Mensais Recorrentes** *(Kimai)* | `PORT_ALGORITHM` / `PORT_CONCEPT` | Implementa a resolução de preços por hora em 5 níveis (Atividade $\rightarrow$ Processo $\rightarrow$ Cliente $\rightarrow$ Advogado $\rightarrow$ Padrão), cálculo de margem líquida (custo interno vs. faturável) e fee mensal recorrente (*retainers*), essenciais para o modelo de negócios da advocacia brasileira. |
| **6** | **Exposição como Servidor MCP (Model Context Protocol)** *(Stella / Nanojud)* | `PORT_CONCEPT` | Permite que qualquer agente de IA moderno (Claude Desktop, IDEs, sidecars) conecte-se com segurança ao BR-LAWYER para interagir com processos, documentos e intimações via protocolo padronizado da indústria. |
| **7** | **Assinatura Eletrônica de Contratos & Clientes via OpenSign** *(OpenSign)* | `INTEGRATE_SIDECAR` / `INTEGRATE_API` | Integração de sidecar containerizado para envio de procurações e contratos de honorários com assinatura eletrônica em smartphones, coleta de OTP, trilha de auditoria completa e certificado de conclusão anexado aos autos. |
| **8** | **Assinatura Digital Qualificada ICP-Brasil & Protocolo PJeOffice** *(CNJ / PJe)* | `INTEGRATE_API` / `PORT_ALGORITHM` | Implementação de conector WebSocket local (porta 8800) para acionar tokens A3 via PJeOffice e assinador PAdES em Java com BouncyCastle para certificados A1, viabilizando peticionamento judicial oficial. |
| **9** | **Classificador de Metadados Local por Machine Learning (TF-IDF)** *(Paperless-ngx)* | `PORT_ALGORITHM` | Utiliza os índices do Apache Lucene 9.12 (`lucene-classification`) para prever automaticamente tipos de petições, correspondentes e tags para novos documentos em milissegundos, sem gastar tokens de LLM e preservando a privacidade local. |
| **10** | **Revisão Tabular em Lote (*Tabular Reviews*) com Citações Fundamentadas** *(OpenSpecter / Stella)* | `PORT_CONCEPT` | Permite extração estruturada de cláusulas e dados em matrizes comparativas sobre dezenas de contratos ou peças processuais, com exportação para Excel e links que apontam para o trecho exato do documento original. |

---

## 6. Conclusão da Pesquisa

A análise dos 10 projetos comprova que o ecossistema *open source* dispõe de soluções maduras e perfeitamente complementares ao **BR-LAWYER**. A estratégia adotada — priorizando o porte algorítmico e conceitual diretamente para a arquitetura Java/Angular corporativa e utilizando contêineres sidecar isolados apenas quando estritamente necessário — preserva 100% da identidade arquitetural do j-lawyer.org, garante facilidade perpétua de merge com melhorias do upstream e entrega ao mercado jurídico brasileiro um sistema robusto, moderno e em estrita conformidade regulatória.
