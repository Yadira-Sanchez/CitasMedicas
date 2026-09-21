package com.example.citasmedicas.ui.medicamentos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicamentoViewModel : ViewModel() {

    // Campos del formulario
    var nombre by mutableStateOf("")
    var tipoSeleccionado by mutableStateOf("Pastilla")
    var dosis by mutableStateOf("")
    var frecuencia by mutableStateOf("Cada 8 horas")
    var intervaloPersonalizado by mutableStateOf("")
    var horaToma by mutableStateOf("08:00")
    var fechaInicio by mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
    var fechaFin by mutableStateOf("")

    // Estados de UI
    var mensajeError by mutableStateOf<String?>(null)
    var guardadoExitoso by mutableStateOf(false)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Lógica de Placeholder Dinámico (RF05)
    val sugerenciaDosis: String
        get() = when (tipoSeleccionado) {
            "Pastilla" -> "Ej. 1 tableta o 500 mg"
            "Jarabe" -> "Ej. 10 ml o 1 cucharada"
            "Inyección" -> "Ej. 1 ampolla o 2.5 ml"
            "Gotas" -> "Ej. 5 gotas"
            else -> "Escribe la dosis"
        }

    fun guardarMedicamento() {
        if (nombre.trim().length < 2) {
            mensajeError = "Ingresa el nombre del medicamento."
            return
        }
        if (dosis.trim().isEmpty()) {
            mensajeError = "Especifica la dosis."
            return
        }
        if (frecuencia == "Personalizar" && intervaloPersonalizado.trim().isEmpty()) {
            mensajeError = "Ingresa el intervalo de tiempo personalizado."
            return
        }

        val uid = auth.currentUser?.uid
        if (uid == null) {
            mensajeError = "No se pudo identificar al usuario activo. Inicie sesión de nuevo."
            return
        }

        mensajeError = null

        val frecuenciaFinal = if (frecuencia == "Personalizar") {
            "Cada ${intervaloPersonalizado.trim()} horas"
        } else {
            frecuencia
        }

        // Datos requeridos según el Spec de RF05
        val nuevoMedicamento = mapOf(
            "nombre" to nombre.trim(),
            "tipo" to tipoSeleccionado,
            "dosis" to dosis.trim(),
            "frecuencia" to frecuenciaFinal,
            "horaToma" to horaToma,
            "fechaInicio" to fechaInicio,
            "fechaFin" to fechaFin.trim(),
            "yaFueTomado" to false
        )

        firestore.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .add(nuevoMedicamento)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    guardadoExitoso = true
                } else {
                    mensajeError = "No se pudo guardar el medicamento. Revisa tu conexión a internet."
                }
            }
    }

    fun limpiarFormulario() {
        nombre = ""
        tipoSeleccionado = "Pastilla"
        dosis = ""
        frecuencia = "Cada 8 horas"
        intervaloPersonalizado = ""
        horaToma = "08:00"
        fechaInicio = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        fechaFin = ""
        mensajeError = null
        guardadoExitoso = false
    }
}
