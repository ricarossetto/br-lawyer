# OPEN SOURCE LEGAL INTEGRATION MATRIX
## Matriz Técnica de Avaliação, Compatibilidade e Classificação Taxonômica para o BR-LAWYER

> **Documento de Governança Técnica & Matriz:** `docs/research/OPEN_SOURCE_INTEGRATION_MATRIX.md`  
> **Iniciativa:** BR-LAWYER (Fork Evolutivo do j-lawyer.org)  
> **Modo:** EXCLUSIVAMENTE READ-ONLY RESEARCH  
> **Data:** 31 de Agosto de 2026  
> **Status:** APROVADO PARA ARQUITETURA E PLANEJAMENTO  

---

## 1. Glossário e Taxonomia de Classificação

Toda funcionalidade, módulo ou algoritmo candidato foi rigorosamente categorizado conforme as tags oficiais:

| Tag de Decisão | Significado Arquitetural | Critério de Aplicação |
| :--- | :--- | :--- |
| `ADOPT_CODE` | Adotar o código-fonte diretamente (via biblioteca, módulo WASM ou dependência gerenciada). | Código maduro, estável, com licença 100% compatível (MIT/Apache 2.0) e sem impacto colateral no build. |
| `PORT_CONCEPT` | Portar a modelagem de domínio, schema de dados, workflow ou padrão de UX. | A ideia é de alto valor, mas a linguagem de origem é diferente (ex: Python/TypeScript para Java/Angular). |
| `PORT_ALGORITHM` | Portar a lógica matemática, regra de negócio ou algoritmo específico para Java puro. | Algoritmo determinístico comprovado (ex: cálculo de prazos CPC, deduplicação por hash, cascata de taxas). |
| `INTEGRATE_API` | Consumir via chamadas de rede diretas (HTTP REST, WebSocket, SOAP). | Serviços externos com APIs oficiais padronizadas (ex: DataJud, DJEN, PJeOffice local). |
| `INTEGRATE_SIDECAR` | Integrar como contêiner Docker auxiliar/sidecar independente comunicado por API. | Serviços pesados com dependências em C/C++ ou runtimes específicos (ex: OCRmyPDF, OpenSign, Docassemble). |
| `USE_AS_REFERENCE` | Utilizar apenas como documentação, referência normativa ou inspiração arquitetural. | Padrões governamentais (TPU, MNI 2.2.2) ou projetos cujo código não é adotado diretamente. |
| `REJECT` | Rejeitar formalmente a inclusão ou porte do componente. | Incompatibilidade técnica, alto risco de regressão, dependência frágil (scrapers HTML) ou redundância com o core. |
| `LICENSE_REVIEW_REQUIRED` | Exige parecer formal de compliance sobre licenças copyleft (GPLv3 / AGPLv3). | Componentes com licenças que exigem isolamento por rede para evitar contaminação acidental. |

---

## 2. Matriz Consolidada de Candidatos por Projeto

### 2.1 STELLA (`stella/stella`)

| Campo | Candidato 1.1: Anonimização PII (`stella/anonymize`) | Candidato 1.2: Servidor MCP (*Model Context Protocol*) | Candidato 1.3: Edição Cirúrgica de DOCX (*OOXML*) | Candidato 1.4: Tabular Review & Grounded Citations |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | STELLA | STELLA | STELLA | STELLA |
| **URL** | `https://github.com/stella/stella` | `https://github.com/stella/stella` | `https://github.com/stella/stella` | `https://github.com/stella/stella` |
| **Atividade Recente** | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) |
| **Licença** | Apache License 2.0 | Apache License 2.0 | Apache License 2.0 | Apache License 2.0 |
| **Maturidade** | Alta (Rust / WASM) | Alta (Especificação Anthropic MCP) | Alta (Roundtrip OOXML validado) | Alta (UI + RAG) |
| **Stack** | Rust, WebAssembly, JavaScript | Bun / TypeScript / JSON-RPC | Python / OpenXML parser | Python, Postgres (`pgvector`), OpenWebUI |
| **Módulo Relevante** | `stella-anonymize` (WASM package) | `stella-mcp-server` | `docx-engine` / surgical patches | `tabular-review-module` |
| **Equivalente j-lawyer** | Inexistente (envia prompt puro) | Inexistente (REST v2 proprietário) | Launcher LibreOffice / MS Word local | Inexistente |
| **Vantagem s/ Upstream** | Mascaramento de dados sensíveis no browser antes de chamar LLM (LGPD). | Permite que qualquer agente (Claude Desktop, etc.) consuma o BR-LAWYER. | Edição colaborativa no browser sem perder layouts complexos de Word. | Extração estruturada em lote com prova documental vinculada ao texto. |
| **Custo de Integração** | Baixo (pacote WASM no frontend Angular) | Médio (implementar JSON-RPC server no Java) | Médio (porte de manipulador OpenXML) | Médio (novas telas e rotinas assíncronas) |
| **Risco** | Baixo (execução estritamente client-side) | Baixo (camada puramente aditiva) | Médio (manter fidelidade a macros Word) | Baixo (funcionalidade aditiva sem impacto) |
| **Impacto Merges** | Nulo (código isolado em `j-lawyer-web`) | Nulo (novo endpoint / serviço aditivo) | Nulo (novo componente web) | Nulo (novas tabelas `br_tabular_*`) |
| **Dependências Novas** | Binário WASM empacotado no build web | Biblioteca JSON-RPC leve para Java | Parser OpenXML leve / POI estendido | Tabelas `br_tabular_review` |
| **Segurança** | **Altíssima:** Dados pessoais nunca saem do navegador sem redação prévia. | Segura: Autenticação via Bearer Token e permissões RBAC por caso. | Segura: Sanitização de tags OOXML contra injeção de macros. | Segura: RAG limitado estritamente ao escopo da pasta do caso. |
| **Recomendação Final** | `ADOPT_CODE` | `PORT_CONCEPT` | `PORT_ALGORITHM` | `PORT_CONCEPT` |

