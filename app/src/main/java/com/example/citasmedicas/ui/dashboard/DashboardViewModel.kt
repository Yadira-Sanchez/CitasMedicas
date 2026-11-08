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
import java.time.format.DateTimeParseException

data class DashboardUiState(
    val nombreUsuario: String = "Cargando...",
    val fechaSeleccionada: LocalDate = LocalDate.now(),
    val listaMedicamentos: List<Medicamento> = emptyList(), // Lista completa para otras pantallas
    val listaMedicamentosDelDia: List<Medicamento> = emptyList(), // Filtrada para el calendario
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
    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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
                filtrarMedicamentosPorFecha(
                    fecha = uiState.fechaSeleccionada
                )
            }
        }
    }

    fun cargarCitasPorFecha(
        fecha: LocalDate
    ) {
        val uid = auth.currentUser?.uid ?: return
        val fechaString = fecha.format(formatoFecha)

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

    private fun filtrarMedicamentosPorFecha(
        fecha: LocalDate
    ) {
        val filtrados = uiState.listaMedicamentos.filter { med ->
            // 1. Parsear med.fechaInicio. Si es nula o vacía, no incluir.
            if (med.fechaInicio.isNullOrBlank()) return@filter false

            try {
                // Análisis con trim() para evitar errores por espacios accidentales.
                val inicio = LocalDate.parse(
                    med.fechaInicio.trim(), 
                    formatoFecha
                )
                
                // 2. Si la fecha seleccionada es anterior a inicio, retorna false.
                if (fecha.isBefore(inicio)) return@filter false

                // 3. Manejo de fechaFin opcional
                if (!med.fechaFin.isNullOrBlank()) {
                    val fin = LocalDate.parse(
                        med.fechaFin.trim(), 
                        formatoFecha
                    )
                    // Si la fecha seleccionada es posterior a fin, retorna false.
                    if (fecha.isAfter(fin)) return@filter false
                }
                
                // Si fechaFin es nula/blanca o la fecha está en rango, retorna true.
                true
            } catch (e: DateTimeParseException) {
                // Si el formato es incorrecto, no lo mostramos para evitar errores.
                false
            }
        }
        uiState = uiState.copy(
            listaMedicamentosDelDia = filtrados
        )
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
        filtrarMedicamentosPorFecha(
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

        docRef.update(
            "yaFueTomado", 
            nuevoEstado == "Tomar"
        )
    }

    override fun onCleared() {
        listenerCitas?.remove()
        listenerMedicamentos?.remove()
    }
}
