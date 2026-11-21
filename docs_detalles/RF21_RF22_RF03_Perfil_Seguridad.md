# Spec Técnico: Módulo de Perfil, Seguridad y Sesión (RF21, RF03, RF22)

## 1. Información General
- **Requerimientos cubiertos:**
    - **RF21:** Gestión del perfil de usuario (consulta y edición de datos).
    - **RF03:** Cambio de contraseña del usuario.
    - **RF22:** Cierre de sesión seguro con confirmación.
- **Módulo:** Módulo 1 / 5 - Gestión de Cuenta y Perfil (`ui/perfil/`).

---

## 2. Arquitectura de la Pantalla (`PantallaPerfil.kt`)

| Sección | Requerimiento | Componentes UI | Lógica / Backend |
| :--- | :--- | :--- | :--- |
| **Datos Personales** | RF21 | `OutlinedTextField` (Nombre, , etc.), Correo (solo lectura), Botón *"Guardar cambios"*. | Lee y actualiza el documento `usuarios/{uid}` en Cloud Firestore. Valida campos obligatorios. |
| **Seguridad** | RF03 | Botón *"Cambiar contraseña"* que abre `AlertDialog` con contraseña actual, nueva y confirmación. | Reautentica con `EmailAuthProvider.getCredential()` y ejecuta `user.updatePassword()`. |
| **Cierre de Sesión** | RF22 | Botón *"Cerrar sesión"* al pie + `AlertDialog` de confirmación. | Ejecuta `FirebaseAuth.getInstance().signOut()` y navega a `"login"` limpiando el backstack (`popUpTo(0)`). |

---

## 3. Reglas de Validación
1. **Datos de perfil:** El campo `nombre` no puede guardarse vacío. El correo electrónico no es editable directamente desde esta vista.
2. **Cambio de contraseña:**
    - La contraseña actual es obligatoria para evitar el error `FirebaseAuthRecentLoginRequiredException`.
    - La nueva contraseña debe tener al menos **6 caracteres** y coincidir con el campo de confirmación.
3. **Limpieza de sesión:** Al cerrar sesión, presionar el botón "Atrás" del dispositivo no debe permitir regresar al Dashboard ni a datos previos.