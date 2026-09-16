# Spec Técnico: RF12 - Visualización del Calendario

## 1. Información General
- **Código y Nombre:** RF12 - Visualización del calendario
- **Módulo:** Módulo 4 - Dashboard y Calendario
- **Objetivo:** Ofrecer una vista de calendario mensual y diaria que unifique y muestre los medicamentos programados y las citas médicas del usuario.

## 2. Historia de Usuario
**Como** usuario  
**Quiero** visualizar mis medicamentos y citas en un calendario  
**Para** organizar mis actividades médicas de manera sencilla y centralizada.

## 3. Alcance y Límites
- **Incluye:**
    - Componente de calendario interactivo (vista mensual/semanal).
    - Selección de cualquier fecha del calendario.
    - Indicadores visuales (puntos/marcas de color) en los días que tienen actividades agendadas.
    - Lista de actividades del día seleccionado dividida en dos secciones: **Medicamentos** y **Citas Médicas**.
    - Clic en una actividad para abrir un diálogo o pantalla con los detalles (`RF14` y `RF15`).
- **No incluye:**
    - Edición directa de la cita o medicamento desde la vista rápida del calendario (redirecciona a las pantallas correspondientes RF06/RF10).

## 4. Especificación de UI y Componentes

| Elemento / Componente | Descripción | Acción / Estado |
| :--- | :--- | :--- |
| **Calendario Interactivo** | Vista de mes/semana con selector de día | Al tocar un día, resalta la fecha y actualiza la lista inferior |
| **Indicadores de Actividad** | Puntos de color sobre los días (ej: Azul = Cita, Verde = Medicamento) | Indica presencia de registros en esa fecha |
| **Lista de Actividades** | `LazyColumn` con tarjetas clasificadas por hora | Muestra las tareas programadas para la fecha seleccionada |
| **Tarjeta de Medicamento** | Muestra nombre, hora y dosis | Al hacer clic, abre detalle del medicamento (`RF14`) |
| **Tarjeta de Cita Médica** | Muestra doctor, especialidad y hora | Al hacer clic, abre detalle de la cita (`RF15`) |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial:** Selecciona automáticamente la fecha actual (`Hoy`) y carga sus actividades.
- **Estado Sin Actividades:** Si el día seleccionado no tiene registros, muestra: *"No hay medicamentos ni citas para esta fecha."*
- **Estado de Carga:** Muestra un indicador discreto mientras consulta los documentos de Firestore para el mes visible.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaCalendario.kt` (Ubicación: `ui.dashboard`)
- **ViewModel:** `CalendarioViewModel.kt`
- **Backend/Persistencia:** Consulta combinada en Cloud Firestore a las colecciones `usuarios/{uid}/medicamentos` y `usuarios/{uid}/citas`.

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Navegación por Fechas:** Seleccionar un día futuro y verificar que la lista cambie a los eventos de esa fecha.
- [ ] **Visualización Mixta:** Comprobar que en un mismo día se muestren correctamente tanto los medicamentos como las citas.
- [ ] **Consulta de Detalle:** Tocar una cita o medicamento y verificar que muestre la información completa del registro.