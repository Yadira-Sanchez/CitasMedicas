package com.example.citasmedicas.data

import com.google.firebase.Timestamp

// Datos del usuario que se registra
data class Usuario(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = ""
)

// Datos del medicamento (RF08, RF14)
data class Medicamento(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    val horaToma: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val yaFueTomado: Boolean = false,
    val recordatorioActivo: Boolean = true
)

// Datos de la cita médica (RF09)
data class CitaMedica(
    val id: String = "",
    val especialidad: String = "", // Especialidad o Motivo
    val doctor: String = "", // Opcional
    val lugar: String = "",   // Ubicación
    val fecha: String = "",  // DD/MM/YYYY
    val hora: String = "",   // HH:MM
    val recordatorioActivo: Boolean = true,
    val notas: String = "",
    val creadoEn: Timestamp = Timestamp.now()
)
