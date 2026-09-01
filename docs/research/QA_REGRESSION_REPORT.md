# Relatório de QA & Testes de Regressão: BR-LAWYER

> **Documento de Referência:** `docs/research/QA_REGRESSION_REPORT.md`  
> **Autor:** Subagent QA & Regression Specialist  
> **Data:** 31 de Agosto de 2026  
> **Status da Auditoria:** Concluída com Sucesso (100% de cobertura nos 4 pilares solicitados)

---

## 1. Auditoria dos Testes Unitários de Domínio Jurídico Brasileiro

Localização: `j-lawyer-server-common/src/test/java/com/jdimension/jlawyer/domain/legal/cnj/`

### 1.1 `CnjNumberValidatorTest.java` & `CnjNumberValidator.java`
- **Conformidade Algorítmica:** O algoritmo implementa a fórmula oficial da Resolução CNJ nº 65/2008 baseada na norma **ISO 7064 Módulo 97 Base 10**. A decomposição modular $R_1 \dots R_4$ com ajuste $DD = 98 - R_4$ (com $98 \to 01$) foi auditada matematicamente.
- **Análise de Overflow:** O cálculo modular utiliza tipos `long`. O valor intermediário máximo em `r3 * 1000000L + o4 * 100L` atinge no máximo `96.999.900`, valor imensamente inferior ao limite de `Long.MAX_VALUE` ($9 \times 10^{18}$) e até de `Integer.MAX_VALUE` ($2,14 \times 10^9$). **Risco de overflow numérico: ZERO.**
- **Casos de Borda Identificados e Gaps:**
  1. *Filtro Permissivo de Caracteres:* O método `CnjNumberValidator.isValid(String)` aplica `NON_DIGITS.matcher(cnjString.trim()).replaceAll("")` diretamente antes de checar se a entrada respeita a máscara ou se possui caracteres alfanuméricos espúrios. Se uma string contiver `"0001234-56.2023.8.26.0100XYZ"`, o `NON_DIGITS` remove `XYZ` e a validação retorna `true`. As constantes `CNJ_FORMATTED_PATTERN` e `CNJ_DIGITS_ONLY_PATTERN` estão declaradas no validador mas não são checadas estritamente no `isValid`.
  2. *Validação Estrutural de Segmento e Tribunal:* `isValid` valida $J \in [1..9]$, mas não valida se $TR$ corresponde aos tribunais reais daquele segmento (ex: STF/STJ/CNJ usam apenas `00`, TRFs usam `01..06`, TRTs usam `01..24`, TJs usam `01..27`).
  3. *Métodos de Objeto `CnjNumber`:* A suíte não cobre explicitamente `compareTo()`, `equals()`, `hashCode()` e o lançamento de `IllegalArgumentException` em `CnjNumberValidator.parse("invalido")`.

### 1.2 `CpfCnpjValidatorTest.java` & `CpfCnpjValidator.java`
- **Validação de CPF (Módulo 11):** Implementa os pesos decrescentes $10..2$ e $11..2$ com regra de resto $< 2 \to 0$. Rejeição correta de dígitos repetidos (`111.111.111-11`, `000.000.000-00`).
- **Validação de CNPJ Numérico (14 dígitos):** Implementa pesos $\{5,4,3,2,9,8,7,6,5,4,3,2\}$ e $\{6,5,4,3,2,9,8,7,6,5,4,3,2\}$. Rejeita sequências repetidas.
- **Gaps e Casos de Borda para Fase 2:**
  1. *CNPJ Alfanumérico (Receita Federal IN RFB 2.222/2024):* O JavaDoc e a constante `NON_ALPHANUMERIC` existem no código, porém `isValidCnpj` ainda restringe para `clean.length() != 14` com `NON_DIGITS`. A conversão de caracteres alfanuméricos (tabela ASCII $- 48$) é um requisito mandatório da Fase 2.
  2. *Strings com Espaços em Branco / Caracteres de Controle:* Strings como `"   "`, `"\t\n"` são rejeitadas por tamanho, mas entradas com letras misturadas sofrem stripping similar ao CNJ.

---

## 2. Validador de Paridade e Integridade de ResourceBundles (`_pt_BR.properties`)

### 2.1 Resultado da Auditoria nos 10 Arquivos `_pt_BR.properties` Existentes
Todos os 10 bundles criados no baseline da Fase 1 foram inspecionados quanto a paridade de chaves, integridade de encoding (UTF-8) e ausência de caracteres corrompidos (mojibake):

