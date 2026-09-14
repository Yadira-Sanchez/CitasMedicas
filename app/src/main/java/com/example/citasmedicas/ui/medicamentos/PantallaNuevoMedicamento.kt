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

    // Si se guarda bien, volvemos al inicio
    LaunchedEffect(modelo.guardadoExitoso) {
        if (modelo.guardadoExitoso) {
            alGuardar()
            modelo.limpiarFormulario()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Medicamento", color = AzulPrimario, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(estadoScroll)
        ) {
            // Nombre del Medicamento
            Text("Nombre del Medicamento", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = modelo.nombre,
                onValueChange = { modelo.nombre = it },
                placeholder = { Text("Ej. Ibuprofeno") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
            )

            Spacer(Modifier.height(16.dp))

            // Selección de Tipo (Chips) - RF05
            Text("Tipo", fontWeight = FontWeight.Bold)
            val tipos = listOf("Pastilla", "Jarabe", "Inyección", "Gotas")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipos.forEach { tipo ->
                    FilterChip(
                        selected = modelo.tipoSeleccionado == tipo,
                        onClick = { modelo.tipoSeleccionado = tipo },
                        label = { Text(tipo) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Dosis con Placeholder Dinámico - RF05
            Text("Dosis / Cantidad", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = modelo.dosis,
                onValueChange = { modelo.dosis = it },
                placeholder = { Text(modelo.sugerenciaDosis) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Frecuencia (Menú desplegable simple)
            Text("Frecuencia", fontWeight = FontWeight.Bold)
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
                            text = { Text(opcion) },
                            onClick = {
                                modelo.frecuencia = opcion
                                expandido = false
                            }
                        )
                    }
                }
            }

            // Si es personalizado, mostrar campo extra
            if (modelo.frecuencia == "Personalizar") {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = modelo.intervaloPersonalizado,
                    onValueChange = { modelo.intervaloPersonalizado = it },
                    label = { Text("¿Cada cuántas horas?") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Primera toma (TimePicker)
            Text("Primera toma", fontWeight = FontWeight.Bold)
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

            Spacer(Modifier.height(16.dp))

            // Fechas Inicio y Fin (DatePicker)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Fecha de inicio", fontWeight = FontWeight.Bold)
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
                    Text("Fecha de fin", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = modelo.fechaFin,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Opcional") },
                        trailingIcon = {
                            IconButton(onClick = { mostrarCalendarioFin = true }) {
                                Icon(Icons.Default.DateRange, null)
                            }
                        }
                    )
                }
            }

            // Mostrar mensaje de error si existe
            modelo.mensajeError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(32.dp))

            // Botón Guardar
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
                Text("Guardar Medicamento y Programar Alerta", fontSize = 14.sp)
            }
        }
    }

    // --- Diálogos de Selección (Popups) ---

    // Reloj para la hora
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

    // Calendario para Fecha Inicio
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
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }

    // Calendario para Fecha Fin
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
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }
}

// Componente auxiliar para el diálogo de hora (que no viene por defecto en M3)
@Composable
fun TimePickerDialog(
    alCerrar: () -> Unit,
    alConfirmar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        confirmButton = {
            TextButton(onClick = alConfirmar) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) { Text("Cancelar") }
        },
        text = { contenido() }
    )
}
