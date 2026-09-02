import csv
import html
import json
import re
from translate_master import EXACT_TRANSLATIONS

def unescape_all(s):
    return html.unescape(s).strip()

def translate_phrase(s):
    if not s or not s.strip():
        return s
    clean = unescape_all(s)
    if clean in EXACT_TRANSLATIONS:
        return EXACT_TRANSLATIONS[clean]
    if s in EXACT_TRANSLATIONS:
        return EXACT_TRANSLATIONS[s]
    return None

# Check forms
with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

untranslated_forms = []
for r in form_rows:
    val = r['Value']
    t = translate_phrase(val)
    if not t:
        untranslated_forms.append((r['File'], int(r['Line']), val, html.unescape(val)))

print(f"Forms total: {len(form_rows)}, untranslated: {len(untranslated_forms)}")
if untranslated_forms:
    print(f"Sample untranslated forms (first 20):")
    for f, l, raw, clean in untranslated_forms[:20]:
        print(f"  {raw!r} (clean: {clean!r}) in {f}:{l}")

# Check inits
with open('.agents/explorer_survey_1/raw_init_matches.csv', 'r', encoding='utf-8-sig') as f:
    init_rows = list(csv.DictReader(f))

untranslated_inits = []
for r in init_rows:
    val = r['Str']
    t = translate_phrase(val)
    if not t:
        untranslated_inits.append((r['File'], int(r['Line']), val))

print(f"Inits total: {len(init_rows)}, untranslated: {len(untranslated_inits)}")

# Check jops
with open('.agents/explorer_survey_1/raw_jop_matches.csv', 'r', encoding='utf-8-sig') as f:
    jop_rows = list(csv.DictReader(f))

untranslated_jops = []
for r in jop_rows:
    val = r['Str']
    t = translate_phrase(val)
    if not t:
        untranslated_jops.append((r['File'], int(r['Line']), val))

print(f"JOPs total: {len(jop_rows)}, untranslated: {len(untranslated_jops)}")
