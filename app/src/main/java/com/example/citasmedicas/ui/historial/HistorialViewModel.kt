package com.example.citasmedicas.ui.historial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citasmedicas.data.RegistroHistorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistorialViewModel : ViewModel() {

    var listaHistorial by mutableStateOf<List<RegistroHistorial>>(
        emptyList()
    )
        private set

    var estaCargando by mutableStateOf(
        false
    )
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // RF19: Lectura en tiempo real del historial
    fun obtenerHistorial() {
        val uid = auth.currentUser?.uid ?: return

        estaCargando = true

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "historial_medicamentos"
        ).orderBy(
            "timestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { instantaneo, error ->
            estaCargando = false

            if (error != null) {
                // Esta línea imprimirá el error real en la consola Logcat
                android.util.Log.e("ERROR_HISTORIAL", "Error de Firestore: ${error.message}", error)
                return@addSnapshotListener
            }

            if (instantaneo != null) {
                // Convertir documentos a objetos de tipo RegistroHistorial
                val lista = instantaneo.toObjects(
                    RegistroHistorial::class.java
                )
                listaHistorial = lista
            }
        }
    }
}
