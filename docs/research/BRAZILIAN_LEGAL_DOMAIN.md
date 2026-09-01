# ARQUITETURA DO DOMÍNIO JURÍDICO BRASILEIRO — BR-LAWYER
**Documento de Referência Arquitetural e Especificação de Normas Oficiais**
**Versão:** 1.0.0
**Status:** Aprovado para Implementação

---

## 1. VISÃO GERAL E FUNDAMENTAÇÃO NORMATIVA

O **BR-LAWYER** é uma adaptação e extensão enterprise do ecossistema *j-lawyer.org* projetada especificamente para atender às exigências materiais, processuais e regulatórias do Poder Judiciário Brasileiro.

A arquitetura de domínio fundamenta-se estritamente nas seguintes bases legais e regulamentares:
- **Constituição Federal de 1988 (CF/88)**: Artigos 92 a 130-A (Organização e competência do Poder Judiciário e Funções Essenciais à Justiça).
- **Código de Processo Civil (Lei nº 13.105/2015 - CPC/2015)**: Normas fundamentais, contagem de prazos em dias úteis (art. 219), publicações e atos eletrônicos (art. 193 a 199 e 220).
- **Consolidação das Leis do Trabalho (Decreto-Lei nº 5.452/1943 - CLT)** com as alterações da **Lei nº 13.467/2017 (Reforma Trabalhista)**: Artigo 775 (prazos em dias úteis).
- **Código de Processo Penal (Decreto-Lei nº 3.689/1941 - CPP)**: Artigo 798 (prazos contínuos e dias corridos).
- **Lei do Processo Eletrônico (Lei nº 11.419/2006)**: Comunicação eletrônica de atos processuais, diário eletrônico e intimações.
- **Estatuto da Advocacia e da OAB (Lei nº 8.906/1994)**: Inscrições principal, suplementar e estagiários.
- **Lei Geral de Proteção de Dados Pessoais (Lei nº 13.709/2018 - LGPD)**: Tratamento de dados pessoais no âmbito contencioso e consultivo.
- **Resoluções do Conselho Nacional de Justiça (CNJ)**:
  - *Resolução CNJ nº 65/2008*: Numeração Processual Única (NPU).
  - *Resolução CNJ nº 46/2007*: Tabelas Processuais Unificadas (TPU).
  - *Resolução CNJ nº 331/2020*: Base Nacional de Dados do Poder Judiciário (DataJud).
  - *Resolução CNJ nº 455/2022*: Diário de Justiça Eletrônico Nacional (DJEN), Domicílio Judicial Eletrônico e Plataforma Digital do Poder Judiciário (PDPJ-Br).
  - *Resolução CNJ nº 185/2013*: Sistema Processo Judicial Eletrônico (PJe) e indisponibilidade de sistemas.

---

## 2. PADRÃO CNJ DE NUMERAÇÃO PROCESSUAL UNIFICADA (NPU)

### 2.1 Estrutura do Formato NPU (Resolução CNJ nº 65/2008)
O identificador único de todo processo no Brasil possui 20 dígitos numéricos (com máscara: 25 caracteres):

$$\mathbf{NNNNNNN-DD.AAAA.J.TR.OOOO}$$

| Campo | Tamanho | Descrição | Exemplo |
| :--- | :---: | :--- | :--- |
| **`NNNNNNN`** | 7 dígitos | Número sequencial do processo na unidade de origem por ano | `0001234` |
| **`DD`** | 2 dígitos | Dígito Verificador (DV) calculado via Módulo 97 Base 10 | `56` |
| **`AAAA`** | 4 dígitos | Ano de distribuição / ajuizamento da ação | `2026` |
| **`J`** | 1 dígito | Identificador do Segmento de Justiça (1 a 9) | `8` (Estadual) |
| **`TR`** | 2 dígitos | Identificador do Tribunal / Região Judiciária | `26` (TJSP) |
| **`OOOO`** | 4 dígitos | Identificador da Unidade de Origem / Foro / Comarca / Vara | `0100` (Fórum Central) |

*Expressão Regular Oficial (com e sem máscara):*
```regex
^(?<ordem>\d{7})-?(?<dv>\d{2})\.?(?<ano>\d{4})\.?(?<justica>\d)\.?(?<tribunal>\d{2})\.?(?<origem>\d{4})$
```

---

### 2.2 Algoritmo de Cálculo e Validação do Dígito Verificador (Módulo 97 Base 10)
O algoritmo oficial adotado pelo CNJ baseia-se na norma **ISO 7064 MOD 97-10**.

