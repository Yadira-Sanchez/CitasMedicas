package com.example.citasmedicas.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.citasmedicas.data.Medicamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context, 
        intent: Intent
    ) {
        // Escuchar reinicio completo o desbloqueo inicial
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid ?: return
            val firestore = FirebaseFirestore.getInstance()
            val scheduler = AndroidAlarmScheduler(context)

            // Consultar medicamentos con recordatorio activo para volver a programar en el sistema
            firestore.collection("usuarios")
                .document(uid)
                .collection("medicamentos")
                .whereEqualTo("recordatorioActivo", true)
                .get()
                .addOnSuccessListener { documentos ->
                    for (doc in documentos) {
                        val medicamento = doc.toObject(Medicamento::class.java).copy(id = doc.id)
                        scheduler.programar(medicamento)
                    }
                }
        }
    }
}
