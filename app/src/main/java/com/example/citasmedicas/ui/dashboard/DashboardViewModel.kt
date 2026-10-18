package com.example.citasmedicas.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DashboardUiState(
    val nombreUsuario: String = "Cargando...",
    val fechaSeleccionada: LocalDate = LocalDate.now(),
    val listaMedicamentos: List<Medicamento> = emptyList(),
    val listaCitasDelDia: List<CitaMedica> = emptyList(),
    val diasSemana: List<LocalDate> = emptyList()
)

class DashboardViewModel : ViewModel() {
    var uiState by mutableStateOf(
        DashboardUiState()
    )
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var listenerCitas: ListenerRegistration? = null
    private var listenerMedicamentos: ListenerRegistration? = null

    init {
        generarSemana()
        cargarDatosUsuario()
        cargarMedicamentos()
        cargarCitasPorFecha(
            fecha = uiState.fechaSeleccionada
        )
    }

    private fun cargarDatosUsuario() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).get().addOnSuccessListener { documento ->
            if (documento != null && documento.exists()) {
                val nombre = documento.getString(
                    "nombre"
                ) ?: "Usuario"
                uiState = uiState.copy(
                    nombreUsuario = nombre
                )
            }
        }
    }

    private fun cargarMedicamentos() {
        val uid = auth.currentUser?.uid ?: return
        listenerMedicamentos = firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "medicamentos"
        ).addSnapshotListener { instantaneo, error ->
            if (error == null && instantaneo != null) {
                val medicamentos = instantaneo.documents.mapNotNull { doc ->
                    doc.toObject(
                        Medicamento::class.java
                    )?.copy(
                        id = doc.id
                    )
                }
                uiState = uiState.copy(
                    listaMedicamentos = medicamentos
                )
            }
        }
    }

    fun cargarCitasPorFecha(
        fecha: LocalDate
    ) {
        val uid = auth.currentUser?.uid ?: return
        val fechaString = fecha.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy"
            )
        )

        listenerCitas?.remove()
        
        listenerCitas = firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "citas"
        ).whereEqualTo(
            "fecha", 
            fechaString
        ).addSnapshotListener { instantaneo, error ->
            if (error == null && instantaneo != null) {
                val citas = instantaneo.documents.mapNotNull { doc ->
                    doc.toObject(
                        CitaMedica::class.java
                    )?.copy(
                        id = doc.id
                    )
                }
                uiState = uiState.copy(
                    listaCitasDelDia = citas
                )
            }
        }
    }

    private fun generarSemana() {
        val hoy = LocalDate.now()
        val semana = (0..6).map { 
            hoy.plusDays(
                it.toLong()
            ) 
        }
        uiState = uiState.copy(
            diasSemana = semana
        )
    }

    fun seleccionarFecha(
        fecha: LocalDate
    ) {
        uiState = uiState.copy(
            fechaSeleccionada = fecha
        )
        cargarCitasPorFecha(
            fecha = fecha
        )
    }

    fun actualizarEstadoMedicamento(
        medicamento: Medicamento,
        nuevoEstado: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "medicamentos"
        ).document(
            medicamento.id
        )

        when (nuevoEstado) {
            "Tomar" -> docRef.update(
                "yaFueTomado", 
                true
            )
            "Pasar" -> docRef.update(
                "yaFueTomado", 
                false
            )
            "Reprogramar" -> docRef.update(
                "yaFueTomado", 
                false
            )
        }
    }

    override fun onCleared() {
        // No llamamos a super.onCleared() porque el IDE marca que es vacío
        listenerCitas?.remove()
        listenerMedicamentos?.remove()
    }
}
