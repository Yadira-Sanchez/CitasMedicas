package com.example.citasmedicas.ui.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleCita(
    citaId: String,
    alEditar: (String) -> Unit,
    alVolver: () -> Unit,
    modelo: CitaViewModel = viewModel()
) {
    val cita = modelo.citaSeleccionada
    var mostrarDialogoEliminar by remember { 
        mutableStateOf(false) 
    }

    LaunchedEffect(
        key1 = citaId
    ) {
        modelo.obtenerCitaPorId(
            id = citaId
        )
    }

    LaunchedEffect(
        key1 = modelo.eliminacionExitosa
    ) {
        if (modelo.eliminacionExitosa) {
            alVolver()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalle de la Cita",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            alVolver()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (cita == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues = padding
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AzulPrimario
                )
            }
        } else {
            ContenidoDetalleCita(
                cita = cita,
                modifier = Modifier.padding(
                    paddingValues = padding
                ),
                alEditar = {
                    alEditar(cita.id)
                },
                alEliminar = {
                    mostrarDialogoEliminar = true
                }
            )
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEliminar = false
            },
            title = {
                Text(
                    text = "¿Eliminar cita médica?",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar esta cita? Se cancelará el recordatorio asociado.",
                    fontFamily = FontFamily.Default
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modelo.eliminarCita(
                            citaId = citaId
                        )
                        mostrarDialogoEliminar = false
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontFamily = FontFamily.Default
                    )
                }
            }
        )
    }
}

@Composable
fun ContenidoDetalleCita(
    cita: CitaMedica,
    modifier: Modifier = Modifier,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            )
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
    ) {
        Text(
            text = cita.especialidad,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = AzulPrimario,
            fontFamily = FontFamily.Default
        )

        Spacer(
            modifier = Modifier.height(
                height = 24.dp
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                size = 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = AzulPrimario
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        all = 20.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "PROGRAMADA PARA",
                        color = Color.White.copy(
                            alpha = 0.8f
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        text = cita.fecha,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        text = "a las ${cita.hora}",
                        color = Color.White.copy(
                            alpha = 0.9f
                        ),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Default
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        size = 12.dp
                    ),
                    color = Color.White.copy(
                        alpha = 0.2f
                    ),
                    modifier = Modifier.size(
                        size = 56.dp
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(
                                size = 32.dp
                            )
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(
                height = 24.dp
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                size = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    vertical = 8.dp
                )
            ) {
                ItemDetalleCita(
                    icono = Icons.Default.Person,
                    titulo = "Médico / Especialista",
                    valor = if (cita.doctor.isEmpty()) "No especificado" else cita.doctor,
                    colorFondoIcono = Color(0xFFE3F2FD),
                    colorIcono = Color(0xFF1976D2)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 24.dp
                    ),
                    thickness = 0.5.dp
                )
                ItemDetalleCita(
                    icono = Icons.Default.LocationOn,
                    titulo = "Lugar / Ubicación",
                    valor = cita.lugar,
                    colorFondoIcono = Color(0xFFE8F5E9),
                    colorIcono = Color(0xFF388E3C)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 24.dp
                    ),
                    thickness = 0.5.dp
                )
                ItemDetalleCita(
                    icono = Icons.Default.NotificationsActive,
                    titulo = "Recordatorio",
                    valor = if (cita.recordatorioActivo) "Activado" else "Desactivado",
                    colorFondoIcono = Color(0xFFFFFDE7),
                    colorIcono = Color(0xFFFBC02D)
                )
                
                if (cita.notas.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 24.dp
                        ),
                        thickness = 0.5.dp
                    )
                    ItemDetalleCita(
                        icono = Icons.AutoMirrored.Filled.Notes,
                        titulo = "Notas adicionales",
                        valor = cita.notas,
                        colorFondoIcono = Color(0xFFF3E5F5),
                        colorIcono = Color(0xFF7B1FA2)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(
                weight = 1f
            )
        )
        
        Spacer(
            modifier = Modifier.height(
                height = 32.dp
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp
            )
        ) {
            OutlinedButton(
                onClick = {
                    alEditar()
                },
                modifier = Modifier
                    .weight(
                        weight = 1f
                    )
                    .height(
                        height = 56.dp
                    ),
                shape = RoundedCornerShape(
                    size = 16.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF0F7FF),
                    contentColor = AzulPrimario
                ),
                border = null
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(
                        width = 8.dp
                    )
                )
                Text(
                    text = "Editar",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                )
            }

            Button(
                onClick = {
                    alEliminar()
                },
                modifier = Modifier
                    .weight(
                        weight = 1f
                    )
                    .height(
                        height = 56.dp
                    ),
                shape = RoundedCornerShape(
                    size = 16.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(
                        width = 8.dp
                    )
                )
                Text(
                    text = "Eliminar",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

@Composable
fun ItemDetalleCita(
    icono: ImageVector,
    titulo: String,
    valor: String,
    colorFondoIcono: Color,
    colorIcono: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                all = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(
                size = 44.dp
            ),
            shape = RoundedCornerShape(
                size = 12.dp
            ),
            color = colorFondoIcono
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(
                        size = 22.dp
                    )
                )
            }
        }
        
        Spacer(
            modifier = Modifier.width(
                width = 16.dp
            )
        )
        
        Column {
            Text(
                text = titulo,
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Default
            )
            Text(
                text = valor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Default
            )
        }
    }
}
