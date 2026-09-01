# BR-LAWYER — Brazilian Legal Domain & Persistence (Phase 2 Gap Analysis & Integrity Patch)

> **Documento de Auditoria e Fechamento:** `docs/development/BRAZILIAN_DOMAIN_PHASE2_GAP_ANALYSIS.md`  
> **Iniciativa:** BR-LAWYER (Fork Evolutivo do j-lawyer.org)  
> **Branch de Execução:** `feat/brazilian-domain` & `fix/phase2-domain-integrity`  
> **Data:** 01 de Setembro de 2026  
> **Status:** CONCLUÍDO E INTEGRADO COM SUCESSO (V3_6_0_10)

---

## 1. Sumário Executivo

A Fase 2 do BR-LAWYER engloba dois pilares essenciais:
1. **Brazilian Data Enrichment & Validation:** Consulta de CEP, CNPJ, normalização de endereços, conflito de interesses fonético e diálogos Swing (Concluído nos commits `8e9bf7036` a `56c004746`).
2. **Brazilian Legal Domain & Persistence Model:** Modelagem persistente e relacional de pessoas (CPF, CNPJ, RG, Razão Social, múltiplas OABs), processos judiciais (NPU/CNJ, Tribunal, Segmento, Grau, Órgão Julgador, Comarca, Classes e Assuntos TPU, Segredo de Justiça) e catálogo canônico do Judiciário e TPU.
3. **Integrity Patch (V3_6_0_10):** Complementação de todos os 24 TRTs, 27 TREs, 3 TJMs, Conselhos (95 órgãos no total), metadados de versionamento TPU e relacionamento normalizado `br_case_tpu_subjects`.

---

## 2. Matriz Consolidada de Requisitos da Fase 2

### A. Brazilian Core Domain

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Cálculo e Validação NPU (CNJ 65/2008)** | `DONE` | `CnjNumber.java` e `CnjNumberValidator.java` com algoritmo ISO 7064 MOD 97-10 e decomposição de 7 campos. |
| **Validação CPF / CNPJ (Módulo 11)** | `DONE` | `CpfCnpjValidator.java` e `BrazilianDocumentValidator.java` com detecção de sequências repetidas e cálculo de DV. |
| **Classificação Pessoa Física (PF) vs Jurídica (PJ)** | `DONE` | Campo formal `personType` (PF, PJ), Razão Social (`tradeName`), Nome Fantasia (`fantasyName`), IE/IM. |
| **Múltiplos Registros OAB (Advogado)** | `DONE` | Entidade `BrLawyerRegistration` (número, UF, tipo: PRINCIPAL, SUPLEMENTAR, ESTAGIARIO e status ativo/inativo). |
| **Metadados Processuais Brasileiros** | `DONE` | NPU limpo/formatado, Tribunal, Grau, Órgão Julgador, Comarca, Subseção, Segredo de Justiça e Status em `ArchiveFileBean`. |
| **Taxonomia TPU (Tabelas Processuais Unificadas)** | `DONE` | Entidades `BrTpuClass` e `BrTpuSubject` com versionamento e proveniência. |
| **Relacionamento Processo ↔ Assuntos TPU** | `DONE` | Tabela normalizada `br_case_tpu_subjects` e entidade JPA `BrCaseTpuSubject`. |

### B. Persistence (JPA & Flyway Migrations)

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Flyway Migration `V3_6_0_9`** | `DONE` | Criação do schema inicial de domínio brasileiro, colunas em `contacts` e `cases`, tabelas de OAB, tribunais e TPU. |
| **Flyway Migration `V3_6_0_10`** | `DONE` | Patch de integridade: 95 órgãos/tribunais (24 TRTs, 27 TREs, 27 TJs, 6 TRFs, 3 TJMs, Conselhos), versionamento TPU e `br_case_tpu_subjects`. |
| **Extensão Persistente de `AddressBean` (Tabela `contacts`)** | `DONE` | Colunas `cpf`, `cnpj`, `rg`, `person_type`, `trade_name`, `fantasy_name`, `state_registration`, `municipal_registration`. |
| **Entidade `BrLawyerRegistration` (Tabela `br_lawyer_registrations`)** | `DONE` | Entidade JPA 1:N associada a `AddressBean` para múltiplas inscrições OAB por profissional. |
| **Extensão Persistente de `ArchiveFileBean` (Tabela `cases`)** | `DONE` | Colunas `cnj_number`, `cnj_number_clean`, `court_code`, `justice_segment`, `jurisdiction_degree`, `court_unit`, `comarca`, `tpu_class_code`, `tpu_class_name`, `tpu_subject_codes`, `secrecy_level`, `distribution_date`. |
| **Entidade Catálogo `BrJudiciaryCourt` (Tabela `br_judiciary_courts`)** | `DONE` | Catálogo persistente dos 95 órgãos e tribunais com tipologia (`court_type`), segmentos $J=1..9$, UFs e números de região. |
| **Entidade Catálogo `BrTpuClass` e `BrTpuSubject`** | `DONE` | Tabelas `br_tpu_classes` e `br_tpu_subjects` com campos de versão, data de importação e checksum. |
| **Índices de Performance Relacional** | `DONE` | Índices B-Tree em `contacts(cpf)`, `contacts(cnpj)`, `cases(cnj_number_clean)`, `cases(court_code)`, `br_case_tpu_subjects(case_id)`. |

### C. Data Enrichment & Validações

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Provedores de Endereço e CEP (BrasilAPI / ViaCEP)** | `DONE` | Implementados com fallback automático, timeout e normalização de logradouro/bairro/UF/IBGE. |
| **Provedores de CNPJ / Empresa (BrasilAPI / ReceitaWS)** | `DONE` | Implementados com extração de QSA, CNAE principal/secundários, natureza jurídica e capital social. |
| **Detecção de Conflito de Interesses e Deduplicação** | `DONE` | `BrazilianContactDeduplicator` com Levenshtein, Metaphone PT-BR e busca cruzada por CPF/CNPJ. |
| **Circuit Breaker e Cache com TTL** | `DONE` | Resiliência contra lentidão de APIs governamentais públicas. |

### D. Swing UI (Desktop Client)

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Diálogo de Configuração de Integrações Brasileiras** | `DONE` | `BrazilianIntegrationsConfigDialog.java` para teste e habilitação de providers. |
| **Diálogo de Enriquecimento de Empresa (CNPJ)** | `DONE` | `CompanyEnrichmentDialog.java` com seleção seletiva de campos e QSA. |
| **Diálogo de Comparação de Dados (Contact Diff)** | `DONE` | `ContactDiffDialog.java` com destaque de divergências e mesclagem campo a campo. |
| **Integração no Painel de Contatos (`AddressPanel`)** | `DONE` | Mapeamento e persistência de CPF, CNPJ, Razão Social, IE/IM e botões de enriquecimento. |
| **Integração no Painel de Processos (`ArchiveFilePanel`)** | `DONE` | Validação e salvamento de NPU/CNJ e normalização de segmentos. |

### E. Serviços EJB & REST API v7

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **`BrazilianDataEnrichmentService`** | `DONE` | EJB Stateless para orquestração de provedores externos, deduplicação e conflitos. |
| **`BrazilianLegalDomainService`** | `DONE` | EJB Stateless para gestão de OAB, NPU CNJ, tribunais e assuntos TPU normalizados. |
| **`TpuImportService`** | `DONE` | EJB Stateless para importação e atualização versionada de classes e assuntos TPU. |
| **REST Endpoints v7** | `DONE` | `/rest/v7/enrichment/*` e `/rest/v7/brazil/domain/*`. |