#### 2.2.1 Cálculo do Dígito Verificador ($DD$)
Dados os componentes $N = NNNNNNN$, $A = AAAA$, $J = J$, $T = TR$, $O = OOOO$:
1. Concatena-se $N$ com zeros à esquerda (7 dígitos): $B_1 = N$.
2. Calcula-se o resto $R_1 = B_1 \pmod{97}$.
3. Concatena-se $R_1$ com $A$ e $JTR$: $B_2 = (R_1 \times 10^4 + A)$.
4. Calcula-se o resto $R_2 = B_2 \pmod{97}$.
5. Concatena-se $R_2$ com $J$, $TR$, $O$ e dois zeros finais (equivalente a multiplicar por $100$):
   $$B_3 = (R_2 \times 10^7 + (J \times 10^6 + TR \times 10^4 + O)) \times 100$$
6. Calcula-se $R_3 = B_3 \pmod{97}$.
7. O dígito verificador é obtido por:
   $$DD = 98 - R_3$$
   *(Se $DD$ for menor que 10, adiciona-se zero à esquerda, e.g., "05").*

#### 2.2.2 Validação de Número Completo ($NNNNNNNDDAAAAJTROOOO$)
Para validar um número NPU existente, realiza-se o rearranjo:
$$\text{Número Modificado} = NNNNNNN + AAAA + J + TR + OOOO + DD$$
O número é considerado matematicamente **VÁLIDO** se, e somente se:
$$\text{Número Modificado} \pmod{97} \equiv 1$$

---

### 2.3 Implementação de Referência em Java (Pronta para EJB/Domain Core)

```java
package com.jdimension.jlawyer.domain.legal.cnj;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CnjNumberValidator {

    private static final Pattern CNJ_PATTERN = Pattern.compile(
        "^(\\d{7})-?(\\d{2})\\.?(\\d{4})\\.?(\\d)\\.?(\\d{2})\\.?(\\d{4})$"
    );
    private static final BigInteger MOD97 = BigInteger.valueOf(97);

    private CnjNumberValidator() {}

    public static boolean isValid(String cnjNumber) {
        if (cnjNumber == null) return false;
        Matcher matcher = CNJ_PATTERN.matcher(cnjNumber.trim());
        if (!matcher.matches()) return false;

        String ordem = matcher.group(1);
        String dv = matcher.group(2);
        String ano = matcher.group(3);
        String justica = matcher.group(4);
        String tribunal = matcher.group(5);
        String origem = matcher.group(6);

        // Rearranjo ISO 7064: NNNNNNNAAAAJTROOOODD
        String rearranged = ordem + ano + justica + tribunal + origem + dv;
        BigInteger bigNum = new BigInteger(rearranged);
        return bigNum.mod(MOD97).intValue() == 1;
    }

    public static String calculateDv(String ordem, String ano, String justica, String tribunal, String origem) {
        String baseWithoutDv = String.format("%07d%04d%s%02d%04d00",
                Long.parseLong(ordem),
                Integer.parseInt(ano),
                justica,
                Integer.parseInt(tribunal),
                Integer.parseInt(origem));

        // Particionamento aritmético modular para evitar overflow em inteiros primitivos
        long r1 = Long.parseLong(ordem) % 97;
        long r2 = Long.parseLong(String.format("%02d%04d%s", r1, Integer.parseInt(ano), justica)) % 97;
        long r3 = Long.parseLong(String.format("%02d%02d%04d00", r2, Integer.parseInt(tribunal), Integer.parseInt(origem))) % 97;
        
        long dv = 98 - r3;
        return String.format("%02d", dv);
    }
    
    public static String format(String clean20Digits) {
        if (clean20Digits == null || clean20Digits.length() != 20) return clean20Digits;
        return clean20Digits.replaceFirst("(\\d{7})(\\d{2})(\\d{4})(\\d)(\\d{2})(\\d{4})", "$1-$2.$3.$4.$5.$6");
    }
}
```

---

## 3. SEGMENTOS DE JUSTIÇA E MAPA DE TRIBUNAIS NO BRASIL

### 3.1 Tabela Geral dos Segmentos de Justiça ($J = 1 \dots 9$)

```
                        ┌──────────────────────────────────────────────┐
                        │      SUPREMO TRIBUNAL FEDERAL (STF) J=1      │
                        │           Guardião da Constituição           │
                        └──────────────────────┬───────────────────────┘
                                               │
               ┌───────────────────────────────┼───────────────────────────────┐
               ▼                               ▼                               ▼
  ┌─────────────────────────┐    ┌─────────────────────────┐    ┌─────────────────────────┐
  │ SUPERIOR TRIB. JUSTIÇA  │    │ TRIBUNAL SUPERIOR TRAB. │    │ TRIB. SUPERIOR ELEITORAL│
  │        (STJ) J=3        │    │        (TST) J=5        │    │        (TSE) J=6        │
  └────────────┬────────────┘    └────────────┬────────────┘    └────────────┬────────────┘
               │                              │                              │
     ┌─────────┴─────────┐                    ▼                              ▼
     ▼                   ▼               TRTs (01 a 24)                 TREs (01 a 27)
Justiça Federal    Justiça Estadual      Varas do Trabalho              Zonas Eleitorais
TRF1..TRF6 (J=4)   TJ01..TJ27 (J=8)
```

