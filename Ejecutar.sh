#!/bin/bash
# Script de ejecucion para Linux/macOS
# Ejecutar desde la raiz del proyecto: ./Ejecutar.sh

cd "$(dirname "$0")"
mvn clean compile exec:java -q
