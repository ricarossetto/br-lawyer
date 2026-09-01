# RELATÓRIO DE PESQUISA TÉCNICA: FONTES DE DADOS GOVERNAMENTAIS E INSTITUCIONAIS BRASILEIRAS (BR-LAWYER)

**Documento de Referência Arquitetural e Especificação de Integração de Dados**  
**Data:** 01/09/2026  
**Status:** Pesquisa Concluída / Base Aprovada para Implementação  

---

## 1. SUMÁRIO EXECUTIVO E CONTEXTO ARQUITETURAL

O **BR-LAWYER** requer integração contínua e precisa com as principais bases de dados oficiais do Estado Brasileiro e órgãos reguladores de classe para suportar:
1. **Cadastro Unificado de Partes, Clientes e Terceiros:** Validação de CPF (pessoas físicas), CNPJ (pessoas jurídicas), endereçamento fiscal com códigos IBGE e domicílio fiscal.
2. **Conformidade Regulatória e Prevenção a Fraudes:** Validação cadastral no Cadastro Nacional dos Advogados (CNA/OAB), consulta de Certidões Negativas de Débitos Federais (CND RFB/PGFN) e validação de identidade (Datavalid/Serpro).
3. **Módulo Financeiro e Tributário:** Emissão de NFS-e (utilizando o código IBGE de 7 dígitos), conferência de contas bancárias (ISPB / Código COMPE) e conciliação de recebíveis/depósitos judiciais via PIX e STR/SPB do BACEN.
4. **Governança de Privacidade e LGPD (Lei nº 13.709/2018):** Operação sob bases legais estritas (art. 7º, incisos II, V, VI, IX e XII; art. 11, II, "d") e princípios de minimização de dados e *privacy by design*.

---

## 2. RECEITA FEDERAL DO BRASIL (RFB)

### 2.1 Base de Dados Públicos do CNPJ (Open Data)

A Receita Federal do Brasil disponibiliza mensalmente os dados cadastrais públicos de todas as pessoas jurídicas registradas no país através do portal de dados abertos (`dados.gov.br` / `arquivos.receitafederal.gov.br/dados/cnpj/dados_abertos_cnpj/`).

#### 2.1.1 Estrutura e Layout dos Arquivos
Os dados são distribuídos em arquivos compactados (`.zip`), formatados em CSV delimitado por ponto e vírgula (`;`), codificação `ISO-8859-1` / `UTF-8` e aspas duplas como qualificador de texto.

| Tabela / Arquivo | Conteúdo Principal | Campos Relevantes para o BR-LAWYER |
| :--- | :--- | :--- |
| **`Empresas`** | Cadastro base da PJ | `CNPJ_BASICO` (8 dígitos), `RAZAO_SOCIAL`, `NATUREZA_JURIDICA` (4 dígitos CONCLA), `QUALIFICACAO_RESPONSAVEL`, `CAPITAL_SOCIAL`, `PORTE_EMPRESA`, `ENTE_FEDERATIVO_RESPONSAVEL`. |
| **`Estabelecimentos`** | Matrizes e Filiais | `CNPJ_BASICO`, `CNPJ_ORDEM` (4 dígitos), `CNPJ_DV` (2 dígitos), `MATRIZ_FILIAL` (1-Matriz, 2-Filial), `NOME_FANTASIA`, `SITUACAO_CADASTRAL`, `DATA_SITUACAO`, `MOTIVO_SITUACAO`, `CIDADE_EXTERIOR`, `PAIS`, `DATA_INICIO_ATIVIDADE`, `CNAE_FISCAL_PRINCIPAL`, `CNAE_FISCAL_SECUNDARIA`, `TIPO_LOGRADOURO`, `LOGRADOURO`, `NUMERO`, `COMPLEMENTO`, `BAIRRO`, `CEP`, `UF`, `MUNICIPIO` (Código TOM/RFB), `DDD1`, `TELEFONE1`, `DDD2`, `TELEFONE2`, `EMAIL`. |
| **`Socios`** | Quadro Societário (QSA) | `CNPJ_BASICO`, `IDENTIFICADOR_SOCIO` (1-PJ, 2-PF, 3-Estrangeiro), `NOME_SOCIO_RAZAO_SOCIAL`, `CPF_CNPJ_SOCIO` (com descaracterização/máscara LGPD para PF: `***NNNNNN**`), `QUALIFICACAO_SOCIO`, `DATA_ENTRADA_SOCIEDADE`, `PAIS`, `REPRESENTANTE_LEGAL`, `NOME_REPRESENTANTE`, `QUALIFICACAO_REPRESENTANTE`, `FAIXA_ETARIA`. |
| **`Simples`** | Opção Simples e SIMEI | `CNPJ_BASICO`, `OPCAO_SIMPLES` (S/N), `DATA_OPCAO_SIMPLES`, `DATA_EXCLUSAO_SIMPLES`, `OPCAO_SIMEI` (S/N), `DATA_OPCAO_SIMEI`, `DATA_EXCLUSAO_SIMEI`. |
| **`Cnaes`** | Tabela de Atividades Econômicas | `CODIGO` (7 dígitos), `DESCRICAO`. |
| **`Naturezas`** | Tabela CONCLA | `CODIGO` (4 dígitos), `DESCRICAO`. |
| **`Motivos`** | Motivos de Situação Cadastral | `CODIGO` (2 dígitos), `DESCRICAO`. |
| **`Municipios`** | De-Para TOM RFB $\leftrightarrow$ Nome | `CODIGO` (4 dígitos TOM), `DESCRICAO` (converter para Código IBGE de 7 dígitos). |
| **`Qualificacoes`** | Qualificações de Sócios | `CODIGO` (2 dígitos), `DESCRICAO` (ex: 05-Administrador, 49-Sócio-Administrador, 65-Titular Pessoa Física). |

