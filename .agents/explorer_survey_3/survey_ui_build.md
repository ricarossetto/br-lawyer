# Relatório de Levantamento e Auditoria: Requisitos R3 e R4 (UI Desktop & Test/Build Infrastructure)

**Data do Levantamento**: 02 de Setembro de 2026  
**Investigador**: `teamwork_preview_explorer_survey_ui_build`  
**Escopo**: Módulos `j-lawyer-client`, `j-lawyer-backupmgr`, `j-lawyer-io-common` e reatores Maven  

---

## Sumário Executivo

Este documento consolida o levantamento exaustivo dos requisitos **R3** (Menus, Diálogos de Sistema, Visualizadores, Assistente IA, Configurações, Gerenciador de Backup e Títulos de Janelas) e **R4** (Infraestrutura de Testes, Compilação Maven, Testes de Internacionalização e Gaps de Validação pt-BR).

A investigação identificou:
1. A estrutura base do cliente e do gerenciador de backup possui suporte a pt-BR, com dezenas de ResourceBundles criados;
2. **Entretanto**, persistem **dezenas de strings residuais em alemão** hardcoded em código Java, arquivos `.form`, tooltips, títulos de janelas (`setTitle`), mensagens de `JOptionPane`, cabeçalhos de tabela e arrays de botões (`new String[]{"Ja", "Nein"}`);
3. A infraestrutura de testes unitários (`PtBrLocalizationTest`, `M1ChallengerStressTest`, `BrazilianUiUtilsTest`, `BrazilianUiUtilsChallengerTest`, `BackupMgrLocalizationTest`, `BackupMgrChallengerTest`) executa e passa com **100% de sucesso** (42 testes de i18n/domain, 193 testes no cliente total), e a compilação multi-módulo (`mvn clean package -pl j-lawyer-client -am`) compila com **100% de sucesso** (Java 17, 8 módulos);
4. **Existem 6 gaps críticos de cobertura de testes**, notadamente a ausência de varredura estática de literais alemães em classes/forms, ausência de testes de inicialização de UI headless e ausência de verificação da instalação de chaves pt-BR no `UIManager` em tempo de execução.

---

## 1. Levantamento Detalhado do Requisito R3 (Desktop UI & Diálogos)

### 1.1. Barra de Menus do Desktop, Submenus e Aceleradores de Teclado
- **Arquivos principais**:
  - `j-lawyer-client/src/main/java/com/jdimension/jlawyer/client/JKanzleiGUI.java`
  - `j-lawyer-client/src/main/java/com/jdimension/jlawyer/client/JKanzleiGUI.form`
  - `j-lawyer-client/src/main/java/com/jdimension/jlawyer/client/JKanzleiGUI_pt_BR.properties`
  - `j-lawyer-client/src/main/java/com/jdimension/jlawyer/client/Main.java`
  - `j-lawyer-client/src/main/java/com/jdimension/jlawyer/client/Modules_pt_BR.properties`

- **Estrutura de Menus Identificada**:
  - **Arquivo (`menu.file`)**: Importar Bancos, Importar CEPs, Importar Contatos, Sair (`menu.file.exit`).
  - **Exibir / Ansicht**: No arquivo `JKanzleiGUI.form` (Linha 119), o menu de exibição ainda possui texto literal hardcoded `"Ansicht"` em vez de ler da propriedade pt-BR (`"Exibir"`).
  - **Janela / Fenster**: No arquivo `JKanzleiGUI.form` (Linha 1112), o menu de janelas possui texto hardcoded `"Fenster"` em vez de ler da propriedade pt-BR (`"Janela"`).
  - **Configurações (`menu.settings`)**: Submenus para Contatos, Processos, Documentos, Tamanho da Fonte, Temas, Perfil do Escritório, Perfil do Usuário, Gestão de Usuários, Backup, Monitoramento e Console do Administrador.
  - **Ajuda / ? (`menu.?`)**: Monitor de Documentos, Ajuda Online, Sobre...

- **Aceleradores e Atalhos de Teclado**:
  - Registrados em `Main.java` (Linhas 883–1080) e `JKanzleiGUI.java` (Linhas 809–817):
    - `Ctrl+K` / `⌘+K`: Busca Global (`GlobalSearchDialog`)
    - `Shift+F1` / `⌘+1`: Painel Principal (`DesktopPanel`)
    - `Shift+F2` / `⌘+2`: Cadastrar Novo Processo (`NewArchiveFilePanel`)
    - `Shift+F3` / `⌘+3`: Pesquisar Processos (`EditArchiveFilePanel`)
    - `Shift+F4` / `⌘+4`: Cadastrar Novo Contato (`NewAddressPanel`)
    - `Shift+F5` / `⌘+5`: Pesquisar Contatos (`EditAddressPanel`)
    - `Shift+F6` / `⌘+6`: Prazos e Audiências por Vencimento (`ArchiveFileReviewsOverviewPanel`)
    - `Shift+F7` / `⌘+7`: Caixa de Entrada de E-mails (`EmailInboxPanel`)
    - `Shift+F8` / `⌘+8`: Workflow Jurídico (`BrazilianWorkflowPanel`)
    - `Shift+F9` / `⌘+9`: Digitalizações / Scans (`ScannerPanel`)

