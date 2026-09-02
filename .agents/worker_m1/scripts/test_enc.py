import os

fpath = 'j-lawyer-client/src/main/java/com/iradraconis/shrinkify/ShrinkifyGui.java'

for enc in ['utf-8', 'iso-8859-1', 'windows-1252', 'cp1252']:
    try:
        with open(fpath, 'r', encoding=enc) as f:
            lines = f.readlines()
            line = lines[823].strip()
            print(f"Encoding {enc:12s}: line 824 = {line}")
    except Exception as e:
        print(f"Encoding {enc:12s}: Error {e}")
