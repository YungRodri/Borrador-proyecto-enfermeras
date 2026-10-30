# Bosquejo visual de tres roles

Estado: propuesta de interfaz ejecutable. No implementa autenticación, permisos,
solicitudes, cálculos nuevos de cobertura ni guardado del escenario.

El rediseño visual presenta los perfiles como **Enfermera**, **Coordinador** y
**Administrador**. Corresponden a Enfermería, Coordinación y Jefatura en las tablas
de este documento y conservan las claves internas `enfermeria`, `coordinacion` y
`jefatura`. Administrador mantiene el recorrido de consulta de cobertura e informes;
el cambio de nombre no implementa permisos ni gestión de cuentas.
Los ajustes visuales se detallan en [CAMBIOS_VISUALES_2026.md](CAMBIOS_VISUALES_2026.md).

El código nuevo se concentra en `TurnosEnfermeria.vista.prototipo`. Su entrada independiente
es `PrototipoTurnos`. No importa controladores ni el gestor de archivos del sistema.
El cambio en `Main` añade el modo 3 y permite seleccionar el bosquejo antes de cargar CSV.
Los modos 1 y 2 conservan el modelo, los controladores y el guardado originales.

## Recorridos y acciones previstas

| Vista | Actor | Qué se representa | Implementación pendiente |
| --- | --- | --- | --- |
| Selector | Los tres roles | Entradas separadas para explorar | Cuentas, sesión y autorización. El selector solo navega. |
| Mi agenda | Enfermería | Turnos propios y detalle de un turno afectado | Vínculo cuenta-personal; agenda efectiva, período y sustituciones. |
| Mis solicitudes | Enfermería | Solicitud en revisión y comunicación de ausencia | Validar necesidad; entidad Solicitud, estados, propiedad y persistencia. |
| Resumen operativo | Coordinación | Dotación, ausencia y déficit del escenario | Indicadores calculados y tareas pendientes del área autorizada. |
| Planificación | Coordinación | Asignaciones y formularios de alta/edición | Edición temporal segura, filtros y confirmación de asignaciones. |
| Personal | Coordinación | Dotación y formularios de registro/edición | Datos mínimos, identidad interna y desactivación con referencias conservadas. |
| Ausencias y cobertura | Coordinación | Ausencia, candidatos y sustitución | Ausencia independiente, original obligatorio, elegibilidad y disponibilidad. |
| Cobertura del servicio | Jefatura | Mínimo, asignados efectivos y déficit | Cálculo por intervalo y permisos de consulta. |
| Informes | Jefatura | Horas planificadas y resumen de incidencias | Filtros, cálculo común con el panel y exportación. |

Los botones abren formularios o detalles. **Ver qué haría esta acción** explica la acción futura;
no confirma un resultado ni modifica las tablas. Los controles de área y período muestran
un único escenario. Las tablas son de consulta, sin edición ni filtros funcionales.

## Escenario ficticio compartido

- Área piloto A; período del 12 al 18 de octubre de 2026.
- Seis personas ficticias E-001 a E-006 registradas en el área.
- Cuatro bloques ilustrativos; no es una semana completa de funcionamiento.
- Mínimo ficticio de dos personas por bloque, pendiente de validación institucional.
- Ocho registros originales; T-001 afecta a E-001 el día 12 de 07:00 a 15:00.
- En la fotografía ficticia, coordinación ya registró la ausencia de E-001 para T-001.
  Se conserva el turno afectado y su cobertura está pendiente. SOL-001 sigue en revisión
  operativa: una comunicación por sí sola no modifica una asignación.
- E-002 tiene T-002 en ese intervalo y es un candidato ocupado.
- E-003 y E-004 aparecen como candidatos disponibles en el ejemplo.
- E-007 es externo al área y se muestra con elegibilidad por validar; no pertenece a la dotación de seis.
- Siete asignaciones efectivas de ocho horas suman 56 horas planificadas. Tres bloques
  completos y uno con déficit de una persona. La ausencia todavía no tiene reemplazo.
- E-001 tiene un turno efectivo de ocho horas el día 17, además del registro afectado del día 12.

Estos valores se escriben como ejemplos fijos; no proceden del modelo original, una entrevista,
una institución ni un algoritmo. Las horas no acreditan asistencia efectiva.
No se incluyen datos reales de personal o pacientes, diagnósticos ni certificados.

## Trazabilidad con los requisitos candidatos del informe

