import json
import re
import html

with open('.agents/worker_m1/unique_clean_strings.json', 'r', encoding='utf-8') as f:
    strings = json.load(f)

print(f"Loaded {len(strings)} strings to map")
