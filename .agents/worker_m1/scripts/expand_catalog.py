import csv
import html
import json
import os
import re
from collections import defaultdict
from master_ptbr_catalog import CATALOG

def load_csv(name, val_col):
    path = os.path.join('.agents', 'explorer_survey_1', name)
    with open(path, 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        return [{
            'file': r[file_col],
            'line': int(r['Line']),
            'val': r[val_col],
            'clean': html.unescape(r[val_col]).strip()
        } for r in reader]

forms = load_csv('raw_form_matches.csv', 'Value')
inits = load_csv('raw_init_matches.csv', 'Str')
jops = load_csv('raw_jop_matches.csv', 'Str')
others = load_csv('raw_other_matches.csv', 'Str')

unmatched = defaultdict(list)
for item in forms:
    v = item['val']
    c = item['clean']
    if v not in CATALOG and c not in CATALOG:
        unmatched['forms'].append(item)

for item in inits:
    v = item['val']
    c = item['clean']
    if v not in CATALOG and c not in CATALOG:
        unmatched['inits'].append(item)

for item in jops:
    v = item['val']
    c = item['clean']
    if v not in CATALOG and c not in CATALOG:
        unmatched['jops'].append(item)

for item in others:
    v = item['val']
    c = item['clean']
    if v not in CATALOG and c not in CATALOG:
        unmatched['others'].append(item)

print(f"Unmatched forms: {len(unmatched['forms'])} / {len(forms)}")
print(f"Unmatched inits: {len(unmatched['inits'])} / {len(inits)}")
print(f"Unmatched jops: {len(unmatched['jops'])} / {len(jops)}")
print(f"Unmatched others: {len(unmatched['others'])} / {len(others)}")
