# Spec Técnico: RF05 - Registro de Medicamentos

## 1. Información General
- **Código y Nombre:** RF05 - Registro de medicamentos
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Permitir al usuario registrar un medicamento detallando su nombre, tipo (pastilla, jarabe, inyección, gotas), dosis, frecuencia de toma, horarios e indicaciones mediante un formulario estructurado en tarjetas.

## 2. Historia de Usuario
**Como** usuario con tratamiento médico  
**Quiero** registrar la información completa de mis medicamentos de forma clara y guiada  
**Para** mantener un control riguroso de mis dosis y programar futuros recordatorios en mi calendario.

## 3. Alcance y Límites
- **Incluye:**
  - Formulario estructurado en 2 tarjetas (`Card`) principales para mejorar la legibilidad visual y evitar sobrecarga de pantalla.
  - Selección de **Tipo de Medicamento** (Pastilla, Jarabe, Inyección, Gotas) mediante chips de opción única.
  - Campos de texto estándar (fuente Sans-Serif) para Nombre, Dosis y Notas/Indicaciones opcionales.
  - Asignación de frecuencia (ej: Cada 8 horas, Diario, Personalizado) y hora inicial de toma mediante un selector de hora (`TimePicker`).
  - Selección de rango de fechas de tratamiento (*Fecha de inicio* y *Fecha de fin* opcional) mediante diálogo de fecha (`DatePicker`).
  - Almacenamiento directo en Cloud Firestore vinculado al `UID` del usuario activo (`usuarios/{uid}/medicamentos`).
- **No incluye:**
  - Programación de alarmas o notificaciones push del sistema operativo (corresponde a RF08/RF13).

## 4. Especificación de UI y Estructura por Tarjetas

### Tarjeta 1: Información General del Medicamento
| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Nombre del medicamento** | Sí | `.trim()`, no vacío, mínimo 2 caracteres | `OutlinedTextField` | `"Ingresa el nombre del medicamento."` |
| **Tipo de medicamento** | Sí | Seleccionar 1 opción (Pastilla, Jarabe, Inyección, Gotas) | `SingleChoiceSegmentedButtonRow` / `FilterChip` | `"Selecciona el tipo de medicamento."` |
| **Dosis / Cantidad** | Sí | `.trim()`, no vacío (ej: "1 pastilla", "10 ml") | `OutlinedTextField` | `"Especifica la dosis."` |

### Tarjeta 2: Programación, Horarios e Indicaciones
| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Frecuencia** | Sí | Seleccionar una opción válida | `ExposedDropdownMenu` / `FilterChip` | `"Selecciona una frecuencia."` |
| **Primera toma (Hora)** | Sí | Debe ser una hora válida seleccionada | `OutlinedTextField` con clic para `TimePicker` | `"Selecciona la hora de la primera toma."` |
| **Fecha de inicio** | Sí | Fecha válida (por defecto la fecha actual) | `OutlinedTextField` con `DatePickerDialog` | `"Selecciona la fecha de inicio."` |
| **Fecha de fin** | No | Opcional | `OutlinedTextField` con `DatePickerDialog` | N/A |
| **Notas / Indicaciones** | No | Opcional (ej: "Tomar con alimentos") | `OutlinedTextField` (multilínea) | N/A |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial:** Formulario limpio estructurado en 2 `Card` elevadas, tipografía predeterminada del sistema (Sans-Serif) y fecha actual predeterminada.
- **Estado de Carga (`Loading`):** Muestra un `CircularProgressIndicator` en el botón de guardado mientras se escribe en Firestore.
- **Estado de Error:** Despliega un `Snackbar` o mensaje en texto rojo si falta algún campo obligatorio o si ocurre un fallo de red.
- **Estado de Éxito:** Notifica la creación del medicamento, limpia el formulario reactivamente y regresa automáticamente a la pantalla anterior o al Dashboard (`RF04`).

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaNuevoMedicamento.kt` (`ui.medicamentos`)
- **ViewModel:** `MedicamentoViewModel.kt` (`ui.medicamentos`)
- **Modelo de datos:** `Medicamento.kt` (`data`)
- **Backend / Persistencia:** Cloud Firestore en la ruta `usuarios/{uid}/medicamentos/`

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Diseño Limpio:** Verificar que la interfaz esté dividida en 2 tarjetas (`Card`) sin tipografías cursivas ni desalineaciones.
- [ ] **Guardado en Firestore:** Llenar el formulario, presionar *"Guardar Medicamento"* y confirmar la aparición del nuevo documento dentro de `usuarios/{uid}/medicamentos`.
- [ ] **Validación Bloqueante:** Comprobar que no se permita guardar si el nombre o la dosis están vacíos.
- [ ] **Selectores Nativo:** Confirmar que al presionar en los campos de hora y fecha se desplieguen los diálogos nativos `TimePicker` y `DatePicker`.