# Sistema de Gestión de Turnos de Enfermería

Proyecto de Programación Avanzada desarrollado en Java, con consola e interfaz gráfica Swing.

## Propósito y datos del sistema

El sistema permite organizar los turnos de enfermería por área hospitalaria, consultar disponibilidad y registrar licencias y sustituciones. La comprobación de horarios busca evitar asignaciones incompatibles para una misma persona, incluyendo los cambios que cubre como sustituta.

Cada enfermera tiene RUT, nombre, apellidos, edad, especialidad y área asignada. Su historial contiene turnos regulares, licencias y cambios de turno. Los eventos tienen un identificador, fecha, horario y observación; los cambios incluyen el RUT de la sustituta y el motivo.

El registro principal es un `TreeMap<String, Enfermera>`, cuya clave es el RUT normalizado. Cada enfermera contiene un `ArrayList<Turno>`. Estas son las dos colecciones principales anidadas. Las listas de resultados y agrupaciones por área son auxiliares.

## Requisitos

- JDK 11 instalado.
- Apache NetBeans con soporte para proyectos Java y Ant. Proyecto probado en NetBeans 17.

El programa utiliza bibliotecas del JDK y no requiere instalar dependencias externas.

## Preparación

Descomprimir el ZIP completo en una carpeta del computador.

Dentro de la carpeta del proyecto se encuentran `src`, `nbproject`, `build.xml`, `Ejecutar.bat`, `Ejecutar.sh` y este README.

## Opción 1: ejecutar sin abrir NetBeans

### Windows

Hacer doble clic en `Ejecutar.bat`. Este archivo compila el código y luego inicia el programa.

Para esta opción, los comandos `java` y `javac` deben estar disponibles en el PATH del sistema.

### Linux o macOS

Abrir una terminal en la carpeta del proyecto y ejecutar:

sh Ejecutar.sh

Al iniciar, escribir `1` para trabajar en consola o `2` para abrir las ventanas y presionar Enter.

## Opción 2: abrir desde NetBeans
Se incluyen los archivos de configuración para abrir el proyecto directamente. Puede utilizarse otra versión de NetBeans compatible con proyectos Java y Ant, siempre que el JDK utilizado para compilar y ejecutar sea 11 o superior. Algunas versiones del IDE pueden requerir un JDK más reciente para iniciarse.

1. Abrir NetBeans y seleccionar **File > Open Project**.
2. Seleccionar la carpeta descomprimida.
3. Pulsar **Open Project**. Aparecerá el proyecto **TurnosEnfermeria**.
4. Hacer clic derecho sobre el proyecto y seleccionar **Clean and Build**.
5. Comprobar que en **Output** aparezca **BUILD SUCCESSFUL**.
6. Hacer clic derecho sobre el proyecto y seleccionar **Run**.
7. En **Output**, escribir `1` para consola o `2` para ventanas y presionar Enter.

La interfaz gráfica se abre después de ingresar `2`; no aparece automáticamente al pulsar Run.

Si el panel Output está oculto, se puede mostrar presionando **Ctrl + 4**.

No es necesario crear un proyecto nuevo ni agregar las clases manualmente. La apertura, compilación y ejecución gráfica fueron comprobadas en NetBeans 17.

## Guardado de datos

Para cerrar el programa, utilizar **Guardar y salir** en las ventanas o la opción **0** en la consola.

Los datos se guardan en la carpeta `resources`, dentro del proyecto:

- `enfermeras.csv`
- `turnos.csv`

Los archivos se cargan al volver a ejecutar el programa. Si no existen, se utilizan los datos iniciales incluidos en el código.

## Informe

El informe del proyecto se encuentra en `v3Informe.pdf`.
