# Reglas permanentes del proyecto

## Objetivo

Estamos creando una aplicación de citas médicas, medicamentos
y recordatorios.

## Regla principal del código

Escribir siempre código sencillo, claro y fácil de entender para una
persona que está aprendiendo a programar.

## Tecnología

- Usar Kotlin.
- Usar Jetpack Compose para crear las pantallas.
- Usar Material 3 para los componentes visuales.
- Usar arquitectura MVVM.
- Usar Firebase Authentication para registrar usuarios e iniciar sesión.
- Usar Firebase Cloud Messaging para notificaciones cuando sea necesario.
- Permitir funciones básicas sin internet si es posible.
- No usar Room inicialmente.
- No usar XML para crear pantallas nuevas, excepto si es necesario para una
  funcionalidad específica.
- Usar Firebase Firestore para guardar y consultar la información de medicamentos, citas y perfiles.
- Mantener siempre la sesión activa de Firebase Auth.
- Al iniciar la aplicación (en MainActivity / NavGraph), verificar si `FirebaseAuth.getInstance().currentUser != null`. Si existe un usuario activo, navegar directamente a `PantallaInicio` sin pasar por `PantallaLogin`.

## Reglas para escribir código

- Usar nombres claros para variables, funciones y clases.
- Evitar código complicado.
- Evitar abreviaturas difíciles de entender.
- Crear funciones pequeñas.
- Una función debe realizar una sola tarea.
- Evitar repetir código.
- No crear clases o archivos sin necesidad.
- Mantener el código fácil de leer.
- No agregar librerías nuevas sin explicar el motivo.
- No cambiar archivos que no estén relacionados con la tarea actual.
- No eliminar código funcional sin explicar el motivo.
- Implementar primero la solución más sencilla.

## Reglas para la interfaz

- Crear pantallas simples y fáciles de usar.
- Usar textos claros en botones y mensajes.
- Validar los campos obligatorios.
- Mostrar mensajes claros cuando exista un error.
- Mostrar un mensaje cuando la información se guarde correctamente.
- Mostrar estados de carga, error y contenido.
- Usar componentes reutilizables.
- Mantener compatibilidad con modo calro y oscuro.
- Agregar accesibilidad cuando sea necesario.


# Guía de Estilo y Reglas Generales de UI/UX 

## 1. Tipografía y Textos
- **Fuente Obligatoria:** Usar estrictamente la tipografía predeterminada del sistema (`FontFamily.Default` / Sans-Serif).
- **Prohibiciones:** Queda estrictamente prohibido el uso de fuentes cursivas, manuscritas o decorativas en toda la aplicación (formularios, títulos, botones y listas).

## 2. Componentes y Layout
- **Botón Flotante (FAB):** Debe ubicarse siempre en la esquina inferior derecha ('exepto el boton de chatbot, ese se mantiene en la esquina inferior izquierda') (`Alignment.BottomEnd`) con margen de `16.dp` respecto a los bordes.
- **Estructura de Formularios:** Agrupar campos relacionados dentro de tarjetas (`Card` elevadas) con `padding` interno uniforme (16.dp) para evitar listas verticales infinitas y desordenadas.
- **Navegación Inferior:** La `NavigationBar` debe usarse únicamente para cambiar entre las secciones principales, no para abrir formularios de creación directamente.
## Forma de trabajar

Antes de programar:

1. Leer el requerimiento indicado por el usuario.
2. Explicar con palabras sencillas qué se va a construir.
3. Indicar qué archivos se crearán o modificarán.
4. Explicar el plan paso a paso.
5. No realizar cambios grandes sin explicar primero el plan.

Durante la programación:

- Implementar solamente el requerimiento solicitado.
- No agregar funciones que no estén en el requerimiento.
- Seguir todos los criterios de aceptación.
- Mantener el código sencillo.
- Reutilizar el código existente cuando sea posible.
- No modificar partes que no estén relacionadas con la tarea.

Después de programar:

1. Explicar qué se hizo.
2. Indicar qué archivos fueron modificados.
3. Explicar cómo probar la funcionalidad.
4. Indicar si existe algún error.
5. Indicar si quedó alguna tarea pendiente.

## Reglas para explicar

- Explicar todo con palabras sencillas.
- No asumir que el usuario conoce conceptos avanzados.
- Explicar los conceptos difíciles con ejemplos.
- Mostrar solamente el código necesario.
- Cuando se modifique un archivo, explicar qué cambió.
- Usar español para las explicaciones.

## Regla de lectura de documentación

- La IA debe procesar únicamente el archivo de especificación (.md) adjuntado explícitamente en el chat mediante `@`.
- No asumir ni leer archivos de requerimientos que no hayan sido citados en el mensaje actual.