---

### 1.2. Splash Screen e Diálogo de Login
- **Arquivos principais**:
  - `com.jdimension.jlawyer.client.StartupSplashFrame.java`
  - `com.jdimension.jlawyer.client.SplashThread.java` / `SplashThread_pt_BR.properties`
  - `com.jdimension.jlawyer.client.LoginDialog.java` / `LoginDialog.form` / `LoginDialog_pt_BR.properties`
  - `com.jdimension.jlawyer.client.ImportConnectionProfileDialog.java` / `ImportConnectionProfileDialog.form`
  - `com.jdimension.jlawyer.client.UserCredentialsDialog.java` / `UserCredentialsDialog.form`

- **Anomalias e Resíduos Alemães Encontrados**:
  - `LoginDialog.java`:
    - Linha 1314: `setTitle("j-lawyer.org Login");` -> residual hardcoded;
    - Linha 1673: `cmdImportProfile.setToolTipText("Profil aus Zwischenablage importieren");` -> alemão residual;
    - Linha 1988: `jLabel22.setText("3. Profil durch scannen des QR-Codes übernehmen");` -> alemão residual;
    - Linha 2513: `JOptionPane.showMessageDialog(this, "Profil konnte nicht gespeichert werden", "Profil speichern", JOptionPane.ERROR_MESSAGE);` -> mensagem e título em alemão;
    - Linha 2527: `JOptionPane.showInputDialog(this, "Name des Verbindungsprofils: ", "Neues Verbindungsprofil anlegen", ...);` -> diálogo em alemão;
    - Linha 2544, 2563: `JOptionPane.showMessageDialog(this, "Profil konnte nicht hinzugefügt werden", "Profil hinzufügen", JOptionPane.ERROR_MESSAGE);` -> mensagem e título em alemão;
    - Linha 2551: `dlg.setTitle("Profil aus Zwischenablage einfügen");` -> título do diálogo de importação em alemão.
  - `UserCredentialsDialog.java`:
    - Linha 721: `setTitle("Nutzer wechseln");` -> título em alemão ("Trocar Usuário");
    - Linha 724: `jLabel1.setText("Nutzer:");` -> label em alemão ("Usuário:");
    - Linha 727: `jLabel2.setText("Passwort:");` -> label em alemão ("Senha:");
    - Linha 759: `cmdConfirm.setText("Anwenden");` -> botão em alemão ("Confirmar" / "Aplicar").
  - `SplashThread.java`:
    - Linha 1055: `JOptionPane.showOptionDialog(..., new String[]{"Ja", "Nein"}, "Nein");` -> botões do diálogo em alemão ("Sim", "Não").

---

### 1.3. Diálogos de Autos Processuais (Autos e Processos)
- **Arquivos principais no pacote `com.jdimension.jlawyer.client.editors.files`**:
  - `ArchiveFilePanel.java` / `ArchiveFilePanel.form` (Painel central dos autos do processo)
  - `AddAddressSearchDialog.java` / `AddAddressSearchDialog.form`
  - `InvolvedPartyEntryPanel.java` / `InvolvedPartyEntryPanel.form`
  - `ExportAsPdfMergeStep.java`
  - `BulkEditCasePermissions.java` / `CaseExportDialog.java`
  - `ArchiveFileReviewsFindPanel.java` / `QuickArchiveFileSearchPanel.java`

