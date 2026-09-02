import csv
import html
import json

with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

file_col = [c for c in form_rows[0].keys() if 'File' in c][0]

unique_form_values = {}
for r in form_rows:
    raw_val = r['Value']
    unesc = html.unescape(raw_val)
    if raw_val not in unique_form_values:
        unique_form_values[raw_val] = {
            'unescaped': unesc,
            'occurrences': []
        }
    unique_form_values[raw_val]['occurrences'].append({
        'file': r[file_col],
        'line': int(r['Line']),
        'property': r['Property']
    })

print(f"Total unique raw form values: {len(unique_form_values)}")

with open('.agents/worker_m1/form_values.json', 'w', encoding='utf-8') as f:
    json.dump(unique_form_values, f, indent=2, ensure_ascii=False)

print("Saved form_values.json")
