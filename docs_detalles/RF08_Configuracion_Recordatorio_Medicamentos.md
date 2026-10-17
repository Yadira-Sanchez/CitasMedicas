# Spec Técnico: RF08 - Configuración de Recordatorios de Medicamentos

## 1. Información General
- **Código y Nombre:** RF08 - Configuración de recordatorios de medicamentos
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Permitir al usuario definir la fecha de inicio, hora de toma, frecuencia y el estado (activado/desactivado) de los recordatorios de un medicamento, guardando dicha configuración en Cloud Firestore y programando las alarmas reales correspondientes en el dispositivo.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** configurar la fecha, hora, frecuencia y estado de activación del recordatorio de mi medicamento  
**Para** recibir avisos puntuales en el momento establecido y llevar el control estricto de mis tomas.

## 3. Alcance y Límites
- **Incluye:**
    - Configuración de los parámetros del recordatorio:
        - **Fecha de inicio:** Seleccionable mediante `DatePickerDialog` (por defecto fecha actual).
        - **Hora inicial de toma:** Seleccionable mediante `TimePickerDialog`.
        - **Frecuencia:** Opciones predefinidas (ej. "Cada 8 horas", "Cada 12 horas", "Diario") o intervalo personalizado.
        - **Estado del Recordatorio (Switch / Toggle):** Opción para activar o desactivar las notificaciones de ese medicamento sin eliminar el registro.
    - Almacenamiento de la configuración dentro del documento del medicamento en Cloud Firestore (`usuarios/{uid}/medicamentos/{id}`).
    - Programación nativa mediante `AlarmManager` en Android cuando el recordatorio esté **activado**.
    - Cancelación de la alarma programada en el sistema cuando el usuario **desactive** el switch del recordatorio.
    - Persistencia de la configuración tras cerrar la app o reiniciar el dispositivo (`RECEIVE_BOOT_COMPLETED`).
    - Mensaje de confirmación en pantalla (`Snackbar`): *"Configuración de recordatorios guardada correctamente."*.
- **No incluye:**
    - La visualización de la alerta o pantalla flotante en el momento exacto en que suena la alarma (eso se gestiona en el `RF13`).

## 4. Especificación de UI y Componentes Visuales

### Tarjeta de Programación de Recordatorios (`Card` Elevada)

| Campo / Elemento | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error / Descripción |
| :--- | :---: | :--- | :--- | :--- |
| **Estado del Recordatorio** | Sí | `Boolean` (`true` / `false`) | `Switch` (Material 3) | Activa o pausa las alarmas del medicamento |
| **Fecha de Inicio** | Sí | Debe ser una fecha válida | `OutlinedTextField` + `DatePickerDialog` | `"Selecciona una fecha de inicio válida."` |
| **Hora de la Toma** | Sí | Debe ser una hora válida | `OutlinedTextField` + `TimePickerDialog` | `"Selecciona la hora de la toma."` |
| **Frecuencia** | Sí | Seleccionar una frecuencia válida | `ExposedDropdownMenu` / Chips | `"Selecciona la frecuencia de la toma."` |

---

## 5. Comportamiento y Estados del Sistema

### Flujo de Activación / Desactivación (`Switch`)
1. **Recordatorio Activado (`Switch = true`):**
    - Guarda los campos de fecha, hora y frecuencia en Firestore (`recordatorioActivo = true`).
    - Invoca el `AlarmScheduler` para calcular la hora exacta en milisegundos y programar la alarma nativa mediante `AlarmManager.setExactAndAllowWhileIdle()`.
    - Muestra el `Snackbar`: *"Recordatorio activado y guardado correctamente."*.

2. **Recordatorio Desactivado (`Switch = false`):**
    - Actualiza el campo en Firestore (`recordatorioActivo = false`).
    - Invoca el `AlarmScheduler` para cancelar la alarma correspondiente usando el `PendingIntent` e `id` del medicamento.
    - Muestra el `Snackbar`: *"Recordatorio desactivado."*.

### Estados de Pantalla
- **Estado de Carga (`Loading`):** Mientras se actualizan los datos en Firestore y se programa el `AlarmManager`, el botón o switch muestra un indicador de carga.
- **Estado de Éxito:** Muestra el mensaje de retroalimentación en `Snackbar` y refleja el estado real del `Switch`.
- **Estado de Error:** Despliega un mensaje en texto rojo si ocurre un problema al escribir en la base de datos o si faltan permisos del sistema operativo.

---

## 6. Arquitectura (MVVM + Firebase + Android Native)

- **Vista (UI):** `PantallaNuevoMedicamento.kt` / `PantallaEditarMedicamento.kt` (`ui.medicamentos`)
- **ViewModel:** `MedicamentoViewModel.kt` (`ui.medicamentos`)
- **Gestor de Alarmas:** `AlarmScheduler.kt` (Encargado de `AlarmManager` y `PendingIntent`)
- **Ruta Firestore:** `firestore.collection("usuarios").document(uid).collection("medicamentos").document(medicamentoId).update(datosRecordatorio)`

---

## 7. Criterios de Aceptación (Pruebas Manuales)

- [ ] **Selección de Parámetros:** El usuario puede seleccionar la fecha de inicio con `DatePickerDialog`, la hora con `TimePickerDialog` y definir la frecuencia.
- [ ] **Control del Switch:** El usuario puede activar y desactivar los recordatorios usando el componente `Switch`.
- [ ] **Guardado en Firestore:** Al guardar la configuración, los campos `fechaInicio`, `horaToma`, `frecuencia` y `recordatorioActivo` se guardan en el documento de Firestore.
- [ ] **Programación de Alarma Nativa:** Si el `Switch` está activado, el sistema calcula la hora y registra la alarma exacta en el `AlarmManager` de Android.
- [ ] **Cancelación de Alarma:** Si el `Switch` se desactiva, el sistema cancela la alarma activa en el dispositivo para no enviar notificaciones.
- [ ] **Mensaje de Retroalimentación:** Se muestra un `Snackbar` informando al usuario que la configuración fue guardada de manera exitosa.