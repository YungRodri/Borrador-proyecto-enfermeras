# Interfaz de enfermería — rediseño visual

La rama `prototipo-tres-roles` integra el rediseño recibido en el ZIP del equipo.
La intervención visual se concentra en `TurnosEnfermeria.vista.prototipo`.

## ¿Qué se mejoró?

- Pantalla de selección de perfiles replanteada con un lenguaje más cercano y una jerarquía visual sencilla.
- Los nombres visibles de las entradas son **Enfermera**, **Coordinador** y **Administrador**.
- Menú lateral claro, con estado activo visible y acceso a «Cambiar de perfil».
- Tarjetas, formularios, botones, avisos y tablas con tamaños y colores más cómodos para lectura prolongada.
- Textos en las pantallas internas más cercanos y menos técnicos.
- Aviso persistente para distinguir la demostración del funcionamiento real.
- Sin librerías externas, imágenes de terceros ni dependencias adicionales.

## Compatibilidad

No se alteraron las rutas de navegación ni los identificadores internos de rol:
`enfermeria`, `coordinacion` y `jefatura` (esta última se muestra como Administrador).
Las pantallas y los formularios mantienen los mismos identificadores y callbacks.

## Archivos Java modificados

- `PrototipoPanel.java`: selector de roles, barra superior, menú y presentación de formularios.
- `TemaPrototipo.java`: componentes compartidos y colores.
- `PantallasPrototipo.java`: ajustes puntuales de textos de interfaz.
- `PrototipoTurnos.java`: título de la ventana.

El resto del código fuente Java, los modelos, los controladores y la configuración de
compilación del repositorio se preservaron.

## Verificación informada en el ZIP recibido

- Compilación completa del proyecto con `ant clean build` (JDK 21, objetivo Java 11): correcta.
- Inicio del prototipo en entorno gráfico Swing: correcto.
- Navegación por 3 roles y 8 pantallas: correcta.
- Apertura y cierre del formulario «Comunicar ausencia»: correcta.
- Capturas verificadas con ventana de 1250×790 y 1024×650 (en esta última aparece barra de desplazamiento).

Estas pruebas no sustituyen las pruebas manuales en Windows/IntelliJ del usuario.
El prototipo sigue utilizando datos de ejemplo y no guarda cambios.

## Comprobación de integración · 30 de octubre de 2026

- Compilación independiente de las 24 fuentes Java con OpenJDK 17 y objetivo Java 11 (`--release 11`): correcta.
- Selección de los tres perfiles, navegación por las ocho pantallas y retorno al selector: correctos en la comprobación automatizada de componentes Swing.
- Renderizado de las pantallas a 1250×790 y 1024×650, con desplazamiento vertical en las vistas que lo necesitan.
- Comprobación de las catorce rutas de formulario, su contenido y el texto de la acción prevista.
- Comparación con la rama anterior: los modelos, controladores, CSV de ejemplo en el código y modos originales no cambian.

Esta comprobación se realizó sin escritorio gráfico. No incluye la apertura de ventanas
nativas ni de diálogos modales, ni una prueba del archivo `.bat` en Windows.

## Historial y recuperación

La versión previa al rediseño está conservada en Git:
[c4636a4](https://github.com/mateocuetoc-hub/gestion-turnos-enfermeria-is2026/tree/c4636a41680d378e450e7cf3d29bf8517099e999).
El ZIP del equipo incluía una copia de respaldo idéntica a esa versión. No se añade esa
copia comprimida al repositorio porque el historial conserva los mismos archivos.
