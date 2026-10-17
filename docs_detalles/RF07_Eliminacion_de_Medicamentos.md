# Spec Técnico: RF07 - Eliminación de Medicamentos

## 1. Información General
- **Código y Nombre:** RF07 - Eliminación de medicamentos
- **Módulo:** Módulo 2 - Gestión de Medicamentos
- **Objetivo:** Permitir al usuario eliminar un medicamento de su lista cuando ya no lo necesite, requiriendo confirmación previa y borrando el documento correspondiente de Cloud Firestore para dejar de generar sus recordatorios.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** eliminar un medicamento registrado  
**Para** mantener mi lista limpia cuando ya no necesite realizar su seguimiento y evitar recibir futuros recordatorios asociados.

## 3. Alcance y Límites
- **Incluye:**
    - Opción o botón visible de **Eliminar** (icono de papelera o botón de acción destacado en color de advertencia/rojo) en la tarjeta o pantalla de detalle del medicamento (`RF14`).
    - **Diálogo de Confirmación Obligatorio (`AlertDialog`):** Solicita confirmación explícita al usuario antes de proceder con el borrado.
    - Borrado físico y definitivo del documento en Cloud Firestore en la ruta `usuarios/{uid}/medicamentos/{id}` utilizando `.delete()`.
    - Cancelación/eliminación de los recordatorios asociados a dicho medicamento en la base de datos para prevenir notificaciones futuras.
    - Notificación de retroalimentación en la interfaz mediante `Snackbar`: *"Medicamento eliminado correctamente."*.
    - Actualización reactiva e inmediata de la lista de medicamentos en pantalla (`RF14`), desapareciendo el registro eliminado.
- **No incluye:**
    - Papelera de reciclaje ni opción de desarmar/restaurar un medicamento una vez eliminado.

## 4. Especificación de UI y Diálogo de Confirmación

### Diálogo de Confirmación de Eliminación (`AlertDialog`)

- **Despliegue:** Se muestra inmediatamente al presionar la opción o botón "Eliminar" de cualquier medicamento.
- **Componente Visual:** `AlertDialog` de Material 3 con tipografía del sistema (`FontFamily.Default`).
- **Estructura del Diálogo:**
    - **Título:** `"Eliminar medicamento"`
    - **Mensaje:** `"¿Estás seguro de que deseas eliminar [Nombre del Medicamento]? Esta acción borrará el registro de tu base de datos y no se podrá deshacer."`
    - **Botón Confirmar ("Eliminar"):**
        - Estilo: Color destacado / Rojo de advertencia (`ButtonDefaults.buttonColors`).
        - Acción: Ejecuta la función de borrado en Firestore (`.delete()`), cierra el diálogo, muestra el `Snackbar` *"Medicamento eliminado correctamente."* y actualiza la lista.
    - **Botón Cancelar ("Cancelar"):**
        - Estilo: `OutlinedButton` o `TextButton`.
        - Acción: Cierra el cuadro de diálogo inmediatamente sin realizar ninguna modificación en Firestore.

---

## 5. Comportamiento y Estados de Pantalla

- **Estado Inicial:** Usuario visualiza la lista o detalle del medicamento con la opción de eliminar presente.
- **Estado Diálogo Abierto:** Muestra el `AlertDialog` bloqueando el fondo para evitar acciones accidentales.
- **Estado de Carga en Borrado (`Loading`):** Mientras Firestore ejecuta la operación `.delete()`, el botón de confirmación del diálogo se desactiva y muestra un indicador de carga (`CircularProgressIndicator`).
- **Estado de Error:** Si la operación falla por problemas de conexión a internet, se cierra el diálogo y se despliega un mensaje: *"No se pudo eliminar el medicamento. Revisa tu conexión."*.
- **Estado de Éxito (`Success`):** El documento se remueve de la base de datos, la interfaz reactiva remueve el elemento de la lista sin reiniciar la pantalla y se muestra el `Snackbar` *"Medicamento eliminado correctamente."*.

---

## 6. Arquitectura (MVVM + Firebase Firestore)

- **Vista (UI):** `PantallaMedicamentos.kt` / `PantallaDetalleMedicamento.kt` (`ui.medicamentos`)
- **ViewModel:** `MedicamentoViewModel.kt` (`ui.medicamentos`)
- **Ruta Firestore:** `firestore.collection("usuarios").document(uid).collection("medicamentos").document(medicamentoId).delete()`

---

## 7. Criterios de Aceptación (Pruebas Manuales)

- [ ] **Despliegue de Confirmación:** Al tocar la opción de "Eliminar", se despliega la ventana flotante (`AlertDialog`) pidiendo confirmación explícita.
- [ ] **Acción de Cancelar:** Al presionar "Cancelar" en el diálogo, el cuadro se cierra y el medicamento se mantiene intacto en la lista y en Firestore.
- [ ] **Borrado Real en Firestore:** Al presionar "Eliminar" dentro del diálogo de confirmación, la entrada desaparece en tiempo real de la consola de Firebase Firestore (`usuarios/{uid}/medicamentos/{id}`).
- [ ] **Actualización Reactiva de UI:** El medicamento desaparece de la pantalla del usuario inmediatamente sin necesidad de recargar la app.
- [ ] **Mensaje de Éxito:** Se muestra un `Snackbar` en la parte inferior de la pantalla informando: *"Medicamento eliminado correctamente."*.