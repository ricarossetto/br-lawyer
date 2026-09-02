import csv

with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

with open('.agents/explorer_survey_1/raw_init_matches.csv', 'r', encoding='utf-8-sig') as f:
    init_rows = list(csv.DictReader(f))

form_files = set(r[[c for c in r.keys() if 'File' in c][0]].replace('.form', '.java') for r in form_rows)
init_files = set(r[[c for c in r.keys() if 'File' in c][0]] for r in init_rows)

print(f"Unique form files (as .java): {len(form_files)}")
print(f"Unique init files: {len(init_files)}")
print(f"Intersection: {len(form_files.intersection(init_files))}")
print(f"Init files not in form files: {len(init_files - form_files)}")
print(f"Form files not in init files: {len(form_files - init_files)}")
