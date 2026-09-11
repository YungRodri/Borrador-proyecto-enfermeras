@echo off
setlocal
cd /d "%~dp0"

if not exist build mkdir build

echo Compilando el proyecto...
javac --release 11 -encoding UTF-8 -d build -sourcepath src/main/java src/main/java/TurnosEnfermeria/Main.java

if errorlevel 1 (
    echo.
    echo No se pudo compilar. Revise los errores anteriores.
    pause
    exit /b 1
)

echo.
echo Iniciando el sistema...
java -cp build TurnosEnfermeria.Main

pause
endlocal