package com.example.citasmedicas.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { 
        SnackbarHostState() 
    }

    var mostrarDialogoContrasena by remember { 
        mutableStateOf(value = false) 
    }
    var mostrarDialogoCerrarSesion by remember { 
        mutableStateOf(value = false) 
    }

    LaunchedEffect(
        key1 = uiState.mensajeEstado
    ) {
        uiState.mensajeEstado?.let { mensaje ->
            snackbarHostState.showSnackbar(
                message = mensaje
            )
            viewModel.limpiarMensaje()
        }
    }

    // Cerrar el diálogo de contraseña solo si fue exitoso
    LaunchedEffect(
        key1 = uiState.cambioExitoso
    ) {
        if (uiState.cambioExitoso) {
            mostrarDialogoContrasena = false
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
                }
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
                .verticalScroll(state = rememberScrollState())
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(size = 100.dp),
                shape = CircleShape,
                color = AzulPrimario.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(size = 48.dp),
                        tint = AzulPrimario
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(height = 24.dp)
            )

            // SECCIÓN DATOS PERSONALES (RF21)
            Text(
                text = "Datos Personales",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(alignment = Alignment.Start),
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(height = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 12.dp)
                ) {
                    var nombreEdit by remember(uiState.nombre) { 
                        mutableStateOf(value = uiState.nombre) 
                    }

                    OutlinedTextField(
                        value = nombreEdit,
                        onValueChange = { nombreEdit = it },
                        label = { Text(text = "Nombre completo") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Person, 
                                contentDescription = null
                            ) 
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { },
                        label = { Text(text = "Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Email, 
                                contentDescription = null
                            ) 
                        },
                        readOnly = true,
                        enabled = false
                    )

                    Button(
                        onClick = { 
                            viewModel.actualizarPerfil(nombreEdit) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Guardar cambios")
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(height = 24.dp)
            )

            // SECCIÓN SEGURIDAD (RF03)
            Text(
                text = "Seguridad",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(alignment = Alignment.Start),
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(height = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp)
                ) {
                    OutlinedButton(
                        onClick = { mostrarDialogoContrasena = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock, 
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(width = 8.dp))
                        Text(text = "Cambiar contraseña")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(height = 32.dp)
            )

            // SECCIÓN SESIÓN (RF22)
            Button(
                onClick = { mostrarDialogoCerrarSesion = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                ),
                shape = RoundedCornerShape(size = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp, 
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(
                    text = "Cerrar sesión", 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // DIÁLOGOS
    if (mostrarDialogoContrasena) {
        DialogoCambioContrasena(
            uiState = uiState,
            alLimpiarErrorActual = { viewModel.limpiarErrorContrasenaActual() },
            alCerrar = { mostrarDialogoContrasena = false },
            alConfirmar = { actual, nueva ->
                viewModel.cambiarContrasena(actual, nueva)
            }
        )
    }

    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = { Text(text = "Cerrar sesión") },
            text = { Text(text = "¿Seguro que deseas salir?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        viewModel.cerrarSesion(alCerrarSesion)
                    }
                ) {
                    Text(
                        text = "Sí, salir", 
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCerrarSesion = false }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
fun DialogoCambioContrasena(
    uiState: PerfilUiState,
    alLimpiarErrorActual: () -> Unit,
    alCerrar: () -> Unit,
    alConfirmar: (String, String) -> Unit
) {
    var actual by remember { mutableStateOf(value = "") }
    var nueva by remember { mutableStateOf(value = "") }
    var confirmar by remember { mutableStateOf(value = "") }
    
    var visibleActual by remember { mutableStateOf(value = false) }
    var visibleNueva by remember { mutableStateOf(value = false) }
    var visibleConf by remember { mutableStateOf(value = false) }

    // Validaciones en tiempo real
    val errorNueva = if (nueva.isNotEmpty() && nueva.length < 6) "Mínimo 6 caracteres" else null
    val errorConfirmar = if (confirmar.isNotEmpty() && confirmar != nueva) "Las contraseñas no coinciden" else null
    
    val puedeConfirmar = actual.isNotEmpty() && 
                         nueva.length >= 6 && 
                         nueva != actual && 
                         confirmar == nueva && 
                         !uiState.isLoading

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text(text = "Cambiar Contraseña") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                CampoContrasena(
                    valor = actual,
                    onValueChange = { 
                        actual = it
                        alLimpiarErrorActual()
                    },
                    label = "Contraseña actual",
                    visible = visibleActual,
                    alCambiarVisibilidad = { visibleActual = !visibleActual },
                    isError = uiState.errorContrasenaActual != null,
                    supportingText = uiState.errorContrasenaActual
                )
                CampoContrasena(
                    valor = nueva,
                    onValueChange = { nueva = it },
                    label = "Nueva contraseña",
                    visible = visibleNueva,
                    alCambiarVisibilidad = { visibleNueva = !visibleNueva },
                    isError = errorNueva != null,
                    supportingText = errorNueva
                )
                CampoContrasena(
                    valor = confirmar,
                    onValueChange = { confirmar = it },
                    label = "Confirmar contraseña",
                    visible = visibleConf,
                    alCambiarVisibilidad = { visibleConf = !visibleConf },
                    isError = errorConfirmar != null,
                    supportingText = errorConfirmar
                )

                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { alConfirmar(actual, nueva) },
                enabled = puedeConfirmar
            ) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = alCerrar,
                enabled = !uiState.isLoading
            ) {
                Text(text = "Cancelar")
            }
        }
    )
}

@Composable
fun CampoContrasena(
    valor: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    alCambiarVisibilidad: () -> Unit,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = alCambiarVisibilidad) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        isError = isError,
        supportingText = {
            if (supportingText != null) {
                Text(text = supportingText)
            }
        },
        singleLine = true
    )
}
