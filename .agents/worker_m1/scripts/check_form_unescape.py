import csv
import html
import os
import re

with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

with open('.agents/explorer_survey_1/raw_init_matches.csv', 'r', encoding='utf-8-sig') as f:
    init_rows = list(csv.DictReader(f))

print(f"Total form rows: {len(form_rows)}")
print(f"Total init rows: {len(init_rows)}")

# Let's inspect some examples of form values and unescaped form values
unescaped_forms = {}
for r in form_rows:
    raw_val = r['Value']
    clean_val = html.unescape(raw_val)
    unescaped_forms[raw_val] = clean_val

print("Sample form values unescaped:")
for k in list(unescaped_forms.keys())[:15]:
    print(f"  RAW: {k!r} -> CLEAN: {unescaped_forms[k]!r}")
