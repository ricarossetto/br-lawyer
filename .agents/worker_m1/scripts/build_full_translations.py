import json
import re
import html
import os

# Load all unmatched files
with open('.agents/worker_m1/unmatched_forms.json', 'r', encoding='utf-8') as f:
    unmatched_forms = json.load(f)

with open('.agents/worker_m1/unmatched_inits.json', 'r', encoding='utf-8') as f:
    unmatched_inits = json.load(f)

with open('.agents/worker_m1/unmatched_jops.json', 'r', encoding='utf-8') as f:
    unmatched_jops = json.load(f)

with open('.agents/worker_m1/unmatched_others.json', 'r', encoding='utf-8') as f:
    unmatched_others = json.load(f)

print(f"Unmatched counts: forms={len(unmatched_forms)}, inits={len(unmatched_inits)}, jops={len(unmatched_jops)}, others={len(unmatched_others)}")
