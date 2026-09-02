import json
import html
import re

with open('.agents/worker_m1/form_values.json', 'r', encoding='utf-8') as f:
    form_data = json.load(f)

with open('.agents/worker_m1/init_values.json', 'r', encoding='utf-8') as f:
    init_data = json.load(f)

with open('.agents/worker_m1/jop_values.json', 'r', encoding='utf-8') as f:
    jop_data = json.load(f)

with open('.agents/worker_m1/other_values.json', 'r', encoding='utf-8') as f:
    other_data = json.load(f)

all_strings = set()

# Raw and unescaped form strings
for raw_k, info in form_data.items():
    all_strings.add(raw_k)
    all_strings.add(info['unescaped'])

# Init strings
for k in init_data.keys():
    all_strings.add(k)

# Jop strings
for k in jop_data.keys():
    all_strings.add(k)

# Other strings
for k in other_data.keys():
    all_strings.add(k)

print(f"Total unique raw/clean strings across all sets: {len(all_strings)}")

# Filter out empty or whitespace-only strings
cleaned_strings = sorted(s for s in all_strings if s.strip())
print(f"Non-empty unique strings: {len(cleaned_strings)}")

with open('.agents/worker_m1/all_unique_strings.json', 'w', encoding='utf-8') as f:
    json.dump(cleaned_strings, f, indent=2, ensure_ascii=False)

print("Saved all_unique_strings.json")
