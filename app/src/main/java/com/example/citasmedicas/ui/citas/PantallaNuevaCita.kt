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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevaCita(
    alGuardar: () -> Unit,
    alVolver: () -> Unit,
    modelo: CitaViewModel = viewModel()
) {
    val estadoScroll = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var mostrarReloj by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    // Manejar éxito al guardar
    LaunchedEffect(modelo.guardadoExitoso) {
        if (modelo.guardadoExitoso) {
            snackbarHostState.showSnackbar("Cita médica registrada correctamente.")
            delay(1500)
            alGuardar()
            modelo.limpiarFormulario()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nueva Cita Médica", 
                        color = AzulPrimario, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = FontFamily.Default 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            
            // Tarjeta 1: Información Principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Información de la Cita", fontWeight = FontWeight.Bold, color = AzulPrimario, fontFamily = FontFamily.Default)

                    Text("Motivo / Especialidad *", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.motivo,
                        onValueChange = { modelo.motivo = it },
                        placeholder = { Text("Ej. Cardiología o Chequeo", fontFamily = FontFamily.Default) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.MedicalServices, null) }
                    )

                    Text("Doctor / Profesional", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.doctor,
                        onValueChange = { modelo.doctor = it },
                        placeholder = { Text("Ej. Dr. Roberto Gómez (Opcional)", fontFamily = FontFamily.Default) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )

                    Text("Lugar / Ubicación *", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.lugar,
                        onValueChange = { modelo.lugar = it },
                        placeholder = { Text("Ej. Hospital Central, Consultorio 302", fontFamily = FontFamily.Default) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) }
                    )
                }
            }

            // Tarjeta 2: Fecha, Hora y Recordatorio
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Programación", fontWeight = FontWeight.Bold, color = AzulPrimario, fontFamily = FontFamily.Default)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("Fecha *", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                            OutlinedTextField(
                                value = modelo.fecha,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { mostrarCalendario = true }) {
                                        Icon(Icons.Default.CalendarToday, null)
                                    }
                                }
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Hora *", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                            OutlinedTextField(
                                value = modelo.hora,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { mostrarReloj = true }) {
                                        Icon(Icons.Default.Schedule, null)
                                    }
                                }
                            )
                        }
                    }

                    // Interruptor de Recordatorio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Activar recordatorio", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                            Text("Notificar antes de la cita", fontSize = 12.sp, color = Color.Gray, fontFamily = FontFamily.Default)
                        }
                        Switch(
                            checked = modelo.recordatorioActivo,
                            onCheckedChange = { modelo.recordatorioActivo = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AzulPrimario)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                    Text("Notas adicionales", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.notas,
                        onValueChange = { modelo.notas = it },
                        placeholder = { Text("Ej. Ir en ayunas, llevar exámenes previos...", fontFamily = FontFamily.Default) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )
                }
            }

            // Mensaje de error dinámico
            modelo.mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 4.dp), fontSize = 14.sp, fontFamily = FontFamily.Default)
            }

            Spacer(Modifier.height(8.dp))

            // Botón de Guardar
            Button(
                onClick = { modelo.guardarCita() },
                enabled = !modelo.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
            ) {
                if (modelo.cargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Cita Médica", fontSize = 16.sp, fontFamily = FontFamily.Default)
                }
            }
        }
    }

    // Selectores de Fecha y Hora
    if (mostrarReloj) {
        val estadoReloj = rememberTimePickerState(initialHour = 8, initialMinute = 0)
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(onClick = {
                    modelo.hora = String.format("%02d:%02d", estadoReloj.hour, estadoReloj.minute)
                    mostrarReloj = false
                }) { Text("Confirmar", fontFamily = FontFamily.Default) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) { Text("Cancelar", fontFamily = FontFamily.Default) }
            },
            text = { TimePicker(state = estadoReloj) }
        )
    }

    if (mostrarCalendario) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let {
                        val fecha = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        modelo.fecha = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                    mostrarCalendario = false
                }) { Text("OK", fontFamily = FontFamily.Default) }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }
}
