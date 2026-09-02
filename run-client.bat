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

java -Duser.language=pt -Duser.country=BR --add-exports=jdk.crypto.cryptoki/sun.security.pkcs11=ALL-UNNAMED -jar j-lawyer-client.jar %*
