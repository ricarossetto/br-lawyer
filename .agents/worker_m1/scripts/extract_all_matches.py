import csv
import json

def extract_csv(filename, str_col):
    with open(f'.agents/explorer_survey_1/{filename}', 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        file_col = [c for c in reader.fieldnames if 'File' in c][0]
        results = {}
        for r in reader:
            val = r[str_col]
            if val not in results:
                results[val] = []
            results[val].append({
                'file': r[file_col],
                'line': int(r['Line']),
                'code': r.get('Code', '')
            })
        return results

init_dict = extract_csv('raw_init_matches.csv', 'Str')
jop_dict = extract_csv('raw_jop_matches.csv', 'Str')
other_dict = extract_csv('raw_other_matches.csv', 'Str')

with open('.agents/worker_m1/init_values.json', 'w', encoding='utf-8') as f:
    json.dump(init_dict, f, indent=2, ensure_ascii=False)

with open('.agents/worker_m1/jop_values.json', 'w', encoding='utf-8') as f:
    json.dump(jop_dict, f, indent=2, ensure_ascii=False)

with open('.agents/worker_m1/other_values.json', 'w', encoding='utf-8') as f:
    json.dump(other_dict, f, indent=2, ensure_ascii=False)

print(f"Unique inits: {len(init_dict)}")
print(f"Unique jops: {len(jop_dict)}")
print(f"Unique others: {len(other_dict)}")
