# Relatório Abrangente de Levantamento de Bundles e Localização (R2)

**Data**: 02/09/2026  
**Investigador**: `teamwork_preview_explorer_survey_bundles`  
**Escopo**: Levantamento e auditoria exaustiva de requisitos R2 (ResourceBundles, Paridade de Bundles Raiz e Fallbacks) em todos os 16 módulos do repositório BR-LAWYER / ATRIUM.

---

## 1. Sumário Executivo

- **Total de Módulos no Repositório**: 16 submódulos funcionais (+ 2 poms agregadores).
- **Módulos com ResourceBundles Java**: 2 módulos (`j-lawyer-client` e `j-lawyer-backupmgr`).
- **Total de Famílias de Bundles (i18n)**: **43 famílias**.
  - `j-lawyer-backupmgr`: 1 família (10 chaves).
  - `j-lawyer-client`: 42 famílias (551 chaves).
- **Total de Arquivos `.properties` no Repositório**: **220 arquivos** (168 de i18n, 3 de tema Look & Feel, 36 de metadados de imagens de fundo, 12 de bibliotecas/configuração do NetBeans, 1 de teste de backupmgr).
- **Total de Chaves de Tradução (Root/Default)**: **561 chaves**.
- **Total de Chaves de Tradução (`pt_BR`)**: **561 chaves**.
- **Total de Chaves em Alemão (`de`)**: **561 chaves**.
- **Total de Chaves em Inglês (`en`)**: **521 chaves** (39 famílias possuem `_en.properties`).
- **Status de Paridade Raiz vs `pt_BR`**: **100% de Paridade (561/561 chaves idênticas em conteúdo e estrutura)**.
- **Resíduos em Alemão nos Bundles Raiz/pt-BR**: **0 strings em alemão residuais**. Todas as strings nos bundles padrão e `_pt_BR` estão traduzidas para o Português do Brasil com escape unicode apropriado (`\uXXXX`).
- **Módulo Web (`j-lawyer-web`)**: Utiliza i18n em JSON Angular (`de.json` com 1.966 linhas e `en.json`), sem arquivo `pt_BR.json` (módulo com compilação opt-in via perfil Maven `-Pweb`).

---

## 2. Auditoria dos 16 Módulos do Repositório

| # | Módulo | Tipo de Módulo | Arquivos Java | GUI (.form/.fxml) | Bundles (.properties) | Status i18n |
|---|---|---|:---:|:---:|:---:|---|
| 1 | `j-lawyer-backupmgr` | Desktop App (JavaFX) | 7 | 1 FXML | 3 files (1 família) | **100% Localizado em pt-BR** |
| 2 | `j-lawyer-client` | Desktop App (Swing/FlatLaf) | 485 | 114 .form | 165 files (42 famílias) | **100% Localizado em pt-BR** |
| 3 | `j-lawyer-cloud` | Cloud Storage Client | 10 | 0 | 0 | Sem bundles de UI |
| 4 | `j-lawyer-fax` | SIP/Fax Integration | 53 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 5 | `j-lawyer-ide` | Scripting/IDE Plugin | 7 | 1 .form (`GroovyIde.form`) | 0 | Strings hardcoded no `.form` (R1) |
| 6 | `j-lawyer-invoicing` | Invoicing/Boleto | 3 | 0 | 0 | Sem bundles de UI |
| 7 | `j-lawyer-io-common` | IO Utilities / Models | 1 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 8 | `j-lawyer-server-api` | EJB Remote API / DTOs | 83 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 9 | `j-lawyer-server-common` | Core Server Domain | 124 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 10 | `j-lawyer-server-entities` | JPA Entities / Migrations | 120 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 11 | `j-lawyer-server/j-lawyer-io` | REST / IO Endpoints | 242 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 12 | `j-lawyer-server/j-lawyer-server-ear` | EAR Packaging | 0 | 0 | 0 | Sem bundles de UI |
| 13 | `j-lawyer-server/j-lawyer-server-ejb` | Business EJB Services | 273 | 0 | 0 | Mensagens de log/backend |
| 14 | `j-lawyer-server/j-lawyer-server-io` | Document IO Service | 2 | 0 | 0 (apenas nblibraries) | Sem bundles de UI |
| 15 | `j-lawyer-server/j-lawyer-server-war` | Web Admin & JAX-RS | 16 | 0 | 0 | Sem bundles de UI |
| 16 | `j-lawyer-web` | Web Client (Angular) | 1 | 0 | 2 JSON (`de.json`, `en.json`) | Requer `pt-BR.json` para web |

---

## 3. Inventário Completo das 43 Famílias de ResourceBundles

