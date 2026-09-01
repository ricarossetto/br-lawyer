# Relatório de Auditoria e Inventário de I18N / ResourceBundles (BR-LAWYER)

> **Documento Alvo para Registro:** `docs/research/I18N_BUNDLE_AUDIT.md`  
> **Data da Auditoria:** 31 de Agosto de 2026  
> **Auditor:** SUBAGENT I18N AUDITOR  
> **Repositório / Workspace:** `c:\projetos IA\BR-LAWYER\br-lawyer`

---

## 1. Resumo Executivo (Executive Summary)

Foi realizada uma auditoria exaustiva de internacionalização (i18n) e inventário completo de `ResourceBundle` em todos os submódulos do projeto BR-LAWYER (`j-lawyer-client`, `j-lawyer-server`, `j-lawyer-backupmgr`, `j-lawyer-web`, `j-lawyer-cloud`, `j-lawyer-fax`, `j-lawyer-ide`, `j-lawyer-invoicing`, `j-lawyer-io-common`).

### Métricas Principais da Auditoria
* **Total de ResourceBundles mapeados (.properties):** **38 bundles**
* **Total de chaves i18n existentes nos bundles default:** **400 chaves**
* **Total de chaves traduzidas para `pt_BR`:** **196 chaves**
* **Taxa de Cobertura pt-BR nos Bundles Existentes:** **49,0%** (196 / 400 chaves)
* **Bundles com arquivo `_pt_BR.properties` implementado:** **10 bundles (26,3%)**
* **Bundles existentes SEM `_pt_BR.properties`:** **28 bundles (73,7%)**
* **UI Components Críticos com 0% de i18n (Strings Hardcoded em Alemão/Inglês no código Java):** **9 módulos principais** (Processos/Akte, Contatos, Financeiro/RVG, Calendário/Prazos, Digitalização/Documentos, Assistente IA, Backup Manager JavaFX, Telas de Configurações Avançadas e Servidor).

---

## 2. Inventário Completo de ResourceBundles por Módulo e Variação

### 2.1 Módulo `j-lawyer-client` — Bundles com Tradução pt-BR Existente (10 Bundles / 196 Chaves)

| # | Caminho do Bundle / Arquivo Base | Variações Presentes | Total Chaves (Default) | Chaves pt-BR | Cobertura pt-BR | Status |
|---|---|---|---|---|---|---|
| 1 | `com.jdimension.jlawyer.client.AboutDialog` | `.properties`, `_en.properties`, `_pt_BR.properties` | 4 | 4 | 100% | ✅ Completo |
| 2 | `com.jdimension.jlawyer.client.AdminConsoleFrame` | `.properties`, `_en.properties`, `_pt_BR.properties` | 4 | 4 | 100% | ✅ Completo |
| 3 | `com.jdimension.jlawyer.client.JKanzleiGUI` | `.properties`, `_en.properties`, `_pt_BR.properties` | 60 | 60 | 100% | ⚠️ Parcial (código Java possui menus hardcoded fora do bundle) |
| 4 | `com.jdimension.jlawyer.client.LoginDialog` | `.properties`, `_en.properties`, `_pt_BR.properties` | 20 | 20 | 100% | ✅ Completo |
| 5 | `com.jdimension.jlawyer.client.Main` | `.properties`, `_en.properties`, `_pt_BR.properties` | 7 | 7 | 100% | ✅ Completo |
| 6 | `com.jdimension.jlawyer.client.Modules` | `.properties`, `_en.properties`, `_pt_BR.properties` | 29 | 29 | 100% | ✅ Completo |
| 7 | `com.jdimension.jlawyer.client.SplashThread` | `.properties`, `_en.properties`, `_pt_BR.properties` | 47 | 47 | 100% | ✅ Completo |
| 8 | `com.jdimension.jlawyer.client.StartupSplashFrame` | `.properties`, `_en.properties`, `_pt_BR.properties` | 3 | 3 | 100% | ✅ Completo |
| 9 | `com.jdimension.jlawyer.client.components.MultiCalDialog` | `.properties`, `_en.properties`, `_pt_BR.properties` | 3 | 3 | 100% | ✅ Completo |
| 10 | `com.jdimension.jlawyer.client.editors.EditorsRegistry` | `.properties`, `_en.properties`, `_pt_BR.properties` | 19 | 19 | 100% | ✅ Completo |
| **Subtotal** | **10 Bundles** | | **196** | **196** | **100%** | |

