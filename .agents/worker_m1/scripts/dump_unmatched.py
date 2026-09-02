import csv
import html
import json
import os
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

def dump_unique_unmatched(items, outfile):
    unm = {}
    for it in items:
        v = it['val']
        c = it['clean']
        if v not in CATALOG and c not in CATALOG:
            if c not in unm:
                unm[c] = {
                    'raw_samples': set(),
                    'files': set()
                }
            unm[c]['raw_samples'].add(v)
            unm[c]['files'].add(f"{it['file']}:{it['line']}")
    
    out_dict = {}
    for c, data in sorted(unm.items()):
        out_dict[c] = {
            'raw': list(data['raw_samples']),
            'files': list(data['files'])[:5]
        }
    with open(outfile, 'w', encoding='utf-8') as f:
        json.dump(out_dict, f, indent=2, ensure_ascii=False)
    print(f"Saved {len(out_dict)} items to {outfile}")

dump_unique_unmatched(forms, '.agents/worker_m1/unmatched_forms.json')
dump_unique_unmatched(inits, '.agents/worker_m1/unmatched_inits.json')
dump_unique_unmatched(jops, '.agents/worker_m1/unmatched_jops.json')
dump_unique_unmatched(others, '.agents/worker_m1/unmatched_others.json')