Abaixo está o mapeamento detalhado de cada família de bundles, suas variantes de locale, contagem de chaves e conformidade de paridade:

| # | Módulo / Pacote | Nome Base do Bundle | Chaves (Raiz) | Chaves (`pt_BR`) | Chaves (`de`) | Chaves (`en`) | Outros Locales | Paridade Raiz vs pt-BR | Resíduos Alemão |
|---|---|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | `j-lawyer-backupmgr/org.jlawyer.backupmgr.controller` | `backupmgr` | 10 | 10 | 10 | 0 | — | 100% MATCH | 0 |
| 2 | `j-lawyer-client/com.jdimension.jlawyer.client` | `AboutDialog` | 4 | 4 | 4 | 4 | — | 100% MATCH | 0 |
| 3 | `j-lawyer-client/com.jdimension.jlawyer.client` | `AdminConsoleFrame` | 4 | 4 | 4 | 4 | — | 100% MATCH | 0 |
| 4 | `j-lawyer-client/com.jdimension.jlawyer.client` | `JKanzleiGUI` | 60 | 60 | 60 | 60 | — | 100% MATCH | 0 |
| 5 | `j-lawyer-client/com.jdimension.jlawyer.client` | `LoginDialog` | 20 | 20 | 20 | 20 | — | 100% MATCH | 0 |
| 6 | `j-lawyer-client/com.jdimension.jlawyer.client` | `Main` | 7 | 7 | 7 | 7 | — | 100% MATCH | 0 |
| 7 | `j-lawyer-client/com.jdimension.jlawyer.client` | `Modules` | 30 | 30 | 30 | 30 | — | 100% MATCH | 0 |
| 8 | `j-lawyer-client/com.jdimension.jlawyer.client` | `SplashThread` | 47 | 47 | 47 | 47 | — | 100% MATCH | 0 |
| 9 | `j-lawyer-client/com.jdimension.jlawyer.client` | `StartupSplashFrame` | 3 | 3 | 3 | 3 | — | 100% MATCH | 0 |
| 10 | `j-lawyer-client/com.jdimension.jlawyer.client.components` | `MultiCalDialog` | 3 | 3 | 3 | 3 | — | 100% MATCH | 0 |
| 11 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `BackupConfigurationDialog` | 28 | 28 | 28 | 28 | — | 100% MATCH | 0 |
| 12 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `BankSearchDialog` | 6 | 6 | 6 | 6 | — | 100% MATCH | 0 |
| 13 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `BankSearchThread` | 2 | 2 | 2 | 2 | — | 100% MATCH | 0 |
| 14 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `CitySearchDialog` | 6 | 6 | 6 | 6 | — | 100% MATCH | 0 |
| 15 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `CitySearchThread` | 2 | 2 | 2 | 2 | — | 100% MATCH | 0 |
| 16 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `CustomFieldConfigurationDialog` | 9 | 9 | 9 | 9 | — | 100% MATCH | 0 |
| 17 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `CustomLauncherOptionsDialog` | 16 | 16 | 16 | 16 | — | 100% MATCH | 0 |
| 18 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `DrebisConfigurationDialog` | 7 | 7 | 7 | 7 | — | 100% MATCH | 0 |
| 19 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `FontSizeConfigDialog` | 8 | 8 | 8 | 8 | — | 100% MATCH | 0 |
| 20 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `ImportBanksDialog` | 4 | 4 | 4 | 4 | — | 100% MATCH | 0 |
| 21 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `ImportBanksThread` | 6 | 6 | 6 | 6 | — | 100% MATCH | 0 |
| 22 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `ImportContactsDialog` | 12 | 12 | 12 | 12 | — | 100% MATCH | 0 |
| 23 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `ImportZipCodesDialog` | 4 | 4 | 4 | 4 | — | 100% MATCH | 0 |
| 24 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `ImportZipCodesThread` | 6 | 6 | 6 | 6 | — | 100% MATCH | 0 |
| 25 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `OptionGroupConfigurationDialog` | 3 | 3 | 3 | 3 | — | 100% MATCH | 0 |
| 26 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `ProfileDialog` | 18 | 18 | 18 | 18 | — | 100% MATCH | 0 |
| 27 | `j-lawyer-client/com.jdimension.jlawyer.client.configuration` | `UserProfileDialog` | 4 | 4 | 4 | 4 | — | 100% MATCH | 0 |
| 28 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `DesktopPanel` | 8 | 8 | 8 | 8 | — | 100% MATCH | 0 |
| 29 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `LastChangedEntryPanel` | 5 | 5 | 5 | 5 | — | 100% MATCH | 0 |
| 30 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `LastChangedTimerTask` | 2 | 2 | 2 | 2 | — | 100% MATCH | 0 |
| 31 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `ReviewDueEntryPanel` | 24 | 24 | 24 | 24 | — | 100% MATCH | 0 |
| 32 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `ReviewsDueTimerTask` | 2 | 2 | 2 | 2 | — | 100% MATCH | 0 |
| 33 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `SystemStateTimerTask` | 2 | 2 | 2 | 2 | — | 100% MATCH | 0 |
| 34 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `TaggedEntryPanel` | 5 | 5 | 5 | 5 | — | 100% MATCH | 0 |
| 35 | `j-lawyer-client/com.jdimension.jlawyer.client.desktop` | `TaggedTimerTask` | 2 | 2 | 2 | 2 | — | 100% MATCH | 0 |
| 36 | `j-lawyer-client/com.jdimension.jlawyer.client.editors` | `EditorsRegistry` | 19 | 19 | 19 | 19 | — | 100% MATCH | 0 |
| 37 | `j-lawyer-client/com.jdimension.jlawyer.client.editors` | `ShowURLDialog` | 6 | 6 | 6 | 6 | — | 100% MATCH | 0 |
| 38 | `j-lawyer-client/com.jdimension.jlawyer.client.editors.addresses` | `CaseForContactEntryPanel` | 8 | 8 | 8 | 8 | — | 100% MATCH | 0 |
| 39 | `j-lawyer-client/de.costache.calendar` | `calendar` | 3 | 3 | 3 | 0 | `fr`, `zh` | 100% MATCH | 0 |
| 40 | `j-lawyer-client/com.jdimension.jlawyer.client.enrichment` | `BrazilianIntegrationsConfigDialog` | 32 | 32 | 32 | 32 | — | 100% MATCH | 0 |
| 41 | `j-lawyer-client/com.jdimension.jlawyer.client.enrichment` | `CompanyEnrichmentDialog` | 52 | 52 | 52 | 52 | — | 100% MATCH | 0 |
| 42 | `j-lawyer-client/com.jdimension.jlawyer.client.enrichment` | `ContactDiffDialog` | 35 | 35 | 35 | 35 | — | 100% MATCH | 0 |
| 43 | `j-lawyer-client/themes` | `FlatIntelliJLaf` | 27 | 27 | 27 | 0 | — | 100% MATCH | 0 |

