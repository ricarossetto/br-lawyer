# BR-LAWYER — Open Source Adoption Roadmap & Engineering Governance

> **Documento de Governança de Produto & Roadmap:** `docs/development/OPEN_SOURCE_ADOPTION_ROADMAP.md`  
> **Iniciativa:** BR-LAWYER (Fork Evolutivo do j-lawyer.org)  
> **Status:** APROVADO  
> **Data:** 31 de Agosto de 2026  
> **Origem da Pesquisa:** `docs/research/OPEN_SOURCE_LEGAL_ECOSYSTEM.md` & `docs/research/OPEN_SOURCE_INTEGRATION_MATRIX.md`

---

## 1. Princípios Fundamentais & Árvore de Decisão de Reuso

O **BR-LAWYER** é concebido e mantido como um **fork evolutivo sustentável do j-lawyer.org**. Projetos open-source externos funcionam exclusivamente como **insumo arquitetural**, não como autorização para adotar cegamente todas as ferramentas ou transformar o sistema em uma colagem fragmentada de microsserviços.

### Árvore de Decisão Obrigatória de Engenharia (Reuse Decision Tree):
```
1. KEEP J-LAWYER
   └── O recurso existente no j-lawyer atende ao requisito brasileiro com estabilidade?
       └── SIM: Manter o código original sem alterações.
       └── NÃO: Avançar para o próximo nível.
            ↓
2. EXTEND J-LAWYER
   └── É possível estender a classe, serviço EJB ou componente com sobrecargas aditivas?
       └── SIM: Adicionar métodos/decorators em pacotes dedicados (*.domain.legal.*, *.integration.brazil.*).
       └── NÃO: Avançar para o próximo nível.
            ↓
3. PORT CONCEPT / ALGORITHM
   └── O algoritmo matemático, modelo de domínio ou fluxo de UX pode ser reimplementado em Java 17 / Angular puro?
       └── SIM: Portar a lógica para Jakarta EE / TypeScript, mantendo a JVM unificada e zero dependências externas.
       └── NÃO: Avançar para o próximo nível.
            ↓
4. INTEGRATE EXTERNAL SERVICE (API / Webhooks / Sidecar Opcional)
   └── O serviço externo possui API padronizada e opera como extensão independente (ex: OpenSign, OCRmyPDF)?
       └── SIM: Integrar via chamadas HTTP REST / Webhooks isolados, sem travar o core se o serviço estiver ausente.
       └── NÃO: Avançar para o próximo nível.
            ↓
5. ADOPT EXTERNAL CODE (WASM / Bibliotecas Maven)
   └── A biblioteca possui licença 100% compatível (Apache 2.0 / MIT) e se integra sem impacto colateral no build?
       └── SIM: Adotar dependência gerenciada canônica do Maven Central ou pacote WASM client-side.
       └── NÃO: REJEITAR formalmente.
```

---

## 2. Matriz de Classificação Taxonômica das Propostas

| Categoria | Definição de Engenharia |
| :--- | :--- |
| **P0** | Incorporar dentro das fases já planejadas do núcleo do BR-LAWYER. |
| **P1** | Incorporar após a estabilização do core brasileiro (Fases 7 e 8). |
| **P2** | Integração opcional via contêiner sidecar / plugin (sem acoplamento obrigatório no core). |
| **EXPERIMENTAL** | Requer pesquisa técnica prévia e prova de conceito restrita a contratos oficiais. |
| **REJECT** | Complexidade injustificada, redundância ou fragilidade técnica. |

---

## 3. Detalhamento dos Itens por Nível de Prioridade

### 3.1 Prioridade P0 — Fases do Core Planejado

#### 1. Padrões de Resiliência Judiciária & Conectores CNJ (Origem: NanoJud / CNJ Oficial)
- **Classificação:** `P0`
- **Fase de Destino:** **Fase 4 (Integrações Públicas: DJEN & DataJud)**
- **Decisão:** `PORT_ALGORITHM` & `INTEGRATE_API` (Java Puro).
- **Escopo Técnico:**
  - Implementação nativa no `JudicialSystemAdapter`:
    - `DjenAdapter.java`: Leitura da ComunicaAPI pública (`/api/v1/comunicacao`) com busca por OAB/UF e NPU.
    - `DataJudAdapter.java`: Consulta ao cluster Elasticsearch da API Pública do CNJ (`search_after`).
    - *Provenance Envelope:* Rastreabilidade com SHA-256 do payload bruto, timestamp UTC e tribunal de origem.
    - *Deterministic Deduplication:* Hash `NPU + TipoAto + DataDisponibilizacao + HashTexto` para evitar duplicação de intimações.
    - *Resilience Engine:* Rate limiting via Token Bucket, Exponential Backoff com jitter e Circuit Breaker por tribunal.
