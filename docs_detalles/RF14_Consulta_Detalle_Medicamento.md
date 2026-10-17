# Spec Técnico: RF14 - Consulta de Detalles del Medicamento

## 1. Información General
- **Código y Nombre:** RF14 - Consulta de detalles del medicamento
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Permitir al usuario seleccionar un medicamento de su lista y visualizar sus detalles completos (nombre, tipo, dosis, frecuencia, horario programado y rango de fechas) consultando los datos en tiempo real desde Cloud Firestore.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** seleccionar un medicamento de mi lista  
**Para** consultar de forma clara y detallada su información (nombre, dosis, frecuencia y horario programado).

## 3. Alcance y Límites
- **Incluye:**
    - Consulta en tiempo real del documento seleccionado en `usuarios/{uid}/medicamentos/{id}` en Cloud Firestore.
    - Despliegue de los detalles en una tarjeta/modal o pantalla de detalle en Jetpack Compose.
    - Visualización organizada de los campos:
        - Nombre del medicamento.
        - Tipo (Pastilla, Jarabe, Inyección, Gotas).
        - Dosis registrada (ej: "1 tableta", "10 ml").
        - Frecuencia (ej: "Cada 8 horas").
        - Horario programado (Hora de primera toma, ej: "08:00 AM").
        - Fechas de tratamiento (Fecha inicio y fecha fin opcional).
    - Presencia de los puntos de acceso/botones visibles para **Editar** (`RF06`) y **Eliminar** (`RF07`) (sin ejecutar sus flujos en este RF).
- **No incluye:**
    - Edición directa de campos desde la vista de detalle.
    - Borrado en Firestore (eso corresponde al RF07).

## 4. Especificación de UI y Componentes

| Campo / Elemento | Componente Visual | Datos / Origen (Firestore) | Regla de Visualización |
| :--- | :--- | :--- | :--- |
| **Nombre** | `Text` (Título en negrita) | Campo `nombre` | Tipografía por defecto (`FontFamily.Default`) |
| **Tipo** | `AssistChip` / `Badge` | Campo `tipo` | Muestra el tipo (Pastilla, Jarabe, etc.) |
| **Dosis** | `Text` | Campo `dosis` | Muestra la cantidad (ej. "500 mg") |
| **Frecuencia** | `Text` | Campo `frecuencia` | Muestra el intervalo entre tomas |
| **Horario Programado** | `Text` con icono de reloj | Campo `horaToma` | Muestra la hora de la toma |
| **Fechas** | `Text` con icono de calendario | Campos `fechaInicio` y `fechaFin` | Muestra rango o fecha de inicio |
| **Botón Regresar** | `IconButton` (Flecha atrás) | N/A | Vuelve a la lista general |
| **Botón Editar** | `OutlinedButton` | N/A | Acceso directo para RF06 |
| **Botón Eliminar** | `IconButton` / `Button` | N/A | Acceso directo para RF07 |

## 5. Comportamiento y Estados de Pantalla
- **Estado de Carga (`Loading`):** Muestra un `CircularProgressIndicator` mientras se obtiene la información del medicamento desde Firestore.
- **Estado de Contenido (`Success`):** Muestra la tarjeta con todos los campos organizados e íconos descriptivos.
- **Estado de Error / No Encontrado:** Muestra el mensaje: *"No se pudo cargar la información del medicamento."* con opción de reintentar.

## 6. Arquitectura (MVVM + Firebase Firestore)
- **Vista (UI):** `PantallaDetalleMedicamento.kt` (o Modal en `PantallaMedicamentos.kt`) (`ui.medicamentos`)
- **ViewModel:** `MedicamentoViewModel.kt` (`ui.medicamentos`)
- **Modelo:** `Medicamento.kt` (`data`)
- **Ruta Firestore:** `usuarios/{uid}/medicamentos/{medicamentoId}`

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Selección de Medicamento:** Al tocar una tarjeta de la lista en `PantallaMedicamentos.kt`, se abre la vista o modal de detalle con la información del medicamento elegido.
- [ ] **Campos Completos:** Verificar que se muestre el nombre, dosis, tipo, frecuencia, hora programada y fechas correspondientes.
- [ ] **Botones de Acción Presentes:** Confirmar la presencia de los botones para editar y eliminar dentro de la pantalla de detalle.
- [ ] **Estilo de Texto:** Toda la pantalla usa la tipografía predeterminada del sistema (`FontFamily.Default`).