---

## 4. Análise de Mecânica de Fallback no Java Runtime

### 4.1. Hierarquia de Resolução do `ResourceBundle.getBundle`

No ecossistema Java SE e NetBeans Platform / Swing:
1. Quando uma chamada do tipo `ResourceBundle.getBundle("com/jdimension/jlawyer/client/LoginDialog")` é executada sem passar o `Locale` explicitamente:
   - O Java invoca `ResourceBundle.getBundle(baseName, Locale.getDefault())`.
   - No `Main.java` e `StartupSplashFrame.java`, o locale padrão da JVM é setado logo na inicialização para `pt_BR` via:
     ```java
     Locale ptBrLocale = new Locale("pt", "BR");
     Locale.setDefault(ptBrLocale);
     Locale.setDefault(Locale.Category.FORMAT, ptBrLocale);
     Locale.setDefault(Locale.Category.DISPLAY, ptBrLocale);
     ```
   - A ordem de busca de arquivos pelo `ResourceBundle.Control` padrão é:
     1. `com/jdimension/jlawyer/client/LoginDialog_pt_BR.properties`
     2. `com/jdimension/jlawyer/client/LoginDialog_pt.properties` (se existisse)
     3. `com/jdimension/jlawyer/client/LoginDialog.properties` (Bundle Raiz / Default)
2. Se qualquer chave não existir em `_pt_BR.properties`:
   - O Java delega para o parent bundle na cadeia de herança (`LoginDialog.properties`).
   - Como os arquivos padrão/raiz (`.properties`) foram 100% sincronizados com o conteúdo em Português Brasileiro (pt-BR), qualquer fallback de chave ausente ou qualquer lookup sem locale resulta imediatamente no texto em Português, eliminando qualquer vazamento de texto em alemão.
3. Se a aplicação for executada em um ambiente com Locale desconfigurado ou diferente (ex: `Locale.ROOT`, `es_ES`, `en_US` onde faltam propriedades específicas):
   - A resolução cai diretamente no bundle padrão/raiz (`Foo.properties`).
   - Sendo o bundle raiz 100% em Português, o usuário recebe a experiência completa em Português do Brasil.

---

## 5. Auditoria de Resíduos e Diferenças de Tradução