- **Não fazer:** Não usar web scrapers frágeis no core. Apenas APIs oficiais.

#### 2. Enhanced Conflict Check (Origem: LawLink / Prática Forense OAB)
- **Classificação:** `P0`
- **Fase de Destino:** **Fase 3 (Domínio de Pessoas, Partes & Compliance Ético)**
- **Decisão:** `EXTEND J-LAWYER` & `PORT_ALGORITHM`.
- **Escopo Técnico:**
  - Auditar a funcionalidade existente (`Kollisionen`) e estendê-la para conformidade com o Código de Ética da OAB:
    - Busca cruzada difusa (*Fuzzy Matching*) com algoritmo Levenshtein e fonética PT-BR em nomes de partes.
    - Busca estrita por CPF e CNPJ (raiz e filial).
    - Mapeamento de aliases, nomes sociais e sócios/representantes legais.
    - Verificação contra partes contrárias históricas em processos encerrados.
    - Workflow formal de liberação de conflito com justificativa gravada em log de auditoria imutável por um sócio.
- **Não fazer:** Não substituir a estrutura de contatos (`AddressBean`); estender via `ConflictCheckService.java`.

#### 3. Extensão do Motor de Templates DOCX (Origem: Docassemble / docx-stamper)
- **Classificação:** `P0`
- **Fase de Destino:** **Fase 3 (Documentos & Templates Avançados)**
- **Decisão:** `EXTEND J-LAWYER` & `ADOPT_CODE` (`docx-stamper`).
- **Escopo Técnico:**
  - Integrar a biblioteca Java `docx-stamper` (baseada em SpEL) no `j-lawyer-server-ejb`:
    - Suporte a condicionais (`{{#if(temProcuracao)}}...{{/if}}`), loops de repetição de linhas de tabela (`{{#repeat(partes)}}...{{/repeat}}`) e expressões de formatação monetária/extenso.
    - Preservação de 100% dos modelos legados do j-lawyer baseados em substituição simples `${placeholder}`.
    - Montagem documental guiada através de questionários dinâmicos baseados em JSON Schema no `j-lawyer-web`.
- **Não fazer:** Não embutir o monólito Python do Docassemble no runtime padrão do WildFly.

#### 4. Precificação Inteligente & Contratos Mensais Recorrentes (Origem: Kimai)
- **Classificação:** `P0`
- **Fase de Destino:** **Fase 6 (Financeiro, Honorários & NFS-e)**
- **Decisão:** `PORT_ALGORITHM` & `PORT_CONCEPT`.
- **Escopo Técnico:**
  - Implementar o `RateResolverService.java` com cascata de resolução de taxas horárias em 5 níveis:
    $$\text{Taxa} = \text{Coalesce}(\text{Taxa Atividade}, \text{Taxa Processo}, \text{Taxa Cliente}, \text{Taxa Advogado}, \text{Taxa Global})$$
  - Adição de campos `internalCostRate` e `isBillable` em `TimesheetPosition` para cálculo de margem líquida por processo.
  - Suporte a *Monthly Reset Retainers* (contratos de partido mensal com horas inclusas e excedentes).
  - Trava de períodos contábeis encerrados (*Lockdown Periods*), impedindo alteração de horas após faturamento.
- **Não fazer:** Não reescrever o motor financeiro do j-lawyer; adicionar as regras aditivamente no EJB.

#### 5. Classificação Inteligente & Normalização de Documentos (Origem: Paperless-ngx)
- **Classificação:** `P0`
- **Fase de Destino:** **Fase 3 (Documentos & Indexação Local)**
- **Decisão:** `EXTEND J-LAWYER` & `PORT_ALGORITHM`.
- **Escopo Técnico:**
  - Utilizar o motor **Apache Lucene 9.12** existente e o módulo `lucene-classification`:
    - Treinamento local com classificador TF-IDF para sugestão automática de tipo documental (Petição Inicial, Procuração, Sentença, Guia de Custas, Documento Pessoal) e tags sugeridas.
    - Triagem em milissegundos no servidor local sem custo de tokens de IA.
- **Não fazer:** Não substituir o Apache Lucene pelo Whoosh/Postgres FTS.

---

### 3.2 Prioridade P1 — Pós-Core Estabilizado

