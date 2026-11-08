package com.example.citasmedicas.ui.asistente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citasmedicas.data.MensajeChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AsistenteUiState(
    val listaMensajes: List<MensajeChat> = listOf(
        MensajeChat(
            texto = "¡Hola! Soy tu asistente médico virtual. ¿En qué puedo ayudarte hoy?",
            esDelUsuario = false
        )
    ),
    val textoMensaje: String = "",
    val estaEscribiendo: Boolean = false
)

class AsistenteViewModel : ViewModel() {
    var uiState by mutableStateOf(
        AsistenteUiState()
    )
        private set

    fun actualizarTexto(
        nuevoTexto: String
    ) {
        uiState = uiState.copy(
            textoMensaje = nuevoTexto
        )
    }

    fun enviarMensaje() {
        val texto = uiState.textoMensaje.trim()
        if (texto.isEmpty()) return

        val nuevoMensajeUsuario = MensajeChat(
            texto = texto,
            esDelUsuario = true
        )

        uiState = uiState.copy(
            listaMensajes = uiState.listaMensajes + nuevoMensajeUsuario,
            textoMensaje = "",
            estaEscribiendo = true
        )

        generarRespuestaAsistente(
            mensajeUsuario = texto
        )
    }

    private fun generarRespuestaAsistente(
        mensajeUsuario: String
    ) {
        viewModelScope.launch {
            // Simulamos un retraso de "pensamiento" de la IA
            delay(1500)

            val respuestaTexto = when {
                mensajeUsuario.contains("hola", ignoreCase = true) -> 
                    "¡Hola de nuevo! Estoy aquí para ayudarte con tus medicamentos y citas."
                
                mensajeUsuario.contains("cita", ignoreCase = true) -> 
                    "Puedes revisar tus citas en la sección de 'Citas' o agregar una nueva con el botón '+' en el inicio."
                
                mensajeUsuario.contains("medicina", ignoreCase = true) || 
                mensajeUsuario.contains("medicamento", ignoreCase = true) -> 
                    "Recuerda que es importante tomar tus medicinas a la hora exacta. ¿Quieres que te ayude a revisar tu horario?"
                
                mensajeUsuario.contains("gracias", ignoreCase = true) -> 
                    "¡De nada! Estoy para servirte."
                
                else -> "Entiendo. Como tu asistente virtual, te sugiero mantener al día tus registros de salud. ¿Hay algo específico sobre tus recordatorios que desees saber?"
            }

            val respuestaAsistente = MensajeChat(
                texto = respuestaTexto,
                esDelUsuario = false
            )

            uiState = uiState.copy(
                listaMensajes = uiState.listaMensajes + respuestaAsistente,
                estaEscribiendo = false
            )
        }
    }
}