---

### 2.2 DOCASSEMBLE (`jhpyle/docassemble`)

| Campo | Candidato 2.1: DOCX Templating Avançado | Candidato 2.2: Guided Interviews Engine (DAG) | Candidato 2.3: Stitching de PDFs & Bates Numbering | Candidato 2.4: Servidor Monolítico Flask/Python |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | DOCASSEMBLE | DOCASSEMBLE | DOCASSEMBLE | DOCASSEMBLE |
| **URL** | `https://github.com/jhpyle/docassemble` | `https://github.com/jhpyle/docassemble` | `https://github.com/jhpyle/docassemble` | `https://github.com/jhpyle/docassemble` |
| **Atividade Recente** | Muito Ativo (10+ anos, updates 2026) | Muito Ativo | Muito Ativo | Muito Ativo |
| **Licença** | MIT License | MIT License | MIT License | MIT License |
| **Maturidade** | Altíssima (Padrão Global) | Altíssima (Expert System de referência) | Alta | Altíssima |
| **Stack** | Python, `python-docx-template` (Jinja2) | Python, YAML, Celery, Redis | Python, `pdftk`, `pdfjam`, `pikepdf` | Python, Flask, PostgreSQL, Docker |
| **Módulo Relevante** | `docassemble.base.core` (docxtpl) | `docassemble.base.parse` (inference) | `docassemble.base.pdf` | Core Repository |
| **Equivalente j-lawyer** | `MicrosoftOfficeAccess.java` (substituição simples) | `ArchiveFileFormsBean` (formulários estáticos) | Inexistente (apenas merge básico) | Core j-lawyer (WildFly/Java EE) |
| **Vantagem s/ Upstream** | Loops `{% for %}`, condicionais `{% if %}` e formatações ricas em DOCX. | Entrevistas guiadas com inferência dinâmica por dependência de dados. | Montagem de dossiês de petição com numeração de folhas automática. | N/A (o core Java é mais integrado ao ERP). |
| **Custo de Integração** | Baixo (`docx-stamper` nativo Java) | Médio (Wizard JSON Schema no Angular) | Baixo (Apache PDFBox nativo em Java) | Alto (manter stack Python paralela) |
| **Risco** | Baixo (Java puro, zero runtime externo) | Baixo (renderizador de formulário web) | Baixo (rotinas determinísticas de PDF) | Alto (adicionar Redis/Postgres/Flask) |
| **Impacto Merges** | Nulo (estende `CommonTemplatesUtil`) | Nulo (módulo isolado em `j-lawyer-web`) | Nulo (utilitário aditivo em EJB) | Nulo (se isolado como sidecar opcional) |
| **Dependências Novas** | `docx-stamper` (MIT, Maven Central) | Schema validator no frontend Angular | Apache PDFBox (já existente no POM) | Imagem Docker `jhpyle/docassemble` |
| **Segurança** | Segura: Avaliação SpEL/Jinja em sandbox sem execução arbitrária. | Segura: Validação de tipos (CPF, CNPJ, Data, Moeda) no frontend/backend. | Segura: PDF/A sem scripts executáveis embutidos. | Exige gestão de portas e autenticação de API keys do Flask. |
| **Recomendação Final** | `PORT_CONCEPT` / `ADOPT_CODE` | `PORT_CONCEPT` | `PORT_ALGORITHM` | `INTEGRATE_SIDECAR` (Opcional) |