| $J$ | Segmento de Justiça | Tribunais Superiores / Regionais | Faixa de $TR$ |
| :---: | :--- | :--- | :--- |
| **1** | **Supremo Tribunal Federal** | STF (Cúpula Constitucional) | `00` |
| **2** | **Conselho Nacional de Justiça** | CNJ (Órgão de Governança e Controle) | `00` |
| **3** | **Superior Tribunal de Justiça** | STJ (Uniformização Infraconstitucional) | `00` |
| **4** | **Justiça Federal Comum** | TRF1 a TRF6 + Seções Judiciárias (SJ) | `01` a `06` |
| **5** | **Justiça do Trabalho** | TST (`00`) + TRT1 a TRT24 | `00` a `24` |
| **6** | **Justiça Eleitoral** | TSE (`00`) + TREs (26 Estados + DF) | `00` a `27` |
| **7** | **Justiça Militar da União** | STM (`00`) + 1ª a 12ª CJMs | `00` a `12` |
| **8** | **Justiça Estadual e do DF** | Tribunais de Justiça dos 26 Estados + TJDFT | `01` a `27` |
| **9** | **Justiça Militar Estadual** | TJMMG (`13`), TJMRS (`21`), TJMSP (`26`) | `13`, `21`, `26` |

---

### 3.2 Mapeamento Completo de $TR$ por Estado ($J=8$ e $J=6$)

| $TR$ | UF | TJ (Estadual $J=8$) | TRE (Eleitoral $J=6$) | TRT (Trabalhista $J=5$) | TRF (Federal $J=4$) |
| :---: | :---: | :--- | :--- | :--- | :--- |
| **01** | AC | TJAC | TRE-AC | TRT14 (RO/AC) | TRF1 (DF, GO, TO, MT, BA, PI, MA, PA, AM, AP, AC, RO, RR) |
| **02** | AL | TJAL | TRE-AL | TRT19 (AL) | TRF5 (PE, CE, RN, PB, AL, SE) |
| **03** | AP | TJAP | TRE-AP | TRT08 (PA/AP) | TRF1 |
| **04** | AM | TJAM | TRE-AM | TRT11 (AM/RR) | TRF1 |
| **05** | BA | TJBA | TRE-BA | TRT05 (BA) | TRF1 |
| **06** | CE | TJCE | TRE-CE | TRT07 (CE) | TRF5 |
| **07** | DF | TJDFT | TRE-DF | TRT10 (DF/TO) | TRF1 |
| **08** | ES | TJES | TRE-ES | TRT17 (ES) | TRF2 (RJ/ES) |
| **09** | GO | TJGO | TRE-GO | TRT18 (GO) | TRF1 |
| **10** | MA | TJMA | TRE-MA | TRT16 (MA) | TRF1 |
| **11** | MT | TJMT | TRE-MT | TRT23 (MT) | TRF1 |
| **12** | MS | TJMS | TRE-MS | TRT24 (MS) | TRF3 (SP/MS) |
| **13** | MG | TJMG | TRE-MG | TRT03 (MG) | TRF6 (Criado pela Lei 14.226/2021) |
| **14** | PA | TJPA | TRE-PA | TRT08 (PA/AP) | TRF1 |
| **15** | PB | TJPB | TRE-PB | TRT13 (PB) | TRF5 |
| **16** | PR | TJPR | TRE-PR | TRT09 (PR) | TRF4 (RS, SC, PR) |
| **17** | PE | TJPE | TRE-PE | TRT06 (PE) | TRF5 |
| **18** | PI | TJPI | TRE-PI | TRT22 (PI) | TRF1 |
| **19** | RJ | TJRJ | TRE-RJ | TRT01 (RJ) | TRF2 (RJ/ES) |
| **20** | RN | TJRN | TRE-RN | TRT21 (RN) | TRF5 |
| **21** | RS | TJRS | TRE-RS | TRT04 (RS) | TRF4 |
| **22** | RO | TJRO | TRE-RO | TRT14 (RO/AC) | TRF1 |
| **23** | RR | TJRR | TRE-RR | TRT11 (AM/RR) | TRF1 |
| **24** | SC | TJSC | TRE-SC | TRT12 (SC) | TRF4 |
| **25** | SE | TJSE | TRE-SE | TRT20 (SE) | TRF5 |
| **26** | SP | TJSP | TRE-SP | TRT02 (Grande SP/Baixada) / TRT15 (Campinas/Interior) | TRF3 (SP/MS) |
| **27** | TO | TJTO | TRE-TO | TRT10 (DF/TO) | TRF1 |

