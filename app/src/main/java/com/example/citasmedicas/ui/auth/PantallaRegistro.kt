package com.example.citasmedicas.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.ui.theme.AzulPrimario

@Composable
fun PantallaRegistro(
    alRegistrar: () -> Unit,
    irALogin: () -> Unit,
    modelo: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }

    // Si se registra bien, vamos a la pantalla principal o login
    LaunchedEffect(modelo.registroExitoso) {
        if (modelo.registroExitoso) {
            alRegistrar()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
        Spacer(Modifier.height(20.dp))

        // Campo de Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; modelo.limpiarMensajes() },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Campo de Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it; modelo.limpiarMensajes() },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Campo de Contraseña con el "ojito"
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it; modelo.limpiarMensajes() },
            label = { Text("Contraseña (mínimo 6 letras)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val imagen = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { claveVisible = !claveVisible }) {
                    Icon(imageVector = imagen, contentDescription = "Ver contraseña")
                }
            }
        )

        // Mostrar errores si existen
        modelo.mensajeError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(20.dp))

        // Botón de Registro
        if (modelo.cargando) {
            CircularProgressIndicator(color = AzulPrimario)
        } else {
            Button(
                onClick = { modelo.registrarUsuario(nombre, correo, clave) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarme")
            }
        }

        Spacer(Modifier.height(10.dp))

        // Botón para volver al Login
        TextButton(onClick = irALogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
