import json
import html
import os

with open('.agents/worker_m1/form_values.json', 'r', encoding='utf-8') as f:
    form_data = json.load(f)

# Let's inspect all 452 raw form values and their file occurrences
print(f"Total raw form values: {len(form_data)}")
