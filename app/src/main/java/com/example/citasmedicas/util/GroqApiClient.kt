package com.example.citasmedicas.util

import com.example.citasmedicas.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GroqApiClient {

    private val apiKey = BuildConfig.GROQ_API_KEY
    // Cambiamos a este modelo que es el más estable para evitar el 404
    private val model = "openai/gpt-oss-120b"
    private val baseUrl = "https://api.groq.com/openai/v1/chat/completions"

    suspend fun enviarPregunta(
        systemPrompt: String,
        preguntaUsuario: String
    ): String = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isEmpty() || apiKey == "DEBUG") {
                return@withContext "Error: La API Key está vacía. Revise local.properties."
            }

            val url = URL(baseUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.doOutput = true

            val body = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", preguntaUsuario)
                    })
                })
            }.toString()

            OutputStreamWriter(conn.outputStream).use { it.write(body) }

            val code = conn.responseCode
            // Si el código no es 200, leemos el errorStream para saber qué pasó
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseStr = stream.bufferedReader().readText()

            if (code !in 200..299) {
                // ESTO ES LO MÁS IMPORTANTE: Mira el Logcat para ver este mensaje
                println("GROQ_ERROR_JSON: $responseStr")

                return@withContext when(code) {
                    404 -> "Error 404: El modelo '$model' no se encontró o la URL es incorrecta."
                    401 -> "Error 401: La API Key no es válida."
                    else -> "Error $code: Error en el servidor de IA."
                }
            }

            JSONObject(responseStr)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()

        } catch (e: Exception) {
            e.printStackTrace()
            "Error de conexión: ${e.localizedMessage}"
        }
    }
}