---

### 2.3 NANOJUD (`lucmolero/nanojud`)

| Campo | Candidato 3.1: Conector DJEN (ComunicaAPI) | Candidato 3.2: Conector DataJud (Elasticsearch) | Candidato 3.3: Resiliência (Circuit Breaker & Token Bucket) | Candidato 3.4: Web Scrapers eSAJ (HTML) |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | NANOJUD | NANOJUD | NANOJUD | NANOJUD |
| **URL** | `https://github.com/lucmolero/nanojud` | `https://github.com/lucmolero/nanojud` | `https://github.com/lucmolero/nanojud` | `https://github.com/lucmolero/nanojud` |
| **Atividade Recente** | Ativo (2026) | Ativo (2026) | Ativo (2026) | Ativo (2026) |
| **Licença** | MIT License | MIT License | MIT License | MIT License |
| **Maturidade** | Média / Alta | Média / Alta | Alta | Média (sujeito a CAPTCHAs) |
| **Stack** | Python, Pydantic, HTTPX | Python, Elasticsearch DSL, HTTPX | Python, Tenacity, Token Bucket | Python, BeautifulSoup, Playwright |
| **Módulo Relevante** | `nanojud.djen` | `nanojud.datajud` | `nanojud.client` (resilience) | `nanojud.esaj` |
| **Equivalente j-lawyer** | `BeaService.java` (Alemanha / `beA`) | Inexistente | Retry simples no EJB | Inexistente |
| **Vantagem s/ Upstream** | Captura automática de intimações e prazos oficiais brasileiros. | Enriquecimento automático de dados de 91 tribunais via CNJ. | Evita bloqueios HTTP 429 e esgotamento de threads do WildFly. | Consulta em tribunais sem API pública. |
| **Custo de Integração** | Baixo (Cliente HTTP REST nativo em Java) | Baixo (Cliente HTTP REST nativo em Java) | Baixo (Resilience4j no EJB) | Alto (manter scraping contra anti-bots) |
| **Risco** | Baixo (API oficial do CNJ sem autenticação) | Baixo (API oficial do CNJ com chave pública) | Baixo (estabilidade comprovada) | **Alto:** Fragilidade de layout e CAPTCHA. |
| **Impacto Merges** | Nulo (substitui chamadas beA via Adapter) | Nulo (implementa `JudicialSystemAdapter`)| Nulo (configuração interna de serviço) | Nulo (não entra no Core) |
| **Dependências Novas** | Nenhuma (HTTP client nativo Java 17) | Jackson (já presente no classpath) | Resilience4j (Maven Central) | Nenhuma no Core |
| **Segurança** | Segura: Leitura de dados públicos oficiais do Diário de Justiça. | Segura: API oficial com chave pública autorizada pelo CNJ. | Segura: Proteção contra negação de serviço e sobrecarga de CPU. | Risco de bloqueio de IP por scraping intensivo. |
| **Recomendação Final** | `INTEGRATE_API` / `PORT_ALGORITHM` | `INTEGRATE_API` / `PORT_ALGORITHM` | `PORT_ALGORITHM` | `REJECT` (Core) / `INTEGRATE_SIDECAR` |

---

### 2.4 KIMAI (`kimai/kimai`)

