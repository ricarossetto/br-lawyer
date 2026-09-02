import csv
import html
import os
import json
from collections import defaultdict

def read_csv_rows(filename):
    path = os.path.join('.agents', 'explorer_survey_1', filename)
    with open(path, 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        rows = []
        for r in reader:
            rows.append({
                'file': r[file_col],
                'line': int(r['Line']),
                'prop': r.get('Property', ''),
                'val': r.get('Value') or r.get('Str', ''),
                'code': r.get('Code', '')
            })
        return rows

forms = read_csv_rows('raw_form_matches.csv')
inits = read_csv_rows('raw_init_matches.csv')
jops = read_csv_rows('raw_jop_matches.csv')
others = read_csv_rows('raw_other_matches.csv')

print(f"Forms: {len(forms)}, Inits: {len(inits)}, JOPs: {len(jops)}, Others: {len(others)}")

# Let's inspect unique values
unique_all = defaultdict(list)
for item in forms:
    val = html.unescape(item['val']).strip()
    unique_all[val].append(('form', item['file'], item['line']))

for item in inits:
    val = item['val'].strip()
    unique_all[val].append(('init', item['file'], item['line']))

for item in jops:
    val = item['val'].strip()
    unique_all[val].append(('jop', item['file'], item['line']))

for item in others:
    val = item['val'].strip()
    unique_all[val].append(('other', item['file'], item['line']))

print(f"Total unique strings across all CSVs: {len(unique_all)}")

# Save to inventory JSON
with open('.agents/worker_m1/inventory.json', 'w', encoding='utf-8') as f:
    json.dump({k: len(v) for k, v in sorted(unique_all.items())}, f, indent=2, ensure_ascii=False)

print("Saved inventory.json")
