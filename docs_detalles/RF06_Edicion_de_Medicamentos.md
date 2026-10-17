# Spec Técnico: RF06 - Edición de Medicamentos

## 1. Información General
- **Código y Nombre:** RF06 - Edición de medicamentos
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Permitir al usuario modificar la información, dosis, frecuencia y horarios de un medicamento registrado previamente, actualizando los datos en Cloud Firestore tras confirmar los cambios.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** modificar la información de un medicamento de mi lista  
**Para** mantener actualizados mis datos, dosis, frecuencia y horarios programados cuando mi tratamiento cambie.

## 3. Alcance y Límites
- **Incluye:**
    - Carga automática de los datos actuales del medicamento dentro del formulario de edición.
    - Campos editables:
        - Nombre del medicamento.
        - Tipo (Pastilla, Jarabe, Inyección, Gotas).
        - Dosis (ej. "1 tableta", "10 ml").
        - Frecuencia (ej. "Cada 8 horas", "Personalizado").
        - Hora programada de la primera toma (`TimePickerDialog`).
        - Fechas de inicio y fin (`DatePickerDialog`).
    - Aplicación de las mismas reglas de validación del registro (`RF05`).
    - **Diálogo de confirmación:** Al presionar "Guardar cambios", muestra un `AlertDialog` preguntando: *"¿Estás seguro de que deseas guardar los cambios realizados en este medicamento?"*.
    - Actualización directa del documento en Cloud Firestore en la ruta `usuarios/{uid}/medicamentos/{id}` usando `.update()`.
    - Notificación de retroalimentación mediante `Snackbar`: *"Medicamento actualizado correctamente."*.
    - Regreso automático a la lista o pantalla de detalles tras guardar con éxito.
- **No incluye:**
    - Creación de un nuevo documento (se modifica el documento existente mediante su `id`).
    - Programación nativa de alarmas o notificaciones del sistema (eso corresponde al `RF08` / `RF13`).

## 4. Especificación de UI y Formulario de Edición

### Estructura por Tarjetas (`Card` Elevadas)

#### Tarjeta 1: Información General
| Campo | Obligatorio | Reglas de Validación                          | Componente Visual | Mensaje de Error |
| :--- | :---: |:----------------------------------------------| :--- | :--- |
| **Nombre** | Sí | `.trim()`, no vacío, mínimo 4 caracteres      | `OutlinedTextField` | `"Ingresa el nombre del medicamento."` |
| **Tipo** | Sí | Seleccionar 1 opción (Pastilla, Jarabe, etc.) | `FilterChip` / Segmented Control | `"Selecciona el tipo de medicamento."` |
| **Dosis** | Sí | `.trim()`, no vacío                           | `OutlinedTextField` | `"Especifica la dosis."` |

#### Tarjeta 2: Programación y Horarios
| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Frecuencia** | Sí | Seleccionar una opción válida | `ExposedDropdownMenu` / Chips | `"Selecciona una frecuencia."` |
| **Hora de toma** | Sí | Hora válida seleccionada | `OutlinedTextField` + `TimePicker` | `"Selecciona la hora de la toma."` |
| **Fecha inicio** | Sí | Fecha válida | `OutlinedTextField` + `DatePicker` | `"Selecciona la fecha de inicio."` |
| **Fecha fin** | No | Opcional | `OutlinedTextField` + `DatePicker` | N/A |

---

## 5. Diálogo de Confirmación (`AlertDialog`)

- **Despliegue:** Se muestra al presionar el botón *"Guardar Cambios"* si las validaciones del formulario son correctas.
- **Título:** `"Confirmar actualización"`
- **Mensaje:** `"¿Estás seguro de que deseas guardar los cambios realizados en este medicamento?"`
- **Botón Confirmar ("Guardar"):** Ejecuta la función de actualización en Firestore (`.update()`), muestra el `Snackbar` *"Medicamento actualizado correctamente."* y regresa a la pantalla anterior.
- **Botón Cancelar ("Cancelar"):** Cierra el diálogo y mantiene al usuario en el formulario sin modificar la base de datos.

---

## 6. Comportamiento y Estados de Pantalla

- **Estado Inicial / Carga de Datos:** El formulario se abre prellenado con los valores del medicamento seleccionado.
- **Estado Carga en Envío (`Loading`):** Se desactiva el botón de guardado y se muestra un `CircularProgressIndicator` mientras Firestore procesa la actualización.
- **Estado de Error:** Muestra un mensaje en texto rojo o `Snackbar` si ocurre un fallo de conexión a la red.
- **Estado de Éxito (`Success`):** Notifica al usuario con un `Snackbar` y navega hacia la pantalla anterior.

---

## 7. Arquitectura (MVVM + Firebase Firestore)

- **Vista (UI):** `PantallaNuevoMedicamento.kt` (en modo edición) o `PantallaEditarMedicamento.kt` (`ui.medicamentos`)
- **ViewModel:** `MedicamentoViewModel.kt` (`ui.medicamentos`)
- **Ruta Firestore:** `firestore.collection("usuarios").document(uid).collection("medicamentos").document(medicamentoId).update(datos)`

---

## 8. Criterios de Aceptación (Pruebas Manuales)

- [ ] **Datos Prellenados:** Al ingresar a la pantalla de edición, los campos muestran la información actual del medicamento seleccionado.
- [ ] **Modificación de Campos:** Se pueden cambiar el nombre, la dosis, la frecuencia, la hora y las fechas sin problemas.
- [ ] **Diálogo de Confirmación:** Al presionar *"Guardar Cambios"*, se abre la ventana desplegable pidiendo confirmación.
- [ ] **Cancelación en Diálogo:** Si se presiona *"Cancelar"*, el diálogo se cierra y la base de datos no se modifica.
- [ ] **Actualización en Firestore:** Al confirmar la edición en el diálogo, los datos actualizados se guardan en la base de datos en la consola de Firebase Firestore.
- [ ] **Mensaje de Éxito:** Se muestra la alerta *"Medicamento actualizado correctamente."* y se retorna a la pantalla anterior.