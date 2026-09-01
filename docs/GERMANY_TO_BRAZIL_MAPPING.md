# Mapeamento Conceitual e Técnico: Alemanha (j-lawyer) → Brasil (BR-LAWYER)

> **Documento de Referência de Domínio:** `docs/GERMANY_TO_BRAZIL_MAPPING.md`  
> **Versão:** 1.0.0  
> **Objetivo:** Estabelecer a equivalência rigorosa e a estratégia de adaptação entre as estruturas do j-lawyer.org e os requisitos materiais, processuais e operacionais da advocacia brasileira.

---

## 1. Matriz Geral de Equivalência de Domínio

| Domínio / Subsistema | Conceito Original (j-lawyer.org - Alemanha) | Equivalente no BR-LAWYER (Brasil) | Estratégia Técnica de Adaptação |
| :--- | :--- | :--- | :--- |
| **Identificação Processual** | `Aktenzeichen` (Formatos livres por tribunal alemão, e.g. `12 O 345/26`) | **Numeração Processual Única CNJ (NPU)** (`NNNNNNN-DD.AAAA.J.TR.OOOO`) | Validação estrita via ISO 7064 MOD 97-10 (Resolução CNJ nº 65/2008). Parsing automático de segmento judiciário ($J$) e tribunal ($TR$). |
| **Comunicação Judicial Oficial** | `beA` (Besonderes elektronisches Anwaltspostfach - BRAK) | **DJEN (Diário de Justiça Eletrônico Nacional)** + **PDPJ-Br** + **Portais Eletrônicos (PJe, e-SAJ, eproc, Projudi)** | Arquitetura plugável `JudicialSystemAdapter`. Ingestão pública oficial via `ComunicaAPI` (DJEN) e enriquecimento via `DataJud`. Isolamento e desativação do wrapper proprietário `j-lawyer-bea-wrapper.jar`. |
| **Cadastro de Pessoas Físicas** | `Person` (Nome, Endereço, Data de Nascimento, sem ID fiscal obrigatório) | **Pessoa Física com CPF e RG/CIN** | Adição de campo `cpf` com validação de Módulo 11, `rgNumero`, `rgOrgaoEmissor`, `rgUf` e compatibilidade com Lei nº 14.534/2023. |
| **Cadastro de Pessoas Jurídicas** | `Unternehmen` (Firma, Rechtsform, HRB/HRA Handelsregister) | **Pessoa Jurídica com CNPJ, Razão Social, Nome Fantasia, IE e IM** | Adição de campos `cnpj` (suporte ao novo padrão alfanumérico IN RFB 2229/2024), `razaoSocial`, `nomeFantasia`, `inscricaoEstadual`, `inscricaoMunicipal`. |
| **Habilitação Profissional** | `Rechtsanwalt` (BRAK Bar ID) | **Advogado com Inscrição OAB/UF** | Suporte a número OAB, UF de emissão e tipo de inscrição (Principal, Suplementar, Estagiário - Lei 8.906/1994). |
| **Honorários e Faturamento** | `RVG` (Rechtsanwaltsvergütungsgesetz - Tabela legal compulsória alemã) | **Honorários Contratuais (Êxito, Fixos, Part-time), Honorários Sucumbenciais (CPC art. 85) e Tabelas Orientativas OAB** | Substituição da calculadora rígida RVG por módulo flexível de contratos de honorários, medição de horas (Time Billing), cálculo de sucumbência e conciliação por processo. |
| **Documentos Fiscais** | `ZUGFeRD` / `XRechnung` (Normas EN 16931 e padrão XML fiscal europeu) | **Nota Fiscal de Serviços Eletrônica (NFS-e)** + **Recibo de Honorários Advocatícios** | Substituição dos templates Mustang/ZUGFeRD por `FiscalProvider` com suporte a NFS-e (padrão Nacional / ABRASF) e geração de recibos profissionais com cálculo de retenções (IRRF, ISS, PIS/COFINS/CSLL). |
| **Sistema Bancário e Pagamentos**| `SEPA` (IBAN, BIC, Transferências e Débito em Conta Europeu) | **PIX** + **Boleto Bancário** + **TED/Transferência** + **Extratos OFX/CNAB** | Modelagem de dados bancários nacionais: Código COMPE/ISPB do banco (FEBRABAN), agência, conta corrente, chaves PIX (CPF, CNPJ, Email, Celular, EVP/Aleatória) e QR Code estático/dinâmico. |
| **Geolocalização e Endereços** | `PLZ` (Postleitzahl - 5 dígitos) + `CityData` Alemão | **CEP (8 dígitos)** + **Tabela de Municípios IBGE** + **UFs** | Formatação `XXXXX-XXX`, integração com serviços de busca de CEP (ViaCEP / Offline IBGE) e preenchimento automático de Bairro, Logradouro, Município e UF. |
| **Calendários e Prazos** | Feriados da Alemanha (por Estado/Bundesland) e contagem contínua | **Calendário Forense Brasileiro (CPC/2015, CLT, CPP, Lei 5.010/66, Recesso art. 220)** | Motor temporal com contagem em dias úteis (CPC art. 219 e CLT art. 775), regra tripartite DJEN (Disponibilização $D_0 \rightarrow$ Publicação $D_1 \rightarrow$ Início $D_2$), recesso forense (20/dez a 20/jan) e feriados específicos por tribunal. |
| **Acompanhamentos e Diligências**| `Wiedervorlage` (Conceito alemão de reapresentação periódica de pasta) | **Acompanhamentos Processuais / Tarefas com Follow-up / Lembretes de Diligência** | Adaptação do mecanismo de `Wiedervorlage` para tarefas de monitoramento de andamento no tribunal, triagem de diários e prazos de cumprimento. |
| **Seguro Jurídico** | `Drebis` / `Rechtsschutzversicherung` (Portais alemães de seguradoras de despesas judiciais) | **Assistência Judiciária Gratuita (AJG / Justiça Gratuita)** / **Convênios OAB-Defensoria** | Desativação dos serviços específicos `DrebisService` e conversão dos campos para controle de gratuidade de justiça e assistência suplementar. |

