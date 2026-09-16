package com.example.citasmedicas.data

// Datos del usuario que se registra
data class Usuario(
    val nombre: String,
    val correo: String,
    val contrasena: String
)

// Datos de la medicina que queremos guardar
data class Medicamento(
    val nombre: String,
    val tipo: String, // Ejemplo: Pastilla, Jarabe...
    val dosis: String, // Ejemplo: 1 tableta, 10 ml...
    val frecuencia: String, // Ejemplo: Cada 8 horas
    val horaToma: String, // A qué hora se toma la primera vez
    val fechaInicio: String,
    val fechaFin: String = "", // Es opcional
    val yaFueTomado: Boolean = false // Para saber si ya se tomó la medicina hoy
)

// Datos de la cita médica para la agenda y dashboard
data class CitaMedica(
    val id: String = "",
    val doctor: String,
    val especialidad: String,
    val fecha: String, // Formato YYYY-MM-DD o similar
    val hora: String,
    val ubicacion: String,
    val estado: String = "Pendiente" // Pendiente, Confirmada, etc.
)
