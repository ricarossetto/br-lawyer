import json
import re
import html

# Load all unique strings
with open('.agents/worker_m1/unique_clean_strings.json', 'r', encoding='utf-8') as f:
    unique_strings = json.load(f)

print(f"Total unique strings: {len(unique_strings)}")
