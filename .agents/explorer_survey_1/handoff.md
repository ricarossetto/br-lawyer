# Handoff Report — Survey R1: Forms & Java UI Localization

## 1. Observation

A systematic, automated and manual read-only investigation was conducted across `j-lawyer-client`, `j-lawyer-backupmgr`, and `j-lawyer-io-common`.

Key verified observations:
- **Total Production Java Files**: 814 files (806 in `j-lawyer-client`, 7 in `j-lawyer-backupmgr`, 1 in `j-lawyer-io-common`).
- **Total NetBeans `.form` Files**: 297 files (all 297 located in `j-lawyer-client`).
- **NetBeans `.form` Properties with German Strings**: 624 occurrences across 187 `.form` files (`raw_form_matches.csv`).
- **`initComponents()` Hardcoded German Strings**: 738 occurrences across 201 `.java` files (`raw_init_matches.csv`).
- **`JOptionPane` Invocations with German Strings**: 601 occurrences across 151 `.java` files (`raw_jop_matches.csv`).
- **Other Java UI German Strings** (Action Listeners, Popups, Custom Models, ToolRegistry, RestoreExecutor): 1,461 occurrences across 237 `.java` files (`raw_other_matches.csv`).
- **Total Unique Files with German UI Strings**: 527 unique files (340 `.java` files + 187 `.form` files).
- **`j-lawyer-backupmgr` Findings**: `RestoreExecutor.java` (lines 775, 782, 819, 822, 851, 868, 925, 969, 983, 987, 991, 995, 1009, 1013, 1020, 1024, 1028, 1032, 1036, 1054, 1068, 1121, 1197, 1220, 1282) throws exceptions and sends progress callbacks with German strings that are directly displayed to the user via `BackupMgrController.java:81-127` (`lblProgress.setText(...)`).
- **`j-lawyer-io-common` Findings**: `Monitor.java` is a pure technical metrics POJO with 0 UI strings and 0 German texts.

## 2. Logic Chain

1. **GUI Builder Coupling**: NetBeans `.form` XML files store GUI declarations. During build or IDE design, NetBeans synchronizes `.form` XML with `initComponents()` in Java. If a change is made to `.form` only, Maven will compile whatever is currently in `.java`. If a change is made to `.java` only, opening the `.form` in GUI Builder reverts the `.java` code. Therefore, replacements in `.form` properties and `initComponents()` must be performed simultaneously.
2. **Dynamic UI vs. ResourceBundles**: Many dialogs and action handlers instantiate `JOptionPane` and `JButton` directly using hardcoded strings (e.g. `JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Akte", "Fehler", JOptionPane.ERROR_MESSAGE)` and `new String[]{"Ja", "Nein", "Abbrechen"}`). These bypass `ResourceBundle.getString(...)` and must be translated directly in the Java source files.
3. **Subsystem Clustering**: The 3,424 occurrences are clustered into 14 distinct functional subsystems, with the highest concentrations in Cases & Files Management (`ArchiveFilePanel` - 295 matches Java, 52 matches .form), AI Assistant Ingo (`ToolRegistry` - 350 matches), Configuration (`UserAdministrationDialog`, `MailboxSetupDialog`), and Communications (`SendEmailFrame`, `BeaInboxPanel`).
4. **Safety & Integrity**: The translations follow the official ATRIUM canonical terminology dictionary (e.g., `Processo`, `Cliente`, `Parte Contrária`, `Lembrete`, `Prazo`, `Audiência`, `Salvar`, `Cancelar`), avoiding competitor terminology and maintaining standard legal concepts in pt-BR.

## 3. Caveats

- Investigation is read-only; no production files were modified during this survey.
- Backend services outside `j-lawyer-client`, `j-lawyer-backupmgr`, and `j-lawyer-io-common` (such as `j-lawyer-server` and `j-lawyer-ai`) were not in the scope of R1, although `ToolRegistry.java` in `j-lawyer-client` sends tool descriptions to `j-lawyer-ai`.
- Tests in `src/test/java` were excluded from UI replacement statistics as they are addressed under R4.

## 4. Conclusion

The R1 survey is 100% complete and exhaustive. All 3,424 German string occurrences across 527 unique files have been cataloged with exact line numbers, code snippets, context classifications, and recommended pt-BR translations. The full report is available at `c:\projetos IA\BR-LAWYER\br-lawyer\.agents\explorer_survey_1\survey_forms.md`, accompanied by structured datasets (`raw_form_matches.csv`, `raw_init_matches.csv`, `raw_jop_matches.csv`, `raw_other_matches.csv`).

## 5. Verification Method

To verify findings independently:
1. Inspect `survey_forms.md` and CSV datasets in `c:\projetos IA\BR-LAWYER\br-lawyer\.agents\explorer_survey_1\`.
2. Run ripgrep / git grep searches for German words:
   ```bash
   git grep -n -E "Abbrechen|Speichern|Schlie.en|L.schen|Hinzuf.gen|Bearbeiten|Drucken" -- "j-lawyer-client/src/main/java" "j-lawyer-client/src/main/resources"
   ```
3. Verify `RestoreExecutor.java` German strings:
   ```bash
   git grep -n -E "Pr.fe|Wiederherstellung|Datenbank|Verzeichnis" -- "j-lawyer-backupmgr/src/main/java"
   ```
