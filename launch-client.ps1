# BR-LAWYER Desktop Client Launcher (PowerShell)
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$TargetDir = Join-Path $ScriptDir "j-lawyer-client\target"
$JarPath = Join-Path $TargetDir "j-lawyer-client.jar"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  BR-LAWYER Desktop Client - Inicializacao (pt-BR)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path $JarPath)) {
    Write-Error "O arquivo $JarPath nao foi encontrado. Execute: mvn clean package -pl j-lawyer-client -am"
    exit 1
}

Set-Location $TargetDir
& java -Xms256m -Xmx2048m "-Dfile.encoding=UTF-8" "-Duser.language=pt" "-Duser.country=BR" "--add-exports=jdk.crypto.cryptoki/sun.security.pkcs11=ALL-UNNAMED" -jar j-lawyer-client.jar $args