---

## 2. Detalhamento Técnico das Substituições

### 2.1 Identificação Processual: Aktenzeichen → CNJ NPU
- **No j-lawyer:** O número do processo (`ArchiveFileBean.fileNumber` ou `referenceNumber`) era um texto genérico sem validação formal de dígito.
- **No BR-LAWYER:** 
  - Criação da classe `CnjNumberValidator` no core EJB e na camada de UI.
  - O campo `cnjNumber` armazena 20 dígitos numéricos puros no banco de dados e exibe a máscara `NNNNNNN-DD.AAAA.J.TR.OOOO` nas interfaces.
  - Extração automática de:
    - Segmento de Justiça ($J=1 \dots 9$)
    - Tribunal de Origem ($TR=01 \dots 27$)
    - Ano de Ajuizamento ($AAAA$)
    - Unidade de Origem ($OOOO$)

---

### 2.2 Comunicações Judiciais: beA → DJEN / DataJud / Adapters
- **No j-lawyer:** Dependência de `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar` e `BeaService.java` para conexão com a rede da Ordem dos Advogados da Alemanha (BRAK).
- **No BR-LAWYER:**
  - `BeaService` é marcado como `@Deprecated` / desativado por padrão de build.
  - Criação da interface unificada `JudicialSystemAdapter`:
    - `DjenAdapter`: Ingestão de publicações via API oficial do CNJ (`ComunicaAPI`).
    - `DataJudAdapter`: Consulta de andamentos e metadados via API Pública do DataJud (Elasticsearch).
    - `PjeAdapter` / `EprocAdapter`: Adaptadores estruturados para importação assistida de dados de processos.

---

