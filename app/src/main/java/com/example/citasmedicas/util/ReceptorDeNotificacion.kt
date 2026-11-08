package com.example.citasmedicas.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.citasmedicas.data.Medicamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReceptorDeNotificacion : BroadcastReceiver() {

    override fun onReceive(
        context: Context, 
        intent: Intent
    ) {
        val gestor = GestorDeNotificaciones(context)
        val scheduler = AndroidAlarmScheduler(context)

        when (intent.action) {
            // --- ALERTAS DE MEDICAMENTOS ---
            "ALERTA_MEDICAMENTO" -> {
                val idMedicamento = intent.getStringExtra("MEDICAMENTO_ID") ?: return
                val nombre = intent.getStringExtra("MEDICAMENTO_NOMBRE") ?: "Medicamento"
                val dosis = intent.getStringExtra("MEDICAMENTO_DOSIS") ?: ""
                val tipo = intent.getStringExtra("MEDICAMENTO_TIPO") ?: "Pastilla"
                val hora = intent.getStringExtra("MEDICAMENTO_HORA") ?: "00:00"
                
                gestor.mostrarNotificacion(
                    idMedicamento = idMedicamento,
                    nombre = nombre,
                    dosis = dosis,
                    tipo = tipo,
                    hora = hora
                )
            }
            
            // Acción directa desde notificación: Confirmar Toma
            GestorDeNotificaciones.ACCION_CONFIRMAR -> {
                val idMedicamento = intent.getStringExtra("MEDICAMENTO_ID") ?: return
                val resultadoAsync = goAsync()
                confirmarTomaEnFirestore(idMedicamento) {
                    resultadoAsync.finish()
                }
                gestor.cancelarNotificacion(idMedicamento.hashCode())
                Toast.makeText(context, "¡Toma registrada!", Toast.LENGTH_SHORT).show()
            }

            // Acción directa desde notificación: Posponer Toma
            GestorDeNotificaciones.ACCION_POSPONER -> {
                val idMedicamento = intent.getStringExtra("MEDICAMENTO_ID") ?: return
                val nombre = intent.getStringExtra("MEDICAMENTO_NOMBRE") ?: "Medicamento"
                val dosis = intent.getStringExtra("MEDICAMENTO_DOSIS") ?: ""
                val tipo = intent.getStringExtra("MEDICAMENTO_TIPO") ?: "Pastilla"
                val hora = intent.getStringExtra("MEDICAMENTO_HORA") ?: "00:00"

                val med = Medicamento(
                    id = idMedicamento, 
                    nombre = nombre, 
                    dosis = dosis,
                    tipo = tipo,
                    horaToma = hora,
                    recordatorioActivo = true
                )
                scheduler.posponer(med, 10)
                gestor.cancelarNotificacion(idMedicamento.hashCode())
                Toast.makeText(context, "Pospuesto 10 min", Toast.LENGTH_SHORT).show()
            }

            // --- ALERTAS DE CITAS ---
            "ALERTA_CITA" -> {
                val idCita = intent.getStringExtra("CITA_ID") ?: return
                val motivo = intent.getStringExtra("CITA_MOTIVO") ?: "Cita Médica"
                val lugar = intent.getStringExtra("CITA_LUGAR") ?: ""
                val hora = intent.getStringExtra("CITA_HORA") ?: ""
                
                gestor.mostrarNotificacionCita(
                    idCita = idCita,
                    motivo = motivo,
                    lugar = lugar,
                    hora = hora
                )
            }
        }
    }

    private fun confirmarTomaEnFirestore(
        idMedicamento: String, 
        alTerminar: () -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .document(idMedicamento)
            .update("yaFueTomado", true)
            .addOnCompleteListener {
                alTerminar()
            }
    }
}