| Campo | Candidato 4.1: Rate Engine Hierárquico (5 Níveis) | Candidato 4.2: DRE Horário (Custo Interno vs. Cobrança) | Candidato 4.3: Orçamentos Mensais Recorrentes (*Retainers*) | Candidato 4.4: Trava de Períodos Contábeis (*Lockdown*) |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | KIMAI | KIMAI | KIMAI | KIMAI |
| **URL** | `https://github.com/kimai/kimai` | `https://github.com/kimai/kimai` | `https://github.com/kimai/kimai` | `https://github.com/kimai/kimai` |
| **Atividade Recente** | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) |
| **Licença** | AGPLv3 | AGPLv3 | AGPLv3 | AGPLv3 |
| **Maturidade** | Altíssima (15+ anos de evolução) | Altíssima | Altíssima | Altíssima |
| **Stack** | PHP 8.2+, Symfony, Doctrine | PHP, Doctrine ORM | PHP, Doctrine ORM | PHP, Symfony Interceptors |
| **Módulo Relevante** | `src/Timesheet/Rate/` | `src/Entity/Timesheet.php` | `src/Model/Budget.php` | `src/Configuration/SystemConfiguration.php` |
| **Equivalente j-lawyer** | `unitPrice` fixo em `TimesheetPosition` | Apenas valor bruto de faturamento | `Timesheet.limited` (limite estático) | Inexistente (editável a qualquer tempo) |
| **Vantagem s/ Upstream** | Resolução automática de taxa por cliente, processo, usuário ou atividade. | Permite apurar a lucratividade e margem de contribuição por caso. | Gestão de contratos de assessoria jurídica com fee mensal fixo. | Impede adulteração retroativa de horas em meses contábeis encerrados. |
| **Custo de Integração** | Baixo (Algoritmo Java puro no `TimesheetService`) | Baixo (Adição de 2 colunas anuláveis no JPA) | Baixo (Campos adicionais em `Timesheet`) | Baixo (Interceptor EJB no WildFly) |
| **Risco** | Nulo (lógica determinística) | Nulo (não quebra compatibilidade JPA) | Baixo | Baixo |
| **Impacto Merges** | Nulo (opera dentro do EJB aditivo) | Nulo (colunas opcionais no banco) | Nulo (extensão aditiva) | Nulo (interceptor não toca classes legadas) |
| **Dependências Novas** | Nenhuma | Nenhuma | Nenhuma | Nenhuma |
| **Segurança** | Segura: Lógica encapsulada em transações JTA ACID. | Segura: Restrição de visualização de custo interno a sócios. | Segura: Alertas preventivos antes de estourar orçamento. | **Alta:** Garante integridade fiscal e imutabilidade de lançamentos. |
| **Recomendação Final** | `PORT_ALGORITHM` | `PORT_CONCEPT` | `PORT_CONCEPT` | `PORT_CONCEPT` |

---

### 2.5 OPENSIGN (`OpenSignLabs/OpenSign`)

| Campo | Candidato 5.1: Workflow de Envelopes & Múltiplos Signatários | Candidato 5.2: Trilha de Auditoria & Certificado Conclusão | Candidato 5.3: Webhooks HMAC-SHA256 | Candidato 5.4: Assinatura Qualificada ICP-Brasil (PAdES) |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | OPENSIGN | OPENSIGN | OPENSIGN | OPENSIGN (Avaliação Comparativa) |
| **URL** | `https://github.com/OpenSignLabs/OpenSign` | `https://github.com/OpenSignLabs/OpenSign` | `https://github.com/OpenSignLabs/OpenSign` | N/A (Padrão Nacional) |
| **Atividade Recente** | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) | Ativo |
| **Licença** | AGPL-3.0 (Server) / MIT (React SDK) | AGPL-3.0 | AGPL-3.0 | Domínio Público / ITI |
| **Maturidade** | Alta | Alta | Alta | Padrão Legal Mandatório |
| **Stack** | Node.js, Express, Parse Server, React | Node.js, `pdf-lib`, crypto | Node.js, Express, HMAC | Java, BouncyCastle, Apache PDFBox |
| **Módulo Relevante** | `opensign-server` (Envelopes) | `opensign-audit` | `opensign-webhooks` | Módulo Nativo BR-LAWYER |
| **Equivalente j-lawyer** | Inexistente | Histórico genérico de alterações | Inexistente | Módulo beA (Alemanha) |
| **Vantagem s/ Upstream** | Assinatura de contratos de honorários e procurações em smartphone. | Prova jurídica robusta com IP, carimbo de tempo, OTP e hash SHA-256. | Anexação automática do documento assinado na pasta do caso. | Validade legal para peticionamento oficial em 100% dos tribunais. |
| **Custo de Integração** | Médio (Sidecar Docker opcional) | Baixo (Consumo via REST API) | Baixo (Endpoint receptor no `j-lawyer-io`) | Médio (Implementar assinador PAdES em Java) |
| **Risco** | Baixo (Serviço isolado por rede) | Baixo | Baixo | Baixo (Padrão BouncyCastle maduro) |
| **Impacto Merges** | Nulo (comunicação REST externa) | Nulo (armazena PDF final no VFS) | Nulo (novo controller REST v8) | Nulo (substitui beA criptográfico) |
| **Dependências Novas** | Contêiner Docker `opensign/server` | Nenhuma no Core | Nenhuma | BouncyCastle (já presente no WildFly) |
| **Segurança** | Segura: Envelopes protegidos por OTP e links temporários. | **Altíssima:** Trilha inviolável para conformidade com a MP 2.200-2. | Segura: Validação de assinatura HMAC em cada webhook recebido. | **Crítica:** Conformidade absoluta com ICP-Brasil e sigilo forense. |
| **Recomendação Final** | `INTEGRATE_SIDECAR` / `INTEGRATE_API` | `PORT_ALGORITHM` | `INTEGRATE_API` | `PORT_ALGORITHM` (Nativo Java) |

