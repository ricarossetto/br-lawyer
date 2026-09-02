import csv
import html
import json
import os
import re

# Load all 4 CSV datasets
def load_csv(name, val_col):
    path = os.path.join('.agents', 'explorer_survey_1', name)
    with open(path, 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        return [{
            'file': r[file_col],
            'line': int(r['Line']),
            'val': r[val_col],
            'prop': r.get('Property', ''),
            'code': r.get('Code', '')
        } for r in reader]

forms = load_csv('raw_form_matches.csv', 'Value')
inits = load_csv('raw_init_matches.csv', 'Str')
jops = load_csv('raw_jop_matches.csv', 'Str')
others = load_csv('raw_other_matches.csv', 'Str')

print(f"Total entries: forms={len(forms)}, inits={len(inits)}, jops={len(jops)}, others={len(others)}")
