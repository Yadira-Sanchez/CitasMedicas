# Spec Técnico: RF17 y RF18 - Confirmación, Omisión y Posposición de Medicamentos

## 1. Información General
* **Código y Nombre:** RF17 (Confirmación y omisión de medicamento) y RF18 (Posponer recordatorio).
* **Módulo:** Módulo 2 - Gestión de Medicamentos y Alertas.
* **Objetivo:** Permitir al usuario confirmar, omitir o posponer un medicamento directamente desde la interfaz de alerta o notificación, asegurando persistencia real en Cloud Firestore (`yaFueTomado`) y reprogramación de alarmas nativas en el sistema mediante `AlarmManager`.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** confirmar la toma de mi medicamento, omitirla o posponerla desde la pantalla de alerta  
**Para** llevar un control preciso de mi tratamiento y recibir un nuevo aviso en el horario ajustado si lo necesito.

## 3. Alcance y Límites
* **Incluye:**
    * **Pantalla de Alerta / Detalle (`PantallaAlertaMedicamento.kt`):**
        * Interfaz visual con los datos del medicamento (nombre, dosis, instrucciones).
        * **Botón Confirmar Toma:** Acción principal en verde (`Color(0xFF2E7D32)`) que actualiza el estado de la toma a realizada.
        * **Botón Posponer 10 min:** Acción secundaria en ámbar (`Color(0xFFF57C00)`) que programa un nuevo aviso.
        * **Enlace Omitir:** Opción para descartar la dosis del periodo actual.
    * **Persistencia Real (`RF17`):** Actualización inmediata en Cloud Firestore del campo correspondiente al estado del medicamento (`yaFueTomado = true` o registro en historial).
    * **Reprogramación Real (`RF18`):** Uso del `AlarmManager` de Android para recalcular los milisegundos exactos (ej. `System.currentTimeMillis() + 10 * 60 * 1000`) y disparar una nueva alerta transcurrido el tiempo.
* **No incluye:**
    * Modificación de la hora base original del tratamiento de forma permanente al posponer (la posposición es temporal para el evento actual).

## 4. Especificación de Componentes y UI (`PantallaAlertaMedicamento.kt`)

| Sección / Elemento | Componente Visual | Datos / Lógica asociada | Acción |
| :--- | :--- | :--- | :--- |
| **Cabecera** | `Row` + `Icon` + `Text` | Identificador visual de alerta | Visual en solitario |
| **Título Principal** | `Text` (Titular) | *"Alerta de Medicamento"* | Visual en solitario |
| **Tarjeta Central** | `Card` elevada (16.dp) | Nombre, concentración y dosis del medicamento | Muestra el detalle del medicamento |
| **Botón Confirmar** | `Button` (Verde) | Actualiza Firestore (`yaFueTomado = true`) | Muestra `Snackbar`, registra y cierra la pantalla |
| **Botón Posponer** | `Button` (Ámbar) | Configura nueva alarma en `AlarmManager` (+10 min) | Muestra `Snackbar`, reprograma y cierra la pantalla |
| **Enlace Omitir** | `TextButton` discreto | Registra estado omitido / descarta aviso | Cierra la alerta sin alterar tomas futuras |

---

## 5. Arquitectura y Componentes Técnicos (MVVM + Firebase + Android Nativo)

* **Vista (UI):** `PantallaAlertaMedicamento.kt` (`ui.medicamentos`) utilizando tipografía limpia (`FontFamily.Default`).
* **ViewModel:** `MedicamentoViewModel.kt` o gestor de lógica encargado de comunicarse con la base de datos.
* **Operación Firestore (`RF17`):**
  ```kotlin
  firestore.collection("usuarios")
      .document(uid)
      .collection("medicamentos")
      .document(id)
      .update("yaFueTomado", true)


- Gestión de Alarmas (**RF18**): Implementación de `AlarmManager.setExactAndAllowWhileIdle()` apuntando al `BroadcastReceiver` correspondiente con el nuevo intervalo de tiempo en milisegundos.

## 6. Criterios de Aceptación (Pruebas Manuales)
- [ ] **[RF17] Confirmación Exitosa:** Al pulsar el botón "Confirmar Toma", el estado del medicamento se actualiza de forma real en Firestore y la interfaz muestra un `Snackbar` de confirmación.
- [ ] **[RF17] Omisión de Dosis:** Al pulsar "Omitir esta dosis", el sistema descarta el aviso actual limpiando la pantalla sin alterar el registro histórico general de forma incorrecta.
- [ ] **[RF18] Posponer Funcional:** Al pulsar "Posponer 10 min", el sistema interactúa con el `AlarmManager` y programa un nuevo recordatorio exactamente para 10 minutos después.
- [ ] **[UI Homogénea]** La pantalla mantiene la coherencia visual con el resto de la aplicación, utilizando los colores institucionales (verde y ámbar) y componentes estructurados verticalmente en Compose.