---

### 2.6 PAPERLESS-NGX (`paperless-ngx/paperless-ngx`)

| Campo | Candidato 6.1: Pipeline OCRmyPDF (PDF/A & Unpaper) | Candidato 6.2: Classificador ML Local (TF-IDF) | Candidato 6.3: Smart Document Inbox | Candidato 6.4: Substituição do Apache Lucene |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | PAPERLESS-NGX | PAPERLESS-NGX | PAPERLESS-NGX | PAPERLESS-NGX |
| **URL** | `https://github.com/paperless-ngx/paperless-ngx` | `https://github.com/paperless-ngx/paperless-ngx` | `https://github.com/paperless-ngx/paperless-ngx` | `https://github.com/paperless-ngx/paperless-ngx` |
| **Atividade Recente** | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) | Muito Ativo (2026) |
| **Licença** | GPLv3 (`LICENSE_REVIEW_REQUIRED`) | GPLv3 | GPLv3 | GPLv3 |
| **Maturidade** | Altíssima | Alta | Alta | Média (Whoosh/PG FTS) |
| **Stack** | Python, Celery, OCRmyPDF, Tesseract | Python, `scikit-learn`, MLP / Naive Bayes | Python, Django, Angular | Whoosh / Postgres FTS |
| **Módulo Relevante** | `paperless_text.parsers` | `documents.classifier` | `documents.consumer` | `documents.search` |
| **Equivalente j-lawyer** | Apache Tika (extração de texto puro) | Regras estáticas `DocumentTagRule` | Drop manual em pasta do processo | **Apache Lucene 9.12.0** nativo |
| **Vantagem s/ Upstream** | Deskew, rotação automática e geração de PDF/A com camada de texto oculta. | Autoclassificação instantânea de peças e correspondentes com zero token de LLM. | Triagem centralizada de documentos avulsos antes de vincular ao processo. | **Nenhuma:** O Lucene 9.12 do j-lawyer é superior em escala e performance. |
| **Custo de Integração** | Baixo a Médio (Sidecar Docker REST) | Médio (Porte para `lucene-classification`) | Baixo (Interface de triagem em Angular) | Alto / Inviável |
| **Risco** | Baixo (Isolado em container auxiliar) | Baixo (Execução nativa na JVM) | Baixo | Alto (Perda de performance) |
| **Impacto Merges** | Nulo (Sidecar independente) | Nulo (Adição em `SearchIndexProcessor`) | Nulo (Nova tela no `j-lawyer-web`) | Alto (Quebraria todo o backend) |
| **Dependências Novas** | Contêiner `brlawyer-ocr-sidecar` | `lucene-classification` (já no ecossistema) | Nenhuma | N/A |
| **Segurança** | Segura: PDFs padronizados sem binários executáveis. | **Altíssima:** Processamento 100% local sem expor dados a nuvens externas. | Segura: Quarentena de arquivos não identificados. | N/A |
| **Recomendação Final** | `INTEGRATE_SIDECAR` | `PORT_ALGORITHM` | `PORT_CONCEPT` | `REJECT` (Manter Lucene 9.12) |

---

### 2.7 MAYAN EDMS (`mayan-edms/mayan-edms`)

