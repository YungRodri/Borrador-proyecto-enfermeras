#!/bin/sh

cd "$(dirname "$0")" || exit 1

mkdir -p build || exit 1

echo "Compilando el proyecto..."
if ! javac --release 11 -encoding UTF-8 -d build \
    -sourcepath src/main/java \
    src/main/java/TurnosEnfermeria/Main.java; then
    echo "No se pudo compilar. Revise los errores anteriores."
    exit 1
fi

echo "Iniciando el sistema..."
java -cp build TurnosEnfermeria.Main