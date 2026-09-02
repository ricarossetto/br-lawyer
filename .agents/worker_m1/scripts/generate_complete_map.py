import csv
import html
import json
import os
import re

# Load raw matches
def load_all():
    files = {
        'forms': ('raw_form_matches.csv', 'Value'),
        'inits': ('raw_init_matches.csv', 'Str'),
        'jops': ('raw_jop_matches.csv', 'Str'),
        'others': ('raw_other_matches.csv', 'Str')
    }
    data = {}
    for k, (fn, col) in files.items():
        path = os.path.join('.agents', 'explorer_survey_1', fn)
        with open(path, 'r', encoding='utf-8-sig') as f:
            reader = csv.DictReader(f)
            file_col = [c for c in reader.fieldnames if 'File' in c][0]
            data[k] = [{
                'file': r[file_col],
                'line': int(r['Line']),
                'val': r[col],
                'prop': r.get('Property', ''),
                'code': r.get('Code', '')
            } for r in reader]
    return data

data = load_all()

# Build comprehensive translation table
print("Building comprehensive translation table...")