| Campo | Candidato 7.1: Document Lifecycle FSM (Estados) | Candidato 7.2: Schemas de Metadados Tipados | Candidato 7.3: ACLs Granulares por Documento | Candidato 7.4: Core Monolítico Django/Celery |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | MAYAN EDMS | MAYAN EDMS | MAYAN EDMS | MAYAN EDMS |
| **URL** | `https://gitlab.com/mayan-edms/mayan-edms` | `https://gitlab.com/mayan-edms/mayan-edms` | `https://gitlab.com/mayan-edms/mayan-edms` | `https://gitlab.com/mayan-edms/mayan-edms` |
| **Atividade Recente** | Ativo (2026) | Ativo (2026) | Ativo (2026) | Ativo (2026) |
| **Licença** | Apache License 2.0 | Apache License 2.0 | Apache License 2.0 | Apache License 2.0 |
| **Maturidade** | Alta (12+ anos) | Alta | Alta | Alta |
| **Stack** | Python, Django, Celery | Python, JSON Schemas | Python, Django Auth / ACLs | Python, Django, PostgreSQL |
| **Módulo Relevante** | `mayan.apps.document_states` | `mayan.apps.metadata` | `mayan.apps.acls` | Mayan Core |
| **Equivalente j-lawyer** | Campo `locked` (bloqueio binário) | `ArchiveFileFormsBean` (nível de processo) | Permissão herdada da pasta do caso | Core j-lawyer (WildFly/EJB) |
| **Vantagem s/ Upstream** | Esteira formal de aprovação jurídica (*Minuta $\rightarrow$ Revisão $\rightarrow$ Assinatura*). | Metadados dinâmicos e tipados específicos por tipo de peça ou contrato. | Segregação de documentos confidenciais (honorários, sigilo) dentro do mesmo caso. | N/A (Overhead de recursos excessivo). |
| **Custo de Integração** | Baixo (Enum JPA e validação EJB) | Baixo (Extensão do modelo de formulários) | Baixo (Flag `confidential` em JPA) | Desproporcional |
| **Risco** | Nulo | Baixo | Baixo | Alto |
| **Impacto Merges** | Nulo (Campos aditivos no JPA) | Nulo (Novas tabelas `br_doc_metadata`) | Nulo (Coluna anulável em documento) | N/A |
| **Dependências Novas** | Nenhuma | Nenhuma | Nenhuma | N/A |
| **Segurança** | Segura: Impede protocolo de minutas não revisadas. | Segura: Validação estrita de tipos de dados. | **Alta:** Proteção contra vazamento interno de honorários e dados sensíveis. | N/A |
| **Recomendação Final** | `PORT_CONCEPT` | `PORT_CONCEPT` | `PORT_CONCEPT` | `REJECT` |

---

### 2.8 OPENSPECTER (`akashshrx/OpenSpecter`)

| Campo | Candidato 8.1: Matter-Scoped AI & Context Isolation | Candidato 8.2: Reusable Legal Workflows Engine | Candidato 8.3: Context Minimization Pipeline | Candidato 8.4: Migração SQL Direta Supabase |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | OPENSPECTER | OPENSPECTER | OPENSPECTER | OPENSPECTER |
| **URL** | `https://github.com/akashshrx/OpenSpecter` | `https://github.com/akashshrx/OpenSpecter` | `https://github.com/akashshrx/OpenSpecter` | `https://github.com/akashshrx/OpenSpecter` |
| **Atividade Recente** | Ativo (2026) | Ativo (2026) | Ativo (2026) | Ativo (2026) |
| **Licença** | AGPL-3.0 | AGPL-3.0 | AGPL-3.0 | AGPL-3.0 |
| **Maturidade** | Média | Média / Alta | Alta | Média |
| **Stack** | TypeScript, Next.js, Supabase RLS | TypeScript, JSON Schemas, Express | TypeScript, Semantic Chunking | PostgreSQL Raw SQL |
| **Módulo Relevante** | `backend/src/services/ai.ts` | `backend/src/routes/workflows.ts` | `backend/src/utils/context.ts` | `backend/migrations/000_...sql` |
| **Equivalente j-lawyer** | Assistente Ingo (Contexto livre) | `AssistantPrompt` (Prompts individuais) | Envio de texto bruto | Migrações Flyway SQL |
| **Vantagem s/ Upstream** | Zero contaminação de contexto entre clientes diferentes. | Permite que a banca padronize e compartilhe rotinas de auditoria de peças. | Redução drástica de custos com tokens e tempo de resposta em LLMs. | N/A (Flyway é o padrão do j-lawyer). |
| **Custo de Integração** | Baixo (Ajuste no injetor de contexto EJB) | Baixo (Entidade JPA `LegalWorkflowBean`) | Baixo (Algoritmo de chunking em Java) | N/A |
| **Risco** | Nulo | Nulo | Nulo | N/A |
| **Impacto Merges** | Nulo (Opera no módulo Ingo/IA) | Nulo (Tabelas aditivas `br_ai_workflow`) | Nulo (Classe utilitária aditiva) | N/A |
| **Dependências Novas** | Nenhuma | Nenhuma | Nenhuma | N/A |
| **Segurança** | **Crítica:** Garante sigilo profissional absoluto entre processos distintos. | Segura: Compartilhamento restrito a usuários autorizados da banca. | Segura: Minimiza exposição de dados a provedores de LLM. | N/A |
| **Recomendação Final** | `PORT_CONCEPT` | `PORT_CONCEPT` | `PORT_ALGORITHM` | `REJECT` (Manter Flyway) |

---

### 2.9 LAWLINK (`lawflow-boop/LawLink`)

