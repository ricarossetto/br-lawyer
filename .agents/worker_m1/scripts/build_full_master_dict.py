import csv
import html
import json
import os
import re

# Load all 4 CSVs
def load_csv(filename, val_col):
    path = os.path.join('.agents', 'explorer_survey_1', filename)
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

print(f"Forms: {len(forms)}, Inits: {len(inits)}, JOPs: {len(jops)}, Others: {len(others)}")
