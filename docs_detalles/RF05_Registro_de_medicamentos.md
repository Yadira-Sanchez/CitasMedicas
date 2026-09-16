# Spec Técnico: RF05 - Registro de Medicamentos

## 1. Información General
- **Código y Nombre:** RF05 - Registro de medicamentos
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Permitir al usuario registrar un medicamento detallando su nombre, dosis, frecuencia de toma e indicaciones.

## 2. Historia de Usuario
**Como** usuario con tratamiento médico  
**Quiero** registrar la información de mis medicamentos  
**Para** mantener un control de mis dosis y programar futuros recordatorios.

## 3. Alcance y Límites
- **Incluye:**
   - Formulario en Jetpack Compose con: Nombre del medicamento, Dosis (ej: 500mg, 1 pastilla), Frecuencia (ej: Cada 8 horas, Diario, Personalizado) y Notas/Indicaciones opcionales.
   - Asignación de hora inicial de toma mediante un selector de hora (`TimePicker`).
   - Lógica de frecuencia "Personalizada" con valor dinámico (ej: Cada X días/horas).
   - Almacenamiento en Cloud Firestore vinculado al `UID` del usuario activo.
- **No incluye:**
   - Programación de notificaciones locales del sistema (eso corresponde al RF08/RF13).

## 4. Especificación de UI y Formulario

| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Nombre del medicamento** | Sí | `.trim()`, no vacío, mínimo 2 caracteres | `OutlinedTextField` | `"Ingresa el nombre del medicamento."` |
| **Dosis** | Sí | `.trim()`, no vacío (ej: "1 pastilla", "10 ml") | `OutlinedTextField` | `"Especifica la dosis."` |
| **Frecuencia** | Sí | Seleccionar una opción válida | `ExposedDropdownMenu` / `FilterChip` | `"Selecciona una frecuencia."` |
| **Hora de inicio** | Sí | Debe ser una hora válida seleccionada | `OutlinedTextField` con clic para `TimePicker` | `"Selecciona la hora de la primera toma."` |
| **Notas / Indicaciones** | No | Opcional (ej: "Tomar con alimentos") | `OutlinedTextField` (multilínea) | N/A |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial:** Formulario limpio con valores por defecto.
- **Estado de Carga:** Muestra indicador de progreso mientras guarda el documento en Firestore.
- **Estado de Error:** Muestra un mensaje amigable si falla la conexión a Firestore o si hay campos requeridos faltantes.
- **Estado de Éxito:** Muestra mensaje de confirmación (`"Medicamento guardado correctamente"`) y limpia el formulario o regresa al listado.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaNuevoMedicamento.kt` (Ubicación: `ui.medicamentos`)
- **ViewModel:** `MedicamentoViewModel.kt`
- **Modelo de datos:** `Medicamento.kt` (Ubicación: `data`)
- **Backend:** Cloud Firestore guardando en `usuarios/{uid}/medicamentos/`

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Guardado Correcto:** Llenar todos los datos obligatorios, guardar y confirmar que el documento aparezca en la consola de Firestore dentro del `UID` del usuario.
- [ ] **Validación Obligatoria:** Intentar guardar con el nombre en blanco y comprobar que la interfaz bloquee la acción.
- [ ] **Selección de Hora:** Verificar que al tocar el campo de hora se abra el diálogo de `TimePicker` correctamente.