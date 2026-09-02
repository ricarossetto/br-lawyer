import csv
import os

with open('.agents/explorer_survey_1/raw_init_matches.csv', 'r', encoding='utf-8-sig') as f:
    init_rows = list(csv.DictReader(f))

mismatches = 0
for r in init_rows[:20]:
    fpath = r['File']
    line_no = int(r['Line'])
    expected_str = r['Str']
    
    if os.path.exists(fpath):
        with open(fpath, 'r', encoding='utf-8', errors='replace') as jf:
            lines = jf.readlines()
            if line_no <= len(lines):
                line_content = lines[line_no - 1].strip()
                print(f"File: {os.path.basename(fpath)}:{line_no}")
                print(f"  CSV Str:  {expected_str!r}")
                print(f"  Java Line: {line_content}")
            else:
                print(f"Line {line_no} out of bounds for {fpath}")