- **Anomalias e Resíduos Alemães Encontrados**:
  - `AddAddressSearchDialog.java`:
    - Linha 820: `cmdQuickSearch.setToolTipText("Suchen");` -> deve ser "Pesquisar";
    - Linha 877: `cmbRefType.setModel(new DefaultComboBoxModel<>(new String[] { "Mandant", "Gegner", "Dritte" }));` -> termos em alemão ("Cliente", "Parte Contrária", "Terceiros").
  - `InvolvedPartyEntryPanel.java`:
    - Linha 1103: `cmbRefType.setModel(new DefaultComboBoxModel<>(new String[] { "Mandant", "Gegner", "Dritte" }));` -> termos em alemão.
  - `ArchiveFilePanel.java`:
    - Linha 4441: `cmdToEditMode.setToolTipText("Bearbeiten");` -> "Editar";
    - Linha 4475: `cmdSave.setToolTipText("Speichern");` -> "Salvar";
    - Linhas 6932–6933: `new String[]{"Ja", "Nein"}, "Nein"` -> botões JOptionPane em alemão;
    - Linha 7008: `JOptionPane.showOptionDialog(..., "Eine Datei mit dem Namen ... existiert bereits ...", "Datei ersetzen?", ..., new String[]{"Ja", "Nein"}, "Nein");` -> mensagem, título e botões em alemão;
    - Linhas 8025, 8027, 8077: fallback string `"Akte"` -> "Processo";
    - Linha 8288: `new String[]{"cat-doc", "Dokument"}` -> "Documento";
    - Linhas 8839, 8924, 8959, 9097, 9171, 9219, 9244, 9432: Diálogos `JOptionPane.showMessageDialog` com textos e títulos em alemão (`"Fehler"`, `"Es können nur PDF-Dateien verkleinert werden"`, `"Es können nur PDF-Dateien gesplittet werden"`, `"Ungültige Seitenzahlen eingegeben"`, `"Es können nur PDF-Dateien anonymisiert werden"`, `"Es können nur PDF-Dateien gestempelt werden"`, `"Fehler beim Stempeln des PDFs"`, `"Es können nur PDF-Dateien umsortiert werden"`, `"Fehler beim Erstellen des Forderungskontos"`).
  - `ArchiveFileReviewsFindPanel.java`:
    - Linha 1092: `cmdPrint.setToolTipText("Drucken");` -> "Imprimir".
  - `QuickArchiveFileSearchPanel.java`:
    - Linha 929: `cmdQuickSearch.setToolTipText("Suchen");` -> "Pesquisar".
  - `BulkEditCasePermissions.java`:
    - Linhas 985, 1025: Diálogos JOptionPane com títulos `"Erfolg"` e `"Fehler"`.
  - `CaseExportDialog.java`:
    - Linha 210: Mensagem mista `"Erro ao carregar der synchronisierten Akten: "` e título `"Fehler"`.
  - `ExportAsPdfMergeStep.java`:
    - Linha 758: `chooser.setApproveButtonText("Speichern");` -> "Salvar".

---

### 1.4. Visualizadores de Documentos (PDF, Imagens, Markdown) e Compactador
- **Arquivos principais**:
  - `com.jdimension.jlawyer.client.editors.documents.viewer.MarkdownPanel.java` / `.form`
  - `com.jdimension.jlawyer.client.editors.documents.LoadDocumentPreviewThread.java`
  - `com.jdimension.jlawyer.client.editors.documents.SearchAndAssignDialog.java`
  - `com.iradraconis.shrinkify.ShrinkifyGui.java` (Ferramenta integrada de compressão de PDF)

- **Anomalias e Resíduos Alemães Encontrados**:
  - `MarkdownPanel.java` e `MarkdownPanel.form`:
    - Linhas 828, 1765 e Form Linha 46: Aba nomeada `"Vorschau"` e verificação condicional `if ("Vorschau".equals(title))` -> deve ser "Pré-visualização".
  - `LoadDocumentPreviewThread.java`:
    - Linha 826: `JOptionPane.showMessageDialog(pnlPreview, "Falsches Passwort!", "Fehler", JOptionPane.ERROR_MESSAGE);` -> "Senha incorreta!", "Erro".
  - `SearchAndAssignDialog.java`:
    - Linha 999: `cmdQuickSearch.setToolTipText("Suchen");` -> "Pesquisar".
  - `ShrinkifyGui.java`:
    - Linha 724, 820: `setTitle("PDF komprimieren");` -> "Comprimir PDF";
    - Linha 741: `JOptionPane.showMessageDialog(this, "Ungültige Datei... Bitte nur PDF-Dateien übergeben.", "Fehler", JOptionPane.ERROR_MESSAGE);`;
    - Linha 824: `jLabel1.setText("Qualitätsstufe auswählen:");` -> "Nível de qualidade:";
    - Linha 826: Itens do ComboBox em alemão (`"Sehr niedrige Qualität"`, `"Niedrigste Qualität"`, `"Niedrige Qualität"`, `"Mittlere Qualität"`, `"Hohe Qualität"`, `"Sehr hohe Qualität"`);
    - Linha 829: `jLabel2.setText("Auflösung reduzieren auf:");` -> "Reduzir resolução para:";
    - Linha 834: `bwCheckBox.setText("in Schwarz/Weiß konvertieren");` -> "Converter para Preto e Branco";
    - Linha 841: `greyscaleCheckBox.setText("In Graustufen konvertieren");` -> "Converter para Tons de Cinza";
    - Linha 848: `overwriteCheckBox.setText("Urspr. Dateien überschreiben");` -> "Substituir arquivos originais";
    - Linha 865: `saveButton.setText("Komprimieren");` -> "Comprimir".

---

### 1.5. Exportador de Relatórios e Estatísticas
- **Arquivos principais no pacote `com.jdimension.jlawyer.client.editors.reporting`**:
  - `ReportingPanel.java` / `ReportingPanel.form`
  - `DynamicReportContainerPanel.java` / `DynamicReportContainerPanel.form`
  - `ReportEntryPanel.java` / `ReportEntryPanel.form`