---

## 4. TABELAS PROCESSUAIS UNIFICADAS (TPU) DO CNJ

Criadas pela **Resolução CNJ nº 46/2007**, as TPUs padronizam a taxonomia jurídica em todo o território nacional.

```
┌────────────────────────────────────────────────────────────────────────────┐
│                    TABELAS PROCESSUAIS UNIFICADAS (TPU)                    │
├──────────────────────┬──────────────────────┬──────────────────────────────┤
│ 1. CLASSES           │ 2. ASSUNTOS          │ 3. MOVIMENTAÇÕES             │
│    Natureza do rito  │    Matéria material  │    Andamentos e atos         │
│    e tipo de ação    │    e pedido litigioso│    processuais no tempo      │
└──────────────────────┴──────────────────────┴──────────────────────────────┘
```

### 4.1 Árvore de Classes Processuais (Principais Exemplos)
- `7`: Procedimento Comum Cível
- `1116`: Execução Fiscal
- `120`: Mandado de Segurança Cível
- `11532`: Recuperação Judicial
- `170`: Reclamação Trabalhista (Rito Ordinário)
- `1125`: Ação de Cumprimento de Sentença
- `283`: Habeas Corpus Criminal
- `306`: Inquérito Policial
- `271`: Apelação Cível (2º Grau)
- `337`: Recurso Ordinário Trabalhista (2º Grau)

### 4.2 Árvore de Assuntos Processuais (Hierarquia Temática)
- `Direito Civil` (Código Raiz: `899`)
  - `Obrigações` (`7681`) $\rightarrow$ `Espécies de Contratos` (`9580`) $\rightarrow$ `Bancários` (`7752`)
  - `Responsabilidade Civil` (`10431`) $\rightarrow$ `Indenização por Dano Moral` (`7779`)
- `Direito do Trabalho` (`864`)
  - `Rescisão do Contrato de Trabalho` (`2620`) $\rightarrow$ `Verbas Rescisórias` (`2546`)
  - `Duração do Trabalho` (`1658`) $\rightarrow$ `Horas Extras` (`2086`)
- `Direito Previdenciário` (`195`)
  - `Benefícios em Espécie` (`6094`) $\rightarrow$ `Aposentadoria por Tempo de Contribuição` (`6118`)

### 4.3 Árvore de Movimentações Processuais e Complementos
- `26`: Distribuição
- `51`: Conclusão ao Magistrado *(Complementos: Motivo da conclusão)*
- `193`: Julgamento / Decisão *(Complementos: Julgamento de Procedência, Improcedência, Parcial)*
- `60`: Expedição de Documento / Mandado
- `85`: Juntada de Petição
- `132`: Trânsito em Julgado
- `22`: Baixa Definitiva

*Endpoints Públicos do SGT/CNJ:*
- Interface Web: `https://www.cnj.jus.br/sgt/`
- WebService SOAP/REST: `https://www.cnj.jus.br/sgt/sgtws.php`

---

## 5. DIÁRIO DE JUSTIÇA ELETRÔNICO NACIONAL (DJEN) E PDPJ-Br

### 5.1 Arquitetura de Comunicação Processual
Instituído pela **Resolução CNJ nº 455/2022**, o DJEN centraliza a publicação de todos os atos judiciais que não exigem intimação pessoal eletrônica.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      TRIBUNAIS (PJe, e-SAJ, Eproc)                      │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Envio via PDPJ-Br / PCP API
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│              DIÁRIO DE JUSTIÇA ELETRÔNICO NACIONAL (DJEN)               │
│                  Plataforma Digital do Poder Judiciário                 │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Consulta Pública (Sem Auth)
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│               BR-LAWYER / INGESTION WORKER (ComunicaAPI)                │
│             Captura diária por OAB, UF, Processo e Tribunal             │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.2 API Pública Oficial: ComunicaAPI (PDPJ-Br / CNJ)
- **Base URL Produção:** `https://comunicaapi.pje.jus.br/api/v1`
- **Ambiente Homologação:** `https://hcomunicaapi.cnj.jus.br/api/v1`
- **Endpoint de Consulta:** `GET /comunicacao` *(Acesso público, sem token de autorização)*

