# Skill: Generador de Especificaciones Técnicas (Spec)

## Objetivo
Estructurar un Requerimiento Funcional (RF) en un documento `spec.md` detallado, estandarizado y ejecutable para el desarrollo de la aplicación Android.

## Instrucciones para la IA
Cuando se solicite generar un Spec para un Requerimiento Funcional, debes construir la respuesta siguiendo estrictamente la estructura definida a continuación.

---

# ESTRUCTURA OBLIGATORIA DEL SPEC

## 1. Información General
- **Código y Nombre:** [Código RF] - [Nombre del Requerimiento]
- **Módulo:** [Nombre del Módulo al que pertenece]
- **Objetivo:** [Propósito principal de la funcionalidad]

## 2. Historia de Usuario
**Como** [tipo de usuario]  
**Quiero** [acción realizada]  
**Para** [beneficio obtenido]

## 3. Alcance y Limites
- **Incluye:** [Funcionalidades que se construirán]
- **No incluye:** [Limitaciones para la versión actual]

## 4. Especificación de UI y Formulario
| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| [Campo] | Sí/No | [Reglas aplicadas con .trim()] | [OutlinedTextField, Chip, etc.] | "[Mensaje en español]" |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial:** Datos limpios y botón activo.
- **Estado de Carga:** Muestra `CircularProgressIndicator` y desactiva interacción.
- **Estado de Error:** Muestra mensaje amigable en UI.
- **Estado de Éxito:** Notifica la acción y navega a la siguiente pantalla.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** [NombrePantalla.kt] en Compose.
- **ViewModel:** [NombreViewModel.kt] exponiendo `UIState`.
- **Backend/Persistencia:** Integration con Firebase Auth o Firestore.

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] Prueba de caso de éxito.
- [ ] Prueba de validación de campos obligatorios.
- [ ] Prueba de manejo de errores.