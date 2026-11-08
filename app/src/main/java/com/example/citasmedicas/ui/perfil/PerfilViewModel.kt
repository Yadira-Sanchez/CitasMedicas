package com.example.citasmedicas.ui.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class PerfilUiState(
    val nombre: String = "",
    val correo: String = "",
    val estaCargando: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class PerfilViewModel : ViewModel() {
    var uiState by mutableStateOf(
        PerfilUiState()
    )
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val uid = auth.currentUser?.uid ?: return
        val correo = auth.currentUser?.email ?: ""
        
        uiState = uiState.copy(
            estaCargando = true,
            correo = correo
        )

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).get().addOnSuccessListener { documento ->
            if (documento != null && documento.exists()) {
                val nombre = documento.getString("nombre") ?: ""
                uiState = uiState.copy(
                    nombre = nombre,
                    estaCargando = false
                )
            }
        }.addOnFailureListener { e ->
            uiState = uiState.copy(
                mensajeError = "Error al cargar datos: ${e.message}",
                estaCargando = false
            )
        }
    }

    fun actualizarNombre(
        nuevoNombre: String
    ) {
        uiState = uiState.copy(
            nombre = nuevoNombre
        )
    }

    fun guardarCambios() {
        val uid = auth.currentUser?.uid ?: return
        val nombre = uiState.nombre.trim()

        if (nombre.isEmpty()) {
            uiState = uiState.copy(
                mensajeError = "El nombre no puede estar vacío"
            )
            return
        }

        uiState = uiState.copy(
            estaCargando = true,
            mensajeExito = null,
            mensajeError = null
        )

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).update(
            "nombre", 
            nombre
        ).addOnSuccessListener {
            uiState = uiState.copy(
                estaCargando = false,
                mensajeExito = "Perfil actualizado correctamente"
            )
        }.addOnFailureListener { e ->
            uiState = uiState.copy(
                estaCargando = false,
                mensajeError = "No se pudo actualizar: ${e.message}"
            )
        }
    }

    fun limpiarMensajes() {
        uiState = uiState.copy(
            mensajeExito = null,
            mensajeError = null
        )
    }
}
