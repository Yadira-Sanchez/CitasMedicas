# Spec Técnico: RF04 - Visualización de Pantalla Principal (Dashboard)

## 1. Información General
- **Código y Nombre:** RF04 - Visualización de pantalla principal
- **Módulo:** Módulo 4 - Dashboard e Inicio
- **Objetivo:** Mostrar un resumen dinámico e interactivo del día del usuario consultando datos reales desde Firestore, incluyendo tomas de medicamentos (`usuarios/{uid}/medicamentos`), próxima cita médica (`usuarios/{uid}/citas`), selector de fechas de la semana y un modal de acción rápida al seleccionar cualquier toma.

## 2. Historia de Usuario
**Como** usuario  
**Quiero** acceder a una pantalla principal limpia y organizada  
**Para** consultar mis medicamentos del día, gestionar mis tomas rápidamente desde un menú modal, visualizar mi próxima cita médica y navegar ágilmente por la aplicación.

## 3. Alcance y Límites
- **Incluye:**
  - Saludo personalizado obteniendo el campo `nombre` desde `usuarios/{uid}` en Firestore.
  - Tipografía predeterminada del sistema (`FontFamily.Default` / Sans-Serif) en todos los títulos, tarjetas y listas.
  - Sincronización o consulta directa de medicamentos desde `usuarios/{uid}/medicamentos`.
  - Sincronización o consulta directa de la próxima cita desde `usuarios/{uid}/citas`.
  - Selector horizontal de fechas de la semana actual para filtrar las tomas según la fecha elegida.
  - **Modal de Acción Rápida (`ModalBottomSheet`):** Al presionar cualquier tarjeta de medicamento de "Tomas de Hoy", se despliega un panel inferior con los detalles de la toma y tres acciones directas:
    1. **Tomar:** Actualiza en Firestore `yaFueTomado = true` y registra la hora real de la toma.
    2. **Pasar / Omitir:** Marca la toma como saltada (`estado = "omitido"`).
    3. **Reprogramar:** Despliega un `TimePicker` para ajustar la hora de la toma para ese día.
  - `FloatingActionButton` (FAB) circular con icono de suma (`+`) en la esquina inferior derecha (`Alignment.BottomEnd`) para navegación rápida al registro de nuevo medicamento (`RF05`).
  - Barra de navegación inferior (`NavigationBar`) con accesos a: Inicio, Medicamentos (Inventario completo), Citas y Perfil.
- **No incluye:**
  - Datos estáticos de prueba (mock / hardcoded).
  - Lógica de conversación del Chatbot (corresponde a RF23).

## 4. Especificación de UI y Componentes

| Sección / Elemento | Componente Visual | Datos / Estado (Firestore) | Acción al Interactuar |
| :--- | :--- | :--- | :--- |
| **Cabecera** | `Row` + `Text` | Campo `nombre` de `usuarios/{uid}` | Abrir Perfil (`RF21`) |
| **Buscador** | `OutlinedTextField` / `SearchBar` | Filtro en memoria sobre medicamentos/citas | Filtra la lista del día |
| **Selector de Fechas** | `LazyRow` con `FilterChip` | Días de la semana actual (`LocalDate`) | Consulta o filtra las tomas de esa fecha |
| **Tomas de Hoy** | `LazyColumn` + `Card` | Documentos de `usuarios/{uid}/medicamentos` | Clic en la tarjeta despliega el `ModalBottomSheet` |
| **Modal de Acción** | `ModalBottomSheet` | Detalle del medicamento seleccionado | Botones: **Tomar**, **Pasar** y **Reprogramar** |
| **Próxima Cita** | `Card` elevado | Documento más próximo de `usuarios/{uid}/citas` | Botones de acción ("Reprogramar" / "Confirmar") |
| **FAB (+) Nuevo** | `FloatingActionButton` (Inferior Der.) | Icono de Suma (`+`) | Navega a `PantallaNuevoMedicamento` (`RF05`) |
| **Navegación Inferior**| `NavigationBar` | 4 secciones principales | Cambia la pantalla actual |

## 5. Comportamiento y Estados de Pantalla
- **Estado de Carga (`Loading`):** Mientras se realiza la petición asíncrona a Firestore, se muestra un indicador de carga (`CircularProgressIndicator`).
- **Estado Vacío (`Empty`):** Si el usuario no tiene registros en Firestore para la fecha seleccionada, muestra: *"No tienes medicamentos ni citas programadas para hoy."*
- **Estado con Datos (`Success`):** Muestra el saludo con el nombre real del usuario, la lista de tomas guardadas para la fecha seleccionada y la tarjeta de la cita más cercana.
- **Interacción con Modal:** Al presionar "Tomar", "Pasar" o "Reprogramar" en el `ModalBottomSheet`, el documento correspondiente en Firestore se actualiza y la UI refleja el nuevo estado de la toma inmediatamente.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaInicio.kt` (`ui.dashboard`)
- **ViewModel:** `DashboardViewModel.kt` (`ui.dashboard`)
- **Backend/Persistencia (Firestore):**
  - **Perfil:** `firestore.collection("usuarios").document(uid)`
  - **Medicamentos del día:** `firestore.collection("usuarios").document(uid).collection("medicamentos")`
  - **Próxima cita:** `firestore.collection("usuarios").document(uid).collection("citas")`

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Nombre Real y Tipografía:** La pantalla muestra el saludo *"¡Hola, [Nombre Real]!"* usando la fuente Sans-Serif predeterminada del sistema.
- [ ] **Ubicación del FAB:** Confirmar que el botón flotante `+` esté posicionado en la esquina inferior derecha (`Alignment.BottomEnd`) sin tapar la tarjeta de la próxima cita.
- [ ] **Despliegue del Modal:** Al tocar cualquier tarjeta de medicamento de la lista "Tomas de Hoy", se abre el `ModalBottomSheet` con las opciones *Tomar*, *Pasar* y *Reprogramar*.
- [ ] **Persistencia de Acción:** Al presionar "Tomar" dentro del modal, la toma se marca con indicador verde en Firestore y la pantalla se actualiza en tiempo real.