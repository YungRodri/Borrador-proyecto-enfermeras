# Sistema de Gestión de Turnos de Enfermeras

Proyecto académico — Programación Avanzada (SIA)  
Hospital Central | Java 11 + Maven

---

## Requisitos

- JDK 11 (Oracle JDK 11 o compatible)
- Maven 3.x

---

## Compilar y ejecutar

### Linux / macOS
```bash
chmod +x Ejecutar.sh
./Ejecutar.sh
```

### Windows
```
Ejecutar.bat
```

### Manual
```bash
mvn clean compile exec:java
```

---

## Estructura del proyecto

```
new-proyect-enfermeras/
├── pom.xml
├── resources/               ← CSV de datos (se genera automaticamente)
│   ├── enfermeras.csv
│   └── turnos.csv
└── src/main/java/TurnosEnfermeria/
    ├── Main.java            ← Punto de entrada
    ├── modelo/              ← Entidades del dominio
    ├── controlador/         ← Logica de negocio (MVC)
    └── vista/               ← Interfaces graficas (Fase 6)
```

---

## Cumplimiento Rubrica SIA

| Codigo | Descripcion                  | Implementado |
|--------|------------------------------|:------------:|
| SIA-3  | Atributos privados + semilla | ✓ |
| SIA-4  | TreeMap + ArrayList anidado  | ✓ |
| SIA-5  | Sobrecarga en 2 clases       | ✓ |
| SIA-6  | Override en 3 clases         | ✓ |
| SIA-7  | CRUD Enfermeras (colec. 1)   | ✓ |
| SIA-8  | CRUD Turnos (colec. 2)       | ✓ |
| SIA-9  | Filtro exceso turnos noche   | ✓ |
| SIA-10 | Consola + GUI (seleccion)    | Parcial |
| SIA-11 | Persistencia CSV batch       | ✓ |
| SIA-12 | 2 excepciones personalizadas | ✓ |