---

### 2.2 Módulo `j-lawyer-client` — Bundles Críticos FALTANDO `_pt_BR.properties` (28 Bundles / 204 Chaves)

#### A. Editores e Desktop
| # | Caminho do Bundle / Arquivo Base | Variações Presentes | Chaves (Default) | Chaves pt-BR | Faltam (pt-BR) | Prioridade |
|---|---|---|---|---|---|---|
| 11 | `com.jdimension.jlawyer.client.editors.ShowURLDialog` | `.properties`, `_en.properties` | 6 | 0 | 6 | Média |
| 12 | `com.jdimension.jlawyer.client.editors.addresses.CaseForContactEntryPanel` | `.properties`, `_en.properties` | 8 | 0 | 8 | Alta |
| 13 | `com.jdimension.jlawyer.client.desktop.DesktopPanel` | `.properties`, `_en.properties` | 8 | 0 | 8 | Alta |
| 14 | `com.jdimension.jlawyer.client.desktop.LastChangedEntryPanel` | `.properties`, `_en.properties` | 5 | 0 | 5 | Alta |
| 15 | `com.jdimension.jlawyer.client.desktop.LastChangedTimerTask` | `.properties`, `_en.properties` | 2 | 0 | 2 | Média |
| 16 | `com.jdimension.jlawyer.client.desktop.ReviewDueEntryPanel` | `.properties`, `_en.properties` | 20 | 0 | 20 | Crítica |
| 17 | `com.jdimension.jlawyer.client.desktop.ReviewsDueTimerTask` | `.properties`, `_en.properties` | 2 | 0 | 2 | Média |
| 18 | `com.jdimension.jlawyer.client.desktop.SystemStateTimerTask` | `.properties`, `_en.properties` | 2 | 0 | 2 | Baixa |
| 19 | `com.jdimension.jlawyer.client.desktop.TaggedEntryPanel` | `.properties`, `_en.properties` | 5 | 0 | 5 | Alta |
| 20 | `com.jdimension.jlawyer.client.desktop.TaggedTimerTask` | `.properties`, `_en.properties` | 2 | 0 | 2 | Baixa |

#### B. Diálogos de Configuração (`configuration`)
| # | Caminho do Bundle / Arquivo Base | Variações Presentes | Chaves (Default) | Chaves pt-BR | Faltam (pt-BR) | Prioridade |
|---|---|---|---|---|---|---|
| 21 | `com.jdimension.jlawyer.client.configuration.BackupConfigurationDialog` | `.properties`, `_en.properties` | 28 | 0 | 28 | Alta |
| 22 | `com.jdimension.jlawyer.client.configuration.BankSearchDialog` | `.properties`, `_en.properties` | 6 | 0 | 6 | Média |
| 23 | `com.jdimension.jlawyer.client.configuration.BankSearchThread` | `.properties`, `_en.properties` | 2 | 0 | 2 | Baixa |
| 24 | `com.jdimension.jlawyer.client.configuration.CitySearchDialog` | `.properties`, `_en.properties` | 6 | 0 | 6 | Média |
| 25 | `com.jdimension.jlawyer.client.configuration.CitySearchThread` | `.properties`, `_en.properties` | 2 | 0 | 2 | Baixa |
| 26 | `com.jdimension.jlawyer.client.configuration.CustomFieldConfigurationDialog` | `.properties`, `_en.properties` | 9 | 0 | 9 | Alta |
| 27 | `com.jdimension.jlawyer.client.configuration.CustomLauncherOptionsDialog` | `.properties`, `_en.properties` | 16 | 0 | 16 | Média |
| 28 | `com.jdimension.jlawyer.client.configuration.DrebisConfigurationDialog` | `.properties`, `_en.properties` | 7 | 0 | 7 | Baixa (Alemanha) |
| 29 | `com.jdimension.jlawyer.client.configuration.FontSizeConfigDialog` | `.properties`, `_en.properties` | 8 | 0 | 8 | Média |
| 30 | `com.jdimension.jlawyer.client.configuration.ImportBanksDialog` | `.properties`, `_en.properties` | 4 | 0 | 4 | Média |
| 31 | `com.jdimension.jlawyer.client.configuration.ImportBanksThread` | `.properties`, `_en.properties` | 6 | 0 | 6 | Baixa |
| 32 | `com.jdimension.jlawyer.client.configuration.ImportContactsDialog` | `.properties`, `_en.properties` | 12 | 0 | 12 | Alta |
| 33 | `com.jdimension.jlawyer.client.configuration.ImportZipCodesDialog` | `.properties`, `_en.properties` | 4 | 0 | 4 | Média |
| 34 | `com.jdimension.jlawyer.client.configuration.ImportZipCodesThread` | `.properties`, `_en.properties` | 6 | 0 | 6 | Baixa |
| 35 | `com.jdimension.jlawyer.client.configuration.OptionGroupConfigurationDialog` | `.properties`, `_en.properties` | 3 | 0 | 3 | Média |
| 36 | `com.jdimension.jlawyer.client.configuration.ProfileDialog` | `.properties`, `_en.properties` | 18 | 0 | 18 | Crítica |
| 37 | `com.jdimension.jlawyer.client.configuration.UserProfileDialog` | `.properties`, `_en.properties` | 4 | 0 | 4 | Alta |

