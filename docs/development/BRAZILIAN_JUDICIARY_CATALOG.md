# Catálogo Canônico do Poder Judiciário Brasileiro — BR-LAWYER

> **Documento de Referência Normativa e Arquitetural**  
> **Status:** Vigente (V3_6_0_10)  
> **Última Atualização:** 01 de Setembro de 2026

---

## 1. Visão Geral e Fundamento Normativo

O catálogo de órgãos do Poder Judiciário do **BR-LAWYER** foi estruturado com base estrita na **Constituição da República Federativa do Brasil de 1988 (CF/88, art. 92 e seguintes)** e na **Resolução CNJ nº 65/2008**, que regulamenta a **Numeração Processual Única (NPU)**.

O padrão NPU possui o formato:
$$\text{NNNNNNN-DD.AAAA.J.TR.OOOO}$$
onde:
- $\text{NNNNNNN}$: Número sequencial do processo no ano e na unidade de origem.
- $\text{DD}$: Dígitos verificadores calculados pelo algoritmo **ISO 7064 Módulo 97 Base 10**.
- $\text{AAAA}$: Ano de distribuição do processo.
- $\mathbf{J}$: **Segmento do Poder Judiciário (1 a 9)**.
- $\mathbf{TR}$: **Identificador do Tribunal / Região dentro do Segmento**.
- $\text{OOOO}$: Unidade de origem do processo (vara, seção, câmara, tribunal).

---

## 2. Taxonomia dos Segmentos de Justiça ($J$) e Tipos de Órgão

| Segmento ($J$) | Nome Canônico | Tribunais / Órgãos Canônicos | Quantidade | Tipo de Órgão (`court_type`) |
| :---: | :--- | :--- | :---: | :--- |
| **$J=1$** | `SUPERIOR` | **STF** (Supremo Tribunal Federal) | 1 | `TRIBUNAL_SUPERIOR` |
| **$J=2$** | `CONSELHO` | **CNJ** (Conselho Nacional de Justiça) | 1 | `CONSELHO` |
| **$J=3$** | `SUPERIOR` | **STJ** (Superior Tribunal de Justiça) | 1 | `TRIBUNAL_SUPERIOR` |
| **$J=4$** | `JUSTICA_FEDERAL` | **TRF1 a TRF6** (6 TRFs) + **CJF** (Conselho da Justiça Federal) | 7 | `TRIBUNAL_REGIONAL_FEDERAL` / `CONSELHO` |
| **$J=5$** | `JUSTICA_DO_TRABALHO`| **TST** + **TRT1 a TRT24** (24 TRTs) + **CSJT** (Conselho) | 26 | `TRIBUNAL_SUPERIOR` / `TRIBUNAL_REGIONAL_DO_TRABALHO` / `CONSELHO` |
| **$J=6$** | `JUSTICA_ELEITORAL` | **TSE** + **TRE-AC a TRE-TO** (27 TREs) | 28 | `TRIBUNAL_SUPERIOR` / `TRIBUNAL_REGIONAL_ELEITORAL` |
| **$J=7$** | `JUSTICA_MILITAR_UNIAO`| **STM** (Superior Tribunal Militar) | 1 | `TRIBUNAL_SUPERIOR` |
| **$J=8$** | `JUSTICA_ESTADUAL` | **TJAC a TJTO** (27 Tribunais de Justiça Estaduais e DF) | 27 | `TRIBUNAL_DE_JUSTICA` |
| **$J=9$** | `JUSTICA_MILITAR_ESTADUAL`| **TJMSP** (SP), **TJMMG** (MG), **TJMRS** (RS) | 3 | `TRIBUNAL_DE_JUSTICA_MILITAR` |
| **TOTAL** | — | **Total Geral de Órgãos e Tribunais do Catálogo** | **95** | — |

---

## 3. O que NÃO é Tribunal (Conselhos e Órgãos Administrativos)

O BR-LAWYER distingue formalmente **Tribunal com Jurisdição Contenciosa** de **Conselho / Órgão Administrativo de Controle e Gestão**:
- **CNJ** (Conselho Nacional de Justiça - CF/88 Art. 103-B): Órgão de cúpula administrativa e disciplinar de todo o Judiciário ($J=2$, TR=00).
- **CJF** (Conselho da Justiça Federal - CF/88 Art. 105, parágrafo único): Órgão de supervisão administrativa e orçamentária da Justiça Federal de 1º e 2º graus.
- **CSJT** (Conselho Superior da Justiça do Trabalho - CF/88 Art. 111-A, § 2º): Órgão de supervisão administrativa, orçamentária, financeira e patrimonial da Justiça do Trabalho.

Esses órgãos possuem `court_type = 'CONSELHO'` e são representados no catálogo para fins de processos administrativos, consultas disciplinares e correicionais.

---

## 4. Relação com Fontes Externas e Chaves de Integração

- **DataJud (`datajud_code`):** Identificador utilizado nas consultas da API Pública do DataJud / CNJ. Códigos ainda não validados ou não expostos publicamente pelo tribunal são mantidos como `NULL` para evitar alucinação ou quebra de chamadas REST.
- **DJEN (`djen_code`):** Identificador único de órgão publicador no Diário de Justiça Eletrônico Nacional (PDPJ-Br). Valores são preenchidos com as siglas homologadas no portal do CNJ ou `NULL`.
- **Portal Eletrônico (`electronic_portal_url`):** URL base do tribunal para redirecionamento humano ou consulta pública de autos.

---

## 5. Histórico e Evolução do Banco

1. **Migration `V3_6_0_9` (Estado Inicial pós-Fase 2):** Inclusão preliminar de 45 registros (STF, STJ, TST, TSE, STM, CNJ, TRF1-6, 27 TJs, e 6 TRTs selecionados: TRT1, TRT2, TRT3, TRT4, TRT5, TRT15).
2. **Migration `V3_6_0_10` (Patch de Integridade):**
   - Inclusão dos **18 TRTs ausentes** (TRT6 a TRT14, TRT16 a TRT24), totalizando a integridade de todos os 24 TRTs.
   - Inclusão de todos os **27 TREs** (Justiça Eleitoral).
   - Inclusão dos **3 Tribunais de Justiça Militar Estadual** (TJMSP, TJMMG, TJMRS).
   - Inclusão dos conselhos setoriais **CJF** e **CSJT**.
   - Adição da coluna `court_type` e atualização de tipologia em todos os 95 registros.
   - Adição da tabela normalizada `br_case_tpu_subjects` para suporte a múltiplos assuntos TPU por processo.
   - Adição de metadados de versionamento (`source`, `source_version`, `imported_at`, `checksum`) nas tabelas `br_tpu_classes` e `br_tpu_subjects`.