---

### 2.2 CNPJ Alfanumérico (Instrução Normativa RFB nº 2.229/2024)

Diante da iminência de esgotamento da faixa puramente numérica do CNPJ (capacidade máxima teórica de 99 milhões de raízes), a Receita Federal promulgou a **Instrução Normativa RFB nº 2.229, de 15 de outubro de 2024**, instituindo o formato **CNPJ Alfanumérico**, com início de emissão para novas pessoas jurídicas a partir de **julho de 2026**.

#### 2.2.1 Regras de Estrutura
- **Tamanho Total:** Mantém **14 caracteres**.
- **Composição:**
  - **Raiz (posições 1 a 8):** Alfanumérica (números `0-9` e letras maiúsculas `A-Z`).
  - **Ordem/Estabelecimento (posições 9 a 12):** Alfanumérica (números `0-9` e letras maiúsculas `A-Z`).
  - **Dígitos Verificadores - DV (posições 13 e 14):** **Estritamente Numéricos** (`0-9`).
- **Capacidade:** Salta de $10^8$ (~100 milhões) para $36^8 \approx 2,82 \times 10^{12}$ (quase 3 trilhões) de raízes possíveis.
- **Coexistência:** Inscrições emitidas até junho de 2026 permanecem estritamente numéricas e inalteradas. Sistemas devem aceitar ambos os formatos.

#### 2.2.2 Algoritmo de Cálculo e Validação do Dígito Verificador (Módulo 11 Alfanumérico)
A validação matemática preserva a regra do **Módulo 11 com pesos de 2 a 9**, adaptando a entrada através da **conversão ASCII**:
$$\text{Valor do Caractere} = \text{Código ASCII}(c) - 48$$

*Exemplos de conversão:*
- `'0'` $\rightarrow$ ASCII 48 $\rightarrow 48 - 48 = 0$
- `'9'` $\rightarrow$ ASCII 57 $\rightarrow 57 - 48 = 9$
- `'A'` $\rightarrow$ ASCII 65 $\rightarrow 65 - 48 = 17$
- `'B'` $\rightarrow$ ASCII 66 $\rightarrow 66 - 48 = 18$
- `'Z'` $\rightarrow$ ASCII 90 $\rightarrow 90 - 48 = 42$

---

### 2.3 Naturezas Jurídicas CONCLA/IBGE, CNAEs e Quadro Societário (QSA)

#### 2.3.1 Naturezas Jurídicas Relevantes para a Advocacia e Entidades
No cadastro da RFB e CONCLA/IBGE, destacam-se:
- **`232-1` — Sociedade Unipessoal de Advocacia:** Instituída pela Lei nº 13.247/2016. Natureza jurídica primordial para escritórios individuais de advogados.
- **`233-0` — Sociedade de Advogados:** Sociedade pluripessoal registrada perante a OAB seccional (não registrada em Junta Comercial).
- **`206-2` — Sociedade Empresária Limitada (LTDA):** Registro em Junta Comercial.
- **`204-6` / `205-4` — Sociedade Anônima (S/A Aberta e Fechada):** Registro em Junta Comercial.
- **`213-5` — Empresário (Individual):** Registro em Junta Comercial.
- **`230-5` / `231-3` — Sociedade Simples (Limitada / Pura):** Registro em Cartório de Registro Civil de Pessoas Jurídicas (RCPJ).

#### 2.3.2 CNAE Principal e Secundários
- **`6911-7/01` — Serviços advocatícios:** Atividade privativa de bacharéis em direito inscritos na OAB.
- **`6911-7/02` — Atividades auxiliares da justiça:** Árbitros, peritos, mediadores, liquidantes e administradores judiciais.
- **`6911-7/03` — Agente de propriedade industrial:** Atuação perante o INPI.

---

### 2.4 CPF (Cadastro de Pessoas Físicas)

#### 2.4.1 Situação Cadastral no CPF
1. **REGULAR:** Cadastro completo e regular.
2. **PENDENTE DE REGULARIZAÇÃO:** Omissão de DIRPF em anos obrigatórios.
3. **SUSPENSA:** Inconsistência cadastral.
4. **CANCELADA POR MULTIPLICIDADE:** Duplicidade de inscrição.
5. **TITULAR FALECIDO:** Óbito averbado via SIRC ou espólio.
6. **NULA:** Fraude na inscrição.

