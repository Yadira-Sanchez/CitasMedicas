package com.example.citasmedicas.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.ui.theme.AzulPrimario
import com.example.citasmedicas.R

@Composable
fun PantallaLogin(
    alEntrar: () -> Unit,
    irARegistro: () -> Unit,
    modelo: AuthViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }
    var recordarme by remember { mutableStateOf(false) }

    // Si el login es exitoso, avisamos para cambiar de pantalla
    LaunchedEffect(modelo.loginExitoso) {
        if (modelo.loginExitoso) {
            modelo.reiniciarLoginExitoso()
            alEntrar()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Fondo claro muy sutil para resaltar la tarjeta blanca
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Nombre de la App y Eslogan
            Text(
                text = "Vitalis",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tu salud y bienestar en orden cada día",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            // Tarjeta del Formulario de Inicio de Sesión
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Ingresa tus credenciales para acceder a tus medicamentos y citas",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    // Etiqueta Campo Correo
                    Text(
                        text = "Correo electrónico",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    // Campo de Correo
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; modelo.limpiarMensajes() },
                        placeholder = { Text("usuario@dominio.com", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = null,
                                tint = AzulPrimario
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Ingresa tu correo registrado",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(16.dp))

                    // Etiqueta Campo Contraseña
                    Text(
                        text = "Contraseña",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    // Campo de Contraseña con el "ojito"
                    OutlinedTextField(
                        value = clave,
                        onValueChange = { clave = it; modelo.limpiarMensajes() },
                        placeholder = { Text("Ingresa tu contraseña", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = AzulPrimario
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(6.dp),
                        visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val imagen = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { claveVisible = !claveVisible }) {
                                Icon(imageVector = imagen, contentDescription = "Ver contraseña")
                            }
                        }
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Mínimo 6 caracteres",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(16.dp))

                    // Opciones de Recordarme y Olvidaste contraseña
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = recordarme,
                                onCheckedChange = { recordarme = it }
                            )
                            Text(
                                text = "Recordarme",
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        TextButton(
                            onClick = { /* Acción futura opcional */ },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                fontSize = 14.sp,
                                color = AzulPrimario,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mostrar errores si existen
                    modelo.mensajeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Botón de Entrar o Indicador de Progreso
                    if (modelo.cargando) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AzulPrimario)
                        }
                    } else {
                        Button(
                            onClick = { modelo.iniciarSesion(correo, clave) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Iniciar Sesión",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón para ir a Registro estilizado según el diseño
            TextButton(onClick = irARegistro) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿No tienes una cuenta? ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Regístrate aquí",
                        color = AzulPrimario,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
        }
    }
}
