@echo off
setlocal
cd /d "%~dp0"
if not exist build\prototipo mkdir build\prototipo
echo Compilando el bosquejo visual de tres roles...
javac --release 11 -encoding UTF-8 -d build\prototipo -sourcepath src/main/java src/main/java/TurnosEnfermeria/vista/prototipo/PrototipoTurnos.java
if errorlevel 1 (
    echo Se necesita un JDK 11 o superior con javac en el PATH.
    pause
    exit /b 1
)
java -cp build\prototipo TurnosEnfermeria.vista.prototipo.PrototipoTurnos
if errorlevel 1 pause
endlocal
