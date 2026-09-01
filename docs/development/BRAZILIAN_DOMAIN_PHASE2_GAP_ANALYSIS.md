# BR-LAWYER — Brazilian Legal Domain & Persistence (Phase 2 Gap Analysis)

> **Documento de Auditoria e Fechamento:** `docs/development/BRAZILIAN_DOMAIN_PHASE2_GAP_ANALYSIS.md`  
> **Iniciativa:** BR-LAWYER (Fork Evolutivo do j-lawyer.org)  
> **Branch de Execução:** `feat/brazilian-domain`  
> **Data:** 01 de Setembro de 2026  
> **Status:** AUDITORIA REALIZADA — GAPS EM EXECUÇÃO

---

## 1. Sumário Executivo

A Fase 2 do BR-LAWYER engloba dois pilares essenciais:
1. **Brazilian Data Enrichment & Validation:** Consulta de CEP, CNPJ, normalização de endereços, conflito de interesses fonético e diálogos Swing (Concluído nos commits `8e9bf7036` a `56c004746`).
2. **Brazilian Legal Domain & Persistence:** Modelagem persistente e relacional de pessoas (CPF, CNPJ, RG, Razão Social, múltiplas OABs), processos judiciais (NPU/CNJ, Tribunal, Segmento, Grau, Órgão Julgador, Comarca, Classes e Assuntos TPU, Segredo de Justiça) e catálogo versionável de tribunais e TPU.

Este documento audita o estado atual da Fase 2, classifica cada requisito e orienta as implementações estritamente necessárias para encerramento formal da fase.

---

## 2. Matriz Detalhada de Gaps da Fase 2

### A. Brazilian Core Domain

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Cálculo e Validação NPU (CNJ 65/2008)** | `DONE` | `CnjNumber.java` e `CnjNumberValidator.java` com algoritmo ISO 7064 MOD 97-10 e decomposição de 7 campos. |
| **Validação CPF / CNPJ (Módulo 11)** | `DONE` | `CpfCnpjValidator.java` e `BrazilianDocumentValidator.java` com detecção de sequências repetidas e cálculo de DV. |
| **Classificação Pessoa Física (PF) vs Jurídica (PJ)** | `MISSING` | Requer enum/campo formal `PersonType` (PF, PJ) acoplado a regras de validação documental. |
| **Múltiplos Registros OAB (Advogado)** | `MISSING` | Mapeamento de OAB com número, UF, tipo (PRINCIPAL, SUPLEMENTAR, ESTAGIARIO) e status ativo/inativo. |
| **Metadados Processuais Brasileiros** | `MISSING` | NPU limpo/formatado, Tribunal, Grau, Órgão Julgador, Comarca, Subseção, Segredo de Justiça e Status. |
| **Taxonomia TPU (Tabelas Processuais Unificadas)** | `MISSING` | Estrutura formal de Classes Processuais e múltiplos Assuntos Processuais TPU/CNJ. |

### B. Persistence (JPA & Flyway Migrations)

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Flyway Migration `V3_6_0_9`** | `MISSING` | Criar `V3_6_0_9__BrazilianLegalDomain.sql` com novas tabelas e colunas não-nulas/anuláveis compatíveis. |
| **Extensão Persistente de `AddressBean` (Tabela `contacts`)** | `MISSING` | Colunas `cpf`, `cnpj`, `rg`, `person_type`, `trade_name`, `state_registration`, `municipal_registration`. |
| **Entidade `BrLawyerRegistration` (Tabela `br_lawyer_registrations`)** | `MISSING` | Entidade JPA 1:N associada a `AddressBean` para múltiplas inscrições OAB por profissional. |
| **Extensão Persistente de `ArchiveFileBean` (Tabela `cases`)** | `MISSING` | Colunas `cnj_number`, `cnj_number_clean`, `court_code`, `justice_segment`, `jurisdiction_degree`, `court_unit`, `comarca`, `tpu_class_code`, `tpu_class_name`, `tpu_subject_codes`, `secrecy_level`, `distribution_date`. |
| **Entidade Catálogo `BrJudiciaryCourt` (Tabela `br_judiciary_courts`)** | `MISSING` | Catálogo persistente dos 91 tribunais brasileiros com códigos canônicos e segmentos de justiça. |
| **Entidade Catálogo `BrTpuClass` e `BrTpuSubject`** | `MISSING` | Tabelas `br_tpu_classes` e `br_tpu_subjects` para importação e pesquisa dinâmica de TPU sem hardcode. |
| **Índices de Performance Relacional** | `MISSING` | Índices B-Tree em `contacts(cpf)`, `contacts(cnpj)`, `cases(cnj_number_clean)`, `cases(court_code)`. |

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
| **Diálogo de Resolução de Divergências (Diff)** | `DONE` | `ContactDiffDialog.java` com comparação campo a campo e aplicação seletiva. |
| **Campos Brasileiros no Editor de Contatos (`AddressEditorPanel`)** | `PARTIAL` | Botões de CEP e CNPJ integrados; faltam campos visuais persistentes para CPF, RG, Razão Social e OABs. |
| **Aba / Seção de Processo Brasileiro no Editor de Casos** | `MISSING` | Seção no editor de casos (`ArchiveFileEditorPanel`) para exibição/edição de NPU, Tribunal, Comarca, Vara e TPU. |