#### 6. Matter-Scoped AI & Revisão Tabular de Contratos (Origem: Stella / OpenSpecter)
- **Classificação:** `P1`
- **Fase de Destino:** **Fase 8 (Assistente Ingo, LLMs & IA Jurídica Especializada)**
- **Decisão:** `PORT_CONCEPT` & `PORT_ALGORITHM`.
- **Escopo Técnico:**
  - Isolamento estrito de contexto: consultas de IA, RAG e histórico herdam a fronteira do processo (`ArchiveFileBean`), prevenindo contaminação cruzada (*cross-matter leakage*).
  - *Tabular Review:* Extração estruturada em matriz comparativa sobre dezenas de contratos ou peças, com links bidirecionais apontando para o trecho exato do PDF/DOCX (*grounded citations*).
  - Exportação da matriz para Excel e relatórios de Due Diligence.

#### 7. Anonimização Local de PII em WebAssembly (Origem: Stella `stella/anonymize`)
- **Classificação:** `P1`
- **Fase de Destino:** **Fase 8 (Privacidade & LGPD em IA)**
- **Decisão:** `ADOPT_CODE` (Módulo WASM Apache 2.0).
- **Escopo Técnico:**
  - Execução client-side no navegador (Angular 19) antes de despachar prompts para LLMs externos.
  - Detecção e mascaramento interativo de CPFs, CNPJs, nomes de partes, contas bancárias e dados sensíveis.
  - Tela de homologação humana com visualização de antes/depois da anonimização.

---

### 3.3 Prioridade P2 — Integrações Opcionais via Sidecars / Plugins

#### 8. Assinatura Eletrônica Externa via OpenSign (Origem: OpenSign)
- **Classificação:** `P2` (Opcional / Sidecar)
- **Decisão:** `INTEGRATE_SIDECAR` & `INTEGRATE_API`.
- **Escopo Técnico:**
  - Manter o OpenSign como contêiner Docker auxiliar no `docker-compose.yaml`.
  - Disparo de envelopes via REST API para clientes assinarem procurações e contratos no smartphone (Lei 14.063/2020).
  - Webhook receptor no BR-LAWYER (`/v1/webhooks/opensign`) que baixa o PDF assinado com trilha de auditoria e anexa aos autos automaticamente.
- **Regra:** O BR-LAWYER opera normalmente sem o OpenSign instalado.

#### 9. Pipeline Avançado de Scans via OCRmyPDF (Origem: Paperless-ngx / OCRmyPDF)
- **Classificação:** `P2` (Opcional / Sidecar)
- **Decisão:** `INTEGRATE_SIDECAR`.
- **Escopo Técnico:**
  - Microserviço opcional para deskew, rotação automática e geração de PDF/A-1b / PDF/A-2b a partir de imagens digitalizadas brutas de scanners.
- **Regra:** O processamento normal de documentos do BR-LAWYER (PDFBox/Tika/Lucene) não depende deste sidecar.

---

### 3.4 Categoria EXPERIMENTAL — Pesquisa e Validação Normativa

#### 10. Servidor Model Context Protocol (MCP) (Origem: Stella / Anthropic MCP SDK)
- **Classificação:** `EXPERIMENTAL` (Pós-Fase 7)
- **Decisão:** `PORT_CONCEPT`.
- **Escopo Técnico:**
  - Expor endpoints JSON-RPC / MCP no BR-LAWYER para permitir interação com agentes de IA externos (Claude Desktop, IDEs).
  - **Requisito Obrigatório:** Implementar apenas após estabilização completa do RBAC, respeitando segredo de justiça, autorização por processo e trilha de auditoria estrita.

#### 11. PJeOffice / Assinador Digital Qualificado ICP-Brasil & Protocolo Judicial
- **Classificação:** `EXPERIMENTAL`
- **Decisão:** `INTEGRATE_API` & `PORT_ALGORITHM` (Human-in-the-Loop Obrigatório).
- **Escopo Técnico:**
  - **Pesquisa Técnica Isolada:** Basear-se exclusivamente na documentação oficial do CNJ/PDPJ-Br e normas do ITI.
  - Conector WebSocket local com o PJeOffice (porta `127.0.0.1:8800`) para assinatura com Token A3 físico sem expor o PIN ao navegador.
  - Assinador PAdES em Java com BouncyCastle para certificados A1 (.pfx/.p12).
  - **Regra Inegociável:** Qualquer protocolo de petição ou ciência processual permanece estritamente **HUMAN-IN-THE-LOOP** (proibição de despacho autônomo por agentes de IA).

---

### 3.5 Categoria REJECT — Decisões Explícitas de Não-Adoção

