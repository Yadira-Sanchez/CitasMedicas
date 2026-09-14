# Módulo 1: Autenticación y Gestión de Usuarios

---

# RF01 - Registro de usuario

## Historia de usuario
Como usuario quiero crear una cuenta utilizando mi nombre, correo electrónico y contraseña para poder acceder a la aplicación CitasMedicas.

## Objetivo
Permitir que un usuario cree una cuenta utilizando Firebase Authentication con correo electrónico y contraseña.

## Alcance

### Incluye:
- Mostrar una pantalla de registro en Jetpack Compose.
- Solicitar el nombre del usuario.
- Solicitar el correo electrónico.
- Solicitar una contraseña.- Validar los campos del formulario antes de enviar.
- Crear la cuenta mediante Firebase Authentication.
- Manejar estados de interfaz (Carga, Error, Éxito).
- Mostrar mensajes claros traducidos al español cuando ocurra un error.
- Permitir que el usuario inicie sesión después de registrarse.

### No incluye (Versión Académica V1):
- Verificación por correo electrónico.
- Recuperación de contraseña en esta pantalla.
- Inicio de sesión con Google o redes sociales.
- Edición del perfil de usuario.
- Eliminación de la cuenta.
- Registro diferenciado para médicos o administradores.

---

## Campos del Formulario

| Campo | Obligatorio | Reglas de Validación | Icono / Acción Especial | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Nombre** | Sí | No vacío, mínimo 2 caracteres, sin espacios al inicio/final | Ninguno | `"Escribe tu nombre."` |
| **Correo electrónico** | Sí | Formato válido (`usuario@dominio.com`), sin espacios | Ninguno | `"Escribe un correo electrónico válido."` |
| **Contraseña** | Sí | Mínimo 6 caracteres | **Icono de ojo (TrailingIcon):** Permite alternar la visibilidad del texto | `"La contraseña debe tener al menos 6 caracteres."` |


---

## Reglas de Interfaz (Visibilidad de Contraseña)

### Icono de Mostrar / Ocultar Contraseña (Ojito)
- Usar `IconButton` dentro del parámetro `trailingIcon` del componente `OutlinedTextField` de Material 3.
- Utilizar un estado local booleano en Compose (`isPasswordVisible`) para controlar la visibilidad.
- **Estado Oculto (por defecto):**
  - Visualización: `PasswordVisualTransformation()`.
  - Icono: `Icons.Filled.VisibilityOff` (o el equivalente de Material 3).
- **Estado Visible:**
  - Visualización: `VisualTransformation.None`.
  - Icono: `Icons.Filled.Visibility`.
- Al hacer clic en el icono, debe alternar el estado (`isPasswordVisible = !isPasswordVisible`) únicamente para ese campo.
---

## Reglas de Validación Detalladas

### Nombre
- Es obligatorio.
- No puede contener solamente espacios en blanco.
- Debe aplicar `.trim()` antes de validar.
- Debe tener al menos 2 caracteres.

### Correo electrónico
- Es obligatorio.
- No puede contener espacios.
- Debe contener la estructura `usuario@dominio.com` (no basta con verificar que contenga solo `@`).
- Debe aceptar cualquier dominio válido (`gmail.com`, `hotmail.com`, `outlook.com`, `dominio.edu`, etc.).

### Contraseña
- Es obligatoria.
- Debe tener **mínimo 6 caracteres** (estándar por defecto de Firebase Authentication).
- Debe ocultarse visualmente en la interfaz (`PasswordVisualTransformation`).
- No debe mostrarse en logs ni mensajes de error.


---

## Comportamiento de la Pantalla y Estados de UI

Cuando el usuario presiona el botón **Registrarse**:

1. **Validación Local:** El ViewModel valida los 4 campos. Si hay errores, los muestra en la UI y **NO** llama a Firebase.
2. **Estado de Carga:** Si todo es válido, se activa el estado de carga (`CircularProgressIndicator`), se desactiva el botón y se evita el envío doble de solicitudes.
3. **Llamada a Firebase:** Se invoca la autenticación de Firebase mediante el ViewModel.
4. **Estado de Éxito:** Se muestra el mensaje `"Cuenta creada correctamente."` y se navega a la pantalla de Inicio de Sesión o Pantalla Principal.
5. **Estado de Error:** Si Firebase responde con un error, se mapea a un mensaje amigable en español.

---

## Mapeo de Errores de Firebase

Convertir las excepciones técnicas de Firebase a mensajes sencillos para el usuario:

| Excepción / Situación Firebase | Mensaje a Mostrar en Pantalla |
| :--- | :--- |
| `FirebaseAuthUserCollisionException` | `"Este correo ya está registrado."` |
| `FirebaseAuthInvalidCredentialsException` | `"Escribe un correo electrónico válido."` |
| `FirebaseAuthWeakPasswordException` | `"La contraseña debe tener al menos 6 caracteres."` |
| Error de Red / Conexión | `"No se pudo conectar. Revisa tu conexión a internet."` |
| Error Indefinido / Desconocido | `"No se pudo crear la cuenta. Inténtalo nuevamente."` |

---