### E. EJB Services & API (REST / Remote)

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **`BrazilianDataEnrichmentService` (EJB & REST v7)** | `DONE` | Serviços de enriquecimento, consulta e teste de providers. |
| **`BrazilianLegalDomainService` (EJB Local/Remote & REST v7)** | `MISSING` | Serviço para gerenciamento de dados processuais brasileiros, consulta a catálogo de tribunais e TPU. |
| **DTOs Serializáveis de Domínio Brasileiro** | `MISSING` | `BrazilianCaseDetailsDTO`, `LawyerRegistrationDTO`, `JudiciaryCourtDTO`, `TpuClassDTO`. |

### F. Tests & Validação

| Requisito | Status | Descrição e Diagnóstico |
| :--- | :---: | :--- |
| **Testes de Validadores de Documentos (CNJ, CPF, CNPJ)** | `DONE` | `CnjNumberValidatorTest.java` e `CpfCnpjValidatorTest.java` com dados sintéticos. |
| **Testes de Deduplicação e Divergência UI** | `DONE` | `ContactDiffDialogTest.java` e `BrazilianContactDeduplicatorTest.java`. |
| **Testes de Persistência e Entidades Brasileiras** | `MISSING` | Testes unitários para mapeamento JPA de `BrLawyerRegistration`, `BrJudiciaryCourt`, `AddressBean` e `ArchiveFileBean`. |
| **Build Integral do Reator Maven com Java 17** | `DONE` | Validado no checkpoint da Fase 1 (`BUILD SUCCESS` em todos os módulos). |
| **Smoke Test End-to-End em Container Docker Real** | `PARTIAL` | Validado login e enriquecimento; necessita validação da persistência completa com a nova migration. |

---

## 3. Plano de Implementação Imediato para Fechamento da Fase 2

Para fechar os itens `MISSING` e `PARTIAL` sem introduzir complexidade das fases futuras:

1. **JPA & Persistence Layer:**
   - Criar `BrLawyerRegistration.java`, `BrJudiciaryCourt.java`, `BrTpuClass.java`, `BrTpuSubject.java` em `j-lawyer-server-entities`.
   - Adicionar campos persistentes brasileiros em `AddressBean.java` e `ArchiveFileBean.java`.
   - Criar migration `V3_6_0_9__BrazilianLegalDomain.sql` com schema MariaDB/MySQL/PostgreSQL compatível e carga inicial do catálogo canônico dos 91 tribunais brasileiros e classes TPU principais.
2. **EJB & API Layer:**
   - Criar DTOs em `j-lawyer-server-api` (`com.jdimension.jlawyer.domain.legal.*`).
   - Criar `BrazilianLegalDomainService.java` e interfaces Local/Remote em `j-lawyer-server-ejb`.
   - Expor endpoints REST v7 no `j-lawyer-io`.
3. **Swing Client Integration:**
   - Adicionar painel de dados cadastrais brasileiros e OABs no `AddressEditorPanel`.
   - Adicionar painel de dados processuais brasileiros (NPU, Tribunal, Comarca, Vara, Classe TPU) no editor de casos do cliente desktop.
4. **Testes & Validação:**
   - Testes unitários de persistência e validação.
   - Build completo do reator Maven.
   - Smoke test no container Docker com MariaDB e WildFly.
