package com.example.citasmedicas.ui.citas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citasmedicas.data.CitaMedica
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class CitaViewModel : ViewModel() {

    // Campos del formulario
    var motivo by mutableStateOf("")
    var doctor by mutableStateOf("")
    var lugar by mutableStateOf("")
    var fecha by mutableStateOf("")
    var hora by mutableStateOf("")
    var recordatorioActivo by mutableStateOf(true)
    var notas by mutableStateOf("")

    // Estados de UI
    var cargando by mutableStateOf(false)
    var guardadoExitoso by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    
    // Lista de citas para la pantalla de listado
    var listaCitas by mutableStateOf<List<CitaMedica>>(emptyList())

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun obtenerCitas() {
        val uid = auth.currentUser?.uid ?: return
        cargando = true
        
        firestore.collection("usuarios")
            .document(uid)
            .collection("citas")
            .orderBy("creadoEn", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { instantaneo, error ->
                cargando = false
                if (error != null) {
                    mensajeError = "Error al obtener las citas."
                    return@addSnapshotListener
                }
                
                if (instantaneo != null) {
                    listaCitas = instantaneo.documents.mapNotNull { doc ->
                        doc.toObject(CitaMedica::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    fun guardarCita() {
        if (!validarCampos()) return

        val uid = auth.currentUser?.uid ?: return
        cargando = true

        val nuevaCita = hashMapOf(
            "motivo" to motivo.trim(),
            "doctor" to doctor.trim(),
            "lugar" to lugar.trim(),
            "fecha" to fecha,
            "hora" to hora,
            "recordatorioActivo" to recordatorioActivo,
            "notas" to notas.trim(),
            "creadoEn" to Timestamp.now()
        )

        firestore.collection("usuarios")
            .document(uid)
            .collection("citas")
            .add(nuevaCita)
            .addOnSuccessListener {
                cargando = false
                guardadoExitoso = true // Navegación inmediata
            }
            .addOnFailureListener {
                cargando = false
                mensajeError = "Error al guardar la cita."
            }
    }

    private fun validarCampos(): Boolean {
        if (motivo.trim().length < 2) {
            mensajeError = "Ingresa el motivo o especialidad de la cita."
            return false
        }
        if (lugar.trim().isEmpty()) {
            mensajeError = "Ingresa el lugar o dirección de la cita."
            return false
        }
        if (fecha.isEmpty()) {
            mensajeError = "Selecciona la fecha de la cita."
            return false
        }
        if (hora.isEmpty()) {
            mensajeError = "Selecciona la hora de la cita."
            return false
        }
        mensajeError = null
        return true
    }

    fun limpiarFormulario() {
        motivo = ""
        doctor = ""
        lugar = ""
        fecha = ""
        hora = ""
        recordatorioActivo = true
        notas = ""
        mensajeError = null
        guardadoExitoso = false
        cargando = false
    }
}
