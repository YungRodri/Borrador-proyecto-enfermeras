@echo off
REM Script de ejecucion para Windows
REM Ejecutar desde la raiz del proyecto

cd /d "%~dp0"
mvn clean compile exec:java -q
pause
