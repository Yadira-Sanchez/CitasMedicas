# Módulo 2: Gestión de Medicamentos

---

# RF05 - Registro de medicamentos

## Historia de usuario
Como usuario quiero registrar un medicamento indicando su nombre, tipo, dosis, frecuencia, horario y rango de fechas para gestionar correctamente mis recordatorios.

## Objetivo
Permitir que el usuario guarde la información de un nuevo medicamento mediante un formulario adaptativo para generar las bases de los recordatorios en la aplicación.

## Alcance

### Incluye:
- Mostrar un formulario en Jetpack Compose para el registro de medicamentos.
- Solicitar el nombre del medicamento.
- Seleccionar el Tipo de medicamento (Pastilla, Jarabe, Inyección, Gotas) mediante chips/botones de selección única (`FilterChip` o `SingleChoiceSegmentedButton`).
- Solicitar la Dosis/Cantidad con **placeholder dinámico** según el tipo de medicamento seleccionado.
- Seleccionar la frecuencia mediante un menú desplegable (`ExposedDropdownMenuBox`).
- Seleccionar la hora de la primera toma mediante un selector de hora nativo (`TimePicker` de Material 3).
- Seleccionar Fecha de inicio y Fecha de fin mediante selector de fecha (`DatePicker` de Material 3).
- Validar que los campos obligatorios estén completos.
- Guardar el medicamento y programar las alertas locales/notificaciones.

### No incluye (Versión Académica V1):
- Escaneo de código de barras o foto del medicamento.
- Búsqueda en catálogo externo farmacéutico.

---

## Campos del Formulario y Placeholders Dinámicos

| Campo | Obligatorio | Tipo de Componente | Reglas de Validación | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Nombre del medicamento** | Sí | `OutlinedTextField` | No vacío, mínimo 2 caracteres, `.trim()` | `"Escribe el nombre del medicamento."` |
| **Tipo de medicamento** | Sí | Grupo de Chips (`FilterChip`) | Selección única entre: Pastilla, Jarabe, Inyección, Gotas | `"Selecciona el tipo de medicamento."` |
| **Dosis / Cantidad** | Sí | `OutlinedTextField` | No vacío, el placeholder cambia según el tipo seleccionado | `"Escribe la dosis o cantidad."` |
| **Frecuencia** | Sí | Menú desplegable (`Dropdown`) | Debe seleccionar una opción de la lista prestablecida | `"Selecciona la frecuencia de toma."` |
| **Primera toma (Hora)** | Sí | Selector de hora (`TimePicker`) | Hora válida en formato HH:mm | `"Selecciona la hora de la primera toma."` |
| **Fecha de inicio** | Sí | Selector de fecha (`DatePicker`) | Debe ser igual o posterior a la fecha actual | `"Selecciona la fecha de inicio."` |
| **Fecha de fin** | No | Selector de fecha (`DatePicker`) | Opcional. Si se ingresa, debe ser posterior a la fecha de inicio | `"La fecha de fin debe ser posterior a la de inicio."` |

---

## Lógica de Placeholders Dinámicos para Dosis

El campo **Dosis/Cantidad** debe cambiar su texto de sugerencia (*placeholder*) en tiempo real según la opción seleccionada en **Tipo**:

| Tipo Seleccionado | Icono Representativo | Placeholder Sugerido (`placeholder = { Text(...) }`) |
| :--- | :---: | :--- |
| **Pastilla** *(Por defecto)* | 💊 | `Ej. 1 tableta` o `Ej. 500 mg` |
| **Jarabe** | 🧪 / 🍶 | `Ej. 10 ml` o `Ej. 1 cucharada` |
| **Inyección** | 💉 | `Ej. 1 ampolla` o `Ej. 2.5 ml` |
| **Gotas** | 💧 | `Ej. 5 gotas` |

---

## Opciones Prestablecidas para Frecuencia

El menú desplegable de frecuencia debe ofrecer las siguientes opciones:
- Cada 4 horas
- Cada 6 horas
- Cada 8 horas
- Cada 12 horas
- Una vez al día (Cada 24 horas)
- **Personalizar** *(Habilita campos de intervalo o días específicos)*

---

## Reglas para Frecuencia Personalizada

Cuando el usuario selecciona la opción **"Personalizar"** en el menú desplegable:

1. **Despliegue Dinámico de Campos:** La UI debe mostrar un campo numérico adicional (`OutlinedTextField` con `KeyboardType.Number`) para definir el intervalo personalizado.
2. **Opciones de Intervalo Personalizado:**
    - **Por Horas:** El usuario ingresa un número de horas (ej. *"Cada 5 horas"*).
    - **Por Días:** El usuario selecciona el intervalo en días (ej. *"Cada 2 días"*).
3. **Validación del Campo Personalizado:**
    - Debe ser obligatorio si la opción elegida es "Personalizar".
    - El valor numérico ingresado debe ser mayor a 0.
    - Mensaje de error si está vacío: `"Ingresa el intervalo de tiempo personalizado."`
---

## Reglas de Interfaz y Comportamiento UI

### Selección de Tipo (Chips)
- Se debe mostrar un grupo de opciones visuales donde solo una opción puede estar activa a la vez.
- Al cambiar la selección (ej. de *Pastilla* a *Jarabe*), la variable de estado del placeholder en el ViewModel debe actualizarse automáticamente.

### Selección de Fechas (DatePicker)
- Al hacer clic en los campos "Fecha de inicio" y "Fecha de fin", se debe desplegar el modal `DatePickerDialog` de Material 3.
- El valor seleccionado debe formatearse visualmente en formato local (ej. `dd/mm/aaaa`).

---

## Reglas de Validación Detalladas

1. **Nombre:** Aplicar `.trim()` antes de procesar.
2. **Dosis:** Aplicar `.trim()`. Acepta combinaciones de números y texto libre.
3. **Validación de Fechas:**
    - La fecha de inicio no puede ser menor a la fecha actual.
    - Si el usuario asigna una fecha de fin, el sistema debe comprobar que `fechaFin >= fechaInicio`.

---
