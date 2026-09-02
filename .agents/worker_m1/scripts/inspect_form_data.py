import json
import html

with open('.agents/worker_m1/form_values.json', 'r', encoding='utf-8') as f:
    form_data = json.load(f)

print(f"Total form values: {len(form_data)}")
for k, v in list(form_data.items())[:30]:
    print(f"RAW: {k!r} -> CLEAN: {v['unescaped']!r}")
