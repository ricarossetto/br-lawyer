import os
import glob
import re
import subprocess
import shutil

ROOT = r"c:\projetos IA\BR-LAWYER\br-lawyer"
REPO = os.path.join(ROOT, "maven-repo")
GROUP = "jlawyer.thirdparty"
MAP_FILE = os.path.join(ROOT, "scripts", "lib-gav-map.txt")

os.makedirs(REPO, exist_ok=True)

java_home = r"C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
maven_home = r"C:\tools\apache-maven-3.9.9"
mvn_cmd = os.path.join(maven_home, "bin", "mvn.cmd")

env = os.environ.copy()
env["JAVA_HOME"] = java_home
env["JAVA_TOOL_OPTIONS"] = "-Dfile.encoding=UTF-8"
path_parts = [os.path.join(java_home, "bin"), os.path.join(maven_home, "bin")] + [p for p in env.get("PATH", "").split(";") if "oracle\\java\\javapath" not in p.lower()]
env["PATH"] = ";".join(path_parts)

skip_pattern = re.compile(r"[/\\](bea\.bak|CopyLibs|CopyLibs-2)[/\\]")

def derive_gav(base):
    # base is filename without .jar
    m = re.match(r"^(.*)-([0-9][^-]*(-.*)?)$", base)
    if m:
        return m.group(1), m.group(2)
    return base, "0.0.0"

jar_files = []
for dirpath, dirnames, filenames in os.walk(ROOT):
    if "maven-repo" in dirpath or "target" in dirpath or "build" in dirpath or "dist" in dirpath:
        continue
    if not (os.path.basename(dirpath) in ["lib", "libs"] or "\\lib\\" in dirpath or "\\libs\\" in dirpath):
        continue
    if skip_pattern.search(dirpath):
        continue
    for f in filenames:
        if f.endswith(".jar"):
            jar_files.append(os.path.join(dirpath, f))

print(f"Found {len(jar_files)} JARs to process for in-project repository...")

installed_gavs = set()
with open(MAP_FILE, "w", encoding="utf-8") as map_out:
    for jar in jar_files:
        base = os.path.splitext(os.path.basename(jar))[0]
        artifact, version = derive_gav(base)
        map_out.write(f"{jar} => {GROUP}:{artifact}:{version}\n")
        
        target_dir = os.path.join(REPO, *GROUP.split("."), artifact, version)
        target_jar = os.path.join(target_dir, f"{artifact}-{version}.jar")
        
        if os.path.exists(target_jar):
            installed_gavs.add(f"{GROUP}:{artifact}:{version}")
            continue
            
        print(f"Installing {artifact}:{version} from {os.path.relpath(jar, ROOT)}...")
        cmd = [
            mvn_cmd, "-q",
            "org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file",
            f"-Dfile={jar}",
            f"-DgroupId={GROUP}",
            f"-DartifactId={artifact}",
            f"-Dversion={version}",
            "-Dpackaging=jar",
            f"-DlocalRepositoryPath={REPO}",
            "-DcreateChecksum=true"
        ]
        res = subprocess.run(cmd, env=env, cwd=ROOT, capture_output=True, text=True)
        if res.returncode != 0:
            print(f"Warning: Failed to install {jar}: {res.stderr}")
        else:
            installed_gavs.add(f"{GROUP}:{artifact}:{version}")

print("Overwriting POMs with flat stubs...")
for pom in glob.glob(os.path.join(REPO, "**", "*.pom"), recursive=True):
    parts = os.path.relpath(pom, REPO).split(os.sep)
    version = parts[-2]
    artifact = parts[-3]
    group = ".".join(parts[:-3])
    with open(pom, "w", encoding="utf-8") as f:
        f.write(
            '<?xml version="1.0" encoding="UTF-8"?>\n'
            '<project xmlns="http://maven.apache.org/POM/4.0.0">\n'
            '  <modelVersion>4.0.0</modelVersion>\n'
            f'  <groupId>{group}</groupId>\n  <artifactId>{artifact}</artifactId>\n'
            f'  <version>{version}</version>\n  <packaging>jar</packaging>\n</project>\n'
        )

print(f"Seeding completed successfully! Total unique artifacts in maven-repo: {len(installed_gavs)}")