- **Anomalias e Resíduos Alemães Encontrados**:
  - `ReportingPanel.java`:
    - Linha 1143: `lblPanelTitle.setText("Auswertungen");` -> deve ser "Relatórios e Estatísticas".
  - `ReportEntryPanel.java`:
    - Linha 764: `lblName.setText("Name");` -> "Nome";
    - Linha 769: `lblSpecial.setText("Spezial");` -> "Especial";
    - Linha 771: `lblChart.setText("Chart");` -> "Gráfico";
    - Linha 773: `lblTable.setText("Tabelle");` -> "Tabela".
  - `DynamicReportContainerPanel.java`:
    - Linha 793: `lblDateLabel.setText("Datum:");` -> "Data:".

---

### 1.6. Assistente de Inteligência Artificial (Ingo)
- **Arquivos principais no pacote `com.jdimension.jlawyer.client.assistant`**:
  - `AssistantChatDialog.java` / `AssistantChatPanel.java`
  - `AssistantExtractDialog.java` / `AssistantExtractPanel.java`
  - `AssistantGenerateDialog.java` / `AssistantGenericDialog.java` / `AssistantGenericPanel.java`
  - `AssistantParameterDialog.java` / `AssistantResultDialog.java` / `AssistantVisionDialog.java`
  - `AiChatMessageTextSelection.java` / `ToolApprovalDialog.java`

- **Anomalias e Resíduos Alemães Encontrados**:
  - Títulos de Diálogos:
    - `AiChatMessageTextSelection.java` Linha 697: `setTitle("Textauswahl kopieren");` -> "Copiar seleção de texto";
    - `AssistantChatDialog.java` Linha 1001: `setTitle("Assistent Ingo");` -> "Assistente Ingo";
    - `AssistantGenerateDialog.java` Linha 954: `setTitle("Texte generieren");` -> "Gerar textos";
    - `AssistantParameterDialog.java` Linha 761: `setTitle("Assistent Ingo: Parameter");` -> "Assistente Ingo: Parâmetros";
    - `AssistantResultDialog.java` Linha 708: `setTitle("Assistent Ingo: Ergebnisse");` -> "Assistente Ingo: Resultados".
  - Labels e Tooltips:
    - `AssistantChatDialog.java`:
      - Linha 1019: `cmdCopy.setToolTipText("Text in Zwischenablage kopieren");` -> "Copiar texto para a área de transferência";
      - Linha 1036: `lblRequestType.setText("Transkribieren");` -> "Transcrever";
      - Linha 1039: `cmdSubmit.setToolTipText("Anfrage an Assistent Ingo senden");` -> "Enviar mensagem ao Assistente Ingo";
      - Linha 1047: `cmdInterrupt.setToolTipText("Laufende Anfrage unterbrechen");` -> "Interromper solicitação em andamento";
      - Linha 1055: `cmdTranscribe.setToolTipText("KI-Anfrage diktieren");` -> "Ditar comando por voz";
      - Linha 1064: `cmdPrompt.setToolTipText("Eigene Prompts auswählen");` -> "Selecionar modelos de prompt";
      - Linha 1072: `cmdResetChat.setToolTipText("Chat-Historie zurücksetzen und neuen Chat beginnen");` -> "Limpar histórico e iniciar nova conversa".
    - `AssistantExtractPanel.java` Linha 265: `lblRequestType = new JLabel("Extrahieren");` -> "Extração de Dados".
    - `AssistantChatPanel.java`:
      - Linha 1192: Título `"Fehler"` em JOptionPane;
      - Linha 1557: `"Dokument konnte nicht geöffnet werden: " + ex.getMessage(), "Fehler"`;
      - Linha 1605, 1611: `"Akte konnte nicht geöffnet werden: " + ex.getMessage(), "Fehler"`.

---

### 1.7. Configurações de Rede, Servidor e Painéis Administrativos
- **Arquivos principais**:
  - `com.jdimension.jlawyer.client.JKanzleiGUI.java` (Abertura dos diálogos de configuração do sistema)
  - `com.jdimension.jlawyer.client.configuration.MultiValueTagConfigurationDialog.java`
  - `com.jdimension.jlawyer.client.configuration.ServerMonitoringDialog.java` / `.form`
  - `com.jdimension.jlawyer.client.configuration.UserAdministrationDialog.java` / `.form`
  - `com.jdimension.jlawyer.client.configuration.CustomLauncherOptionsDialog.java` / `.form`
  - `com.jdimension.jlawyer.client.configuration.DocumentsBinDialog.java` / `.form`