#### Parâmetros de Consulta (`Query Parameters`):
| Parâmetro | Tipo | Descrição |
| :--- | :---: | :--- |
| `numeroOab` | String | Número da inscrição na OAB (somente números) |
| `ufOab` | String | Sigla da UF da OAB (ex: `SP`, `RJ`, `MG`) |
| `numeroProcesso` | String | NPU sem máscara ou com máscara |
| `siglaTribunal` | String | Sigla oficial do tribunal (ex: `TJSP`, `TRF3`, `TRT2`) |
| `dataDisponibilizacaoInicio` | String (YYYY-MM-DD) | Data inicial da disponibilização no diário |
| `dataDisponibilizacaoFim` | String (YYYY-MM-DD) | Data final da disponibilização |
| `pagina` | Integer | Número da página (inicia em 1) |
| `itensPorPagina` | Integer | Quantidade de registros por página (padrão: 10, máx: 100) |

#### Exemplo de Resposta JSON da ComunicaAPI:
```json
{
  "count": 1,
  "items": [
    {
      "id": "abc123djen-pub-id",
      "dataDisponibilizacao": "2026-09-01",
      "siglaTribunal": "TJSP",
      "tipoComunicacao": "Intimação",
      "meio": "D",
      "numeroProcesso": "1002345-67.2026.8.26.0100",
      "texto": "Fica a parte autora intimada a se manifestar sobre a contestação no prazo de 15 (quinze) dias úteis.",
      "destinatarios": [
        {
          "nome": "EMPRESA BRASILEIRA DE SERVICOS LTDA",
          "polo": "A",
          "advogados": [
            {
              "nomeAdvogado": "CARLOS EDUARDO SILVA",
              "numeroOab": "123456",
              "ufOab": "SP"
            }
          ]
        }
      ]
    }
  ]
}
```

---

## 6. DATAJUD (BASE NACIONAL DE DADOS DO PODER JUDICIÁRIO)

### 6.1 Arquitetura Elasticsearch e Consulta Pública
O DataJud centraliza os metadados de todos os processos do país (Resolução CNJ nº 331/2020).

- **Base URL:** `https://api-publica.datajud.cnj.jus.br`
- **Índices:** `api_publica_{sigla_tribunal}` (e.g. `api_publica_tjsp`, `api_publica_trf1`, `api_publica_trt2`)
- **Método HTTP:** `POST /api_publica_{sigla_tribunal}/_search`
- **Autenticação:** Header `Authorization: APIKey <CHAVE_PUBLICA_CNJ>`

#### Exemplo de Payload Elasticsearch (Busca por Número de Processo):
```json
{
  "query": {
    "match": {
      "numeroProcesso": "10023456720268260100"
    }
  }
}
```

#### Estrutura do Documento Retornado (`_source`):
```json
{
  "numeroProcesso": "10023456720268260100",
  "classe": {
    "codigo": 7,
    "nome": "Procedimento Comum Cível"
  },
  "sistema": {
    "codigo": 1,
    "nome": "PJe"
  },
  "formato": {
    "codigo": 1,
    "nome": "Eletrônico"
  },
  "tribunal": "TJSP",
  "dataAjuizamento": "2026-03-15T14:30:00.000Z",
  "orgaoJulgador": {
    "codigo": 10543,
    "nome": "2ª Vara Cível do Foro Central",
    "codigoMunicipioIBGE": 3550308
  },
  "assuntos": [
    {
      "codigo": 7779,
      "nome": "Indenização por Dano Moral",
      "principal": true
    }
  ],
  "movimentos": [
    {
      "codigo": 26,
      "nome": "Distribuição",
      "dataHora": "2026-03-15T14:30:00.000Z"
    },
    {
      "codigo": 51,
      "nome": "Conclusão ao Juiz",
      "dataHora": "2026-08-30T10:00:00.000Z"
    }
  ]
}
```

---

## 7. PESSOAS, DOCUMENTOS E ENTIDADES BRASILEIRAS

### 7.1 CPF (Cadastro de Pessoas Físicas)
- **Formato:** 11 dígitos numéricos (`000.000.000-00`).
- **Validação Algorítmica (Módulo 11):**
  - $DV_1 = \left(\sum_{i=1}^9 d_i \times (11 - i)\right) \times 10 \pmod{11}$. Se resto for 10, $DV_1 = 0$.
  - $DV_2 = \left(\sum_{i=1}^{10} d_i \times (12 - i)\right) \times 10 \pmod{11}$. Se resto for 10, $DV_2 = 0$.
  - Rejeitar sequências de dígitos idênticos (`000.000.000-00`, `111.111.111-11`, etc.).

### 7.2 CNPJ (Cadastro Nacional da Pessoa Jurídica)
- **Formato Tradicional:** 14 dígitos numéricos (`00.000.000/0001-00`).
- **Novo Padrão Alfanumérico (IN RFB nº 2.229/2024):** Aceita letras nas 12 primeiras posições para expansão de combinações; os dois últimos caracteres continuam sendo dígitos verificadores numéricos.
- **Validação Algorítmica (Módulo 11 com Pesos Cíclicos):**
  - Pesos $DV_1$: `5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2`.
  - Pesos $DV_2$: `6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2`.

