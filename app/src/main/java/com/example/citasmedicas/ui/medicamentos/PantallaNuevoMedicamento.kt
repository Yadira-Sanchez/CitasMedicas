package com.example.citasmedicas.ui.medicamentos

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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevoMedicamento(
    alGuardar: () -> Unit,
    alVolver: () -> Unit,
    modelo: MedicamentoViewModel = viewModel()
) {
    val estadoScroll = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarReloj by remember { mutableStateOf(false) }
    var mostrarCalendarioInicio by remember { mutableStateOf(false) }
    var mostrarCalendarioFin by remember { mutableStateOf(false) }

    // 3. Optimización de Cierre al Guardar: Navegación inmediata
    LaunchedEffect(modelo.guardadoExitoso) {
        if (modelo.guardadoExitoso) {
            alGuardar()
            modelo.limpiarFormulario()
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
                        text = "Nuevo Medicamento",
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(estadoScroll),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Tarjeta 1: Información Base
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
                        text = "Nombre del Medicamento",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                    OutlinedTextField(
                        value = modelo.nombre,
                        onValueChange = { modelo.nombre = it },
                        placeholder = {
                            Text(
                                text = "Ej. Ibuprofeno",
                                fontFamily = FontFamily.Default
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )

                    Text(
                        text = "Tipo",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                    val tipos = listOf("Pastilla", "Jarabe", "Inyección", "Gotas")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tipos.forEach { tipo ->
                            FilterChip(
                                selected = modelo.tipoSeleccionado == tipo,
                                onClick = { modelo.tipoSeleccionado = tipo },
                                label = {
                                    Text(
                                        text = tipo,
                                        fontFamily = FontFamily.Default
                                    )
                                }
                            )
                        }
                    }

                    Text(
                        text = "Dosis / Cantidad",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                    OutlinedTextField(
                        value = modelo.dosis,
                        onValueChange = { modelo.dosis = it },
                        placeholder = {
                            Text(
                                text = modelo.sugerenciaDosis,
                                fontFamily = FontFamily.Default
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
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
                    
                    // 4. Diseño del Interruptor con contraste estético limpio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Activar recordatorios",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default
                            )
                            Text(
                                text = "Recibir notificaciones en este dispositivo",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Default
                            )
                        }
                        Switch(
                            checked = modelo.recordatorioActivo,
                            onCheckedChange = { modelo.recordatorioActivo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AzulPrimario,
                                checkedTrackColor = AzulPrimario.copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp
                    )

                    Text(
                        text = "Frecuencia",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                    var expandido by remember { mutableStateOf(false) }
                    val opciones = listOf("Cada 4 horas", "Cada 6 horas", "Cada 8 horas", "Cada 12 horas", "Una vez al día", "Personalizar")
                    
                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { expandido = !expandido }
                    ) {
                        OutlinedTextField(
                            value = modelo.frecuencia,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandido
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false }
                        ) {
                            opciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = opcion,
                                            fontFamily = FontFamily.Default
                                        )
                                    },
                                    onClick = {
                                        modelo.frecuencia = opcion
                                        expandido = false
                                    }
                                )
                            }
                        }
                    }

                    if (modelo.frecuencia == "Personalizar") {
                        OutlinedTextField(
                            value = modelo.intervaloPersonalizado,
                            onValueChange = { modelo.intervaloPersonalizado = it },
                            label = {
                                Text(
                                    text = "¿Cada cuántas horas?",
                                    fontFamily = FontFamily.Default
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(
                        text = "Primera toma",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                    OutlinedTextField(
                        value = modelo.horaToma,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { mostrarReloj = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Fecha de inicio",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default
                            )
                            OutlinedTextField(
                                value = modelo.fechaInicio,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(
                                        onClick = { mostrarCalendarioInicio = true }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
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
                                text = "Fecha de fin",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default
                            )
                            OutlinedTextField(
                                value = modelo.fechaFin,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        text = "Opcional",
                                        fontFamily = FontFamily.Default
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { mostrarCalendarioFin = true }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            modelo.mensajeError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = FontFamily.Default
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = { modelo.guardarMedicamento() },
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
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text(
                        text = "Guardar Medicamento",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Default
                    )
                }
            }
        }
    }

    if (mostrarReloj) {
        val estadoReloj = rememberTimePickerState(initialHour = 8, initialMinute = 0)
        TimePickerDialog(
            alCerrar = { mostrarReloj = false },
            alConfirmar = {
                modelo.horaToma = String.format("%02d:%02d", estadoReloj.hour, estadoReloj.minute)
                mostrarReloj = false
            }
        ) {
            TimePicker(
                state = estadoReloj
            )
        }
    }

    if (mostrarCalendarioInicio) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioInicio = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estadoFecha.selectedDateMillis?.let {
                            val fecha = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                            modelo.fechaInicio = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        }
                        mostrarCalendarioInicio = false
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

    if (mostrarCalendarioFin) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioFin = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estadoFecha.selectedDateMillis?.let {
                            val fecha = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                            modelo.fechaFin = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        }
                        mostrarCalendarioFin = false
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

@Composable
fun TimePickerDialog(
    alCerrar: () -> Unit,
    alConfirmar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        confirmButton = {
            TextButton(
                onClick = alConfirmar
            ) {
                Text(
                    text = "Confirmar",
                    fontFamily = FontFamily.Default
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = alCerrar
            ) {
                Text(
                    text = "Cancelar",
                    fontFamily = FontFamily.Default
                )
            }
        },
        text = {
            contenido()
        }
    )
}
