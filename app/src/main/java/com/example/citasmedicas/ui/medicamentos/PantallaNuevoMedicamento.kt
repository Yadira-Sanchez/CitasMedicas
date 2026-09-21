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
import java.time.LocalTime
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
    var mostrarReloj by remember { mutableStateOf(false) }
    var mostrarCalendarioInicio by remember { mutableStateOf(false) }
    var mostrarCalendarioFin by remember { mutableStateOf(false) }

    LaunchedEffect(modelo.guardadoExitoso) {
        if (modelo.guardadoExitoso) {
            alGuardar()
            modelo.limpiarFormulario()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Medicamento", color = AzulPrimario, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default) },
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
            
            // Tarjeta 1: Información Base (Nombre, Selección de Tipo y Dosis)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    val (ejemploNombre, ejemploDosis, unidad) = when (modelo.tipoSeleccionado) {
                        "Jarabe" -> Triple("Ej. Jarabe para la tos", "Ej. 10 ml o 1 cucharada", "ml")
                        "Inyección" -> Triple("Ej. Penicilina", "Ej. 1 ampolla", "ampolla(s)")
                        "Gotas" -> Triple("Ej. Gotas oftálmicas", "Ej. 2 gotas", "gotas")
                        else -> Triple("Ej. Ibuprofeno", "Ej. 1 tableta o 500 mg", "pastilla(s)") // Pastilla por defecto
                    }

                    Text("Nombre del Medicamento", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.nombre,
                        onValueChange = { modelo.nombre = it },
                        placeholder = { Text("Ej. Ibuprofeno", fontFamily = FontFamily.Default) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )

                    Text("Tipo", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                    val tipos = listOf("Pastilla", "Jarabe", "Inyección", "Gotas")
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tipos.forEach { tipo ->
                            FilterChip(
                                selected = modelo.tipoSeleccionado == tipo,
                                onClick = { modelo.tipoSeleccionado = tipo },
                                label = { Text(tipo, fontFamily = FontFamily.Default) }
                            )
                        }
                    }

                    Text("Dosis / Cantidad", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.dosis,
                        onValueChange = { modelo.dosis = it },
                        placeholder = { Text(modelo.sugerenciaDosis, fontFamily = FontFamily.Default) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Tarjeta 2: Programación (Frecuencia, Hora de toma y Fechas)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Frecuencia", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
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
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false }
                        ) {
                            opciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion, fontFamily = FontFamily.Default) },
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
                            label = { Text("¿Cada cuántas horas?", fontFamily = FontFamily.Default) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text("Primera toma", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                    OutlinedTextField(
                        value = modelo.horaToma,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Notifications, null) },
                        trailingIcon = {
                            IconButton(onClick = { mostrarReloj = true }) {
                                Icon(Icons.Default.DateRange, null)
                            }
                        }
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("Fecha de inicio", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                            OutlinedTextField(
                                value = modelo.fechaInicio,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { mostrarCalendarioInicio = true }) {
                                        Icon(Icons.Default.DateRange, null)
                                    }
                                }
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Fecha de fin", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                            OutlinedTextField(
                                value = modelo.fechaFin,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Opcional", fontFamily = FontFamily.Default) },
                                trailingIcon = {
                                    IconButton(onClick = { mostrarCalendarioFin = true }) {
                                        Icon(Icons.Default.DateRange, null)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            modelo.mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Default)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { modelo.guardarMedicamento() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Medicamento y Programar Alerta", fontSize = 14.sp, fontFamily = FontFamily.Default)
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
            TimePicker(state = estadoReloj)
        }
    }

    if (mostrarCalendarioInicio) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioInicio = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let {
                        val fecha = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        modelo.fechaInicio = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                    mostrarCalendarioInicio = false
                }) { Text("OK", fontFamily = FontFamily.Default) }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }

    if (mostrarCalendarioFin) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioFin = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let {
                        val fecha = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        modelo.fechaFin = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                    mostrarCalendarioFin = false
                }) { Text("OK", fontFamily = FontFamily.Default) }
            }
        ) {
            DatePicker(state = estadoFecha)
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
            TextButton(onClick = alConfirmar) { Text("Confirmar", fontFamily = FontFamily.Default) }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) { Text("Cancelar", fontFamily = FontFamily.Default) }
        },
        text = { contenido() }
    )
}
