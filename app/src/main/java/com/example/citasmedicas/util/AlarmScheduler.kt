package com.example.citasmedicas.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

interface AlarmScheduler {
    fun programar(medicamento: Medicamento)
    fun cancelar(medicamento: Medicamento)
    fun posponer(medicamento: Medicamento, minutos: Int)
    
    // Métodos para Citas Médicas
    fun programarCita(cita: CitaMedica)
    fun cancelarCita(cita: CitaMedica)
}

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun programar(
        medicamento: Medicamento
    ) {
        if (!medicamento.recordatorioActivo) return

        val intent = Intent(
            context,
            ReceptorDeNotificacion::class.java
        ).apply {
            action = "ALERTA_MEDICAMENTO"
            putExtra("MEDICAMENTO_ID", medicamento.id)
            putExtra("MEDICAMENTO_NOMBRE", medicamento.nombre)
            putExtra("MEDICAMENTO_DOSIS", medicamento.dosis)
            putExtra("MEDICAMENTO_TIPO", medicamento.tipo)
            putExtra("MEDICAMENTO_HORA", medicamento.horaToma)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamento.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parsear hora y fecha de inicio
        val horaToma = LocalTime.parse(medicamento.horaToma)
        val fechaInicio = LocalDate.parse(
            medicamento.fechaInicio,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        
        // Garantizar que calculamos la próxima ocurrencia (si fechaInicio ya pasó, usamos hoy)
        val hoy = LocalDate.now()
        val fechaBase = if (fechaInicio.isBefore(hoy)) hoy else fechaInicio
        
        // Calcular tiempo de disparo exacto en milisegundos
        var triggerTime = LocalDateTime.of(
            fechaBase, 
            horaToma
        ).atZone(
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        // Si el tiempo calculado ya pasó hoy, se programa para el día siguiente
        if (triggerTime <= System.currentTimeMillis()) {
            triggerTime += 24 * 60 * 60 * 1000 
        }

        try {
            // Programación EXACTA con despertar de procesador (RTC_WAKEUP)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Respaldo (Fallback) si el sistema deniega la alarma exacta en Android 12+
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    override fun posponer(
        medicamento: Medicamento, 
        minutos: Int
    ) {
        val intent = Intent(
            context,
            ReceptorDeNotificacion::class.java
        ).apply {
            action = "ALERTA_MEDICAMENTO"
            putExtra("MEDICAMENTO_ID", medicamento.id)
            putExtra("MEDICAMENTO_NOMBRE", medicamento.nombre)
            putExtra("MEDICAMENTO_DOSIS", medicamento.dosis)
            putExtra("MEDICAMENTO_TIPO", medicamento.tipo)
            putExtra("MEDICAMENTO_HORA", medicamento.horaToma)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamento.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (minutos * 60 * 1000)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    override fun cancelar(
        medicamento: Medicamento
    ) {
        val intent = Intent(
            context,
            ReceptorDeNotificacion::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamento.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // --- Implementación para Citas ---

    override fun programarCita(
        cita: CitaMedica
    ) {
        if (!cita.recordatorioActivo) return

        val intent = Intent(
            context,
            ReceptorDeNotificacion::class.java
        ).apply {
            action = "ALERTA_CITA"
            putExtra("CITA_ID", cita.id)
            putExtra("CITA_MOTIVO", cita.especialidad)
            putExtra("CITA_LUGAR", cita.lugar)
            putExtra("CITA_HORA", cita.hora)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            cita.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val horaCita = LocalTime.parse(cita.hora)
        val fechaCita = LocalDate.parse(
            cita.fecha,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )

        // Programar exactamente a la hora de la cita (o X minutos antes si se deseara)
        val triggerTime = LocalDateTime.of(
            fechaCita,
            horaCita
        ).atZone(
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        // Solo programar si la cita es en el futuro
        if (triggerTime > System.currentTimeMillis()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }

    override fun cancelarCita(
        cita: CitaMedica
    ) {
        val intent = Intent(
            context,
            ReceptorDeNotificacion::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            cita.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