Los identificadores RF se conservan del borrador revisado. La presencia de una pantalla
no demuestra que el requisito esté implementado ni validado con usuarios.

| RF | Situación respecto de la base | Representación en el bosquejo |
| --- | --- | --- |
| RF-01 Acceso y autorización | Nueva; pendiente | Selector de demostración, sin credenciales ni permisos. |
| RF-02 Agenda propia | Consulta existente que requiere adaptación | Mi agenda. |
| RF-03 Comunicación de ausencia/cambio | Nueva; necesidad por validar | Formulario de comunicación. |
| RF-04 Estado de solicitudes | Nueva; dependiente de RF-03 | Mis solicitudes. |
| RF-05 Personal | CRUD existente; requiere permisos y ajustes | Personal, alta y edición visual. |
| RF-06 Dotación por área/período | Requiere adaptación | Personal y resumen operativo. |
| RF-07 Asignar y modificar turnos | Alta existente; edición temporal pendiente | Planificación, alta y edición visual. |
| RF-08 Conflictos temporales | Núcleo existente, revisado anteriormente | Ventana con un conflicto fijo de ejemplo. No llama al validador. |
| RF-09 Ausencia y turnos afectados | Licencia existente; proceso requiere adaptación | Revisión de ausencia. |
| RF-10 Candidatos disponibles | Requiere listado y elegibilidad | Tabla fija de candidatos. |
| RF-11 Sustitución | Existe parcialmente; requiere original y nuevas reglas | Formulario de sustitución. |
| RF-12 Resolver solicitudes | Nueva; condicionada a RF-03 | Resolución propuesta. |
| RF-13 Cobertura efectiva por bloque | Requiere nuevo cálculo | Panel con cifras ficticias. |
| RF-14 Horas planificadas e incidencias | Estadísticas existentes; filtros/exportación pendientes | Informes y vista previa fija. |
| RF-15 Historial auditable | Evolución futura | Fuera de este bosquejo. |

## Orden propuesto para desarrollar después de revisar el diseño

1. Acordar requisitos, política de acceso y necesidad del canal de solicitudes.
2. Revisar las ventanas con el equipo: navegación, términos, tamaños y campos.
3. Separar ausencia, turno afectado y sustitución; conservar las referencias al original.
4. Implementar cuentas y autorización en los servicios y todos los puntos de entrada.
5. Conectar coordinación a las validaciones compartidas; comprobar conflicto y elegibilidad.
6. Conectar agenda efectiva y cobertura con los mismos datos, período y reglas del piloto.
7. Incorporar persistencia y exportación; verificar referencias, errores y reinicio.
8. Ejecutar pruebas de integración, sistema y usabilidad; actualizar evidencias e informe.

La base todavía tiene las brechas observadas: licencia superpuesta rechazada, cambio sin
original aceptado, elegibilidad de otra área sin validar y cobertura confundida con dotación.
Este commit presenta interfaces; no corrige esas reglas ni debe documentarse como si lo hiciera.

## Ejecución

Linux/macOS: `sh EjecutarPrototipo.sh`. Windows: `EjecutarPrototipo.bat`.
JDK 11 o superior, con `java` y `javac` disponibles; una sesión de escritorio gráfico.
Desde NetBeans: Run y opción 3. Con Ant: `ant prototipo`.
Para abrir sin consola, ejecutar `TurnosEnfermeria.vista.prototipo.PrototipoTurnos`.

No se necesitan archivos CSV. Cerrar una ventana no guarda datos del escenario.
Para revisar los modos originales, ejecutar `Ejecutar.sh`/`Ejecutar.bat` y elegir 1 o 2.

## Verificación de este avance

Compilación de todas las fuentes con OpenJDK 17 y objetivo Java 11 (`--release 11`).
Se revisaron el selector, las ocho vistas y catorce formularios mediante renderizado Swing
sin escritorio, incluyendo tamaños reducidos y navegación por los botones de cada rol.
La revisión no equivale a una prueba de ventanas nativas en Windows o Linux: la apertura
de los diálogos modales y la revisión en el escritorio del equipo siguen pendientes.
No se ejecutaron pruebas de aceptación de funciones de negocio nuevas, pues todavía son propuestas.
Se comprobó además que la entrada directa y la opción 3 conservan CSV de prueba intactos
y no intentan cargarlos, y que el modo original mantiene la cancelación ante CSV inválidos.