### 7.3 Carteira de Identidade Nacional (CIN / RG)
- Padrão **Lei nº 14.534/2023**: O CPF torna-se o número único e suficiente de identificação do cidadão no Brasil.
- Legado RG: Registro com número, órgão emissor (SSP, DETRAN, Policia Civil) e UF emissora.

### 7.4 Inscrição OAB (Ordem dos Advogados do Brasil)
- **Estrutura:** `[Número Inscrição]/[UF] - [Tipo]`
- **Tipos de Inscrição (Lei 8.906/1994):**
  1. `Principal / Definitiva`: Domicílio profissional principal.
  2. `Suplementar`: Obrigatória para o advogado que habitualmente exercer a profissão em outra unidade federativa com mais de 5 causas por ano (Art. 10, §2º).
  3. `Estagiário`: Inscrição com validade temporária (máx. 2 anos) para estudantes dos dois últimos anos de Direito.
  4. `Transferida`: Mudança definitiva de domicílio profissional.

### 7.5 Polos e Papéis Processuais
- **Polo Ativo (A):** Autor, Requerente, Exequente, Reclamante, Impetrante, Querelante, Embargante, Suscitante.
- **Polo Passivo (P):** Réu, Requerido, Executado, Reclamado, Impetrado, Querelado, Embargado, Suscitado.
- **Terceiros Interessados / Intervenção:** Assistente Simples, Assistente Litisconsorcial, Opoente, Denunciado à Lide, Chamado ao Processo, Amicus Curiae, Administrador Judicial.
- **Custos Legis (Fiscal da Ordem Jurídica):** Ministério Público (Federal ou Estadual).
- **Auxiliares da Justiça:** Perito Judicial, Leiloeiro Oficial, Depositário, Intérprete/Tradutor, Mediador/Conciliador Judicial.

---

## 8. RAMOS DO DIREITO E MATÉRIAS ESPECIALIZADAS NO BR-LAWYER

| Ramo do Direito | Rito / Legislação Principal | Órgão Julgador Típico | Particularidades / Campos Específicos |
| :--- | :--- | :--- | :--- |
| **Cível Geral** | CPC/2015 | Vara Cível Estadual/Federal | Valor da causa, pedidos cominatórios, execução de título extrajudicial. |
| **Família e Sucessões** | CPC/2015 + Código Civil | Vara de Família | Segredo de justiça automático (CPC art. 189), alimentos, partilha de bens. |
| **Trabalhista** | CLT + CPC subsidiário | Vara do Trabalho (TRT) | CBO da função, CTPS, data de admissão/demissão, FGTS, cálculo de verbas rescisórias. |
| **Previdenciário** | Lei 8.213/91 + CPC | Vara Federal / JEF | NB (Número de Benefício), DER (Data de Entrada do Requerimento), RMI, CNIS. |
| **Tributário & Execução Fiscal**| Lei 6.830/80 + CTN | Vara de Execução Fiscal | CDA (Certidão de Dívida Ativa), tributo questionado, depósito judicial. |
| **Consumidor** | Lei 8.078/90 (CDC) | JEC / Vara Cível | Inversão do ônus da prova, dano moral, negativação indevida (Serasa/SPC). |
| **Bancário** | CDC + Normas BACEN | Vara Cível | Taxa média de juros BACEN, revisão contratual, alienação fiduciária. |
| **Imobiliário & Notarial** | Lei 8.245/91 + Lei 6.015/73 | Vara Cível / Varas de Registros | Matrícula de Imóvel, CRI, despejo, rescisão contratual de incorporação. |
| **Criminal** | CPP | Vara Criminal / Tribunal do Júri | Réu preso vs solto, denúncia, flagrante, prescrição da pretensão punitiva. |
| **Empresarial & Falimentar** | Lei 11.101/2005 | Vara Empresarial/Recuperação | Quadro Geral de Credores (QGC), plano de recuperação, classes de credores. |
| **Administrativo & Público** | Lei 14.133/2021 + Lei 8.429/92 | Vara da Fazenda Pública | Precatórios, RPV, prazo em dobro para Fazenda Pública. |

---

## 9. REGRAS DE PRAZOS PROCESSUAIS E ENGENHARIA TEMPORAL

### 9.1 Regimes Processuais de Contagem

