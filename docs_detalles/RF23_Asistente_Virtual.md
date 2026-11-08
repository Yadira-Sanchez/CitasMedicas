# Spec Técnico: RF23 - Asistente Virtual Inteligente (Groq IA)

## 1. Información General
- **Código y Nombre:** RF23 (Asistente Virtual Inteligente - Chatbot con IA).
- **Módulo:** Módulo 4 - Asistencia e Inteligencia Artificial.
- **Proveedor de IA:** Groq Cloud API.
- **Modelo:** `llama-3.3-70b-versatile` (baja latencia y respuestas en español).

---

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** disponer de un botón flotante circular en la esquina inferior izquierda para interactuar con un chatbot con IA  
**Para** resolver dudas sobre mis tratamientos, consultar dosis, entender la utilidad de mis fármacos y revisar mis próximas citas con asistencia en tiempo real.

---

## 3. Alcance, Reglas de Negocio y Gestión de Historial

### 3.1. Comportamiento del Historial y Estado de Sesión
- **Persistencia en Sesión (Minimizar / Cerrar modal):**  
  Al cerrar o minimizar la ventana flotante del chat durante la misma sesión de la app, **el historial de la conversación se conserva**. Al volver a presionar el botón flotante, la ventana se abre con todos los mensajes anteriores.
- **Acción "Limpiar Chat" / Nueva Consulta:**  
  La barra superior del chat incluye un botón de acción (*"Nuevo chat"* / `Icons.Default.DeleteSweep`) que borra el historial de la pantalla y reinicia el contexto de la conversación.
- **Formato del Descargo de Responsabilidad (Disclaimer):**  
  Toda respuesta emitida por el bot incluirá o finalizará obligatoriamente con una aclaración sutil:
  > *"Nota: Esta información es únicamente orientativa y no sustituye la consulta ni la prescripción de un médico profesional."*

### 3.2. Contexto Dinámico Inyectado al Prompt del Sistema (System Prompt)
Cada vez que se inicia o consulta al bot, el ViewModel construye un `System Prompt` que contiene:
1. El rol del bot: *"Eres VitalisAI, un asistente virtual de salud amigable, claro y conciso."*
2. Lista de medicamentos activos del usuario (Nombre, Dosis, Frecuencia, Hora).
3. Lista de próximas citas médicas programadas (Especialidad, Doctor, Fecha, Hora, Lugar).
4. Instrucción de responder en español y mantener un tono empático y seguro.

---

## 4. Especificación de Componentes de UI (`PantallaChatbotModal.kt`)

| Elemento Visual | Componente Compose | Ubicación / Estado | Acción / Lógica |
| :--- | :--- | :--- | :--- |
| **Botón Flotante (FAB)** | `FloatingActionButton` | Esquina inferior izquierda (`Alignment.BottomStart`) | Abre el modal de chat. |
| **Cabecera del Chat** | `TopAppBar` o `Row` personalizado | Parte superior del modal | Muestra el título *"Vitalis AI"* y estado *"En línea"*. |
| **Botón Limpiar** | `IconButton` (`DeleteSweep`) | Cabecera (Derecha) | Limpia la lista de mensajes de la sesión actual. |
| **Botón Cierre** | `IconButton` (`Close`) | Cabecera (Derecha) | Minimiza/Cierra el modal manteniendo el historial en ViewModel. |
| **Lista de Mensajes** | `LazyColumn` con `rememberLazyListState` | Cuerpo central | Muestra globos de texto diferenciados (Usuario = Azul, IA = Gris/Verde). Realiza autoscroll al último mensaje. |
| **Indicador de Carga** | `CircularProgressIndicator` o puntos animados | Dentro del chat | Se muestra mientras Groq procesa la respuesta HTTP. |
| **Campo de Texto + Enviar** | `OutlinedTextField` + `IconButton` (`Send`) | Barra inferior del modal | Captura la pregunta y dispara la solicitud a Groq. |

---

## 5. Criterios de Aceptación (Pruebas Manuales)
- [ ] **[RF23-01] Despliegue del Chat:** El FAB en la parte inferior izquierda abre el modal sin superponerse torpemente con la barra de navegación.
- [ ] **[RF23-02] Respuesta Contextual:** Si el usuario pregunta *"¿Qué remedios me tocan hoy?"*, la IA responde basándose en la lista real de medicamentos del usuario.
- [ ] **[RF23-03] Descargo Médico:** Todas las respuestas del chatbot terminan con la nota aclaratoria médica.
- [ ] **[RF23-04] Conservación y Limpieza de Historial:** Al cerrar y abrir el modal, la conversación permanece. Al pulsar el botón de escoba/limpiar, la pantalla de chat queda vacía lista para una nueva consulta.
- [ ] **[RF23-05] Manejo de Errores/Offline:** Si no hay internet o falla la API Key de Groq, la app muestra un mensaje flotante (*Snackbar*) indicando *"No se pudo conectar con el asistente. Intenta más tarde"*.