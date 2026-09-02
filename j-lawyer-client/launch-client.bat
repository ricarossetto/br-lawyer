@echo off
title BR-LAWYER Desktop Client (pt-BR)
echo ========================================================
echo   BR-LAWYER Desktop Client - Inicializacao (pt-BR)
echo ========================================================

set "SCRIPT_DIR=%~dp0"
set "TARGET_DIR=%SCRIPT_DIR%target"
set "JAR_PATH=%TARGET_DIR%\j-lawyer-client.jar"

if not exist "%JAR_PATH%" (
    echo [ERRO] O arquivo j-lawyer-client.jar nao foi encontrado em %TARGET_DIR%.
    echo Execute primeiro: mvn clean package -pl j-lawyer-client -am
    pause
    exit /b 1
)

cd /d "%TARGET_DIR%"
java -Xms256m -Xmx2048m -Dfile.encoding=UTF-8 -Duser.language=pt -Duser.country=BR --add-exports=jdk.crypto.cryptoki/sun.security.pkcs11=ALL-UNNAMED -jar j-lawyer-client.jar %*