1. **Paridade Raiz vs `_pt_BR`**:
   - Todas as 43 famílias possuem contagem de chaves exatamente idêntica (`root_keys == pt_keys`).
   - Todas as 561 chaves dos arquivos raiz (`.properties`) contêm os mesmos valores (em português) que os arquivos `_pt_BR.properties`.
2. **Resíduos em Alemão**:
   - Uma varredura por termos e raízes lexicais comuns em alemão (`abbrechen`, `speichern`, `schließen`, `suchen`, `löschen`, `mandant`, `gegner`, `akte`, `fehler`, `erfolg`, etc.) revelou **0 ocorrências** de termos em alemão nos arquivos `.properties` padrão e `_pt_BR.properties`.
   - Os arquivos `_de.properties` continuam existindo e preservam os termos originais em alemão para referência e compatibilidade, mas os bundles padrão e `_pt_BR` estão plenamente imunizados.
3. **Casos Especiais**:
   - `BrazilianIntegrationsConfigDialog`, `CompanyEnrichmentDialog`, e `ContactDiffDialog`: São telas exclusivas do contexto brasileiro (CNPJ, CNAE, QSA, IBGE). Nesses diálogos, inclusive os arquivos `_de.properties` e `_en.properties` contêm textos em português para campos específicos da legislação e registros públicos brasileiros.
   - `FlatIntelliJLaf.properties`: Contém configurações do Look & Feel (cores, estilos de abas, tipos de setas), idêntico entre raiz, `_de` e `_pt_BR`.
   - Imagens de fundo (`backgroundsrandom/*.jpg.properties`): 36 arquivos com metadados de fotografias (`location=...`, `photographer=...`), sem impacto de tradução de UI.

---

## 6. Cobertura de Testes Automatizados (i18n & Challenger Suites)

A integridade e paridade dos bundles são garantidas por testes automatizados contínuos no repositório:

1. **`PtBrLocalizationTest`** (`j-lawyer-client`):
   - Testa o carregamento estático e a paridade de chaves de 42 bundles do cliente contra `Locale.ROOT`.
   - Valida formatação de datas (`DateUtils`), tempo relativo legível (`há X minutos`), moeda (`R$ 1.234,56`) e rótulos padrão de `UIManager` (`JOptionPane`, `JFileChooser`).
2. **`M1ChallengerStressTest`** (`j-lawyer-client`):
   - Executa descoberta dinâmica de todos os arquivos `_pt_BR.properties` no `src/main` (mínimo de 40 bundles).
   - Valida placeholders `{0}`, `{1}` e formatação via `MessageFormat.format` para evitar erros de sintaxe em tempo de execução.
   - Testa casos de borda de data (anos bissextos, 29 de fevereiro, datas inválidas, intervalos relativos futuros e passados).
3. **`BackupMgrLocalizationTest`** e **`BackupMgrChallengerTest`** (`j-lawyer-backupmgr`):
   - Validam o bundle `backupmgr.properties` e a paridade com `_pt_BR` e `Locale.ROOT`.

---

## 7. Relação com os Demais Requisitos (R1, R3, R4)

- **R1 (Strings Hardcoded em Forms e Código Java)**: Embora os ResourceBundles estejam 100% paritários e sincronizados, a varredura identificou que partes do código Java ainda contêm strings literais em alemão concatenadas ou em diálogos (ex: mensagens de `JOptionPane` em `JKanzleiGUI.java` como `"Synchronisation gestartet."` ou títulos em `GroovyIde.form`). Esse é o foco central do requisito R1.
- **R2 (Bundles e Fallbacks)**: **100% Atendido e Verificado**.
- **R3 (Menus, Diálogos de Sistema e Títulos de Janelas)**: Os bundles de menus (`Modules.properties`, `JKanzleiGUI.properties`) e diálogos de sistema (`StartupSplashFrame.properties`, `LoginDialog.properties`) estão 100% traduzidos nos bundles.
- **R4 (Validação e Compilação)**: A suíte de testes passa com sucesso na validação dos bundles.

---

## 8. Conclusão e Recomendações

1. **Status dos Bundles Raiz**: Os bundles raiz do repositório já estão em total paridade e sincronizados em Português Brasileiro (pt-BR), cumprindo integralmente o requisito R2.
2. **Manutenção Contínua**: Qualquer novo bundle introduzido no projeto deve obrigatoriamente ser criado com seu arquivo raiz `.properties` contendo o texto em português brasileiro e o correspondente `_pt_BR.properties`.
3. **Módulo Web (`j-lawyer-web`)**: Para o futuro trabalho no cliente web Angular (quando ativado via `-Pweb`), recomenda-se a criação do arquivo `pt-BR.json` correspondente ao `de.json`.
