# Spec Técnico: RF02 - Inicio de Sesión

## 1. Información General
- **Código y Nombre:** RF02 - Inicio de sesión
- **Módulo:** Módulo 1 - Autenticación y Gestión de Usuarios
- **Objetivo:** Permitir a un usuario registrado ingresar a la aplicación autenticándose con su correo electrónico y contraseña.

## 2. Historia de Usuario
**Como** usuario registrado  
**Quiero** ingresar mis credenciales de acceso  
**Para** acceder a mis medicamentos, citas y configuraciones guardadas.

## 3. Alcance y Límites
- **Incluye:**
    - Formulario en Jetpack Compose con campos: Correo electrónico y Contraseña.
    - Validación local de formato de correo y campos no vacíos.
    - Control de visibilidad para el campo de contraseña (icono de ojo).
    - Autenticación remota con Firebase Authentication.
    - Navegación hacia la pantalla principal (`PantallaInicio`) tras el ingreso exitoso.
    - Opción/enlace visible para navegar hacia el registro (`RF01`) o recuperar contraseña (`RF03`).
- **No incluye:**
    - Login biométrico (huella/rostro) ni inicio de sesión con redes sociales.

## 4. Especificación de UI y Formulario

| Campo | Obligatorio | Reglas de Validación | Componente Visual | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Correo electrónico** | Sí | `.trim()`, formato válido (`usuario@dominio.com`) | `OutlinedTextField` | `"Escribe un correo electrónico válido."` |
| **Contraseña** | Sí | `.trim()`, campo no vacío | `OutlinedTextField` + TrailingIcon | `"Ingresa tu contraseña."` |

## 5. Comportamiento y Estados de Pantalla
- **Estado Inicial:** Campos vacíos y botón "Iniciar Sesión" habilitado.
- **Estado de Carga:** Muestra `CircularProgressIndicator` y desactiva el botón para evitar múltiples envíos.
- **Estado de Error:** Muestra mensaje de credenciales incorrectas en un `Snackbar` o texto de error debajo del formulario si Firebase rechaza la autenticación.
- **Estado de Éxito:** Notifica ingreso correcto y redirige a `PantallaInicio`.

## 6. Arquitectura (MVVM + Firebase)
- **Vista (UI):** `PantallaLogin.kt` (Ubicación: `ui.auth`)
- **ViewModel:** `AuthViewModel.kt`
- **Servicio:** `FirebaseAuth.getInstance().signInWithEmailAndPassword()`

## 7. Criterios de Aceptación (Pruebas Manuales)
- [ ] **Inicio Exitoso:** Ingresar correo y contraseña válidos y verificar la navegación a la pantalla principal.
- [ ] **Credenciales Erróneas:** Probar con una contraseña incorrecta y verificar que se muestre el mensaje de error sin cerrar la app.
- [ ] **Campos Vacíos:** Intentar presionar el botón sin llenar datos y comprobar la validación local.