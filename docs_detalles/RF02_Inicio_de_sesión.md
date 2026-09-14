# RF02 - Inicio de sesión

## Historia de usuario
Como usuario quiero iniciar sesión con mi correo y contraseña para acceder de forma segura a mis medicamentos, citas y recordatorios.

## Objetivo
Permitir que un usuario registrado valide sus credenciales mediante Firebase Authentication e ingrese a la pantalla principal de la aplicación.

## Alcance

### Incluye:
- Mostrar una pantalla de Inicio de Sesión en Jetpack Compose.
- Solicitar el correo electrónico del usuario.
- Solicitar la contraseña del usuario.
- Validar los campos localmente antes de enviar.
- Autenticar las credenciales con Firebase Authentication.
- Manejar estados de interfaz (Carga, Error, Éxito).
- Mantener la sesión activa para futuros ingresos.
- Navegar a la pantalla principal (Dashboard) al completar la autenticación.
- Incluir un acceso visual/botón hacia la pantalla de Registro (RF01) y Olvidé contraseña (RF03).
- Inicio de sesión con Google

### No incluye (Versión Académica V1):
- Inicio de sesión con biometría (huella/rostro).
- Inicio de sesión con redes sociales.
- Autenticación en dos pasos (2FA).

---

## Campos del Formulario

| Campo | Obligatorio | Reglas de Validación | Icono / Acción Especial | Mensaje de Error |
| :--- | :---: | :--- | :--- | :--- |
| **Correo electrónico** | Sí | Formato válido (`usuario@dominio.com`), sin espacios | Ninguno | `"Escribe un correo electrónico válido."` |
| **Contraseña** | Sí | No vacía, mínimo 6 caracteres | **Icono de ojo (TrailingIcon):** Permite alternar la visibilidad del texto | `"Escribe tu contraseña."` |

---

## Reglas de Interfaz (Visibilidad de Contraseña)

### Icono de Mostrar / Ocultar Contraseña (Ojito)
- Usar `IconButton` dentro del parámetro `trailingIcon` del `OutlinedTextField` de Material 3.
- Utilizar un estado local booleano en Compose (`isPasswordVisible`) para controlar la visibilidad.
- **Estado Oculto (por defecto):** Visualización mediante `PasswordVisualTransformation()` e icono `Icons.Filled.VisibilityOff`.
- **Estado Visible:** Visualización mediante `VisualTransformation.None` e icono `Icons.Filled.Visibility`.

---

## Reglas de Validación Detalladas

### Correo electrónico
- Es obligatorio.
- Debe aplicar `.trim()` antes de validar o enviar.
- No puede contener espacios en blanco.
- Debe validar la estructura estándar de correo (`usuario@dominio.com`).

### Contraseña
- Es obligatoria.
- Debe aplicar `.trim()` antes de validar.
- Debe tener **mínimo 6 caracteres**.
- Debe mantenerse oculta por defecto en la UI.

---

## Comportamiento de la Pantalla y Estados de UI

Cuando el usuario presiona el botón **Iniciar Sesión**:

1. **Validación Local:** El ViewModel procesa los datos aplicándoles `.trim()`. Si algún campo está vacío o es inválido, muestra el error en la UI y **NO** llama a Firebase.
2. **Estado de Carga:** Si todo es válido, se activa el indicador de carga (`CircularProgressIndicator`), se deshabilita el botón y se evita el envío repetido de peticiones.
3. **Llamada a Firebase:** Se invoca el método `signInWithEmailAndPassword` mediante el ViewModel.
4. **Estado de Éxito:** Se redirige inmediatamente al usuario a la Pantalla Principal (Dashboard) y se limpia la pila de navegación para evitar regresar al Login con el botón de atrás.
5. **Estado de Error:** Si Firebase invalida las credenciales o hay problemas de red, se muestra un mensaje claro en pantalla.

---

## Mapeo de Errores de Firebase

Convertir las excepciones técnicas de Firebase Authentication a mensajes amigables:

| Excepción / Situación Firebase | Mensaje a Mostrar en Pantalla |
| :--- | :--- |
| `FirebaseAuthInvalidCredentialsException` | `"Correo o contraseña incorrectos."` |
| `FirebaseAuthInvalidUserException` | `"No existe una cuenta registrada con este correo."` |
| Error de Red / Conexión | `"No se pudo conectar. Revisa tu conexión a internet."` |
| Error Indefinido / Desconocido | `"Error al iniciar sesión. Inténtalo nuevamente."` |

---
