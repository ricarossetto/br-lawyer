import os
import re

PATTERNS = [
    (r'"Abbrechen"', 'Abbrechen literal'),
    (r'"Speichern"', 'Speichern literal'),
    (r'"Schließen"', 'Schließen literal'),
    (r'"Schliessen"', 'Schliessen literal'),
    (r'"Suchen"', 'Suchen literal'),
    (r'"Löschen"', 'Löschen literal'),
    (r'"Entfernen"', 'Entfernen literal'),
    (r'"Hinzufügen"', 'Hinzufügen literal'),
    (r'"Bearbeiten"', 'Bearbeiten literal'),
    (r'"Drucken"', 'Drucken literal'),
    (r'"Vorschau"', 'Vorschau literal'),
    (r'"Ansicht"', 'Ansicht literal'),
    (r'"Fenster"', 'Fenster literal'),
    (r'"Hilfe"', 'Hilfe literal'),
    (r'"Einstellungen"', 'Einstellungen literal'),
    (r'"Übernehmen"', 'Übernehmen literal'),
    (r'"Mandant"', 'Mandant literal'),
    (r'"Gegner"', 'Gegner literal'),
    (r'"Dritte"', 'Dritte literal'),
    (r'"Akte"', 'Akte literal'),
    (r'"Akten"', 'Akten literal'),
    (r'"Dokument"', 'Dokument literal'),
    (r'"Dokumente"', 'Dokumente literal'),
    (r'"Fehler"', 'Fehler literal'),
    (r'"Erfolg"', 'Erfolg literal'),
    (r'"Nutzer wechseln"', 'Nutzer wechseln literal'),
    (r'"PDF komprimieren"', 'PDF komprimieren literal'),
    (r'"Auswertungen"', 'Auswertungen literal'),
    (r'new String\[\]\s*\{\s*"Ja",\s*"Nein"\s*\}', 'Ja/Nein array'),
    (r'value="Abbrechen"', 'value Abbrechen'),
    (r'value="Speichern"', 'value Speichern'),
    (r'value="Schließen"', 'value Schließen'),
    (r'value="Suchen"', 'value Suchen'),
    (r'value="Löschen"', 'value Löschen'),
    (r'value="Bearbeiten"', 'value Bearbeiten'),
    (r'value="Drucken"', 'value Drucken'),
    (r'value="Ansicht"', 'value Ansicht'),
    (r'value="Fenster"', 'value Fenster'),
    (r'value="Hilfe"', 'value Hilfe'),
    (r'tabTitle="Vorschau"', 'tabTitle Vorschau'),
    (r'tabTitle="Einstellungen"', 'tabTitle Einstellungen'),
    (r'tabTitle="Login"', 'tabTitle Login'),
    (r'tabTitle="Profile"', 'tabTitle Profile'),
    (r'"Dokumenten-Etiketten"', 'Dokumenten-Etiketten'),
    (r'"Staatsangehörigkeiten"', 'Staatsangehörigkeiten'),
    (r'"Rechtsformen"', 'Rechtsformen'),
    (r'"akademische Grade', 'akademische Grade'),
    (r'"Berufe"', 'Berufe'),
    (r'"Rolle / Funktion"', 'Rolle / Funktion'),
    (r'"Länder"', 'Länder'),
    (r'"Titel \(Briefkopf\)"', 'Titel (Briefkopf)'),
    (r'"Währungen"', 'Währungen'),
    (r'"Steuersätze"', 'Steuersätze'),
    (r'"Zeiterfassung: mögliche Taktung', 'Zeiterfassung: mögliche Taktung'),
    (r'"Bundesländer"', 'Bundesländer'),
    (r'"j-lawyer.org Login"', 'j-lawyer.org Login'),
    (r'"Profil aus Zwischenablage importieren"', 'Profil aus Zwischenablage importieren'),
    (r'"3. Profil durch scannen des QR-Codes', '3. Profil durch scannen des QR-Codes'),
    (r'"Profil konnte nicht gespeichert werden"', 'Profil konnte nicht gespeichert werden'),
    (r'"Name des Verbindungsprofils: "', 'Name des Verbindungsprofils'),
    (r'"Neues Verbindungsprofil anlegen"', 'Neues Verbindungsprofil anlegen'),
    (r'"Profil konnte nicht hinzugefügt werden"', 'Profil konnte nicht hinzugefügt werden'),
    (r'"Profil aus Zwischenablage einfügen"', 'Profil aus Zwischenablage einfügen'),
    (r'"Nutzer:"', 'Nutzer: label'),
    (r'"Anwenden"', 'Anwenden button'),
    (r'"Extrahieren"', 'Extrahieren label'),
    (r'"Textauswahl kopieren"', 'Textauswahl kopieren title'),
    (r'"j-lawyer.org Backupmanager"', 'j-lawyer.org Backupmanager title'),
]

found = []
for root_dir in ['j-lawyer-client/src/main', 'j-lawyer-backupmgr/src/main']:
    for root, dirs, files in os.walk(root_dir):
        for f in files:
            if f.endswith('.java') or f.endswith('.form'):
                filepath = os.path.join(root, f)
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as fp:
                    content = fp.read()
                for p, desc in PATTERNS:
                    matches = list(re.finditer(p, content))
                    if matches:
                        for m in matches:
                            line_no = content[:m.start()].count('\n') + 1
                            found.append((filepath, line_no, desc, m.group(0)))

print(f"Total matching items found: {len(found)}")
for fp, line, desc, val in found:
    print(f"{fp}:{line}: [{desc}] -> {val}")
