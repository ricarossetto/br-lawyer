# Project: br-lawyer pt-BR Localization

## Architecture
- **Desktop Client (`j-lawyer-client`)**: Java Swing application with FlatLaf styling, NetBeans `.form` GUI builder files, and dynamic action listeners. Central entry point `Main.java`, main shell `JKanzleiGUI.java`, and hundreds of editor/dialog panels.
- **Backup Manager (`j-lawyer-backupmgr`)**: JavaFX/Swing hybrid utility for database and file backup/restore with localized progress reporting and UI controls.
- **I/O Common (`j-lawyer-io-common`)**: Shared technical and I/O metrics classes.
- **ResourceBundles**: Standard Java `ResourceBundle` hierarchy with default root bundles (`Bundle.properties`, `Messages.properties`) and Brazilian Portuguese bundles (`Bundle_pt_BR.properties`, `Messages_pt_BR.properties`).
- **Data Flow & Localization Strategy**:
  1. Default JVM Locale set to `pt_BR` on startup in `Main.java` and `BackupManager.java`.
  2. Direct string literals in `initComponents()`, `.form` XML definitions, `JOptionPane`, and action handlers are localized into canonical Brazilian legal Portuguese (pt-BR).
  3. All root resource bundles are synchronized 100% with `_pt_BR.properties` so lookups without locale fallback cleanly to Portuguese without German leakage.

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Swing Forms (.form XML) | Replace hardcoded German strings in NetBeans `.form` XML definitions across 187 files | M1 | survey (Explorer 1) |
| 2 | Generated initComponents() | Replace hardcoded German strings in Java `initComponents()` across 201 files | M1 | survey (Explorer 1) |
| 3 | Java Dialogs & JOptionPane | Replace German title, message, and option strings ("Ja", "Nein", "Fehler", etc.) in dialogs across 151 files | M1 | survey (Explorer 1) |
| 4 | Action Listeners & Models | Replace German text in dynamic UI components, table/combo models, and progress callbacks | M1 | survey (Explorer 1) |
| 5 | Backup Manager Strings | Replace German strings in `RestoreExecutor.java` and `BackupManager.java` | M1 | survey (Explorer 1, 3) |
| 6 | Resource Bundle Synchronization | Ensure 100% parity between root `.properties` and `_pt_BR.properties` across all 43 bundle families | M2 | survey (Explorer 2) |
| 7 | Fallback Locale Mechanics | Guarantee JVM default locale `pt_BR` and fallback behavior without German leakage | M2 | survey (Explorer 2) |
| 8 | Desktop Menus & Shortcuts | Localize `JKanzleiGUI.form` and `JKanzleiGUI.java` menu bar, submenus, accelerators | M3 | survey (Explorer 3) |
| 9 | Login & Splash Screen | Localize `LoginDialog.java`, `UserCredentialsDialog.java`, and `SplashThread.java` | M3 | survey (Explorer 3) |
| 10 | Case Files & Party Dialogs | Localize `ArchiveFilePanel.java`, `AddAddressSearchDialog.java`, `InvolvedPartyEntryPanel.java` | M3 | survey (Explorer 3) |
| 11 | Viewers & Compression UI | Localize `MarkdownPanel.java` / `.form`, `ShrinkifyGui.java` (PDF compression) | M3 | survey (Explorer 3) |
| 12 | Reporting & Statistics | Localize `ReportingPanel.java`, `ReportEntryPanel.java` | M3 | survey (Explorer 3) |
| 13 | AI Assistant (Ingo) | Localize `AssistantChatDialog.java`, `AssistantExtractPanel.java`, `AssistantGenerateDialog.java`, `ToolRegistry.java` | M3 | survey (Explorer 3) |
| 14 | Window & Dialog Titles | Localize all system popup titles in `JKanzleiGUI.java` (Etiquetas, Nacionalidades, Formas Jurídicas, etc.) | M3 | survey (Explorer 3) |
| 15 | Maven Build & Test Suite | Execute `mvn clean package -pl j-lawyer-client -am` and `mvn test` verifying 0 errors, 0 failures | M4 | survey (Explorer 3) |
| 16 | UI & Localization Test Harnesses | Validate `PtBrLocalizationTest`, `M1ChallengerStressTest`, `BrazilianUiUtilsTest`, `BackupMgrLocalizationTest` | M4 | survey (Explorer 3) |
| 17 | Static German Residual Scanner | Add static scan test to guarantee 0 residual German strings across all production source files | M4 | survey (Explorer 3) |
| 18 | Adversarial UI Stress Testing | Execute Challenger and Forensic Integrity Audit to verify genuine localization | M4 | survey (Explorer 3) |

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| M1 | Swing Forms, initComponents & Dialogs (R1) | Replace hardcoded German strings in `.form` XML, `initComponents()`, `JOptionPane`, dynamic dialogs, and `RestoreExecutor.java` | none | IN_PROGRESS |
| M2 | Root Resource Bundles & Fallbacks (R2) | Audit and guarantee 100% synchronization and parity of root bundles and `_pt_BR.properties` across all modules | none | VERIFIED_PARITY |
| M3 | Menus, Splash, Login, Viewers & System Titles (R3) | Localize desktop menu bar, splash, login, case panels, PDF/image viewers, Ingo AI, and window titles | M1, M2 | PLANNED |
| M4 | E2E Testing, Maven Verification & Forensic Audit (R4) | Execute full Maven builds, expand test harness with static German residual scanner, run challenger stress tests, and verify 100% clean audit | M1, M2, M3 | PLANNED |

## Interface Contracts
### Canonical Terminology Dictionary (pt-BR)
- `Abbrechen` → `Cancelar`
- `Speichern` → `Salvar`
- `Schließen` → `Fechar`
- `Suchen` → `Pesquisar`
- `Löschen` → `Excluir`
- `Hinzufügen` → `Adicionar`
- `Bearbeiten` → `Editar`
- `Drucken` → `Imprimir`
- `Mandant` → `Cliente`
- `Gegner` → `Parte Contrária`
- `Dritte` → `Terceiros`
- `Akte` → `Processo`
- `Dokument` → `Documento`
- `Ja` / `Nein` → `Sim` / `Não`
- `Fehler` → `Erro`
- `Erfolg` / `Erfolgreich` → `Sucesso` / `Com sucesso`
- `Einstellungen` → `Configurações`
- `Ansicht` → `Exibir`
- `Fenster` → `Janela`
- `Hilfe` → `Ajuda`
- `Vorschau` → `Pré-visualização`

## Code Layout
- `j-lawyer-client/src/main/java/` — Java Swing client source files
- `j-lawyer-client/src/main/resources/` — NetBeans `.form` XML files and `.properties` resource bundles
- `j-lawyer-client/src/test/java/` — Client unit and localization test suites
- `j-lawyer-backupmgr/src/main/java/` — Backup manager source files
- `j-lawyer-backupmgr/src/main/resources/` — Backup manager resource bundles
- `j-lawyer-backupmgr/src/test/java/` — Backup manager unit test suites
- `j-lawyer-io-common/src/main/java/` — Shared I/O components