- **Anomalias e Resíduos Alemães Encontrados**:
  - `JKanzleiGUI.java`: Títulos literais em alemão ao abrir diálogos genéricos de opções:
    - Linha 2764: `dlg.setTitle("Dokumenten-Etiketten");` -> "Etiquetas de Documentos";
    - Linha 2839: `dlg.setTitle("Staatsangehörigkeiten");` -> "Nacionalidades";
    - Linha 2847: `dlg.setTitle("Rechtsformen");` -> "Tipos Societários / Formas Jurídicas";
    - Linha 2855: `dlg.setTitle("akademische Grade (vor dem Namen)");` -> "Títulos Acadêmicos (Antes do Nome)";
    - Linha 2863: `dlg.setTitle("akademische Grade (nach dem Namen)");` -> "Títulos Acadêmicos (Depois do Nome)";
    - Linha 2871: `dlg.setTitle("Berufe");` -> "Profissões";
    - Linha 2879: `dlg.setTitle("Rolle / Funktion");` -> "Papel / Função";
    - Linha 2887: `dlg.setTitle("Länder");` -> "Países";
    - Linha 2895: `dlg.setTitle("Titel (Briefkopf)");` -> "Títulos (Cabeçalho)";
    - Linha 3031: `dlg.setTitle("Währungen");` -> "Moedas";
    - Linha 3046: `dlg.setTitle("Steuersätze");` -> "Alíquotas Tributárias";
    - Linha 3063: `dlg.setTitle("Zeiterfassung: mögliche Taktung (Minuten)");` -> "Controle de Horas: Intervalos (Minutos)";
    - Linha 3219: `dlg.setTitle("Bundesländer");` -> "Estados / Unidades Federativas".
  - `MultiValueTagConfigurationDialog.java`:
    - Linhas 750, 761: `new JMenuItem("Löschen");` -> "Excluir";
    - Linhas 1087, 1105, 1214, 1235: `JOptionPane.showConfirmDialog(..., "Löschen", ...);`.
  - `CustomLauncherOptionsDialog.java` / `UserAdministrationDialog.java`:
    - Bordas de painel: `BorderFactory.createTitledBorder("Einstellungen");` -> "Configurações".
  - `DocumentsBinDialog.java`:
    - Linhas 754, 858: Cabeçalhos da tabela `"gelöscht", "von", "Dateiname", "Akte", "Adresse"` -> "Excluído em", "Por", "Nome do Arquivo", "Processo", "Contato".
  - `ServerMonitoringDialog.java` Linha 1209 / `.form`:
    - Aba nomeada `"Einstellungen"` -> "Configurações".

---

### 1.8. Gerenciador de Backup (`j-lawyer-backupmgr`)
- **Arquivos principais**:
  - `j-lawyer-backupmgr/src/main/java/org/jlawyer/backupmgr/BackupManager.java`
  - `j-lawyer-backupmgr/src/main/java/org/jlawyer/backupmgr/controller/BackupMgrController.java`
  - `j-lawyer-backupmgr/src/main/resources/fxml/backupmgr.fxml`
  - `j-lawyer-backupmgr/src/main/resources/org/jlawyer/backupmgr/controller/backupmgr_pt_BR.properties`
  - `j-lawyer-backupmgr/src/main/resources/org/jlawyer/backupmgr/controller/backupmgr.properties`

- **Situação e Anomalias Encontradas**:
  - O FXML `backupmgr.fxml` já possui labels e botões traduzidos ("Aviso: ", "A restauração substituirá todos os dados existentes!", "Pasta do backup:", "Criptografado com senha:", "Senha do banco de dados:", "Diretório de dados BR-LAWYER:", "Restaurar Backup").
  - `BackupManager.java`:
    - Linha 690: `primaryStage.setTitle("j-lawyer.org Backupmanager");` -> deve ser "BR-LAWYER - Gerenciador de Cópias de Segurança (Backup)";
    - Linhas 703, 706: `System.out.println("j-lawyer.org Backup Manager (ohne grafische Oberflaeche)");` e `System.out.println("j-lawyer.org Backup Manager (grafische Oberflaeche)");` -> mensagens de console em alemão.

---

## 2. Levantamento Detalhado do Requisito R4 (Build & Test Infrastructure)

### 2.1. Configuração do Maven POM e JDK
- **Root POM (`pom.xml`)**:
  - Compilador: `<maven.compiler.release>17</maven.compiler.release>`
  - Codificação: `UTF-8`
  - Plugin de compilação: `maven-compiler-plugin:3.11.0` com `<useIncrementalCompilation>false</useIncrementalCompilation>` para evitar artefatos de classes obsoletos.
  - Plugin de testes: `maven-surefire-plugin:3.2.5`
  - Repositório local em projeto: `maven-repo` para dependências third-party legadas fixadas.
- **Client POM (`j-lawyer-client/pom.xml`)**:
  - Empacotamento: JAR executável (`Main-Class: com.jdimension.jlawyer.client.Main`) com inclusão de classpath `lib/` e execução do plugin `maven-dependency-plugin:copy-dependencies` copiando runtime JARs para `target/lib`.
  - Módulos sombreados inclusos: `j-lawyer-cloud:1.0-SNAPSHOT:shaded` e `j-lawyer-invoicing:1.0-SNAPSHOT:shaded`.
- **Ambiente de Build Verificado**:
  - Maven: `Apache Maven 3.9.9` localizado em `C:\tools\apache-maven-3.9.9\bin\mvn.cmd`
  - Java: `Eclipse Adoptium OpenJDK 17.0.20.1` localizado em `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot`

---

### 2.2. Execução das Suítes de Testes Existentes

Foram executadas as suítes de testes automatizadas via Maven com os seguintes resultados empíricos:

