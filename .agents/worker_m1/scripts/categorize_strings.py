import json
import re

with open('.agents/worker_m1/all_unique_strings.json', 'r', encoding='utf-8') as f:
    strings = json.load(f)

# Group by patterns / keywords
patterns = {
    'buttons_actions': ['Speichern', 'Abbrechen', 'Schließen', 'Löschen', 'Entfernen', 'Hinzufügen', 'Bearbeiten', 'Drucken', 'Suchen', 'Aktualisieren', 'Auswählen', 'Übernehmen', 'Öffnen', 'Senden', 'Import', 'Export', 'Weiter', 'Zurück', 'Fertig', 'Anlegen', 'Erstellen', 'Neu'],
    'core_entities': ['Akte', 'Akten', 'Mandant', 'Gegner', 'Beteiligte', 'Dokument', 'Dokumente', 'Ordner', 'Konto', 'Rechnung', 'Zahlung', 'Frist', 'Termin', 'Wiedervorlage', 'Adresse', 'Kontakt', 'Gericht', 'Rubrum', 'Vorlage', 'Benutzer', 'Passwort'],
    'messages_errors_hints': ['Fehler', 'Hinweis', 'Warnung', 'Erfolg', 'Erfolgreich', 'konnte nicht', 'fehlgeschlagen', 'ungültig', 'Pflichtfeld', 'Wirklich', 'Möchten', 'Soll', 'Sind Sie sicher', 'Bitte warten'],
    'tool_registry': ['Listet', 'Ruft', 'Sucht', 'Gibt', 'Extrahiert', 'Erstellt', 'Löscht', 'Aktualisiert', 'Durchsucht', 'Berechnet', 'Findet', 'Liefert', 'Parameter', 'Suchbegriff', 'Seitennummer', 'Interne ID'],
    'misc': []
}

categorized = {k: [] for k in patterns}

for s in strings:
    assigned = False
    for cat, keywords in patterns.items():
        if cat == 'misc':
            continue
        if any(kw.lower() in s.lower() for kw in keywords):
            categorized[cat].append(s)
            assigned = True
            break
    if not assigned:
        categorized['misc'].append(s)

print("Categorization summary:")
for k, v in categorized.items():
    print(f"  {k:25s}: {len(v)} strings")

with open('.agents/worker_m1/categorized_strings.json', 'w', encoding='utf-8') as f:
    json.dump(categorized, f, indent=2, ensure_ascii=False)
