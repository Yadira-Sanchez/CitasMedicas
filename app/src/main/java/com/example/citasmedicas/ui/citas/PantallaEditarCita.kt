package com.example.citasmedicas.ui.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.ui.theme.AzulPrimario
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarCita(
    citaId: String,
    alActualizar: () -> Unit,
    alVolver: () -> Unit,
    modelo: CitaViewModel = viewModel()
) {
    val estadoScroll = rememberScrollState()
    val snackbarHostState = remember { 
        SnackbarHostState() 
    }
    val scope = rememberCoroutineScope()
    
    var mostrarConfirmacion by remember { 
        mutableStateOf(false) 
    }
    var mostrarReloj by remember { 
        mutableStateOf(false) 
    }
    var mostrarCalendario by remember { 
        mutableStateOf(false) 
    }

    LaunchedEffect(
        key1 = citaId
    ) {
        modelo.obtenerCitaPorId(citaId)
    }

    LaunchedEffect(
        key1 = modelo.citaSeleccionada
    ) {
        modelo.citaSeleccionada?.let {
            modelo.cargarDatosCita(it)
        }
    }

    LaunchedEffect(
        key1 = modelo.actualizacionExitosa
    ) {
        if (modelo.actualizacionExitosa) {
            scope.launch {
                snackbarHostState.showSnackbar("Cita actualizada correctamente.")
                delay(1000)
                alActualizar()
                modelo.limpiarFormulario()
            }
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) 
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Editar Cita Médica", 
                        color = AzulPrimario, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = FontFamily.Default 
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
        }
    ) { padding ->
        if (modelo.citaSeleccionada == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding), 
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AzulPrimario
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(estadoScroll),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: Información de la Cita
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp), 
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información de la Cita", 
                            fontWeight = FontWeight.Bold, 
                            color = AzulPrimario, 
                            fontFamily = FontFamily.Default
                        )

                        Text(
                            text = "Motivo / Especialidad *", 
                            fontWeight = FontWeight.Medium, 
                            fontFamily = FontFamily.Default
                        )
                        OutlinedTextField(
                            value = modelo.motivo,
                            onValueChange = { 
                                modelo.motivo = it 
                            },
                            placeholder = { 
                                Text(
                                    text = "Ej. Cardiología o Chequeo", 
                                    fontFamily = FontFamily.Default
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.MedicalServices, 
                                    contentDescription = null
                                ) 
                            }
                        )

                        Text(
                            text = "Doctor / Profesional", 
                            fontWeight = FontWeight.Medium, 
                            fontFamily = FontFamily.Default
                        )
                        OutlinedTextField(
                            value = modelo.doctor,
                            onValueChange = { 
                                modelo.doctor = it 
                            },
                            placeholder = { 
                                Text(
                                    text = "Ej. Dr. Roberto Gómez", 
                                    fontFamily = FontFamily.Default
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.Person, 
                                    contentDescription = null
                                ) 
                            }
                        )

                        Text(
                            text = "Lugar / Ubicación *", 
                            fontWeight = FontWeight.Medium, 
                            fontFamily = FontFamily.Default
                        )
                        OutlinedTextField(
                            value = modelo.lugar,
                            onValueChange = { 
                                modelo.lugar = it 
                            },
                            placeholder = { 
                                Text(
                                    text = "Ej. Hospital Central", 
                                    fontFamily = FontFamily.Default
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.LocationOn, 
                                    contentDescription = null
                                ) 
                            }
                        )
                    }
                }

                // Tarjeta 2: Programación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp), 
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Programación", 
                            fontWeight = FontWeight.Bold, 
                            color = AzulPrimario, 
                            fontFamily = FontFamily.Default
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Fecha *", 
                                    fontWeight = FontWeight.Medium, 
                                    fontFamily = FontFamily.Default
                                )
                                OutlinedTextField(
                                    value = modelo.fecha,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { 
                                                mostrarCalendario = true 
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday, 
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Hora *", 
                                    fontWeight = FontWeight.Medium, 
                                    fontFamily = FontFamily.Default
                                )
                                OutlinedTextField(
                                    value = modelo.hora,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { 
                                                mostrarReloj = true 
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule, 
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Activar recordatorio", 
                                    fontWeight = FontWeight.Medium, 
                                    fontFamily = FontFamily.Default
                                )
                                Text(
                                    text = "Notificar antes de la cita", 
                                    fontSize = 12.sp, 
                                    color = Color.Gray, 
                                    fontFamily = FontFamily.Default
                                )
                            }
                            Switch(
                                checked = modelo.recordatorioActivo,
                                onCheckedChange = { 
                                    modelo.recordatorioActivo = it 
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AzulPrimario
                                )
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp), 
                            thickness = 0.5.dp
                        )

                        Text(
                            text = "Notas adicionales", 
                            fontWeight = FontWeight.Medium, 
                            fontFamily = FontFamily.Default
                        )
                        OutlinedTextField(
                            value = modelo.notas,
                            onValueChange = { 
                                modelo.notas = it 
                            },
                            placeholder = { 
                                Text(
                                    text = "Ej. Ir en ayunas...", 
                                    fontFamily = FontFamily.Default
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4
                        )
                    }
                }

                modelo.mensajeError?.let {
                    Text(
                        text = it, 
                        color = MaterialTheme.colorScheme.error, 
                        fontFamily = FontFamily.Default
                    )
                }

                Button(
                    onClick = { 
                        mostrarConfirmacion = true 
                    },
                    enabled = !modelo.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrimario
                    )
                ) {
                    if (modelo.cargando) {
                        CircularProgressIndicator(
                            color = Color.White, 
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save, 
                            contentDescription = null
                        )
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                        Text(
                            text = "Guardar cambios", 
                            fontSize = 16.sp, 
                            fontFamily = FontFamily.Default
                        )
                    }
                }
            }
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { 
                mostrarConfirmacion = false 
            },
            title = { 
                Text(
                    text = "Confirmar actualización", 
                    fontFamily = FontFamily.Default 
                ) 
            },
            text = { 
                Text(
                    text = "¿Estás seguro de que deseas guardar los cambios en esta cita?", 
                    fontFamily = FontFamily.Default 
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        mostrarConfirmacion = false
                        modelo.actualizarCita(citaId) 
                    }
                ) {
                    Text(
                        text = "Guardar", 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = FontFamily.Default
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        mostrarConfirmacion = false 
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

    if (mostrarReloj) {
        val estadoReloj = rememberTimePickerState(
            initialHour = 8, 
            initialMinute = 0
        )
        AlertDialog(
            onDismissRequest = { 
                mostrarReloj = false 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modelo.hora = String.format("%02d:%02d", estadoReloj.hour, estadoReloj.minute)
                        mostrarReloj = false
                    }
                ) { 
                    Text(
                        text = "Confirmar", 
                        fontFamily = FontFamily.Default
                    ) 
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        mostrarReloj = false 
                    }
                ) { 
                    Text(
                        text = "Cancelar", 
                        fontFamily = FontFamily.Default
                    ) 
                }
            },
            text = { 
                TimePicker(
                    state = estadoReloj
                ) 
            }
        )
    }

    if (mostrarCalendario) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { 
                mostrarCalendario = false 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        estadoFecha.selectedDateMillis?.let {
                            val fecha = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                            modelo.fecha = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        }
                        mostrarCalendario = false
                    }
                ) { 
                    Text(
                        text = "OK", 
                        fontFamily = FontFamily.Default
                    ) 
                }
            }
        ) {
            DatePicker(
                state = estadoFecha
            )
        }
    }
}
