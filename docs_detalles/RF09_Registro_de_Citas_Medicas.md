# Spec Técnico: RF09 - Registro de Citas Médicas

## 1. Información General
- **Código y Nombre:** RF09 - Registro de citas médicas
- **Módulo:** Módulo 3 - Gestión de Citas Médicas
- **Objetivo:** Permitir al usuario registrar una nueva cita médica especificando el motivo/especialidad, el doctor, la fecha, la hora, la ubicación/lugar y la opción de configurar un recordatorio, guardando los datos en Cloud Firestore para listar la cita en la sección de próximas citas médicas.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** registrar una cita indicando su motivo, doctor, fecha, hora, lugar y la opción de activar un recordatorio  
**Para** organizar mis próximas citas médicas y no olvidar mis compromisos de salud.

## 3. Alcance y Límites
- **Incluye:**
    - Formulario estructurado en tarjetas (`Card` elevadas) para mantener el orden visual.
    - Campos del formulario con validaciones correspondientes:
        - **Motivo / Especialidad:** Campo de texto obligatorio (ej: "Chequeo General", "Cardiología").
        - **Doctor / Profesional:** Campo de texto opcional/recomendado (ej: "Dr. Roberto Gómez").
        - **Fecha de la Cita:** Seleccionable mediante selector nativo `DatePickerDialog` (obligatorio).
        - **Hora de la Cita:** Seleccionable mediante selector nativo `TimePickerDialog` (obligatorio).
        - **Lugar / Ubicación:** Campo de texto obligatorio (ej: "Hospital Central, Cons. 302").
        - **Configuración de Recordatorio (`Switch`):** Opción para activar o desactivar aviso nativo previa a la cita.
        - **Notas / Indicaciones adicionales:** Campo de texto opcional (ej: "Ir en ayunas de 8 horas").
    - Almacenamiento directo del documento en Cloud Firestore en la ruta `usuarios/{uid}/citas/`.
    - Programación opcional de alarma/recordatorio en `AlarmManager` si la opción de recordatorio está activada.
    - Inserción y actualización reactiva en el listado de **Próximas Citas** (`RF04` / `RF12`).
    - Mensaje de confirmación de guardado mediante `Snackbar`: *"Cita médica registrada correctamente."*.
- **No incluye:**
    - Edición de citas creadas (corresponde a RF10).
    - Eliminación de citas (corresponde a RF11).

## 4. Especificación de UI y Estructura por Tarjetas

### Tarjeta 1: Información Principal de la Cita
| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Motivo / Especialidad** | Sí | `.trim()`, no vacío, mínimo 2 caracteres | `OutlinedTextField` | `"Ingresa el motivo o especialidad de la cita."` |
| **Doctor / Profesional** | No | Opcional | `OutlinedTextField` | N/A |
| **Lugar / Ubicación** | Sí | `.trim()`, no vacío | `OutlinedTextField` | `"Ingresa el lugar o dirección de la cita."` |

### Tarjeta 2: Fecha, Hora y Recordatorio
| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Fecha de la Cita** | Sí | Debe ser una fecha válida seleccionada | `OutlinedTextField` + `DatePickerDialog` | `"Selecciona la fecha de la cita."` |
| **Hora de la Cita** | Sí | Debe ser una hora válida seleccionada | `OutlinedTextField` + `TimePickerDialog` | `"Selecciona la hora de la cita."` |
| **Activar Recordatorio**| Sí | `Boolean` (`true` / `false`) | `Switch` (Material 3) | Activa aviso nativo del dispositivo |
| **Notas adicionales** | No | Opcional | `OutlinedTextField` (multilínea) | N/A |

---

## 5. Comportamiento y Estados de Pantalla

- **Estado Inicial:** Formulario limpio estructurado en 2 tarjetas `Card`, tipografía predeterminada del sistema (`FontFamily.Default`), botón *"Guardar Cita"* habilitado.
- **Estado de Carga (`Loading`):** Muestra un `CircularProgressIndicator` en el botón mientras se escribe el documento en Cloud Firestore.
- **Estado de Error:** Despliega un `Snackbar` o mensaje en texto rojo si falta llenar algún campo obligatorio o si ocurre un problema de conexión con Firebase.
- **Estado de Éxito (`Success`):** Notifica al usuario con el `Snackbar` *"Cita médica registrada correctamente."*, limpia el formulario y navega de regreso a la pantalla anterior o al listado de citas.

---

## 6. Arquitectura (MVVM + Firebase Firestore)

- **Vista (UI):** `PantallaNuevaCita.kt` (`ui.citas`)
- **ViewModel:** `CitaViewModel.kt` (`ui.citas`)
- **Modelo de datos:** `CitaMedica.kt` (`data`)
- **Ruta Firestore:** `firestore.collection("usuarios").document(uid).collection("citas")`

---

## 7. Criterios de Aceptación (Pruebas Manuales)

- [ ] **Validación Bloqueante:** Intentar guardar sin haber ingresado el motivo, la fecha, la hora o el lugar despliega los mensajes de error correspondientes sin consultar a Firestore.
- [ ] **Selectores Nativo:** Al pulsar sobre los campos de fecha y hora se despliegan los diálogos nativos `DatePickerDialog` y `TimePickerDialog`.
- [ ] **Guardado en Firestore:** Al llenar los campos obligatorios y presionar *"Guardar Cita"*, se crea un nuevo documento dentro de la colección `usuarios/{uid}/citas/` en la consola de Firebase.
- [ ] **Aparición en Lista:** La nueva cita aparece inmediatamente en la lista de citas agendadas/próximas citas.
- [ ] **Mensaje de Éxito:** Se muestra un `Snackbar` confirmando *"Cita médica registrada correctamente."*.
- [ ] **Estilo de Texto:** Toda la pantalla usa estrictamente la fuente predeterminada del sistema (`FontFamily.Default`).