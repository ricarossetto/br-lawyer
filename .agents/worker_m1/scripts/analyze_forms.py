import csv
import html
from collections import Counter

with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

file_col = [c for c in form_rows[0].keys() if 'File' in c][0]
props = Counter(r['Property'] for r in form_rows)
print("Form properties count:")
for p, c in props.most_common():
    print(f"  {p:20s}: {c}")

files = set(r[file_col] for r in form_rows)
print(f"Total unique form files: {len(files)}")
