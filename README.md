# Sistema de Gestión de Turnos de Enfermería

Proyecto de Programación Avanzada desarrollado en Java, con consola e interfaz gráfica Swing.

## Propósito del sistema

El sistema permite administrar enfermeras y organizar sus turnos por área hospitalaria. Incluye turnos regulares, licencias y cambios de turno con sustituta.

Permite agregar, buscar, editar y eliminar registros, realizar asignaciones por área y consultar estadísticas. La comprobación de disponibilidad busca evitar horarios incompatibles, considerando también los turnos que una enfermera cubre como sustituta.

El registro principal utiliza un `TreeMap<String, Enfermera>`, identificado por el RUT normalizado. Cada enfermera mantiene sus eventos en un `ArrayList<Turno>`.

## Requisitos

- JDK 11 o superior.
- Para abrir el proyecto en el IDE: Apache NetBeans con soporte para Java y Ant.

El proyecto fue probado en NetBeans 17 con JDK 17 y está configurado para compilar para Java 11. Puede abrirse en otras versiones de NetBeans compatibles con proyectos Java y Ant. La versión del IDE elegida puede requerir un JDK más reciente para iniciarse.

El programa utiliza bibliotecas del JDK y no requiere dependencias externas.

## Preparación del ZIP

1. Descomprimir el ZIP completo.
2. Abrir la carpeta que contiene `build.xml`, `nbproject`, `src`, `Ejecutar.bat`, `Ejecutar.sh` y este README.
3. Trabajar desde esa carpeta descomprimida, no desde el interior del ZIP.

Se puede ejecutar el programa con los archivos incluidos o abrirlo desde NetBeans.

## Opción 1: ejecutar sin abrir NetBeans

Los archivos de ejecución compilan el código y luego inician el programa. Requieren que los comandos `java` y `javac` estén disponibles en el PATH del sistema.

### Windows

Hacer doble clic en `Ejecutar.bat`.

### Linux o macOS

Abrir una terminal en la carpeta del proyecto y ejecutar:

```bash
sh Ejecutar.sh
```

### Selección de interfaz

Al iniciar, escribir una opción y presionar Enter:

- `1`: trabajar en consola.
- `2`: abrir la interfaz gráfica.

## Opción 2: abrir desde NetBeans

1. Abrir NetBeans y seleccionar **File > Open Project**.
2. Seleccionar la carpeta que contiene `build.xml`, `nbproject` y `src`.
3. Pulsar **Open Project**. Aparecerá el proyecto **TurnosEnfermeria**.
4. Hacer clic derecho sobre el proyecto y seleccionar **Clean and Build**.
5. Comprobar que en **Output** aparezca **BUILD SUCCESSFUL**.
6. Hacer clic derecho sobre el proyecto y seleccionar **Run**.
7. En **Output**, escribir `1` para consola o `2` para ventanas y presionar Enter.

La interfaz gráfica se abre después de ingresar `2`; no aparece automáticamente al pulsar Run.

Si el panel **Output** está oculto, presionar **Ctrl + 4**.

No es necesario crear un proyecto nuevo ni agregar las clases manualmente.

## Guardado de datos

Para cerrar el programa y guardar los cambios, utilizar **Guardar y salir** en las ventanas o la opción **0** en la consola.

Los datos se guardan dentro de la carpeta `resources` del proyecto:

- `enfermeras.csv`
- `turnos.csv`

Los archivos se cargan al volver a ejecutar el programa. Si ambos archivos están ausentes, se utilizan los datos iniciales incluidos en el código.

Para conservar los registros al trasladar el proyecto, incluir también la carpeta `resources` con sus archivos.

## Generar y consultar Javadoc desde NetBeans

El proyecto incluye comentarios Javadoc en clases y métodos del código. Esta documentación corresponde al requisito opcional **SIA-O3**.

Para generar la documentación:

1. En la pestaña **Projects**, hacer clic derecho sobre **TurnosEnfermeria**.
2. Seleccionar **Generate Javadoc**.
3. Esperar a que en **Output** aparezca **BUILD SUCCESSFUL**.

Para abrirla:

1. Presionar **Ctrl + 2** para mostrar la pestaña **Files / Archivos**.
2. Expandir el proyecto y abrir las carpetas **build > javadoc**.
3. Hacer clic derecho en **index.html** y seleccionar **View / Ver**.
4. La documentación se abrirá en el navegador.

También se puede abrir `build/javadoc/index.html` desde el explorador de archivos del computador.

La generación y apertura fueron comprobadas en NetBeans 17. Durante la generación pueden aparecer advertencias por comentarios o etiquetas pendientes en algunos métodos. La documentación actual se genera correctamente, aunque no todos los métodos están documentados por completo.

La opción **Clean and Build** elimina la carpeta `build`, incluyendo el Javadoc generado. Para consultarlo después de una limpieza, volver a seleccionar **Generate Javadoc**.

## Informe

El informe del proyecto se encuentra en `v4Informe.pdf`.