### 2.3 Sistema Financeiro e Tributário: RVG / SEPA / ZUGFeRD → Padrão Brasileiro
- **No j-lawyer:** Estruturas baseadas em `j-lawyer-invoicing`, `Mustang Project` (faturas ZUGFeRD/XRechnung), contas SEPA (IBAN/BIC) e tabelas da Lei Alemã de Remuneração de Advogados (RVG).
- **No BR-LAWYER:**
  - **Honorários:** Suporte a contratos de honorários:
    - *Honorários Fixos / Parcelados* (com controle de vencimento).
    - *Honorários de Êxito (Ad Exitum)* (vinculados ao proveito econômico obtido).
    - *Honorários Sucumbenciais* (Art. 85 do CPC - 10% a 20% sobre condenação/proveito).
    - *Time Tracking / Horas Trabalhadas* (preservando o excelente motor de apontamentos existente).
  - **Pagamentos:**
    - Geração de cobrança PIX com payload EMVCo (QR Code estático e dinâmico).
    - Suporte a geração de Boletos Bancários e leitura de retornos bancários (OFX e CNAB 240/400).
    - Dados bancários brasileiros: Código COMPE do banco (3 dígitos), Código ISPB (8 dígitos), agência, conta e dígito.
  - **Documentação Fiscal:**
    - Emissão de Recibos de Pagamento de Honorários Advocatícios.
    - Estrutura para integração com provedores de NFS-e (Nota Fiscal de Serviços Eletrônica).

---

### 2.4 Pessoas, Contatos e Estrutura de Partes
- **No j-lawyer:** `AddressBean` continha campos como `salutation`, `title`, `firstname`, `lastname`, `street`, `zipcode`, `city`.
- **No BR-LAWYER:**
  - Extensão sem quebra de compatibilidade JPA:
    - `personType`: Enum (`FISICA`, `JURIDICA`, `AUTORIDADE_ORGAO`).
    - `cpfCnpj`: String limpa (11 dígitos para PF, 14 para PJ) com validação de dígitos verificadores.
    - `rgNumero`, `rgOrgaoEmissor`, `rgUf`: Documento de identidade.
    - `oabNumero`, `oabUf`, `oabTipo`: Inscrição na OAB (Principal, Suplementar, Estagiário).
    - `nomeFantasia`, `inscricaoEstadual`, `inscricaoMunicipal`: Para PJ.
    - `poloProcessual`: Enum (`ATIVO`, `PASSIVO`, `TERCEIRO_INTERESSADO`, `CUSTOS_LEGIS`).

---

### 2.5 Placeholders e Modelos de Documentos
- **No j-lawyer:** Placeholders como `[AKTE_AZ]`, `[MANDANT_NAME]`, `[GEGNER_NAME]`.
- **No BR-LAWYER:**
  - Adição de placeholders em conformidade com a prática forense brasileira:
    - `cliente.nome`, `cliente.cpf`, `cliente.cnpj`, `cliente.rg`, `cliente.endereco_completo`, `cliente.nacionalidade`, `cliente.estado_civil`, `cliente.profissao`.
    - `processo.numero_cnj`, `processo.tribunal`, `processo.vara`, `processo.comarca`, `processo.classe_judicial`, `processo.assunto_principal`, `processo.valor_causa`.
    - `advogado.nome`, `advogado.oab_completa`, `advogado.oab_numero`, `advogado.oab_uf`, `advogado.email`.
    - `parte_contraria.nome`, `parte_contraria.cpf_cnpj`.
    - `escritorio.nome`, `escritorio.cnpj`, `escritorio.endereco`.

---

## 3. Diretrizes de Preservação e Não-Regressão

1. **Campos Legados:** Os campos originais em banco de dados e entidades não são removidos arbitrariamente, mas mapeados ou estendidos para assegurar estabilidade na persistência.
2. **Compatibilidade de APIs:** Os endpoints REST v1 a v7 mantêm contratos válidos; novos campos e endpoints específicos do ecossistema brasileiro são introduzidos sob a versão atualizada v7 (e futuras v8).
3. **Internacionalização:** Nenhuma string é gravada no código fonte; todas as traduções e novas mensagens utilizam Java ResourceBundles (`_pt_BR.properties`).
