package com.example.citasmedicas.ui.medicamentos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.data.RegistroHistorial
import com.example.citasmedicas.util.AndroidAlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MedicamentoViewModel(
    application: Application
) : AndroidViewModel(
    application
) {

    // Campos del formulario
    var nombre by mutableStateOf("")
    var tipoSeleccionado by mutableStateOf("Pastilla")
    var dosis by mutableStateOf("")
    var frecuencia by mutableStateOf("Cada 8 horas")
    var intervaloPersonalizado by mutableStateOf("")
    var horaToma by mutableStateOf("08:00")
    var fechaInicio by mutableStateOf(
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
    )
    var fechaFin by mutableStateOf("")
    var recordatorioActivo by mutableStateOf(true)

    // Estados de UI
    var mensajeError by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf<String?>(null)
    var guardadoExitoso by mutableStateOf(false)
    var actualizacionExitosa by mutableStateOf(false)
    var eliminacionExitosa by mutableStateOf(false)
    var cargando by mutableStateOf(false)

    // Estado para la consulta de detalle
    var medicamentoSeleccionado by mutableStateOf<Medicamento?>(null)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val alarmScheduler = AndroidAlarmScheduler(application)

    val sugerenciaDosis: String
        get() = when (tipoSeleccionado) {
            "Pastilla" -> "Ej. 1 tableta o 500 mg"
            "Jarabe" -> "Ej. 10 ml o 1 cucharada"
            "Inyección" -> "Ej. 1 ampolla o 2.5 ml"
            "Gotas" -> "Ej. 5 gotas"
            else -> "Escribe la dosis"
        }

    // RF14: Obtener un medicamento por ID
    fun obtenerMedicamentoPorId(
        id: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "medicamentos"
        ).document(
            id
        ).get(
        ).addOnSuccessListener { doc ->
            medicamentoSeleccionado = doc.toObject(Medicamento::class.java)?.copy(id = doc.id)
        }
    }

    // RF17: Confirmar Toma del medicamento en Firestore
    fun confirmarToma(
        medicamento: Medicamento,
        alTerminar: () -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: return

        // Verificación esencial: Si el ID es vacío, no podemos hacer nada
        if (medicamento.id.isEmpty()) {
            android.util.Log.e("DEBUG_APP", "Error: El medicamento no tiene ID (está vacío)")
            mensajeError = "Error interno: El medicamento no tiene ID."
            return
        }

        cargando = true

        firestore.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .document(medicamento.id)
            .update("yaFueTomado", true)
            .addOnSuccessListener {
                // 2. Si se actualizó el check, guardamos en el historial
                registrarEnHistorial(medicamento, true)

                cargando = false
                mensajeExito = "¡Medicamento tomado!"
                alTerminar()
            }
            .addOnFailureListener { e ->
                cargando = false
                mensajeError = "No se pudo actualizar: ${e.message}"
                android.util.Log.e("DEBUG_APP", "Fallo al actualizar toma: ${e.message}")
            }
    }

    // RF17: Omitir la toma actual
    fun omitirToma(
        medicamento: Medicamento,
        alTerminar: () -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: return
        cargando = true

        // RF19: Agregar documento al historial de medicamentos
        registrarEnHistorial(
            medicamento = medicamento,
            esTomado = false
        )
        cargando = false
        mensajeExito = "Dosis omitida para este horario."
        alTerminar()
    }

    private fun registrarEnHistorial(medicamento: Medicamento, esTomado: Boolean) {
        val uid = auth.currentUser?.uid ?: return

        val nuevoRegistro = RegistroHistorial(
            medicamentoId = medicamento.id,
            nombreActividad = medicamento.nombre,
            tipo = "MEDICAMENTO",
            accion = if (esTomado) "Tomado" else "Omitido",
            estado = if (esTomado) "Tomado" else "Omitido",
            fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            timestamp = System.currentTimeMillis()
        )


        firestore.collection("usuarios")
            .document(uid)
            .collection("historial_medicamentos")
            .add(nuevoRegistro) // Firebase usará los nombres de los campos de la clase data class
            .addOnSuccessListener {
                android.util.Log.d("DEBUG_APP", "EXITO: Registro guardado")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("DEBUG_APP", "ERROR: ${e.message}")
            }
    }

    // RF18: Posponer Recordatorio (Alarma Nativa con AlarmManager)
    fun posponerRecordatorio(
        medicamento: Medicamento,
        minutos: Int = 10,
        alTerminar: () -> Unit
    ) {
        // Reprograma la alarma nativa del sistema
        alarmScheduler.posponer(
            medicamento = medicamento,
            minutos = minutos
        )
        mensajeExito = "Recordatorio pospuesto $minutos minutos."
        alTerminar()
    }

    fun seleccionarMedicamento(
        medicamento: Medicamento
    ) {
        medicamentoSeleccionado = medicamento
    }

    fun cerrarDetalle() {
        medicamentoSeleccionado = null
    }

    fun eliminarMedicamento(
        medicamentoId: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        cargando = true
        eliminacionExitosa = false

        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "medicamentos"
        ).document(
            medicamentoId
        ).delete(
        ).addOnSuccessListener {
            cargando = false
            eliminacionExitosa = true
            medicamentoSeleccionado = null
        }.addOnFailureListener {
            cargando = false
            mensajeError = "No se pudo eliminar el medicamento."
        }
    }

    fun cargarDatosMedicamento(
        medicamento: Medicamento
    ) {
        nombre = medicamento.nombre
        tipoSeleccionado = medicamento.tipo
        dosis = medicamento.dosis
        val opcionesFijas = listOf("Cada 4 horas", "Cada 6 horas", "Cada 8 horas", "Cada 12 horas", "Una vez al día")
        if (medicamento.frecuencia !in opcionesFijas) {
            frecuencia = "Personalizar"
            intervaloPersonalizado = medicamento.frecuencia.replace("Cada ", "").replace(" horas", "")
        } else {
            frecuencia = medicamento.frecuencia
            intervaloPersonalizado = ""
        }
        horaToma = medicamento.horaToma
        fechaInicio = medicamento.fechaInicio
        fechaFin = medicamento.fechaFin
        recordatorioActivo = medicamento.recordatorioActivo
    }

    fun actualizarMedicamento(
        medicamentoId: String
    ) {
        if (!validarCampos()) return
        val uid = auth.currentUser?.uid ?: return
        cargando = true
        val datosActualizados = obtenerMapaMedicamento()
        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "medicamentos"
        ).document(
            medicamentoId
        ).update(
            datosActualizados
        ).addOnSuccessListener {
            gestionarAlarma(medicamentoId)
            cargando = false
            actualizacionExitosa = true
        }.addOnFailureListener {
            cargando = false
            mensajeError = "No se pudo actualizar el medicamento."
        }
    }

    fun guardarMedicamento() {
        if (!validarCampos()) return
        val uid = auth.currentUser?.uid ?: return
        cargando = true
        val datos = obtenerMapaMedicamento()
        firestore.collection(
            "usuarios"
        ).document(
            uid
        ).collection(
            "medicamentos"
        ).add(
            datos
        ).addOnSuccessListener { docRef ->
            gestionarAlarma(docRef.id)
            cargando = false
            guardadoExitoso = true
        }.addOnFailureListener {
            cargando = false
            mensajeError = "Error al guardar."
        }
    }

    private fun gestionarAlarma(
        id: String
    ) {
        val med = Medicamento(
            id = id,
            nombre = nombre,
            dosis = dosis,
            tipo = tipoSeleccionado,
            horaToma = horaToma,
            fechaInicio = fechaInicio,
            recordatorioActivo = recordatorioActivo
        )
        if (recordatorioActivo) {
            alarmScheduler.programar(med)
        } else {
            alarmScheduler.cancelar(med)
        }
    }

    private fun validarCampos(): Boolean {
        if (nombre.trim().length < 4) {
            mensajeError = "Ingresa el nombre del medicamento (mín. 4 caracteres)."
            return false
        }
        if (dosis.trim().isEmpty()) {
            mensajeError = "Especifica la dosis."
            return false
        }
        if (frecuencia == "Personalizar" && intervaloPersonalizado.trim().isEmpty()) {
            mensajeError = "Ingresa el intervalo de tiempo personalizado."
            return false
        }
        mensajeError = null
        return true
    }

    private fun obtenerMapaMedicamento(): Map<String, Any> {
        val frecuenciaFinal = if (frecuencia == "Personalizar") {
            "Cada ${intervaloPersonalizado.trim()} horas"
        } else {
            frecuencia
        }
        return mapOf(
            "nombre" to nombre.trim(),
            "tipo" to tipoSeleccionado,
            "dosis" to dosis.trim(),
            "frecuencia" to frecuenciaFinal,
            "horaToma" to horaToma,
            "fechaInicio" to fechaInicio,
            "fechaFin" to fechaFin.trim(),
            "recordatorioActivo" to recordatorioActivo,
            "yaFueTomado" to false
        )
    }

    fun limpiarFormulario() {
        nombre = ""
        tipoSeleccionado = "Pastilla"
        dosis = ""
        frecuencia = "Cada 8 horas"
        intervaloPersonalizado = ""
        horaToma = "08:00"
        fechaInicio = LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        fechaFin = ""
        recordatorioActivo = true
        mensajeError = null
        mensajeExito = null
        guardadoExitoso = false
        actualizacionExitosa = false
        eliminacionExitosa = false
        cargando = false
    }
}
