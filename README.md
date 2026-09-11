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

## Funcionalidades

Las operaciones están disponibles en consola y ventanas:

- Agregar, listar, buscar, editar y eliminar enfermeras.
- Registrar, listar, buscar por ID, editar la observación y eliminar turnos.
- Registrar turnos regulares, licencias y cambios con una sustituta.
- Asignar turnos por área; se informa cuáles se registraron y cuáles tuvieron conflicto.
- Filtrar enfermeras que superan una cantidad de turnos de mañana, tarde o noche en un mes.
- Consultar el resumen de un área y las estadísticas del sistema.
- Validar disponibilidad para una nueva asignación, sin registrar turnos.

Las estadísticas muestran horas atribuidas a quien realiza el turno. El filtro por horario cuenta turnos regulares del historial; no suma sustituciones. En la interfaz gráfica también se presentan gráficos estadísticos.

## Reglas principales

- El RUT se normaliza y se valida mediante su dígito verificador; no se modifica durante la edición.
- La edad admitida es de 18 a 70 años.
- Los horarios incompatibles se rechazan. Los turnos nocturnos pueden terminar al día siguiente.
- La sustituta debe estar registrada, ser distinta de la titular y estar disponible.
- Si existe un turno regular de la titular que se superpone con el cambio, debe coincidir exactamente con su fecha y horario. Se retira después de aceptar el cambio. También se permite registrar un cambio sin un turno regular previo coincidente.
- Eliminar un cambio no restaura automáticamente el turno regular que reemplazó.
- No se puede eliminar una enfermera que figure como sustituta en cambios de otras enfermeras. Al eliminar una enfermera se elimina su historial.

## Datos iniciales y persistencia

Cuando no existen los dos CSV, se cargan seis enfermeras y sus eventos iniciales desde el código. Los ejemplos corresponden a septiembre de 2026 e incluyen UCI, Urgencias, Pediatría y Cirugía.

Los datos se mantienen en memoria durante la sesión. Al elegir guardar y salir se escriben:

| Archivo | Contenido |
|---|---|
| `resources/enfermeras.csv` | Datos de las enfermeras |
| `resources/turnos.csv` | Historiales de turnos y sustituciones |

Los CSV utilizan UTF-8, punto y coma como separador y comillas para conservar textos con separadores o saltos de línea. Antes de reemplazar archivos existentes se generan respaldos `.bak`. Si una carga falla, el inicio se cancela para evitar guardar un registro incompleto. Si el guardado falla, se informa al usuario.

Para repetir una demostración desde los datos iniciales, cierre el programa y mueva la carpeta `resources` a una ubicación de respaldo antes de volver a iniciar. Los datos de cada sesión y los archivos compilados no se incluyen en el repositorio.

## Organización del código

| Ruta | Responsabilidad |
|---|---|
| `src/main/java/TurnosEnfermeria/Main.java` | Inicio, selección de interfaz y menú de consola |
| `src/main/java/TurnosEnfermeria/modelo` | Entidades, validaciones, utilidades y persistencia |
| `src/main/java/TurnosEnfermeria/controlador` | Operaciones sobre enfermeras y turnos |
| `src/main/java/TurnosEnfermeria/vista` | Ventanas y estilos Swing |
| `Ejecutar.bat` y `Ejecutar.sh` | Compilación y ejecución |
| `.github/workflows/compilar.yml` | Comprobación de compilación en GitHub Actions |

`Turno` es una clase abstracta. `TurnoRegular`, `Licencia` y `CambioTurno` sobrescriben sus métodos. `AreaHospitalaria` y `TurnoControlador` contienen sobrecargas utilizadas por el programa. Las excepciones propias son `RutInvalidoException` y `TurnoConflictoException`.

## Estado de la entrega

El análisis de datos y funcionalidades se describe en este README. Está pendiente incorporar el diagrama UML de dominio correspondiente al código final.

GitHub Actions comprueba la compilación con Temurin 11; no ejecuta casos de prueba. Una compilación correcta no sustituye la revisión funcional de consola, ventanas y persistencia ni la comprobación final con Oracle JDK 11 en el entorno de entrega.
