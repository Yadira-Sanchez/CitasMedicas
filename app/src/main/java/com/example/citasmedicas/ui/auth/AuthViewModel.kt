package com.example.citasmedicas.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore


// Este es el "cerebro" que maneja la entrada y salida de usuarios
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estados de la pantalla
    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var registroExitoso by mutableStateOf(false)
    var loginExitoso by mutableStateOf(false)

    // Función para crear una cuenta nueva (RF01)
    fun registrarUsuario(nombre: String, correo: String, clave: String) {
        if (nombre.trim().isEmpty()) {
            mensajeError = "Escribe tu nombre."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mensajeError = "Escribe un correo electrónico válido."
            return
        }
        if (clave.length < 6) {
            mensajeError = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        cargando = true
        mensajeError = null

        auth.createUserWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val datosUsuario = mapOf(
                            "nombre" to nombre.trim(),
                            "correo" to correo.trim()
                        )
                        firestore.collection("usuarios").document(uid)
                            .set(datosUsuario)
                            .addOnCompleteListener { tareaFirestore ->
                                cargando = false
                                if (tareaFirestore.isSuccessful) {
                                    registroExitoso = true
                                } else {
                                    mensajeError = "Se creó la cuenta, pero hubo un error al guardar los datos de perfil."
                                }
                            }
                    } else {
                        cargando = false
                        registroExitoso = true
                    }
                } else {
                    cargando = false
                    val excepcion = tarea.exception
                    mensajeError = when (excepcion) {
                        is FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
                        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
                        is FirebaseAuthInvalidCredentialsException -> "El correo no es válido."
                        else -> "No se pudo crear la cuenta. Inténtalo de nuevo."
                    }
                }
            }
    }

    // Función para entrar a la app (RF02)
    fun iniciarSesion(correo: String, clave: String) {
        if (correo.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mensajeError = "Escribe un correo electrónico válido."
            return
        }
        if (clave.isEmpty()) {
            mensajeError = "Escribe tu contraseña."
            return
        }

        cargando = true
        mensajeError = null

        auth.signInWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { tarea ->
                cargando = false
                if (tarea.isSuccessful) {
                    loginExitoso = true
                } else {
                    val excepcion = tarea.exception
                    mensajeError = when (excepcion) {
                        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
                        is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo."
                        else -> "Error al iniciar sesión. Revisa tu internet."
                    }
                }
            }
    }

    fun limpiarMensajes() {
        mensajeError = null
    }
}