```
                         REGIME DO ATO PROCESSUAL
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         ▼                          ▼                          ▼
      CÍVEL                      TRABALHISTA                PENAL
    (CPC art. 219)             (CLT art. 775)           (CPP art. 798)
         │                          │                          │
         ▼                          ▼                          ▼
    DIAS ÚTEIS                 DIAS ÚTEIS                 DIAS CORRIDOS
(Exclui fins de semana     (Exclui fins de semana    (Ininterrupto; se expirar
  e feriados forenses)       e feriados forenses)     em dia não útil, prorroga
                                                       para o 1º dia útil)
```

---

### 9.2 O Ciclo da Intimação Eletrônica (Lei nº 11.419/2006, Art. 4º)
A contagem de prazos decorrentes do DJEN segue rigorosamente a regra tripartite:

$$\begin{aligned}
D_0 &\rightarrow \text{Data da Disponibilização no DJEN (Diário na internet)} \\
D_1 &\rightarrow \text{Data da Publicação} = \text{Primeiro dia útil subsequente a } D_0 \\
D_2 &\rightarrow \text{Termo Inicial do Prazo (Dia do Começo)} = \text{Primeiro dia útil subsequente a } D_1 \\
D_{\text{fatal}} &\rightarrow \text{Termo Final do Prazo} = \text{Resultado da contagem de } N \text{ dias a partir de } D_2
\end{aligned}$$

#### Exemplo Prático de Contagem:
- **Sexta-feira (04/09):** Disponibilização no DJEN ($D_0$).
- **Segunda-feira (07/09):** Feriado Nacional (Independência do Brasil - Dia não útil).
- **Terça-feira (08/09):** Primeiro dia útil $\rightarrow$ **Data da Publicação ($D_1$)**.
- **Quarta-feira (09/09):** Primeiro dia útil subsequente $\rightarrow$ **Termo Inicial ($D_2$, Dia 1 do prazo)**.
- **Prazo de 5 dias úteis:** Quarta (dia 1), Quinta (dia 2), Sexta (dia 3), Segunda (dia 4), Terça (dia 5) $\rightarrow$ **Vencimento em Terça-feira (15/09)**.

---

### 9.3 Feriados, Recesso Forense e Suspensões
1. **Recesso Forense (CPC Art. 220):** Suspensão de prazos e não realização de audiências/julgamentos entre **20 de dezembro e 20 de janeiro**. Prazos que se iniciam ou vencem no recesso ficam prorrogados para o primeiro dia útil subsequente.
2. **Feriados Forenses da Justiça Federal (Lei nº 5.010/1966, Art. 62):** Segunda e terça de Carnaval, quarta-feira de cinzas, Semana Santa (quarta a domingo), 11 de agosto (Criação dos Cursos Jurídicos), 1º e 2 de novembro (Todos os Santos / Finados), 8 de dezembro (Dia da Justiça).
3. **Prazos em Dobro:** Fazenda Pública (CPC art. 183), Defensoria Pública (CPC art. 186) e Ministério Público (CPC art. 180).
4. **Indisponibilidade do Sistema Eletrônico (Resolução CNJ nº 185/2013, Art. 10):** Se o sistema de peticionamento ficar indisponível por tempo superior a 60 minutos ininterruptos entre 06h00 e 23h00, ou no período das 23h00 às 24h00 do dia do vencimento, o prazo é automaticamente prorrogado para o primeiro dia útil seguinte.

---

## 10. PROTEÇÃO CONTRA ALUCINAÇÃO DE PRAZOS E CONFORMIDADE LGPD

### 10.1 Arquitetura Anti-Alucinação para Prazos Processuais

> [!CAUTION]
> **REGRA FUNDAMENTAL:** Modelos de Linguagem (LLMs) NUNCA devem calcular a data de vencimento final ($D_{\text{fatal}}$) de forma livre. O cálculo de prazos deve ser 100% determinístico e auditável por código executável.

