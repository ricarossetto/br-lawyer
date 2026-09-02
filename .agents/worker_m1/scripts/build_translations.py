import csv
import json
import re
from collections import defaultdict

def load_csv(path, val_col):
    with open(path, 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        rows = []
        for r in reader:
            rows.append({
                'file': r[file_col],
                'line': int(r['Line']),
                'val': r[val_col],
                'raw': r
            })
        return rows

forms = load_csv('.agents/explorer_survey_1/raw_form_matches.csv', 'Value')
inits = load_csv('.agents/explorer_survey_1/raw_init_matches.csv', 'Str')
jops = load_csv('.agents/explorer_survey_1/raw_jop_matches.csv', 'Str')
others = load_csv('.agents/explorer_survey_1/raw_other_matches.csv', 'Str')

print(f"Loaded {len(forms)} form rows, {len(inits)} init rows, {len(jops)} jop rows, {len(others)} other rows")

# Let's inspect unique strings in forms, inits, jops
form_vals = set(r['val'] for r in forms)
init_vals = set(r['val'] for r in inits)
jop_vals = set(r['val'] for r in jops)

print(f"Unique in forms: {len(form_vals)}, inits: {len(init_vals)}, jops: {len(jop_vals)}")
