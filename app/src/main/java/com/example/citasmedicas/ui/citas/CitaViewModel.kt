package com.example.citasmedicas.ui.citas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.util.AndroidAlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class CitaViewModel(
    application: Application
) : AndroidViewModel(
    application
) {

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
    var actualizacionExitosa by mutableStateOf(false)
    var eliminacionExitosa by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    
    // Lista de citas para la pantalla de listado
    var listaCitas by mutableStateOf<List<CitaMedica>>(emptyList())
    
    // Cita seleccionada para detalle o edición
    var citaSeleccionada by mutableStateOf<CitaMedica?>(null)

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val alarmScheduler = AndroidAlarmScheduler(application)

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

    fun obtenerCitaPorId(
        id: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("usuarios")
            .document(uid)
            .collection("citas")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                citaSeleccionada = doc.toObject(CitaMedica::class.java)?.copy(id = doc.id)
            }
    }

    fun cargarDatosCita(
        cita: CitaMedica
    ) {
        motivo = cita.especialidad
        doctor = cita.doctor
        lugar = cita.lugar
        fecha = cita.fecha
        hora = cita.hora
        recordatorioActivo = cita.recordatorioActivo
        notas = cita.notas
    }

    fun guardarCita() {
        if (!validarCampos()) return

        val uid = auth.currentUser?.uid ?: return
        cargando = true

        val nuevaCita = hashMapOf(
            "especialidad" to motivo.trim(),
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
            .addOnSuccessListener { docRef ->
                gestionarAlarma(docRef.id)
                cargando = false
                guardadoExitoso = true
            }
            .addOnFailureListener {
                cargando = false
                mensajeError = "Error al guardar la cita."
            }
    }

    fun actualizarCita(
        citaId: String
    ) {
        if (!validarCampos()) return
        val uid = auth.currentUser?.uid ?: return
        cargando = true

        val datosActualizados = mapOf(
            "especialidad" to motivo.trim(),
            "doctor" to doctor.trim(),
            "lugar" to lugar.trim(),
            "fecha" to fecha,
            "hora" to hora,
            "recordatorioActivo" to recordatorioActivo,
            "notas" to notas.trim()
        )

        firestore.collection("usuarios")
            .document(uid)
            .collection("citas")
            .document(citaId)
            .update(datosActualizados)
            .addOnSuccessListener {
                gestionarAlarma(citaId)
                cargando = false
                actualizacionExitosa = true
            }
            .addOnFailureListener {
                cargando = false
                mensajeError = "No se pudo actualizar la cita."
            }
    }

    fun eliminarCita(
        citaId: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        cargando = true

        val citaParaCancelar = citaSeleccionada ?: CitaMedica(id = citaId)

        firestore.collection("usuarios")
            .document(uid)
            .collection("citas")
            .document(citaId)
            .delete()
            .addOnSuccessListener {
                alarmScheduler.cancelarCita(citaParaCancelar)
                cargando = false
                eliminacionExitosa = true
                citaSeleccionada = null
            }
            .addOnFailureListener {
                cargando = false
                mensajeError = "No se pudo eliminar la cita."
            }
    }

    private fun gestionarAlarma(
        id: String
    ) {
        val cita = CitaMedica(
            id = id,
            especialidad = motivo,
            lugar = lugar,
            fecha = fecha,
            hora = hora,
            recordatorioActivo = recordatorioActivo
        )
        if (recordatorioActivo) {
            alarmScheduler.programarCita(cita)
        } else {
            alarmScheduler.cancelarCita(cita)
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
        actualizacionExitosa = false
        eliminacionExitosa = false
        cargando = false
    }
}
