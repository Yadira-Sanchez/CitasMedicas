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
    private fun cargarDatos() {
        // Cargar medicamentos y citas estáticas iniciales de prueba
        uiState = uiState.copy(
            listaMedicamentos = listOf(
                Medicamento("Ibuprofeno", "Pastilla", "400mg", "Cada 8 horas", "08:00 AM", "2023-10-01"),
                Medicamento("Paracetamol", "Pastilla", "500mg", "Cada 6 horas", "02:00 PM", "2023-10-01", yaFueTomado = true)
            ),
            proximaCita = CitaMedica(
                doctor = "Dra. Ana García",
                especialidad = "Cardiología",
                fecha = "Mañana",
                hora = "10:30 AM",
                ubicacion = "Clínica Central, Consultorio 302"
            )
        )

        // Leer el nombre real desde Firestore bajo la ruta usuarios/{uid}
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { documento ->
                    // Usamos .exists() para validar la presencia del documento
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

    fun marcarComoTomado(medicamento: Medicamento) {
        val nuevaLista = uiState.listaMedicamentos.map {
            if (it.nombre == medicamento.nombre && it.horaToma == medicamento.horaToma) {
                it.copy(yaFueTomado = true)
            } else it
        }
        uiState = uiState.copy(listaMedicamentos = nuevaLista)
    }
}
