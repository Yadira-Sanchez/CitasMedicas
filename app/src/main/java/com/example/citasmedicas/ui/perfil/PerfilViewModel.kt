package com.example.citasmedicas.ui.perfil

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PerfilUiState(
    val nombre: String = "",
    val correo: String = "",
    val isLoading: Boolean = false,
    val mensajeEstado: String? = null,
    val esError: Boolean = false,
    val errorContrasenaActual: String? = null,
    val cambioExitoso: Boolean = false
)

class PerfilViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = PerfilUiState()
    )
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        val user = auth.currentUser ?: return
        val email = user.email ?: ""
        val uid = user.uid

        _uiState.update { 
            it.copy(
                correo = email, 
                isLoading = true 
            ) 
        }

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                _uiState.update {
                    it.copy(
                        nombre = doc.getString("nombre") ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false 
                    ) 
                }
            }
        }.addOnFailureListener { e ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    mensajeEstado = "Error al cargar: ${e.message}",
                    esError = true
                )
            }
        }
    }

    fun actualizarPerfil(
        nuevoNombre: String
    ) {
        if (nuevoNombre.isBlank()) {
            _uiState.update {
                it.copy(
                    mensajeEstado = "El nombre no puede estar vacío",
                    esError = true
                )
            }
            return
        }

        val uid = auth.currentUser?.uid ?: return
        _uiState.update { 
            it.copy(
                isLoading = true 
            ) 
        }

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).update(
            "nombre", 
            nuevoNombre
        ).addOnSuccessListener {
            _uiState.update {
                it.copy(
                    nombre = nuevoNombre,
                    isLoading = false,
                    mensajeEstado = "Perfil actualizado con éxito",
                    esError = false
                )
            }
        }.addOnFailureListener { e ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    mensajeEstado = "Error al actualizar: ${e.message}",
                    esError = true
                )
            }
        }
    }

    fun cambiarContrasena(
        actual: String,
        nueva: String
    ) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return

        _uiState.update { 
            it.copy(
                isLoading = true,
                errorContrasenaActual = null,
                cambioExitoso = false
            ) 
        }

        val credential = EmailAuthProvider.getCredential(
            email, 
            actual
        )

        user.reauthenticate(
            credential
        ).addOnSuccessListener {
            user.updatePassword(
                nueva
            ).addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeEstado = "Contraseña cambiada con éxito",
                        esError = false,
                        cambioExitoso = true
                    )
                }
            }.addOnFailureListener { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeEstado = "Error al cambiar contraseña: ${e.message}",
                        esError = true
                    )
                }
            }
        }.addOnFailureListener { e ->
            val mensajeError = if (e is FirebaseAuthInvalidCredentialsException) {
                "La contraseña actual es incorrecta"
            } else {
                "Error de autenticación: ${e.message}"
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorContrasenaActual = mensajeError
                )
            }
        }
    }

    fun limpiarErrorContrasenaActual() {
        _uiState.update { 
            it.copy(
                errorContrasenaActual = null 
            ) 
        }
    }

    fun cerrarSesion(
        onSesionCerrada: () -> Unit
    ) {
        auth.signOut()
        onSesionCerrada()
    }

    fun limpiarMensaje() {
        _uiState.update { 
            it.copy(
                mensajeEstado = null,
                cambioExitoso = false
            ) 
        }
    }
}
