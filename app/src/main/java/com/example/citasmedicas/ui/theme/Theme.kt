package com.example.citasmedicas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaDeColores = lightColorScheme(
    primary = AzulPrimario,
    secondary = VerdeClaro,
    background = Blanco,
    surface = Blanco,
    onPrimary = Blanco,
    onSecondary = AzulPrimario,
    onBackground = AzulPrimario,
    onSurface = AzulPrimario
)

@Composable
fun CitasMedicasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaDeColores,
        typography = Typography, // <-- ESTA LÍNEA ES LA QUE APLICA TU TIPOGRAFÍA GLOBAL
        content = content
    )
}