| Arquivo ResourceBundle | Chaves Base | Chaves pt-BR | Paridade (%) | Encoding / Caracteres | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `AboutDialog_pt_BR.properties` | 4 | 4 | **100%** | UTF-8 Válido (sem escapes corrompidos) | **APROVADO** |
| `AdminConsoleFrame_pt_BR.properties` | 4 | 4 | **100%** | UTF-8 Válido | **APROVADO** |
| `JKanzleiGUI_pt_BR.properties` | 45 | 45 | **100%** | UTF-8 Válido | **APROVADO** |
| `LoginDialog_pt_BR.properties` | 20 | 20 | **100%** | UTF-8 Válido (Placeholders `{0}`, `{1}`, `{2}` consistentes) | **APROVADO** |
| `Main_pt_BR.properties` | 7 | 7 | **100%** | UTF-8 Válido | **APROVADO** |
| `Modules_pt_BR.properties` | 29 | 29 | **100%** | UTF-8 Válido | **APROVADO** |
| `SplashThread_pt_BR.properties` | 39 | 39 | **100%** | UTF-8 Válido | **APROVADO** |
| `StartupSplashFrame_pt_BR.properties` | 3 | 3 | **100%** | UTF-8 Válido | **APROVADO** |
| `MultiCalDialog_pt_BR.properties` | 3 | 3 | **100%** | UTF-8 Válido | **APROVADO** |
| `EditorsRegistry_pt_BR.properties` | 19 | 19 | **100%** | UTF-8 Válido | **APROVADO** |

**Resultado Geral:** **173 de 173 chaves traduzidas com 100% de paridade e 0% de corrupção de encoding.**

---

## 3. Avaliação da Suíte de Testes Existente por Módulo

| Módulo | Testes Existentes | Natureza / Dependências | Diagnóstico de QA | Ação Recomendada |
| :--- | :---: | :--- | :--- | :--- |
| **`j-lawyer-server-ejb`** | 9 classes | JUnit 4. `ArchiveFileServiceTest` e `SystemManagementTest` estão `@Ignore` (vazios ou requerem WildFly vivo). `EmailServiceTest` requer Azure. `CaseNumberGeneratorTest`, `InvoiceNumberGeneratorTest` e `InstantMessagingUtilTest` passam. `LibreOfficeODFTest` e `MicrosoftOfficeDocxTest` possuem caminhos relativos rígidos. | **Baixa Cobertura Real do Core EJB.** Faltam mocks de persistência e container. | Implementar testes de serviço com Mockito / EJB container embutido para regras de negócio sem WildFly. |
| **`j-lawyer-server-common`** | 11 classes | JUnit 4. Testes de CNJ, CPF/CNPJ, JWT, Passwords, Tree e Similaridade são puros e rápidos. Testes de FTP/SFTP/SMB dependem de rede/env vars e pulam graciosamente se não configurados. | **Excelente Qualidade e Estabilidade.** Módulo base mais sólido do projeto. | Expandir testes para OAB, TPU e novo CNPJ alfanumérico. |
| **`j-lawyer-client`** | 9 classes (+3 manuais + 3 em `src/main/java`) | Utilitários (`VersionUtilsTest`, `FileUtilsTest`, `StoredOrderUtilsTest`, `SystemUtilsTest`). 3 testes manuais de remoting em `manual/`. 3 testes soltos em `src/main/java` (`Base64Test`, `TaggingTest`, `MbbTest`). | **Gaps em UI/Formulários e Testes fora de `src/test/java`.** | Mover classes de teste de `src/main/java` para `src/test/java`. Criar testes automatizados de Paridade de Bundles. |
| **`j-lawyer-backupmgr`** | 1 classe (`RestoreExecutorTest`) | JUnit 4. O método de teste está comentado (`// @Test`) e aponta para caminhos de máquina pessoal (`/home/jens/scripts/test.properties`). | **Zero Cobertura Ativa.** | Refatorar para usar banco H2 embutido ou container temporário, reativando a validação. |

---

## 4. Plano de Testes Contínuos de Regressão (Fases 1 e 2)

### 4.1 Fase 1: Baseline, Build Estável & Internacionalização
1. **Pipeline de Testes Unitários Rápidos:** Execução de suítes puras (sem I/O externo) em $< 30\text{s}$ via `mvn test -Dtest=*ValidatorTest,*UtilsTest,*GeneratorTest`.
2. **Gate de Integridade de i18n:** Execução obrigatória do validador de ResourceBundles para garantir que nenhum `_pt_BR.properties` fique com chaves faltando ou caracteres corrompidos.
3. **Higienização de Estrutura de Testes:** Migração das classes de teste soltas em `src/main/java` para seus respectivos pacotes em `src/test/java`.

### 4.2 Fase 2: Domínio Jurídico Brasileiro & Serviços Processuais
1. **Suíte Parametrizada CNJ:** Testar matriz de todos os 90 tribunais brasileiros mapeados (27 TJs, 6 TRFs, 24 TRTs, 3 Tribunais Militares, STF, STJ, TST, TSE, STM, CNJ).
2. **Suíte de Documentos Fiscais:** Testar gerador/validador de CPF, CNPJ tradicional e CNPJ Alfanumérico (RFB 2026), com casos de borda e sanitização de injeção.
3. **Validador Oficial de OAB:** Validar combinações de número (1 a 6 dígitos), UF (27 unidades da federação) e tipo (Advogado, Estagiário, Suplementar).
4. **Motor Temporal e Prazos Forenses:** Testes unitários do motor de contagem de prazos em dias úteis (CPC/CLT) e dias corridos (CPP), feriados móveis/fixos (Lei 5.010/66) e recesso de fim de ano (Art. 220 CPC).
5. **Segurança e LGPD:** Testar mascaramento de dados (PII sanitization) antes do envio para modelos de IA e controle de acesso para casos em Segredo de Justiça.