| Proposta Rejeitada | Motivo da Rejeição Técnica | Alternativa Adotada no BR-LAWYER |
| :--- | :--- | :--- |
| **Web Scrapers HTML (eSAJ, Projudi)** | Fragilidade de layout, quebras contínuas por CAPTCHAs e risco de bloqueio de IP. | Consumo de APIs públicas oficiais (DJEN ComunicaAPI e DataJud). |
| **Substituição do Apache Lucene 9.12** | O Apache Lucene nativo na JVM é superior em escala, latência e robustez a Whoosh/Postgres FTS. | Preservar o Apache Lucene 9.12 com indexação paralela. |
| **Monólitos Flask / Django / Symfony no Core** | Adicionar runtimes Python/PHP ao WildFly destrói a simplicidade de implantação e manutenção do build Maven. | Portar a lógica e algoritmos para Java 17 puro no EJB. |
| **Banco MongoDB / Parse Server no Core** | Fragmenta a consistência transacional ACID do MariaDB/PostgreSQL corporativo. | Manter schema relacional padronizado em JPA/Hibernate. |
| **Peticionamento Autônomo por Agentes** | Violação ética e risco de responsabilidade civil por perda de prazos ou despachos indevidos. | Protocolos judiciais 100% supervisionados por humanos. |

---

## 4. Mapeamento Cronológico no Roadmap de Entregas

```
┌────────────────────────────────────────────────────────────────────────────┐
│ FASE 1: FUNDAÇÃO, I18N PT-BR & SUPPLY CHAIN COMPLIANCE (Concluída)         │
│ • Remoção de binários proprietários (j-lawyer-bea-wrapper)                │
│ • ResourceBundles pt-BR, Dicionários JOrtho e Validador NPU CNJ ISO 7064   │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 2: DOMÍNIO JURÍDICO BRASILEIRO & PERSISTÊNCIA (Em Execução)           │
│ • Entidades especializadas de registro profissional OAB (múltiplas UFs)    │
│ • Entidades processuais brasileiras (CNJ, TPU Classes/Assuntos, Segredo)   │
│ • Catálogo versionável de tribunais e segmentos judiciais                  │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 3: COMPLIANCE ÉTICO, DOCUMENTOS & INTAKE                              │
│ • [P0] Enhanced Conflict Check (Levenshtein + CPF/CNPJ + Partes Históricas)│
│ • [P0] Document Automation com docx-stamper (Jinja/SpEL)                   │
│ • [P0] Autoclassificação de Documentos via Lucene ML                       │
│ • Client Intake & Auto-Conversion para Processo                            │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 4: INTEGRAÇÕES PÚBLICAS & COMUNICAÇÕES PROCESSUAIS                    │
│ • [P0] JudicialSystemAdapter: DJEN (ComunicaAPI) & DataJud (Elasticsearch) │
│ • [P0] Provenance Envelope, Token Bucket Rate Limiting & Deduplicação      │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 5: MOTOR TEMPORAL & CONTROLE DE PRAZOS FORENSES                       │
│ • Cálculo em dias úteis (CPC art. 219) e corridos (CPP)                    │
│ • Calendário forense nacional, feriados regimentais e recesso forense      │
│ • Proibição absoluta de alucinação de prazos fatais                        │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 6: FATURAMENTO BRASILEIRO, HONORÁRIOS & PRECIFICAÇÃO                  │
│ • [P0] Cascata de Taxas Horárias em 5 Níveis (Kimai Pattern)               │
│ • [P0] DRE Horário (Custo Interno vs. Faturável) & Monthly Retainers       │
│ • NFS-e Nacional, Boleto bancário e PIX Dinâmico                           │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 7: INTERFACES ERGONÔMICAS (SWING FLATLAF & WEB ANGULAR 19)            │
│ • Daily Command Center, Inspector Lateral e Busca Global (Ctrl+K)          │
│ • [P2] Integração de Sidecar OpenSign (Assinatura Eletrônica de Clientes)  │
│ • [P2] Integração de Sidecar OCRmyPDF (Deskew & Normalização PDF/A)        │
└────────────────────────────────────────────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────────┐
│ FASE 8: ASSISTENTE INGO, IA JURÍDICA SOBERANA & PRIVACIDADE                │
│ • [P1] Anonimização Local de PII em WebAssembly (Stella Pattern)           │
│ • [P1] Matter-Scoped Context & Revisão Tabular com Grounded Citations      │
│ • [EXPERIMENTAL] Model Context Protocol (MCP Server)                       │
│ • [EXPERIMENTAL] Assinador PAdES ICP-Brasil & PJeOffice WebSocket (8800)   │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Diretrizes de Governança para Contribuições

Qualquer nova proposta de integração ou porte de projeto open source deve:
1. Passar pela **Árvore de Decisão de Reuso** (Seção 1).
2. Apresentar análise de compatibilidade de licença (priorizar MIT/Apache 2.0; isolar AGPL/GPL por rede quando aplicável).
3. Demonstrar ausência de conflitos com merges futuros do j-lawyer.org (`docs/UPSTREAM_COMPATIBILITY_GUIDELINES.md`).
4. Ser acompanhada de suíte de testes unitários com dados 100% sintéticos.
