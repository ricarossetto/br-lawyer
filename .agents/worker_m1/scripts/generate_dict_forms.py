import csv
import html
import json

with open('.agents/worker_m1/form_values.json', 'r', encoding='utf-8') as f:
    form_data = json.load(f)

# Sort by length descending so longer phrases match first if needed
raw_keys = sorted(form_data.keys(), key=lambda k: len(k), reverse=True)
print(f"Total form keys to map: {len(raw_keys)}")
