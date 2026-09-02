@echo off
title BR-LAWYER Desktop Client (pt-BR)
echo ========================================================
echo   BR-LAWYER Desktop Client - Inicializacao (pt-BR)
echo ========================================================

cd /d "%~dp0\j-lawyer-client\target"

if not exist "j-lawyer-client.jar" (
    echo [ERRO] O arquivo j-lawyer-client.jar nao foi encontrado em j-lawyer-client\target.
    echo Execute primeiro: mvn package -pl j-lawyer-client -DskipTests=true
    pause
    exit /b 1
)

set "JAVA_CMD=java"
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
)
if not defined JAVA_HOME (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" (
        set "JAVA_CMD=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe"
    )
)

"%JAVA_CMD%" -Xms256m -Xmx2048m -Dfile.encoding=UTF-8 -Duser.language=pt -Duser.country=BR --add-exports=jdk.crypto.cryptoki/sun.security.pkcs11=ALL-UNNAMED -jar j-lawyer-client.jar %*
