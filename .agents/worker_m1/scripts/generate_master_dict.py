import csv
import html
import json
import re

# Load all strings from 4 CSV files
def load_all_survey_data():
    data = {}
    for name, val_col in [('raw_form_matches.csv', 'Value'),
                          ('raw_init_matches.csv', 'Str'),
                          ('raw_jop_matches.csv', 'Str'),
                          ('raw_other_matches.csv', 'Str')]:
        with open(f'.agents/explorer_survey_1/{name}', 'r', encoding='utf-8-sig') as f:
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
            data[name] = items
    return data

survey = load_all_survey_data()
print("Survey data loaded")
