import csv
import html
import os
import re

with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

file_col = [c for c in form_rows[0].keys() if 'File' in c][0]

remaining_matches = []
for r in form_rows:
    fpath = r[file_col]
    val = r['Value']
    clean = html.unescape(val).strip()

    if os.path.exists(fpath):
        with open(fpath, 'r', encoding='utf-8') as f:
            content = f.read()
            if f'value="{val}"' in content or f'value="{clean}"' in content or f'title="{val}"' in content or f'tabTitle="{val}"' in content or f'toolTipText="{val}"' in content:
                remaining_matches.append((fpath, int(r['Line']), val, clean))

print(f"Total form matches originally: {len(form_rows)}")
print(f"Remaining unmatched in .form files: {len(remaining_matches)}")

with open('.agents/worker_m1/remaining_form_matches.json', 'w', encoding='utf-8') as f:
    import json
    json.dump([{
        'file': m[0],
        'line': m[1],
        'val': m[2],
        'clean': m[3]
    } for m in remaining_matches], f, indent=2, ensure_ascii=False)

print(f"Saved remaining_form_matches.json")
