import json
from collections import Counter

with open('.agents/worker_m1/remaining_form_matches.json', 'r', encoding='utf-8') as f:
    rem = json.load(f)

print(f"Remaining items: {len(rem)}")
unique_clean = Counter(r['clean'] for r in rem)
print(f"Unique clean strings remaining: {len(unique_clean)}")

with open('.agents/worker_m1/remaining_form_unique.json', 'w', encoding='utf-8') as f:
    json.dump({k: v for k, v in unique_clean.most_common()}, f, indent=2, ensure_ascii=False)

print("Saved remaining_form_unique.json")
