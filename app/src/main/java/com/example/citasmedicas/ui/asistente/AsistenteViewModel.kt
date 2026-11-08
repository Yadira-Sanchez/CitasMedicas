package com.example.citasmedicas.ui.asistente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.data.MensajeChat
import com.example.citasmedicas.util.GroqApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AsistenteViewModel : ViewModel() {

    private val groqClient = GroqApiClient()

    private val _mensajes = MutableStateFlow<List<MensajeChat>>(
        value = emptyList()
    )
    val mensajes: StateFlow<List<MensajeChat>> = _mensajes.asStateFlow()

    private val _estaCargando = MutableStateFlow(
        value = false
    )
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    /**
     * Envía una pregunta al asistente inyectando el contexto de salud del usuario.
     */
    fun enviarMensaje(
        pregunta: String,
        medicamentos: List<Medicamento>,
        citas: List<CitaMedica>
    ) {
        if (pregunta.isBlank()) return

        val nuevoMensajeUsuario = MensajeChat(
            texto = pregunta,
            esDelUsuario = true
        )
        _mensajes.value = _mensajes.value + nuevoMensajeUsuario

        viewModelScope.launch {
            _estaCargando.value = true
            try {
                val systemPrompt = construirSystemPrompt(
                    medicamentos = medicamentos,
                    citas = citas
                )
                
                val respuestaTexto = groqClient.enviarPregunta(
                    systemPrompt = systemPrompt,
                    preguntaUsuario = pregunta
                )

                val mensajeIA = MensajeChat(
                    texto = respuestaTexto,
                    esDelUsuario = false
                )
                _mensajes.value = _mensajes.value + mensajeIA
            } catch (e: Exception) {
                val mensajeError = MensajeChat(
                    texto = "Lo siento, no he podido procesar tu solicitud. Revisa tu conexión.",
                    esDelUsuario = false
                )
                _mensajes.value = _mensajes.value + mensajeError
            } finally {
                _estaCargando.value = false
            }
        }
    }

    /**

     * Construye el prompt del sistema con el contexto dinámico del usuario.     */
    private fun construirSystemPrompt(
        medicamentos: List<Medicamento>,
        citas: List<CitaMedica>
    ): String {
        // 1. Quitamos los ** de aquí para que los datos vayan limpios
        val medsInfo = medicamentos.joinToString("\n") {
            "- ${it.nombre}: ${it.dosis} cada ${it.frecuencia} (${it.horaToma} hrs)"
        }
        val citasInfo = citas.joinToString("\n") {
            "- ${it.especialidad} con Dr. ${it.doctor} el ${it.fecha} a las ${it.hora}"
        }

        return """
            Eres VitalisAI, un asistente virtual de salud amigable, claro y conciso.
            Responde SIEMPRE en español.
            
            FORMATO DE RESPUESTA (IMPORTANTE):
            1. Usa solo TEXTO PLANO.
            2. NO uses asteriscos (*), almohadillas (#), barras (|) ni guiones bajos (_).
            3. Para resaltar algo, simplemente usa MAYÚSCULAS en la palabra importante.
            4. Usa listas con guiones simples (-) y deja una línea en blanco entre párrafos.
            
            CONTEXTO DEL USUARIO:
            Medicamentos activos:
            $medsInfo
            
            Próximas citas médicas:
            $citasInfo
            
            INSTRUCCIÓN CRÍTICA:
            Al final, incluye el descargo de responsabilidad en una línea aparte.
            "Nota: Esta información es únicamente orientativa y no sustituye la consulta ni la prescripción de un médico profesional."
        """.trimIndent()
    }

    /**
     * Limpia el historial de la conversación actual.
     */
    fun limpiarHistorial() {
        _mensajes.value = emptyList()
    }
}
