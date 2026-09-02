import json
import csv
import html
import re

# Load all 4 CSV datasets
with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

with open('.agents/explorer_survey_1/raw_init_matches.csv', 'r', encoding='utf-8-sig') as f:
    init_rows = list(csv.DictReader(f))

with open('.agents/explorer_survey_1/raw_jop_matches.csv', 'r', encoding='utf-8-sig') as f:
    jop_rows = list(csv.DictReader(f))

with open('.agents/explorer_survey_1/raw_other_matches.csv', 'r', encoding='utf-8-sig') as f:
    other_rows = list(csv.DictReader(f))

print(f"Loaded: forms={len(form_rows)}, inits={len(init_rows)}, jops={len(jop_rows)}, others={len(other_rows)}")
