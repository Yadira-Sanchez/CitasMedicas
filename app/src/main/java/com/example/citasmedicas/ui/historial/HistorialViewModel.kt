package com.example.citasmedicas.ui.historial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citasmedicas.data.RegistroHistorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class HistorialUiState(
    val listaHistorial: List<RegistroHistorial> = emptyList(),
    val estaCargando: Boolean = false,
    val mensajeError: String? = null
)

class HistorialViewModel : ViewModel() {
    var uiState by mutableStateOf(
        HistorialUiState()
    )
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        cargarHistorial()
    }

    fun cargarHistorial() {
        val uid = auth.currentUser?.uid ?: return
        
        uiState = uiState.copy(
            estaCargando = true
        )

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "historial"
        ).orderBy(
            "timestamp", 
            Query.Direction.DESCENDING
        ).addSnapshotListener { instantaneo, error ->
            if (error != null) {
                uiState = uiState.copy(
                    mensajeError = "Error al cargar historial: ${error.message}",
                    estaCargando = false
                )
                return@addSnapshotListener
            }

            if (instantaneo != null) {
                val registros = instantaneo.documents.mapNotNull { doc ->
                    doc.toObject(
                        RegistroHistorial::class.java
                    )?.copy(
                        id = doc.id
                    )
                }
                uiState = uiState.copy(
                    listaHistorial = registros,
                    estaCargando = false
                )
            }
        }
    }
}
