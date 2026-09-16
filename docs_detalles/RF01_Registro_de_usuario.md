# Spec Técnico: RF01 - Registro de Usuario

## 1. Información General
- **Código y Nombre:** RF01 - Registro de usuario
- **Módulo:** Módulo 1 - Autenticación y Gestión de Usuarios
- **Objetivo:** Permitir que un nuevo usuario cree una cuenta con correo electrónico y contraseña mediante Firebase Authentication.

## 2. Historia de Usuario
**Como** usuario nuevo  
**Quiero** crear una cuenta con mi nombre, correo y contraseña  
**Para** acceder de manera personalizada a la aplicación CitasMedicas.

## 3. Alcance y Límites
- **Incluye:**
  - Formulario de registro en Jetpack Compose.
  - Campos: Nombre, Correo electrónico, Contraseña y Confirmación de contraseña.
  - Mantenimiento de estados de UI (Idle, Loading, Error, Success).
  - Control de visibilidad de contraseña (icono de ojo).
  - Integración directa con Firebase Authentication.
- **No incluye:**
  - Verificación por correo electrónico.
  - Registro con redes sociales (Google, Facebook).

## 4. Especificación de UI y Formulario

| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Nombre** | Sí | `.trim()`, mínimo 2 caracteres, no solo espacios | `OutlinedTextField` | `"Escribe tu nombre."` |
| **Correo electrónico**| Sí | `.trim()`, formato válido (`usuario@dominio.com`) | `OutlinedTextField` | `"Escribe un correo electrónico válido."` |
| **Contraseña** | Sí | Mínimo 6 caracteres | `OutlinedTextField` + TrailingIcon (Ojo) | `"La contraseña debe tener al menos 6 caracteres."` |
| **Confirmar contraseña**| Sí | Debe coincidir exactamente con la contraseña | `OutlinedTextField` + TrailingIcon (Ojo) | `"Las contraseñas no coinciden."` |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial:** Campos vacíos y botón "Registrarse" habilitado.
- **Estado de Carga:** Se activa el indicador de carga (`CircularProgressIndicator`) y se inhabilita el botón.
- **Estado de Error:** Muestra el error mapeado debajo del campo correspondiente o en un `Snackbar`.
- **Estado de Éxito:** Muestra `"Cuenta creada correctamente."` y redirige a la pantalla principal o Login.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaRegistro.kt` (Ubicación: `com.example.citasmedicas.ui.auth`)
- **ViewModel:** `AuthViewModel.kt` (Gestiona validaciones locales y estado de la pantalla)
- **Servicio:** `FirebaseAuth.getInstance().createUserWithEmailAndPassword()`

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Registro Exitoso:** Crear cuenta con datos válidos y verificar la entrada en la consola de Firebase Authentication.
- [ ] **Validación de Correo:** Ingresar `usuario@` y confirmar que muestre el error sin consultar a Firebase.
- [ ] **Contraseña Corta:** Ingresar menos de 6 caracteres y verificar la detención del proceso.
- [ ] **Contraseñas Desiguales:** Escribir contraseñas diferentes y confirmar la alerta.
- [ ] **Prevención de Doble Clic:** Comprobar que el botón se desactive durante la carga.


-