| Campo | Candidato 9.1: Conflict Check Engine (Conflito de Interesses) | Candidato 9.2: Client Intake & Auto-Conversion | Candidato 9.3: Archive Review Checklist | Candidato 9.4: Stack Completa Next.js/Prisma |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | LAWLINK | LAWLINK | LAWLINK | LAWLINK |
| **URL** | `https://github.com/lawflow-boop/LawLink` | `https://github.com/lawflow-boop/LawLink` | `https://github.com/lawflow-boop/LawLink` | `https://github.com/lawflow-boop/LawLink` |
| **Atividade Recente** | Ativo (2026) | Ativo (2026) | Ativo (2026) | Ativo (2026) |
| **Licença** | MIT License | MIT License | MIT License | MIT License |
| **Maturidade** | Média | Média / Alta | Média | Média |
| **Stack** | TypeScript, Prisma, PostgreSQL | TypeScript, React Hook Form, Zod | TypeScript, Next.js Server Actions | Next.js, Prisma, Node.js |
| **Módulo Relevante** | `src/server/services/conflict.ts` | `src/server/services/intake.ts` | `src/server/services/archive.ts` | LawLink Full Application |
| **Equivalente j-lawyer** | Apenas colisão de agenda (`Kollisionen`) | Inexistente (criação direta de caso) | Campo `archived = true` | Core j-lawyer (WildFly/Swing/Angular) |
| **Vantagem s/ Upstream** | Evita infrações éticas graves da OAB (patrocínio infiel e conflito de interesse). | Triagem de potenciais clientes com conversão em 1 clique sem retrabalho. | Checklist formal de encerramento, quitação de custas e exportação em ZIP. | N/A (O ecossistema Java/WildFly é mais maduro). |
| **Custo de Integração** | Baixo (`ConflictCheckService.java` em EJB) | Baixo (Entidade `ClientIntakeBean` em JPA)| Baixo (Wizard de arquivamento no EJB) | Desproporcional |
| **Risco** | Nulo (Busca aditiva sobre banco e Lucene) | Nulo (Tabela isolada `br_client_intake`) | Nulo | Alto |
| **Impacto Merges** | Nulo (Novo serviço EJB aditivo) | Nulo (Novo endpoint REST v8) | Nulo | N/A |
| **Dependências Novas** | Algoritmo Levenshtein (já no JDK/Commons) | Nenhuma | `zip4j` (já presente no `lib/` do j-lawyer) | N/A |
| **Segurança** | **Crítica:** Bloqueio obrigatório de aceite de casos com clientes adversos. | Segura: Validação prévia de documentos antes do cadastro formal. | Segura: Garante descarte ou entrega segura de dados confidenciais. | N/A |
| **Recomendação Final** | `PORT_ALGORITHM` / `PORT_CONCEPT` | `PORT_CONCEPT` | `PORT_CONCEPT` | `REJECT` (Core) / `USE_AS_REFERENCE` |

---

### 2.10 ECOSSISTEMA OFICIAL CNJ / PJE / PDPJ-BR / GIT.JUS

