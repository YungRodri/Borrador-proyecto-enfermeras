# Sistema de Gestión de Turnos de Enfermería

Proyecto de Programación Avanzada desarrollado en Java, con consola e interfaz gráfica Swing.

## Propósito y datos del sistema

El sistema permite organizar los turnos de enfermería por área hospitalaria, consultar disponibilidad y registrar licencias y sustituciones. La comprobación de horarios busca evitar asignaciones incompatibles para una misma persona, incluyendo los cambios que cubre como sustituta.

Cada enfermera tiene RUT, nombre, apellidos, edad, especialidad y área asignada. Su historial contiene turnos regulares, licencias y cambios de turno. Los eventos tienen un identificador, fecha, horario y observación; los cambios incluyen el RUT de la sustituta y el motivo.

El registro principal es un `TreeMap<String, Enfermera>`, cuya clave es el RUT normalizado. Cada enfermera contiene un `ArrayList<Turno>`. Estas son las dos colecciones principales anidadas. Las listas de resultados y agrupaciones por área son auxiliares.

## Requisitos y ejecución

- Oracle JDK 11 para la entrega del curso.
- Los comandos `java` y `javac` deben estar disponibles en la terminal.
- El programa utiliza bibliotecas del JDK; no requiere Maven ni dependencias externas.

### Windows

Descomprima el proyecto y ejecute `Ejecutar.bat`. El archivo compila el código y abre el programa. Al iniciar, ingrese `1` para consola o `2` para ventanas.

Si necesita comprobar la instalación de Java, ejecute:

```bat
java -version
javac -version
```

### Linux o macOS

Desde la carpeta del proyecto:

```sh
sh Ejecutar.sh
```

### Compilación manual

Ejecute estos comandos desde la carpeta que contiene `src`:

```sh
mkdir build
javac --release 11 -encoding UTF-8 -d build -sourcepath src/main/java src/main/java/TurnosEnfermeria/Main.java
java -cp build TurnosEnfermeria.Main
```

Si `build` ya existe, omita el primer comando.

### NetBeans o Eclipse

Cree un proyecto Java sin Maven, seleccione JDK 11 y configure `src/main/java` como carpeta de código fuente. La clase principal es `TurnosEnfermeria.Main`. Use UTF-8 como codificación y la carpeta del proyecto como directorio de trabajo.
