package com.example.citasmedicas.ui.medicamentos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
            mensajeError = "Escribe el nombre del medicamento."
            return
        }
        if (dosis.trim().isEmpty()) {
            mensajeError = "Escribe la dosis o cantidad."
            return
        }
        if (frecuencia == "Personalizar" && intervaloPersonalizado.trim().isEmpty()) {
            mensajeError = "Ingresa el intervalo de tiempo personalizado."
            return
        }

        // Aquí iría la lógica para guardar en Base de Datos o Firebase
        // Por ahora simulamos éxito
        guardadoExitoso = true
        mensajeError = null
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
