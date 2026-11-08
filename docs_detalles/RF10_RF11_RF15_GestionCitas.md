# Documentación Técnica y Funcional: Módulo de Citas (RF10, RF11, RF15)

## Descripción General
Este documento especifica los requerimientos funcionales para la consulta, edición y eliminación de citas médicas en la aplicación Vitalis, asegurando la persistencia en Firebase Firestore y la sincronización con alarmas reales en Android.

---

## 1. RF15: Consulta de Detalles de la Cita
* **Historia de Usuario:** Como usuario quiero consultar los detalles de una cita registrada para conocer toda la información relacionada con ella.
* **Criterios de Aceptación y Componentes UI:**
    - El usuario selecciona una cita desde la pantalla principal de citas (`PantallaCitas.kt`).
    - El sistema muestra una pantalla dedicada (`PantallaDetalleCita.kt`) con:
        - Motivo / Especialidad médica.
        - Doctor asignado.
        - Fecha y hora exacta formateada.
        - Lugar u hospital.
        - Notas adicionales.
    - Se incluyen accesos directos visuales para los botones de **Editar** y **Eliminar**.

---

## 2. RF10: Edición de Citas Médicas
* **Historia de Usuario:** Como usuario quiero modificar los datos de la cita para mantener actualizada mi agenda.
* **Criterios de Aceptación y Lógica:**
    - El usuario puede modificar los campos del formulario (`PantallaEditarCita.kt`).
    - **Validaciones obligatorias:** Ningún campo principal (motivo, fecha, hora, lugar) puede quedar vacío; se muestran mensajes claros de error si falta información.
    - **Persistencia:** Al presionar guardar, los datos se actualizan correctamente en el documento correspondiente de Firebase Firestore.
    - **Sincronización de Alarmas:** El sistema actualiza de forma automática el recordatorio en el `AlarmManager` (cierra la alarma anterior y programa una nueva con los datos actualizados).

---

## 3. RF11: Eliminación de Citas Médicas
* **Historia de Usuario:** Como usuario quiero eliminar una cita cuando haya sido cancelada o ya no sea necesaria.
* **Criterios de Aceptación y Lógica:**
    - Al presionar la opción "Eliminar" en el detalle, el sistema despliega un **diálogo de confirmación** nativo ("¿Estás seguro de que deseas eliminar esta cita?").
    - Si el usuario confirma, la cita se elimina de la lista y se borra físicamente de la colección en Firestore.
    - La cita deja de mostrarse de inmediato en la interfaz y en la vista de calendario.
    - Se cancela y desactiva el recordatorio asociado en el `AlarmManager` para evitar notificaciones huérfanas.