package com.example.citasmedicas.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    viewModel: PerfilViewModel = viewModel(),
    alVolver: () -> Unit,
    alNavegarA: (String) -> Unit,
    alCerrarSesion: () -> Unit
) {
    val uiState = viewModel.uiState
    val snackbarHostState = androidx.compose.runtime.remember { 
        SnackbarHostState() 
    }

    // Mostrar mensajes de éxito o error
    LaunchedEffect(
        key1 = uiState.mensajeExito, 
        key2 = uiState.mensajeError
    ) {
        if (uiState.mensajeExito != null) {
            snackbarHostState.showSnackbar(
                message = uiState.mensajeExito
            )
            viewModel.limpiarMensajes()
        } else if (uiState.mensajeError != null) {
            snackbarHostState.showSnackbar(
                message = uiState.mensajeError
            )
            viewModel.limpiarMensajes()
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState
            ) 
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = alVolver
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { alNavegarA("inicio") },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Home, 
                            contentDescription = "Inicio"
                        ) 
                    },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { alNavegarA("medicamentos") },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Medication, 
                            contentDescription = "Medicinas"
                        ) 
                    },
                    label = { Text("Medicinas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { alNavegarA("citas") },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Event, 
                            contentDescription = "Citas"
                        ) 
                    },
                    label = { Text("Citas") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Person, 
                            contentDescription = "Perfil"
                        ) 
                    },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrimario,
                        selectedTextColor = AzulPrimario,
                        indicatorColor = AzulPrimario.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.height(height = 32.dp)
            )

            // Avatar del usuario
            Surface(
                modifier = Modifier.size(size = 120.dp),
                shape = CircleShape,
                color = AzulPrimario.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(size = 64.dp),
                        tint = AzulPrimario
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(height = 32.dp)
            )

            // Tarjeta de información (RF21)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {
                    // Campo Nombre
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { 
                            viewModel.actualizarNombre(it) 
                        },
                        label = { 
                            Text(
                                text = "Nombre completo"
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Person, 
                                contentDescription = null
                            ) 
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulPrimario,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    // Campo Correo (Solo lectura)
                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { },
                        label = { 
                            Text(
                                text = "Correo electrónico"
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Email, 
                                contentDescription = null
                            ) 
                        },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            disabledTextColor = Color.Gray
                        )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(height = 24.dp)
            )

            // Botón Guardar Cambios
            Button(
                onClick = { 
                    viewModel.guardarCambios() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 56.dp),
                shape = RoundedCornerShape(size = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPrimario
                ),
                enabled = !uiState.estaCargando
            ) {
                if (uiState.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.width(width = 8.dp)
                    )
                    Text(
                        text = "Guardar cambios",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(weight = 1f)
            )

            // Botón Cerrar Sesión (RF22)
            TextButton(
                onClick = alCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(width = 8.dp)
                )
                Text(
                    text = "Cerrar sesión",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
