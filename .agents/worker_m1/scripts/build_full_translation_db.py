import csv
import html
import json
import re

# Let's load all 4 CSVs and examine all unique values
def load_unique(filename, val_col):
    with open(f'.agents/explorer_survey_1/{filename}', 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        items = []
        for r in reader:
            items.append({
                'file': r[file_col],
                'line': int(r['Line']),
                'val': r[val_col],
                'prop': r.get('Property', ''),
                'code': r.get('Code', '')
            })
        return items

forms = load_unique('raw_form_matches.csv', 'Value')
inits = load_unique('raw_init_matches.csv', 'Str')
jops = load_unique('raw_jop_matches.csv', 'Str')
others = load_unique('raw_other_matches.csv', 'Str')

print(f"Loaded {len(forms)} forms, {len(inits)} inits, {len(jops)} jops, {len(others)} others")
