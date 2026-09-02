import csv
import html
import json
import re

# Load all 4 CSV files
def get_rows(filename):
    with open(f'.agents/explorer_survey_1/{filename}', 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        val_col = 'Value' if 'Value' in reader.fieldnames else 'Str'
        return [{
            'file': r[file_col],
            'line': int(r['Line']),
            'val': r[val_col],
            'clean': html.unescape(r[val_col]).strip()
        } for r in reader]

forms = get_rows('raw_form_matches.csv')
inits = get_rows('raw_init_matches.csv')
jops = get_rows('raw_jop_matches.csv')
others = get_rows('raw_other_matches.csv')

# Combine all unique clean strings
all_clean = set()
for r in forms + inits + jops + others:
    if r['clean']:
        all_clean.add(r['clean'])

print(f"Total unique clean strings to translate: {len(all_clean)}")

with open('.agents/worker_m1/unique_clean_strings.json', 'w', encoding='utf-8') as f:
    json.dump(sorted(all_clean), f, indent=2, ensure_ascii=False)

print("Saved unique_clean_strings.json")