```
┌────────────────────────────────────────────────────────────────────────┐
│ 1. CAMADA DE EXTRAÇÃO SEMÂNTICA (LLM / IA)                             │
│    - Extrai tipo de ato (ex: contestação, apelação, réplica)           │
│    - Extrai dispositivo legal citado (ex: art. 335 CPC)                │
│    - Extrai prazo expressamente determinado pelo juiz (ex: "15 dias")  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ JSON Estruturado de Metadados
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ 2. MOTOR DE REGRAS DETERMINÍSTICO (BR-LAWYER Legal Temporal Engine)    │
│    - Aplica calendário forense auditado (nacional, estadual e local)   │
│    - Aplica regime processual (CPC / CLT / CPP)                        │
│    - Calcula D0 -> D1 -> D2 -> Dfatal                                  │
│    - Gera memória de cálculo passo a passo (Explainable Audit Trail)   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Data Fatal + Memória de Cálculo
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ 3. VALIDAÇÃO HUMANA OBRIGATÓRIA (Human-in-the-Loop)                    │
│    - Exibe prazo com status "PENDENTE DE HOMOLOGAÇÃO PELO ADVOGADO"     │
│    - Advogado revisa a certidão de publicação e confirma o vencimento  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Modelo de Trilha de Auditoria Explicável (`Explainable Audit Trail`):
```json
{
  "processo": "1002345-67.2026.8.26.0100",
  "ato": "Contestação",
  "regime": "CPC_2015_DIAS_UTEIS",
  "diasPrescritos": 15,
  "disponibilizacaoD0": "2026-09-04",
  "publicacaoD1": "2026-09-08",
  "justificativaD1": "07/09/2026 é Feriado Nacional (Independência do Brasil - Lei 662/1949)",
  "termoInicialD2": "2026-09-09",
  "diasComputados": [
    { "dia": 1, "data": "2026-09-09", "tipo": "UTIL" },
    { "dia": 2, "data": "2026-09-10", "tipo": "UTIL" },
    { "dia": 3, "data": "2026-09-11", "tipo": "UTIL" },
    { "dia": null, "data": "2026-09-12", "tipo": "SABADO" },
    { "dia": null, "data": "2026-09-13", "tipo": "DOMINGO" },
    { "dia": 4, "data": "2026-09-14", "tipo": "UTIL" },
    { "dia": 5, "data": "2026-09-15", "tipo": "UTIL" }
  ],
  "termoFinalFatal": "2026-09-29T23:59:59",
  "confiabilidadeMotor": "100% Determinístico",
  "status": "PENDENTE_CONFIRMACAO_HUMANA"
}
```

---

### 10.2 Diretrizes de Conformidade com a LGPD (Lei nº 13.709/2018)

1. **Bases Legais de Tratamento no BR-LAWYER:**
   - *Exercício regular de direitos em processo judicial* (Art. 7º, VI).
   - *Execução de contrato de prestação de serviços advocatícios* (Art. 7º, V).
   - *Cumprimento de obrigação legal ou regulatória* (Art. 7º, II).
2. **Tratamento de Segredo de Justiça (CPC Art. 189):**
   - Flag de nível de sigilo herdada automaticamente do DataJud.
   - Restrição de visualização na UI (Controle de Acesso Baseado em Papéis - RBAC).
   - Anonimização/pseudonimização de nomes de partes e testemunhas em logs de telemetria e chamadas para LLMs de terceiros.
3. **Criptografia e Minimização de Dados:**
   - Documentos e dados pessoais indexados no Lucene e no banco de dados devem suportar criptografia at-rest.
   - PII Scrubbing: Sanitização automática de dados bancários, números de cartão e senhas antes do armazenamento ou envio para APIs externas.

---

## 11. MODELO DE DADOS CONCEITUAL (SCHEMAS JPA / DOMÍNIO BR-LAWYER)

```
┌───────────────────────────┐         1..* ┌───────────────────────────┐
│     ProcessoJudicial      ├─────────────►│      ParteProcessual      │
│  (Extends ArchiveFileBean)│              │   (Extends AddressBean)   │
├───────────────────────────┤              ├───────────────────────────┤
│ - id: Long                │              │ - polo: PoloProcessual    │
│ - npu: String (CNJ 20)    │              │ - papel: PapelProcessual  │
│ - tribunal: String (TR)   │              │ - cpfCnpj: String         │
│ - segmento: SegmentoCNJ   │              │ - oabNumero: String       │
│ - classeTpu: Integer      │              │ - oabUf: String           │
│ - assuntoPrincipalTpu: Int│              │ - oabTipo: TipoOab        │
│ - orgaoJulgador: String   │              └───────────────────────────┘
│ - segredoJustica: Boolean │
└─────────────┬─────────────┘
              │ 1..*
              ├───────────────────────────►┌───────────────────────────┐
              │                            │   MovimentacaoProcessual  │
              │                            ├───────────────────────────┤
              │                            │ - codigoTpu: Integer      │
              │                            │ - dataHora: LocalDateTime │
              │                            │ - descricao: String       │
              │                            │ - fonte: FonteDadosJud    │
              │                            └───────────────────────────┘
              │ 1..*
              └───────────────────────────►┌───────────────────────────┐
                                           │      PrazoProcessual      │
                                           ├───────────────────────────┤
                                           │ - regime: RegimePrazo     │
                                           │ - dias: Integer           │
                                           │ - dataDisponibilizacao    │
                                           │ - dataPublicacao          │
                                           │ - termoInicial            │
                                           │ - termoFinalFatal         │
                                           │ - statusHomologacao: Enum │
                                           │ - memoriaCalculoJson: Text│
                                           └───────────────────────────┘
```
