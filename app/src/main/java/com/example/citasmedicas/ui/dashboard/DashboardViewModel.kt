package com.example.citasmedicas.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

data class DashboardUiState(
    val nombreUsuario: String = "Cargando...",
    val fechaSeleccionada: LocalDate = LocalDate.now(),
    val listaMedicamentos: List<Medicamento> = emptyList(),
    val proximaCita: CitaMedica? = null,
    val diasSemana: List<LocalDate> = emptyList()
)

class DashboardViewModel : ViewModel() {
    var uiState by mutableStateOf(DashboardUiState())
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        cargarDatos()
        generarSemana()
    }

    fun cargarDatos() {
        // Asignación de datos estáticos iniciales de Citas
        uiState = uiState.copy(
            proximaCita = CitaMedica(
                doctor = "Dra. Ana García",
                especialidad = "Cardiología",
                fecha = "Mañana",
                hora = "10:30 AM",
                ubicacion = "Clínica Central, Consultorio 302"
            )
        )

        val uid = auth.currentUser?.uid
        if (uid != null) {
            // 1. Leer el nombre real desde Firestore bajo la ruta usuarios/{uid}
            firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { documento ->
                    if (documento != null && documento.exists()) {
                        val nombreReal = documento.getString("nombre")
                        if (!nombreReal.isNullOrBlank()) {
                            uiState = uiState.copy(nombreUsuario = nombreReal)
                        } else {
                            uiState = uiState.copy(nombreUsuario = "Usuario")
                        }
                    } else {
                        uiState = uiState.copy(nombreUsuario = "Usuario")
                    }
                }
                .addOnFailureListener {
                    uiState = uiState.copy(nombreUsuario = "Usuario")
                }

            // 2. Leer medicamentos reales en tiempo real desde la subcolección usuarios/{uid}/medicamentos
            firestore.collection("usuarios").document(uid).collection("medicamentos")
                .addSnapshotListener { instantaneo, error ->
                    if (error == null && instantaneo != null) {
                        val medicamentos = instantaneo.documents.mapNotNull { doc ->
                            val nombre = doc.getString("nombre") ?: return@mapNotNull null
                            val tipo = doc.getString("tipo") ?: "Pastilla"
                            val dosis = doc.getString("dosis") ?: ""
                            val frecuencia = doc.getString("frecuencia") ?: ""
                            val horaToma = doc.getString("horaToma") ?: ""
                            val fechaInicio = doc.getString("fechaInicio") ?: ""
                            val fechaFin = doc.getString("fechaFin") ?: ""
                            val yaFueTomado = doc.getBoolean("yaFueTomado") ?: false
                            Medicamento(nombre, tipo, dosis, frecuencia, horaToma, fechaInicio, fechaFin, yaFueTomado)
                        }
                        uiState = uiState.copy(listaMedicamentos = medicamentos)
                    }
                }
        } else {
            uiState = uiState.copy(nombreUsuario = "Usuario")
        }
    }

    private fun generarSemana() {
        val hoy = LocalDate.now()
        val semana = (0..6).map { hoy.plusDays(it.toLong()) }
        uiState = uiState.copy(diasSemana = semana)
    }

    fun seleccionarFecha(fecha: LocalDate) {
        uiState = uiState.copy(fechaSeleccionada = fecha)
    }

    // Función para actualizar el estado del medicamento en Firestore según corresponda (RF04)
    fun actualizarEstadoMedicamento(medicamento: Medicamento, nuevoEstado: String) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("usuarios").document(uid).collection("medicamentos")
            .whereEqualTo("nombre", medicamento.nombre)
            .whereEqualTo("horaToma", medicamento.horaToma)
            .get()
            .addOnSuccessListener { documentos ->
                for (doc in documentos) {
                    when (nuevoEstado) {
                        "Tomar" -> doc.reference.update("yaFueTomado", true)
                        "Pasar" -> doc.reference.update("yaFueTomado", false)
                        "Reprogramar" -> {
                            doc.reference.update("yaFueTomado", false)
                        }
                    }
                }
            }
    }

    fun marcarComoTomado(medicamento: Medicamento) {
        actualizarEstadoMedicamento(medicamento, "Tomar")
    }
}