#### 2.4.2 Consulta Pública vs. APIs Restritas
- **Consulta Pública Web:** Exige envio mandatório de **CPF + Data de Nascimento**.
- **Nome Social:** Decreto nº 8.727/2016 assegura o uso do nome social em todos os registros do BR-LAWYER.
- **Lei nº 14.534/2023:** CPF como número único de identificação em todo território nacional.

---

## 3. SERPRO (SERVIÇO FEDERAL DE PROCESSAMENTO DE DADOS)

### 3.1 Arquitetura de Comunicação e Autenticação
- **Gateway Oficial:** `https://gateway.apiserpro.serpro.gov.br`
- **Protocolo:** OAuth 2.0 (Client Credentials Grant) gerando Bearer Token JWT (TTL 3600s).
- **APIs Catalogadas:**
  - **Consulta CPF V3:** `GET /consulta-cpf-df/v3/{cpf}?dataNascimento=YYYY-MM-DD`
  - **Consulta CNPJ:** `GET /consulta-cnpj-df/v2/empresa/{cnpj}`
  - **Datavalid:** `POST /datavalid/v3/validate/pf-face`
  - **Consulta CND:** `POST /cnd/v1/certidao`

---

## 4. REDESIM & JUNTAS COMERCIAIS
- Integração nacional via integradores estaduais (JUCESP, JUCERJA, etc.).
- APIs diretas exigem convênios estaduais específicos.
- Estratégia no BR-LAWYER: Fonte primária nos Dados Abertos do CNPJ com enriquecimento em tempo real via SERPRO e BrasilAPI.

---

## 5. OAB (ORDEM DOS ADVOGADOS DO BRASIL) & CNA
- **Cadastro Nacional dos Advogados (CNA):** `cna.oab.org.br`.
- **Campos:** Nome, Inscrição OAB, Seccional (UF), Tipo (Advogado, Estagiário, Suplementar), Situação (Regular, Suspensa, Cancelada, Licenciado).
- **Padrão SPI no BR-LAWYER:** `OabValidationProvider` com implementações `MockOabValidationProvider`, `LocalCacheOabValidationProvider` e provedores comerciais configuráveis.

---

## 6. IBGE (INSTITUTO BRASILEIRO DE GEOGRAFIA E ESTATÍSTICA)
- **API de Localidades:** `https://servicodados.ibge.gov.br/api/v1/localidades/`
- **Código IBGE de 7 Dígitos:** Mandatório para emissão de NFS-e (tag `<CodigoMunicipio>`) e organização territorial de comarcas.

---

## 7. BACEN (BANCO CENTRAL DO BRASIL)
- **Catálogo SPB / STR / COMPE:** Código COMPE de 3 dígitos e ISPB de 8 dígitos.
- **Participantes do PIX:** API OData aberta do portal Olinda Bacen.
- **Aplicação:** Validação de contas para levantamento de depósitos judiciais (MLE/Alvarás) e cobrança de honorários via PIX.

---

## 8. MATRIZ CONSOLIDADA DE FONTES DE DADOS

| Fonte / Entidade | Tipo de Acesso | Custo / Licença | Autenticação | Latência Típica | Aplicação Principal no BR-LAWYER |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Base Aberta CNPJ (RFB)** | Dataset Público Batch | Gratuito (Domínio Público) | Nenhuma | Zero (BD local) | Busca offline, autocompletar PJ, QSA e CNAEs. |
| **Serpro Consulta CPF V3** | API REST Oficial | R$ 0,03 a 0,08 / consulta | OAuth 2.0 (Bearer) | 200 ms - 600 ms | Validação de CPF + Nascimento e situação civil. |
| **Serpro Consulta CNPJ** | API REST Oficial | R$ 0,04 a 0,12 / consulta | OAuth 2.0 (Bearer) | 250 ms - 700 ms | Validação de CNPJ em tempo real com QSA. |
| **Serpro Datavalid** | API REST Oficial | R$ 0,40 a 1,20 / validação | OAuth 2.0 (Bearer) | 800 ms - 2.500 ms | Onboarding e validação biométrica de partes. |
| **Serpro Consulta CND** | API REST Oficial | R$ 0,05 a 0,15 / certidão | OAuth 2.0 (Bearer) | 1.000 ms - 4.000 ms | Emissão e monitoramento de certidões fiscais. |
| **OAB / CNA Nacional** | SPI Plugável | Sem API pública oficial | SPI Provider / Mock | Instantâneo | Validação cadastral de advogados e sócios. |
| **IBGE Localidades** | API REST Pública | Gratuito (Open Data) | Nenhuma | 50 ms - 180 ms | Normalização de endereços e código NFS-e. |
| **BACEN SPB / PIX** | OData / Open Data | Gratuito (Open Data) | Nenhuma | 100 ms - 300 ms | Catálogo de bancos (ISPB/COMPE) e PIX. |
