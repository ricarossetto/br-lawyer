import json
import re
import html
import os

def create_db():
    translations = {}

    # Add all translations
    with open('.agents/worker_m1/scripts/master_ptbr_catalog.py', 'r', encoding='utf-8') as f:
        # execute catalog
        code = f.read()
        local_vars = {}
        exec(code, {}, local_vars)
        translations.update(local_vars['CATALOG'])

    return translations

db = create_db()
print(f"Loaded {len(db)} initial translations")
