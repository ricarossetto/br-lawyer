import csv
import os
import html

with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

file_col = [c for c in form_rows[0].keys() if 'File' in c][0]

forms_by_file = {}
for r in form_rows:
    fpath = r[file_col]
    if fpath not in forms_by_file:
        forms_by_file[fpath] = []
    forms_by_file[fpath].append({
        'line': int(r['Line']),
        'property': r['Property'],
        'value': r['Value'],
        'clean': html.unescape(r['Value']).strip()
    })

print(f"Total .form files with matches: {len(forms_by_file)}")

# Check if corresponding .java file exists
missing_java = []
for fpath in forms_by_file:
    java_path = fpath.replace('.form', '.java')
    if not os.path.exists(java_path):
        # check in src/main/java if .form was in resources
        if 'src/main/resources' in fpath:
            alt_path = fpath.replace('src/main/resources', 'src/main/java').replace('.form', '.java')
            if os.path.exists(alt_path):
                continue
        missing_java.append(fpath)

print(f"Forms with missing .java: {len(missing_java)}")
if missing_java:
    print("Missing:", missing_java)
