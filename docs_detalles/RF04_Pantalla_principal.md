# Spec Técnico: RF04 - Visualización de Pantalla Principal (Dashboard)

## 1. Información General
- **Código y Nombre:** RF04 - Visualización de pantalla principal
- **Módulo:** Módulo 4 - Dashboard e Inicio
- **Objetivo:** Mostrar un resumen dinámico e interactivo del día del usuario, incluyendo tomas de medicamentos, próxima cita médica, acceso al calendario y un asistente virtual de IA.

## 2. Historia de Usuario
**Como** usuario  
**Quiero** acceder a una pantalla principal organizada  
**Para** consultar mis medicamentos del día, mi próxima cita médica, el calendario semanal y disponer de un asistente virtual de IA.

## 3. Alcance y Límites
- **Incluye:**
    - Saludo personalizado con el nombre del usuario cargado desde Firebase.
    - Barra de búsqueda local para filtrar tomas o citas del día.
    - Selector horizontal de fechas de la semana actual.
    - Enlace rápido "Ver calendario" (`RF12`).
    - Lista interactiva de "Tomas de Hoy" con estados visuales (Pendiente con botón "Tomar" vs. "Tomado" con indicador verde).
    - Tarjeta de "Próxima Cita" con detalle del doctor, fecha, hora, ubicación y acciones de "Reprogramar" y "Confirmar".
    - Botón Flotante (FAB) circular en la esquina inferior izquierda para el Asistente de IA (`RF23`).
    - Barra de navegación inferior (`NavigationBar`) con accesos a: Inicio, Medicamentos, Citas/Agenda y Perfil.
- **No incluye:**
    - Procesamiento del lenguaje natural del Chatbot (corresponde a la pantalla/diálogo del RF23).

## 4. Especificación de UI y Componentes

| Sección / Elemento | Componente Visual | Datos / Estado | Acción al Interactuar |
| :--- | :--- | :--- | :--- |
| **Cabecera** | `Row` + `Text` + `AsyncImage` | Nombre del usuario y foto de perfil | Abrir Perfil (`RF21`) |
| **Buscador** | `OutlinedTextField` / `SearchBar` | Texto de búsqueda | Filtra la lista en tiempo real |
| **Selector de Fechas** | `LazyRow` con `FilterChip` / `Card` | Días de la semana actual | Cambia el día seleccionado |
| **Tomas de Hoy** | `LazyColumn` + `Card` | Nombre, hora, dosis y estado (Pendiente/Tomado) | Botón "Tomar" actualiza a "Tomado" (`RF17`) |
| **Próxima Cita** | `Card` elevado | Doctor, especialidad, fecha, ubicación | Botones "Reprogramar" / "Confirmar" |
| **FAB Asistente IA** | `FloatingActionButton` (Inferior Izquierda) | Icono de IA/Bot | Abre el chat interactivo (`RF23`) |
| **Navegación Inferior**| `NavigationBar` + `NavigationBarItem` | 4 pestañas (Inicio, Medicamentos, Citas, Perfil) | Cambia la pantalla actual |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial / Carga:** Muestra la estructura de la pantalla mientras consulta a Firestore las tomas del día y la próxima cita del usuario.
- **Estado Vacío:** Si el usuario no tiene medicamentos ni citas para el día seleccionado, muestra un mensaje amigable: *"No tienes actividades programadas para hoy."*
- **Estado de Lista Cargada:** Renderiza los medicamentos pendientes/completados y la tarjeta de la cita más cercana.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaInicio.kt` (Ubicación: `ui.dashboard`)
- **ViewModel:** `DashboardViewModel.kt`
- **Backend/Persistencia:**
    - Lectura de perfil: `FirebaseAuth.getInstance().currentUser`
    - Lectura de medicamentos: Cloud Firestore (`usuarios/{uid}/medicamentos`)
    - Lectura de citas: Cloud Firestore (`usuarios/{uid}/citas`)

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Saludo Personalizado:** Verificar que al iniciar sesión aparezca el nombre del usuario autenticado.
- [ ] **Filtro de Días:** Al tocar un día diferente en el selector horizontal, la lista "Tomas de Hoy" debe actualizarse según la fecha.
- [ ] **Acción de Tomar Medicamento:** Presionar "Tomar" en una tarjeta pendiente y verificar que cambie visualmente a un indicador verde de "Tomado".
- [ ] **FAB Flotante:** Probar que el botón flotante se ubique en la parte inferior izquierda y sea clickeable.
- [ ] **Navegación:** Comprobar que la barra inferior permita alternar correctamente entre las secciones de la app.