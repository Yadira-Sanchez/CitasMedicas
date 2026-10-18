package com.example.citasmedicas.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.citasmedicas.MainActivity

class GestorDeNotificaciones(private val context: Context) {

    companion object {
        const val CANAL_ID = "recordatorios_medicamentos"
        const val CANAL_NOMBRE = "Recordatorios de Medicamentos"
        const val ACCION_CONFIRMAR = "com.example.citasmedicas.CONFIRMAR_TOMA"
        const val ACCION_POSPONER = "com.example.citasmedicas.POSPONER_TOMA"
        const val ACCION_OMITIR = "com.example.citasmedicas.OMITIR_TOMA"
    }

    init {
        crearCanalDeNotificacion()
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val canal = NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para recordarte tomar tu medicina"
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    fun mostrarNotificacion(
        idMedicamento: String,
        nombre: String,
        dosis: String,
        tipo: String,
        hora: String
    ) {
        val intentPrincipal = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("PANTALLA", "ALERTA")
            putExtra("MEDICAMENTO_ID", idMedicamento)
            putExtra("MEDICAMENTO_NOMBRE", nombre)
            putExtra("MEDICAMENTO_DOSIS", dosis)
            putExtra("MEDICAMENTO_TIPO", tipo)
            putExtra("MEDICAMENTO_HORA", hora)
        }

        val pendingIntentPrincipal = PendingIntent.getActivity(
            context,
            idMedicamento.hashCode(),
            intentPrincipal,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción: Confirmar
        val intentConfirmar = Intent(context, ReceptorDeNotificacion::class.java).apply {
            action = ACCION_CONFIRMAR
            putExtra("MEDICAMENTO_ID", idMedicamento)
        }
        val pendingConfirmar = PendingIntent.getBroadcast(
            context,
            idMedicamento.hashCode() + 1,
            intentConfirmar,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción: Posponer
        val intentPosponer = Intent(context, ReceptorDeNotificacion::class.java).apply {
            action = ACCION_POSPONER
            putExtra("MEDICAMENTO_ID", idMedicamento)
            putExtra("MEDICAMENTO_NOMBRE", nombre)
            putExtra("MEDICAMENTO_DOSIS", dosis)
            putExtra("MEDICAMENTO_TIPO", tipo)
            putExtra("MEDICAMENTO_HORA", hora)
        }
        val pendingPosponer = PendingIntent.getBroadcast(
            context,
            idMedicamento.hashCode() + 2,
            intentPosponer,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("¡Hora de tomar tu medicamento!")
            .setContentText("$nombre - $dosis")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntentPrincipal, true)
            .setContentIntent(pendingIntentPrincipal)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(0, "Confirmar", pendingConfirmar)
            .addAction(0, "Posponer 10 min", pendingPosponer)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(idMedicamento.hashCode(), builder.build())
    }

    fun cancelarNotificacion(idNotificacion: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(idNotificacion)
    }
}