#### C. Biblioteca de Calendário Swing (`de.costache.calendar`)
| # | Caminho do Bundle / Arquivo Base | Variações Presentes | Chaves (Default) | Chaves pt-BR | Faltam (pt-BR) | Prioridade |
|---|---|---|---|---|---|---|
| 38 | `de.costache.calendar.calendar` | `_de.properties`, `_fr.properties`, `_zh.properties` *(default `.properties` e `_en` ausentes!)* | 3 | 0 | 3 | Alta |
| **Subtotal** | **28 Bundles Sem pt-BR** | | **204** | **0** | **204** | |

---

## 3. Mapeamento de Telas e Módulos com Strings Hardcoded (Sem ResourceBundle)

A auditoria revelou que grande parte da interface gráfica construída ao longo dos anos pelo upstream j-lawyer não utiliza `ResourceBundle`, tendo strings em alemão cravadas diretamente no código Java (`.setText("...")`, `.setTitle("...")`, `JOptionPane.showMessageDialog(...)`).

### 3.1 Módulo Processos (`com.jdimension.jlawyer.client.editors.files`)
* **`ArchiveFilePanel.java`** (~7.800 linhas):
  * Mais de **120 strings fixas em alemão** nas abas, menus de contexto e ações:
    * Abas: *"Falldaten"* (Dados do Processo), *"Beteiligte"* (Partes/Envolvidos), *"Dokumente"* (Documentos), *"Wiedervorlagen"* (Prazos/Acompanhamentos), *"Verlauf"* (Histórico), *"Finanzen"* (Financeiro).
    * Menus de contexto de documentos: *"öffnen"*, *"öffnen mit..."*, *"lokal speichern"*, *"duplizieren"*, *"in andere Akte kopieren/verschieben"*, *"umbenennen"*, *"Erstellungsdatum anpassen"*, *"farblich hervorheben"*, *"Favoritendokument an/aus"*, *"Nachricht senden"*, *"löschen"*, *"als PDF zusammenführen"*, *"Texterkennung (OCR)"*.
    * Menus de prazos: *"erledigt"*, *"offen"*, *"duplizieren"*, *"zeitlich verschieben"*, *"in andere Akte verschieben"*.
* **`DateTimePickerDialog.java`**: Reutiliza erroneamente `ShowURLDialog.properties` no código gerado pelo NetBeans.
* **`NewArchiveFileDialog.java` / `CaseDetailsPanel.java`**: Textos e labels de criação de processos em alemão.

