import json

with open('.agents/worker_m1/categorized_strings.json', 'r', encoding='utf-8') as f:
    cat = json.load(f)

misc = cat['misc']
print(f"Misc strings ({len(misc)}):")
for s in misc[:50]:
    print(" -", s)
