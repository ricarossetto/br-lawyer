# DISPATCH — Worker Gen 2 (Milestones M1, M3, M4 Implementation & Build Verification)

## Mission
Complete and verify the end-to-end pt-BR localization across `j-lawyer-client` and `j-lawyer-backupmgr`:
1. **R1 / M1**: Audit & replace hardcoded German strings in NetBeans `.form` XML definitions, Java `initComponents()`, `JOptionPane` dialogs, and `RestoreExecutor.java` in `j-lawyer-backupmgr`.
2. **R2 / M2**: Verify 100% parity of root bundles and `_pt_BR.properties`.
3. **R3 / M3**: Localize desktop menu bar (`Ansicht` -> `Exibir`, `Fenster` -> `Janela`), login & splash screens, case file dialogs (`Mandant`, `Gegner`, `Dritte`, `Akte`), viewers (`Vorschau`), reporting panels, Ingo AI assistant dialogs, and system window titles in `JKanzleiGUI.java`.
4. **R4 / M4**: Build & test verification:
   - Run unit and localization tests: `mvn test -pl j-lawyer-client "-Dtest=PtBrLocalizationTest,M1ChallengerStressTest,BrazilianUiUtilsTest,BrazilianUiUtilsChallengerTest"`
   - Run backup manager tests: `mvn test -pl j-lawyer-backupmgr`
   - Add/verify static residual scanner test if applicable
   - Run full reactor build: `mvn clean package -pl j-lawyer-client -am`
   - Ensure 0 errors, 0 failures, 100% green build.

## Mandatory Reading & Reference Files
- `c:\projetos IA\BR-LAWYER\br-lawyer\.agents\ORIGINAL_REQUEST.md` (Verbatim user requirements)
- `c:\projetos IA\BR-LAWYER\br-lawyer\AGENTS.md` (Project guardrails)
- `c:\projetos IA\BR-LAWYER\br-lawyer\PROJECT.md` (Architecture, Feature inventory, Terminology dictionary)
- `c:\projetos IA\BR-LAWYER\br-lawyer\TEST_INFRA.md` (Test architecture & commands)
- `c:\projetos IA\BR-LAWYER\br-lawyer\.agents\explorer_survey_1\survey_forms.md` (R1 survey catalog)
- `c:\projetos IA\BR-LAWYER\br-lawyer\.agents\explorer_survey_2\survey_bundles.md` (R2 bundle survey catalog)
- `c:\projetos IA\BR-LAWYER\br-lawyer\.agents\explorer_survey_3\survey_ui_build.md` (R3/R4 survey catalog & line numbers)
- Existing survey and translation datasets in `.agents/worker_m1/` and `.agents/explorer_survey_1/`

## Mandatory Integrity Warning
DO NOT CHEAT. All implementations must be genuine. DO NOT hardcode test results, create dummy/facade implementations, or circumvent the intended task. A teamwork_preview_auditor will independently verify your work. Integrity violations WILL be detected and your work WILL be rejected.

## Environment & Tooling
- JDK: `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot`
- Maven: `C:\tools\apache-maven-3.9.9\bin\mvn.cmd`
- PowerShell commands should set `$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"`

## Deliverables in `.agents/worker_gen2_1/`
- `BRIEFING.md`
- `progress.md` (with regular heartbeat updates)
- `changes.md` (complete file-by-file log of changes)
- `handoff.md` (structured handoff report with build & test verification output)

## 2026-09-02T15:49:17Z
You are worker_gen2_1, a teamwork_preview_worker assigned to complete and verify the full pt-BR localization for br-lawyer across all modules.
Working directory: c:\projetos IA\BR-LAWYER\br-lawyer\.agents\worker_gen2_1