### 3.2 Menu Principal e Janela Principal (`JKanzleiGUI.java`)
* Embora possua `JKanzleiGUI_pt_BR.properties` (60 chaves), **mais de 100 itens de menu adicionados em versões recentes** foram injetados com `.setText("...")` direto no Java (linhas 1459 a 2050):
  * `mnuDocumentsBin.setText("Papierkorb")` (Lixeira)
  * `mnuBeaCourtAddressImport.setText("Import: Gerichtsadressen")` (Importar Tribunais)
  * `mnuAddressOptionsStates.setText("Bundesländer")` (Estados / UFs)
  * `mnuAddressOptionsLegalForm.setText("Rechtsformen")` (Naturezas Jurídicas)
  * `mnuFinance.setText("Finanzen")`, `mnuInvoicePools.setText("Belegnummernkreise")`, `mnuInvoiceTaxRates.setText("Steuersätze")` (Alíquotas de Impostos).

### 3.3 Módulo de Assistente de IA (`com.jdimension.jlawyer.client.assistant`)
* `AssistantChatDialog.java` e `AssistantChatPanel.java`:
  * Diálogos de erro e títulos hardcoded em alemão (*"Fehler beim Laden der Akteninformationen"*, *"Akteninformationen laden"*, *"Akte konnte nicht geöffnet werden"*).
* `ToolRegistry.java` (Registro de ferramentas de IA):
  * Mais de **50 descrições de ferramentas e parâmetros em alemão** expostas ao modelo e aos usuários (*"Aktenzeichen der Akte"*, *"Aktennotiz"*, *"Aktensuche"*, *"Akte nicht gefunden"*).

### 3.4 Módulo Backup Manager (`j-lawyer-backupmgr`)
* Interface JavaFX com FXML (`backupmgr.fxml`) e `BackupMgrController.java`:
  * FXML com textos fixos em alemão: *"Hinweis: Die Wiederherstellung ersetzt alle vorhandenen Daten!"*, *"Datensicherungsordner:"*, *"Verschlüsselt mit Passwort:"*, *"Wiederherstellen"*.
  * Controller com mensagens dinâmicas fixas: *"Prüfe Datensicherung..."*, *"Prüfung erfolgreich..."*, *"Wiederherstellung abgeschlossen."*.

### 3.5 Servidor e APIs (`j-lawyer-server`)
* **Exceções de Regra de Negócio:** Mais de **280 exceções lançadas com mensagens em alemão** em `j-lawyer-server-ejb` (ex: `CaseNumberGenerator.java`, `InvoiceNumberGenerator.java`, `SecurityUtils.java`).

---

## 4. Plano de Ação Recomendado (Roadmap de I18N)

### Fase 1: Completar Cobertura dos Bundles Existentes
Criar os **28 arquivos `_pt_BR.properties` faltantes** identificados na seção 2.2:
* 10 arquivos no pacote `com/jdimension/jlawyer/client/desktop` e `editors` (80 chaves).
* 17 arquivos no pacote `com/jdimension/jlawyer/client/configuration` (121 chaves).
* 1 arquivo `calendar_pt_BR.properties` em `de/costache/calendar` (3 chaves).
* **Resultado:** Elevação imediata da cobertura dos bundles existentes para **100% (400/400 chaves)**.

### Fase 2: Extração de Strings nos Painéis Principais (Swing UI)
1. **`JKanzleiGUI.java`:** Mapear as ~100 chamadas `.setText("...")` das barras de menu e vinculá-las a chaves em `JKanzleiGUI.properties` e `JKanzleiGUI_pt_BR.properties`.
2. **`ArchiveFilePanel.java`:** Criar `ArchiveFilePanel.properties`, `ArchiveFilePanel_en.properties` e `ArchiveFilePanel_pt_BR.properties` extraindo todas as strings de abas, menus e diálogos.
3. **`Assistant` & `ToolRegistry`:** Internacionalizar as strings do chat de IA e mensagens de erro.

### Fase 3: Internacionalização do `j-lawyer-backupmgr`
* Criar `backupmgr.properties` e `backupmgr_pt_BR.properties`.
* Atualizar `backupmgr.fxml` para usar sintaxe `%chave` e carregar o `ResourceBundle` no `FXMLLoader`.