#### 1. Testes de Localização e UI Utils (`j-lawyer-client`)
Comando:
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
& "C:\tools\apache-maven-3.9.9\bin\mvn.cmd" test -pl j-lawyer-client "-Dtest=PtBrLocalizationTest,M1ChallengerStressTest,BrazilianUiUtilsTest,BrazilianUiUtilsChallengerTest"
```
**Resultado**:
- `BrazilianUiUtilsChallengerTest`: 22 testes, 0 falhas, 0 erros (Tempo: 0.813s)
- `BrazilianUiUtilsTest`: 7 testes, 0 falhas, 0 erros (Tempo: 0.001s)
- `M1ChallengerStressTest`: 8 testes, 0 falhas, 0 erros (Tempo: 1.832s)
- `PtBrLocalizationTest`: 5 testes, 0 falhas, 0 erros (Tempo: 0.001s)
- **Total**: 42 testes executados com **100% de sucesso**.

#### 2. Testes de Localização do Gerenciador de Backup (`j-lawyer-backupmgr`)
Comando:
```powershell
& "C:\tools\apache-maven-3.9.9\bin\mvn.cmd" test -pl j-lawyer-backupmgr
```
**Resultado**:
- `BackupMgrChallengerTest`: 1 teste, 0 falhas, 0 erros
- `BackupMgrLocalizationTest`: 1 teste, 0 falhas, 0 erros
- **Total**: 2 testes executados com **100% de sucesso**.

#### 3. Suíte Completa de Testes do Cliente (`j-lawyer-client`)
Comando:
```powershell
& "C:\tools\apache-maven-3.9.9\bin\mvn.cmd" test -pl j-lawyer-client
```
**Resultado**:
- **193 testes executados**, 0 falhas, 0 erros, 0 ignorados (Tempo total: 9.320s).
- Sucesso comprovado cobrindo: `BrazilianE2ETestSuite` (Tiers 1 a 4), `BrazilianWorkflowChallengerStressTest`, `ContactDiffDialogTest`, `DocumentPreviewTest`, `MailTest`, `FileUtilsTest`, `StoredOrderUtilsTest`, `SystemUtilsTest`, `VersionUtilsTest`.

#### 4. Compilação Limpa Completa com Dependências (`mvn clean package -pl j-lawyer-client -am`)
Comando:
```powershell
& "C:\tools\apache-maven-3.9.9\bin\mvn.cmd" clean package -pl j-lawyer-client -am -DskipTests
```
**Resultado**:
- Reator completo de 8 módulos compilado com **100% de sucesso**:
  1. `j-lawyer-parent 2.8.0`: SUCCESS [0.561s]
  2. `j-lawyer-fax 2.8.0`: SUCCESS [6.136s]
  3. `j-lawyer-server-common 2.8.0`: SUCCESS [5.483s]
  4. `j-lawyer-cloud 1.0-SNAPSHOT`: SUCCESS [15.188s]
  5. `j-lawyer-invoicing 1.0-SNAPSHOT`: SUCCESS [01:16 min]
  6. `j-lawyer-server-entities 2.8.0`: SUCCESS [7.575s]
  7. `j-lawyer-server-api 2.8.0`: SUCCESS [2.753s]
  8. `j-lawyer-client 2.8.0`: SUCCESS [01:12 min]
- Tempo total: 03:07 min.
- Artefatos finais gerados: `j-lawyer-client/target/j-lawyer-client.jar` e pasta completa de bibliotecas `j-lawyer-client/target/lib/`.

---

## 3. Identificação de Gaps de Cobertura de Testes e Validação E2E

Apesar de a suíte existente reportar 100% de aprovação (193 testes), a auditoria minuciosa do código revelou **6 gaps críticos de validação**:

### Gap 1: Ausência de Varredura Automatizada de Literais Alemães no Código-Fonte e Forms
- **Problema**: `M1ChallengerStressTest` valida apenas a sintaxe e a existência de arquivos `_pt_BR.properties`. Ele **não inspeciona** o código Java (`.java`) nem os arquivos `.form` em busca de literais alemães residuais hardcoded.
- **Evidência**: Foram encontrados literais como `"Speichern"`, `"Bearbeiten"`, `"Suchen"`, `"Löschen"`, `"Drucken"`, `"Mandant"`, `"Gegner"`, `"Fehler"`, `"Auswertungen"`, `"Nutzer wechseln"`, `"PDF komprimieren"` diretamente em arquivos Java e forms que passam despercebidos pelos testes atuais.
- **Recomendação de Teste**: Criar um teste unitário (`GermanLiteralsScanTest` ou adicionar ao `M1ChallengerStressTest`) que execute regex recursivo em `src/main/java` e `src/main/resources` e falhe caso strings literais alemãs proibidas sejam detectadas em chamadas `setText()`, `setTitle()`, `setToolTipText()`, `JOptionPane` e `.form`.

### Gap 2: Ausência de Verificação de Instalação dos Bundles pt-BR no UIManager em Runtime
- **Problema**: O teste `PtBrLocalizationTest` configura o `UIManager` manualmente em `@BeforeClass` antes de testar `UIManager.getString("OptionPane.yesButtonText")`. Ele **não valida** se a classe de bootstrap da aplicação (`Main.java` ou `JKanzleiGUI.java`) de fato instala os textos pt-BR no `UIManager` na inicialização real do sistema.
- **Evidência**: Se `Main.java` não invocar a inicialização das chaves do `UIManager`, caixas de diálogo padrão do Swing (`JOptionPane`, `JFileChooser`) continuarão exibindo botões em inglês ou no idioma da JVM host.
- **Recomendação de Teste**: Adicionar asserção em teste de bootstrap para verificar se a rotina `initUiDefaults()` / `initFlatLaf()` instala as chaves no `UIManager` sem depender de `@BeforeClass` artificial do teste.

### Gap 3: Ausência de Testes Automatizados de Instanciação de Componentes UI em Modo Headless
- **Problema**: Ao executar `mvn test`, tarefas em segundo plano (`LastChangedTimerTask`, `TaggedTimerTask`, `DesktopPanel`) tentam abrir conexões JNDI e invocar diálogos gráficos que disparam `HeadlessException` e `NoInitialContextException` no `AWT-EventQueue`.
- **Evidência**: O log do Surefire registrou stack traces de `HeadlessException` em `ThreadUtils.showErrorDialog` disparados assincronamente durante a execução dos testes.
- **Recomendação de Teste**: Garantir que componentes Swing instanciados em testes unitários usem mocks/stubs apropriados ou que a suíte controle o ciclo de vida dos timers sem poluir os logs de execução.

### Gap 4: Ausência de Teste para Diálogos Customizados e Opções de JOptionPane
- **Problema**: Vários diálogos utilizam arrays explícitos de botões, como `new String[]{"Ja", "Nein"}` (em `SplashThread.java:1055` e `ArchiveFilePanel.java:6932, 7008`). Os testes atuais não interceptam ou auditam esses arrays.
- **Recomendação de Teste**: Auditar e substituir todas as ocorrências por `new String[]{"Sim", "Não"}` ou constantes localizadas e adicionar verificação estática.

### Gap 5: Cobertura Parcial de ResourceBundles em `PtBrLocalizationTest`
- **Problema**: `PtBrLocalizationTest` lista 42 bases de ResourceBundles hardcoded no array `bundleBases`. Se novos bundles forem adicionados ao projeto, esse teste não os inclui automaticamente.
- **Recomendação de Teste**: Expandir `PtBrLocalizationTest` para usar discovery dinâmico de todos os pares `Bundle.properties` e `Bundle_pt_BR.properties` em todos os módulos (como feito parcialmente em `M1ChallengerStressTest`).

### Gap 6: Validação de Títulos de Janelas e Diálogos de Módulos
- **Problema**: Inúmeros diálogos têm seus títulos definidos imperativamente no código Java via `setTitle("...")` com strings literais em alemão (ex: `dlg.setTitle("Staatsangehörigkeiten")`, `dlg.setTitle("Währungen")`, `dlg.setTitle("Assistent Ingo: Ergebnisse")`).
- **Recomendação de Teste**: Integrar a verificação de títulos à auditoria de strings hardcoded ou migrar todos os títulos de janelas para ResourceBundles (`JKanzleiGUI_pt_BR.properties` e bundles específicos de cada diálogo).

---

## 4. Tabela de Inventário de Ações Recomendadas para Correção

| Componente | Arquivo Alvo | Linha | String Atual (Alemão / Incorreta) | Correção Recomendada (pt-BR) |
|---|---|---|---|---|
| Menu Desktop | `JKanzleiGUI.form` | 119 | `Ansicht` | `Exibir` |
| Menu Desktop | `JKanzleiGUI.form` | 1112 | `Fenster` | `Janela` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2764 | `"Dokumenten-Etiketten"` | `"Etiquetas de Documentos"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2839 | `"Staatsangehörigkeiten"` | `"Nacionalidades"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2847 | `"Rechtsformen"` | `"Tipos Societários / Formas Jurídicas"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2855 | `"akademische Grade (vor dem Namen)"` | `"Títulos Acadêmicos (Antes do Nome)"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2863 | `"akademische Grade (nach dem Namen)"` | `"Títulos Acadêmicos (Depois do Nome)"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2871 | `"Berufe"` | `"Profissões"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2879 | `"Rolle / Funktion"` | `"Papel / Função"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2887 | `"Länder"` | `"Países"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 2895 | `"Titel (Briefkopf)"` | `"Títulos (Cabeçalho)"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 3031 | `"Währungen"` | `"Moedas"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 3046 | `"Steuersätze"` | `"Alíquotas Tributárias"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 3063 | `"Zeiterfassung: mögliche Taktung (Minuten)"` | `"Controle de Horas: Intervalos (Minutos)"` |
| Títulos Diálogos | `JKanzleiGUI.java` | 3219 | `"Bundesländer"` | `"Estados / Unidades Federativas"` |
| Diálogo Login | `LoginDialog.java` | 1673 | `"Profil aus Zwischenablage importieren"` | `"Importar perfil da área de transferência"` |
| Diálogo Login | `LoginDialog.java` | 1988 | `"3. Profil durch scannen des QR-Codes..."` | `"3. Importar perfil escaneando o QR Code"` |
| Diálogo Login | `LoginDialog.java` | 2513 | `"Profil konnte nicht gespeichert werden", "Profil speichern"` | `"Não foi possível salvar o perfil", "Salvar Perfil"` |
| Diálogo Login | `LoginDialog.java` | 2527 | `"Name des Verbindungsprofils: ", "Neues Verbindungsprofil anlegen"` | `"Nome do perfil de conexão: ", "Criar Novo Perfil de Conexão"` |
| Diálogo Login | `LoginDialog.java` | 2544, 2563 | `"Profil konnte nicht hinzugefügt werden", "Profil hinzufügen"` | `"Não foi possível adicionar o perfil", "Adicionar Perfil"` |
| Diálogo Login | `LoginDialog.java` | 2551 | `"Profil aus Zwischenablage einfügen"` | `"Importar Perfil da Área de Transferência"` |
| Trocar Usuário | `UserCredentialsDialog.java` | 721, 724, 727, 759 | `"Nutzer wechseln"`, `"Nutzer:"`, `"Passwort:"`, `"Anwenden"` | `"Trocar Usuário"`, `"Usuário:"`, `"Senha:"`, `"Confirmar"` |
| Splash JOptionPane | `SplashThread.java` | 1055 | `new String[]{"Ja", "Nein"}, "Nein"` | `new String[]{"Sim", "Não"}, "Não"` |
| Autos Processo | `ArchiveFilePanel.java` | 4441, 4475 | Tooltips `"Bearbeiten"`, `"Speichern"` | `"Editar"`, `"Salvar"` |
| Autos Processo | `ArchiveFilePanel.java` | 6932, 7008 | `new String[]{"Ja", "Nein"}, "Nein"` | `new String[]{"Sim", "Não"}, "Não"` |
| Autos Processo | `ArchiveFilePanel.java` | 8025, 8077 | `"Akte"` fallback | `"Processo"` |
| Autos Processo | `ArchiveFilePanel.java` | 8839..9432 | Diálogos JOptionPane com `"Fehler"` e textos em alemão | Textos em português e título `"Erro"` |
| Partes do Processo | `InvolvedPartyEntryPanel.java` | 1103 | `"Mandant"`, `"Gegner"`, `"Dritte"` | `"Cliente"`, `"Parte Contrária"`, `"Terceiros"` |
| Busca Contatos | `AddAddressSearchDialog.java` | 820, 877 | Tooltip `"Suchen"`, ComboBox `"Mandant"`, `"Gegner"`, `"Dritte"` | Tooltip `"Pesquisar"`, `"Cliente"`, `"Parte Contrária"`, `"Terceiros"` |
| Compressão PDF | `ShrinkifyGui.java` | 724, 824..865 | `"PDF komprimieren"`, `"Qualitätsstufe auswählen:"`, `"Komprimieren"` | `"Comprimir PDF"`, `"Nível de qualidade:"`, `"Comprimir"` |
| Relatórios | `ReportingPanel.java` | 1143 | `lblPanelTitle.setText("Auswertungen");` | `lblPanelTitle.setText("Relatórios e Estatísticas");` |
| Itens Relatório | `ReportEntryPanel.java` | 764..773 | `"Name"`, `"Spezial"`, `"Chart"`, `"Tabelle"` | `"Nome"`, `"Especial"`, `"Gráfico"`, `"Tabela"` |
| Assistente Ingo | `AssistantChatDialog.java` | 1019..1072 | Tooltips em alemão (`"Anfrage an Assistent Ingo senden"`, etc.) | Tooltips em português |
| Assistente Ingo | `AssistantExtractPanel.java`| 265 | `lblRequestType = new JLabel("Extrahieren");` | `lblRequestType = new JLabel("Extração");` |
| Assistente Ingo | `AiChatMessageTextSelection.java` | 697 | `setTitle("Textauswahl kopieren");` | `setTitle("Copiar Seleção de Texto");` |
| Backup Manager | `BackupManager.java` | 690 | `primaryStage.setTitle("j-lawyer.org Backupmanager");` | `primaryStage.setTitle("BR-LAWYER Backup Manager");` |

---

## 5. Conclusão da Investigação

A infraestrutura de build e testes do projeto encontra-se **robusta e compilável**:
- A compilação limpa Maven (`mvn clean package -pl j-lawyer-client -am`) conclui com 100% de sucesso em Java 17;
- A suíte de 193 testes unitários e de integração do cliente roda de forma rápida e estável;
- Todas as anomalias textuais e literais residuais do requisito **R3** e os gaps de validação do requisito **R4** foram catalogados com caminhos de arquivos e linhas exatas, fornecendo a base técnica completa para os agentes executores.
