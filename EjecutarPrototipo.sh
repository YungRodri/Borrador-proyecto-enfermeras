#!/bin/sh
# Abre únicamente el bosquejo visual con datos ficticios.
cd "$(dirname "$0")" || exit 1
mkdir -p build/prototipo || exit 1
echo "Compilando el bosquejo visual de tres roles..."
if ! javac --release 11 -encoding UTF-8 -d build/prototipo \
    -sourcepath src/main/java \
    src/main/java/TurnosEnfermeria/vista/prototipo/PrototipoTurnos.java; then
    echo "No se pudo compilar. Se necesita un JDK 11 o superior con javac en el PATH."
    exit 1
fi
java -cp build/prototipo TurnosEnfermeria.vista.prototipo.PrototipoTurnos
