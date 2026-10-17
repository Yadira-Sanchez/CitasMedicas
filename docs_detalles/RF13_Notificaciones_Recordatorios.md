# Spec Técnico: RF13 - Notificaciones y Recordatorios

## 1. Información General
- **Código y Nombre:** RF13 - Notificaciones y recordatorios
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Disparar un recordatorio a la hora exacta mediante una notificación flotante superior (Heads-Up Banner) no intrusiva con botones de acción directa, permitiendo al usuario confirmar, posponer u omitir la toma sin interrumpir sus actividades en otras aplicaciones, y abriendo la pantalla detallada de alerta únicamente si interactúa con la notificación o está dentro de la app.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** recibir un aviso sutil en la parte superior de mi pantalla a la hora programada  
**Para** ver mi medicamento y dosis con opciones rápidas de confirmación o posposición con un solo toque, sin que interrumpa las aplicaciones que estoy usando.

## 3. Alcance y Límites
- **Incluye:**
    - **Notificación Flotante Superior (Heads-Up Notification):**
        - Se dispara a la hora exacta fija en `AlarmManager`.
        - Aparece como un banner compacto en la parte superior de la pantalla usando prioridad alta (`NotificationManager.IMPORTANCE_HIGH`).
        - Incluye título: *"¡Hora de tomar tu medicamento!"*, nombre del medicamento y dosis.
        - **Botones de Acción Directa (en la misma notificación):**
            1. **Confirmar:** Actualiza `yaFueTomado = true` en Cloud Firestore inmediatamente en segundo plano.
            2. **Posponer 10 min:** Reprograma la alarma en `AlarmManager` para sonar en 10 minutos.
            3. **Omitir:** Descarta la notificación sin realizar cambios.
    - **Pantalla / Modal de Alerta Completa (`PantallaAlertaMedicamento.kt`):**
        - Se abre si el usuario toca el cuerpo de la notificación o si ya se encuentra navegando dentro de la app a la hora fijada.
        - Título: *"Alarma de Medicamento"* con icono de salud.
        - Nombre del medicamento y concentración en texto grande y resaltado.
        - Tarjeta central (`Card`) con dosis exacta e instrucciones de toma.
        - Botonera interactiva de Material 3:
            - **Confirmar Toma:** Botón principal de alto contraste en verde (`Color(0xFF2E7D32)`).
            - **Posponer 10 min:** Botón secundario en color ámbar (`Color(0xFFF57C00)`).
            - **Omitir esta dosis:** Enlace de texto discreto al pie.
    - Notificaciones de retroalimentación en la interfaz mediante `Snackbar`:
        - *"¡Toma registrada con éxito!"*
        - *"Recordatorio pospuesto 10 minutos"*
        - *"Dosis omitida"*
- **No incluye:**
    - Alertas intrusivas que bloqueen la pantalla completa sobre otras aplicaciones (pantalla completa forzada).

## 4. Especificación de Componentes y UI

### A. Banner de Notificación Flotante Superior (Heads-Up)
| Elemento | Componente de Android | Contenido / Acción |
| :--- | :--- | :--- |
| **Canal** | `NotificationChannel` | Prioridad `IMPORTANCE_HIGH` con sonido y vibración |
| **Título** | Text en notificación | *"¡Hora de tomar tu medicamento!"* |
| **Cuerpo** | Text en notificación | Nombre del medicamento + Dosis (ej: *"Paracetamol 500mg - 1 Pastilla"*) |
| **Acción 1** | `Notification.Action` | **Confirmar:** `PendingIntent` ejecuta borrado/actualización en Firestore en segundo plano |
| **Acción 2** | `Notification.Action` | **Posponer:** `PendingIntent` suma +10 min al `AlarmManager` |

---

### B. Pantalla / Modal de Alerta Completa (`PantallaAlertaMedicamento`)
| Sección / Elemento | Componente Visual | Datos (Firestore / Intent) | Acción |
| :--- | :--- | :--- | :--- |
| **Cabecera** | `Row` + `Icon` + `Text` | *"Alarma de Medicamento"* | Solo visual |
| **Título Principal** | `Text` (Typography Headline) | *"¡Hora de tomar tu medicamento!"* | Solo visual |
| **Medicamento** | `Text` de gran tamaño | Nombre + Concentración | Solo visual |
| **Tarjeta Central** | `Card` elevada (16.dp) | Dosis e Instrucciones de toma | Muestra el detalle completo |
| **Botón Confirmar** | `Button` (Verde) | Texto: *"Confirmar Toma"* | Firestore `yaFueTomado = true`, muestra `Snackbar` y cierra |
| **Botón Posponer** | `Button` (Ámbar) | Texto: *"Posponer 10 min"* | Reprograma `AlarmManager` +10 min y cierra |
| **Enlace Omitir** | `TextButton` discreto | Texto: *"Omitir esta dosis"* | Registra dosis omitida y cierra |

---

## 5. Comportamiento y Flujo del Sistema

[Hora Exacta Programada] ──> BroadcastReceiver (MedicamentoReceiver) │ ┌─────────────────── ┴───────────────────┐ ▼ ▼ [App en Segundo Plano / Cerrada] [App en Primer Plano] │ │ Notificación Flotante Superior Abre Pantalla/Modal de Alerta (Heads-Up Banner) │ │ │ ┌─────────────┼─────────────┐ │ ▼ ▼ ▼ │ [Confirmar] [Posponer] [Tocar Banner] ────────────────┘ │ │

Firestore AlarmManager

Actualizar +10 min

---

## 6. Arquitectura (MVVM + Firebase + Android Native)

- **Vista (UI):** `PantallaAlertaMedicamento.kt` (`ui.medicamentos` / `ui.notificaciones`)
- **Receiver:** `MedicamentoReceiver.kt` (Capta el disparo de `AlarmManager`, crea la notificación flotante o abre la pantalla de alerta).
- **Helper:** `NotificationHelper.kt` (Configura el `NotificationChannel` de prioridad alta).
- **ViewModel:** `MedicamentoViewModel.kt` (`ui.medicamentos`)
- **Operación Firestore:** `firestore.collection("usuarios").document(uid).collection("medicamentos").document(id).update("yaFueTomado", true)`

---

## 7. Criterios de Aceptación (Pruebas Manuales)

- [ ] **Aviso No Intrusivo:** Al sonar la hora exacta con otra app abierta, aparece el banner flotante en la parte superior sin bloquear la pantalla ni interrumpir la actividad.
- [ ] **Acción Confirmar en Banner:** Al pulsar "Confirmar" directamente en la notificación flotante, se actualiza `yaFueTomado` en Firestore sin necesidad de abrir la app.
- [ ] **Acción Posponer en Banner:** Al pulsar "Posponer 10 min" en la notificación flotante, la alarma se programa automáticamente en `AlarmManager` para dentro de 10 minutos.
- [ ] **Despliegue de Detalle al Tocar Banner:** Al hacer clic sobre el cuerpo de la notificación flotante, se abre la pantalla o modal `PantallaAlertaMedicamento.kt`.
- [ ] **Visualización Completa:** La pantalla de detalle usa la tipografía predeterminada del sistema (`FontFamily.Default`) y muestra el nombre en grande, la tarjeta con dosis e instrucciones, y la botonera verde/ámbar/texto.