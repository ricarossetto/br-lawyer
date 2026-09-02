import json
import re

# Let's inspect all unique strings and build a comprehensive translation mapping
with open('.agents/worker_m1/form_values.json', 'r', encoding='utf-8') as f:
    form_data = json.load(f)

with open('.agents/worker_m1/init_values.json', 'r', encoding='utf-8') as f:
    init_data = json.load(f)

with open('.agents/worker_m1/jop_values.json', 'r', encoding='utf-8') as f:
    jop_data = json.load(f)

with open('.agents/worker_m1/other_values.json', 'r', encoding='utf-8') as f:
    other_data = json.load(f)

print(f"Forms unique: {len(form_data)}")
print(f"Inits unique: {len(init_data)}")
print(f"JOPs unique: {len(jop_data)}")
print(f"Others unique: {len(other_data)}")