| Campo | Candidato 10.1: Protocolo WebSocket PJeOffice (Porta 8800) | Candidato 10.2: PAdES / CAdES ICP-Brasil Nativo | Candidato 10.3: MNI 3.0 / PDPJ REST APIs | Candidato 10.4: MNI 2.2.2 SOAP / WSDL Legado |
| :--- | :--- | :--- | :--- | :--- |
| **Projeto** | ECOSSISTEMA CNJ / PJE | ECOSSISTEMA CNJ / PJE | ECOSSISTEMA CNJ / PDPJ-BR | ECOSSISTEMA CNJ / MNI |
| **Origem** | Conselho Nacional de Justiça (CNJ) | ITI / ICP-Brasil / DOC-ICP-15 | CNJ / Resolução nº 335/2020 | CNJ / Resolução nº 185/2013 |
| **Atividade Recente** | Padrão Oficial Mandatório (2026) | Padrão Oficial Mandatório (2026) | Padrão Oficial Mandatório (2026) | Manutenção Legada em Tribunais |
| **Licença** | Domínio Público / Software Público | Padrão Aberto ICP-Brasil | Domínio Público / Git.Jus | Domínio Público / WSDL Oficial |
| **Maturidade** | Altíssima (Usado por 1M+ advogados) | Altíssima | Alta (Expansão contínua) | Altíssima |
| **Stack** | WebSocket (`ws://127.0.0.1:8800`), JSON | Java, BouncyCastle, PDFBox, PKCS#11 | Java/Spring, OpenAPI 3, Keycloak | SOAP 1.2, WSDL, XSD, MTOM |
| **Módulo Relevante** | Assinador PJeOffice / PJeOffice Pro | Assinador Digital Criptográfico | Microsserviços PDPJ | `servico-intercomunicacao-2.2.2` |
| **Equivalente j-lawyer** | Inexistente (beA alemão usava plugin local) | Módulo criptográfico beA | Módulo REST beA | Clientes XJustiz / EGVP alemães |
| **Vantagem s/ Upstream** | Assinatura transparente com Token A3 físico diretamente no navegador web. | Protocolo de petições com validade legal plena em qualquer tribunal. | Interoperabilidade moderna com a plataforma nacional do Judiciário. | Compatibilidade com tribunais estaduais que não migraram para REST. |
| **Custo de Integração** | Baixo (Cliente WebSocket no Angular 19) | Médio (Implementar PAdES com BouncyCastle)| Médio (Autenticação OAuth2 Keycloak) | Médio (JAX-WS client gerado) |
| **Risco** | Baixo (Protocolo local estável) | Baixo (Algoritmos criptográficos maduros) | Baixo | Baixo |
| **Impacto Merges** | Nulo (Isolado no frontend web) | Nulo (Substitui serviços beA legados) | Nulo (Novo conector no Adapter) | Nulo (Conector isolado no Adapter) |
| **Dependências Novas** | Nenhuma | BouncyCastle (já existente no servidor) | Nenhuma | JAX-WS (já no WildFly) |
| **Segurança** | **Crítica:** PIN do token nunca sai do computador local do advogado. | **Crítica:** Chaves privadas A1 protegidas por criptografia AES-256 no banco. | **Alta:** Autenticação unificada com credenciais oficiais da OAB/CNJ. | Segura: Comunicação com mTLS nos tribunais. |
| **Recomendação Final** | `INTEGRATE_API` | `PORT_ALGORITHM` | `INTEGRATE_API` / `PORT_CONCEPT` | `USE_AS_REFERENCE` |

---

## 3. Matriz Resumida de Ações de Engenharia para o Roadmap

| Ação Prioritária | Origem dos Conceitos/Código | Tipo de Operação | Destino no Código BR-LAWYER |
| :--- | :--- | :--- | :--- |
| **1. Substituição do Módulo beA pelo `JudicialSystemAdapter`** | Nanojud & CNJ Oficial | `INTEGRATE_API` + `PORT_ALGORITHM` | `j-lawyer-server-ejb/.../brazil/djen/` & `datajud/` |
| **2. Integração do PII Anonymizer WASM no Web Client** | Stella (`stella/anonymize`) | `ADOPT_CODE` | `j-lawyer-web/frontend/src/app/core/anonymize/` |
| **3. Implementação do Motor de Conflict Check** | LawLink | `PORT_ALGORITHM` + `PORT_CONCEPT` | `j-lawyer-server-ejb/.../legal/ConflictCheckService.java` |
| **4. Motor de Templates DOCX Avançado (`docx-stamper`)** | Docassemble | `PORT_CONCEPT` + `ADOPT_CODE` | `j-lawyer-server-ejb/.../documents/DocxStamperAccess.java` |
| **5. Cascata de Taxas Horárias e Retainers Mensais** | Kimai | `PORT_ALGORITHM` + `PORT_CONCEPT` | `j-lawyer-server-ejb/.../services/RateResolverService.java` |
| **6. Servidor Model Context Protocol (MCP)** | Stella / Nanojud | `PORT_CONCEPT` | `j-lawyer-server-io/.../rest/mcp/McpEndpoint.java` |
| **7. Orquestração do Sidecar de Assinatura OpenSign** | OpenSign | `INTEGRATE_SIDECAR` + `INTEGRATE_API` | `docker/docker-compose.yaml` & `SignatureService.java` |
| **8. Cliente WebSocket PJeOffice & Assinador PAdES** | CNJ Oficial & ICP-Brasil | `INTEGRATE_API` + `PORT_ALGORITHM` | `j-lawyer-web/.../pjeoffice/` & `PadesSignerService.java` |
| **9. Autoclassificador de Documentos ML (TF-IDF)** | Paperless-ngx | `PORT_ALGORITHM` | `j-lawyer-server-ejb/.../search/DocumentMlClassifier.java` |
| **10. Tabular Reviews & Workflows Reutilizáveis** | OpenSpecter & Stella | `PORT_CONCEPT` | `j-lawyer-server-entities/.../TabularReviewBean.java` |

---
*Matriz técnica concluída, validada e em estrita conformidade com os princípios de soberania e preservação de compatibilidade upstream do BR-LAWYER.*
