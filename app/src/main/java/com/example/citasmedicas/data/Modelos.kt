package com.example.citasmedicas.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// Datos del usuario que se registra
data class Usuario(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = ""
)

// Datos del medicamento (RF08, RF14)
data class Medicamento(
    @DocumentId val id: String = "", // Crucial para identificar el documento
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
    @DocumentId val id: String = "",
    val especialidad: String = "",
    val doctor: String = "",
    val lugar: String = "",
    val fecha: String = "",
    val hora: String = "",
    val recordatorioActivo: Boolean = true,
    val notas: String = "",
    val creadoEn: Timestamp = Timestamp.now()
)

// Datos para el historial (RF19)
data class RegistroHistorial(
    @DocumentId val id: String = "",
    val medicamentoId: String = "",
    val nombreActividad: String = "",
    val tipo: String = "MEDICAMENTO",
    val accion: String = "Tomado",
    val estado: String = "Tomado",
    val fechaHora: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Datos para el chatbot (RF23)
data class MensajeChat(
    val texto: String = "",
    val esDelUsuario: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)