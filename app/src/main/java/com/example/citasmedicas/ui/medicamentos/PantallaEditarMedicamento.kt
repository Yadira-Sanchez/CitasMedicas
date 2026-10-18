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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarMedicamento(
    medicamentoId: String,
    alActualizar: () -> Unit,
    alVolver: () -> Unit,
    modelo: MedicamentoViewModel = viewModel()
) {
    val estadoScroll = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }
    var mostrarCalendarioInicio by remember { mutableStateOf(false) }
    var mostrarCalendarioFin by remember { mutableStateOf(false) }

    // Cargar datos al iniciar usando el ID de la ruta
    LaunchedEffect(medicamentoId) {
        // Primero obtenemos el objeto de Firestore
        modelo.obtenerMedicamentoPorId(medicamentoId)
    }

    // Una vez que el medicamentoSeleccionado cambia en el modelo, precargamos el formulario
    LaunchedEffect(modelo.medicamentoSeleccionado) {
        modelo.medicamentoSeleccionado?.let {
            modelo.cargarDatosMedicamento(it)
        }
    }

    // Manejar éxito de actualización
    LaunchedEffect(modelo.actualizacionExitosa) {
        if (modelo.actualizacionExitosa) {
            scope.launch {
                val mensaje = modelo.mensajeExito ?: "Medicamento actualizado correctamente."
                snackbarHostState.showSnackbar(mensaje)
                delay(1000)
                alActualizar()
                modelo.limpiarFormulario()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Medicamento", color = AzulPrimario, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default) },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (modelo.medicamentoSeleccionado == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AzulPrimario)
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
                // Tarjeta 1: Información General
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Información General", fontWeight = FontWeight.Bold, color = AzulPrimario, fontFamily = FontFamily.Default)
                        Text("Nombre", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                        OutlinedTextField(
                            value = modelo.nombre,
                            onValueChange = { modelo.nombre = it },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            placeholder = { Text("Ej. Ibuprofeno", fontFamily = FontFamily.Default) }
                        )
                        Text("Tipo", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                        val tipos = listOf("Pastilla", "Jarabe", "Inyección", "Gotas")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            tipos.forEach { tipo ->
                                FilterChip(
                                    selected = modelo.tipoSeleccionado == tipo,
                                    onClick = { modelo.tipoSeleccionado = tipo },
                                    label = { Text(tipo, fontFamily = FontFamily.Default) }
                                )
                            }
                        }
                        Text("Dosis", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                        OutlinedTextField(
                            value = modelo.dosis,
                            onValueChange = { modelo.dosis = it },
                            placeholder = { Text(modelo.sugerenciaDosis, fontFamily = FontFamily.Default) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Tarjeta 2: Programación (Switch incorporado)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Programación y Horarios", fontWeight = FontWeight.Bold, color = AzulPrimario, fontFamily = FontFamily.Default)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Activar recordatorios", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                                Text("Notificaciones en el móvil", fontSize = 12.sp, color = Color.Gray, fontFamily = FontFamily.Default)
                            }
                            Switch(
                                checked = modelo.recordatorioActivo,
                                onCheckedChange = { modelo.recordatorioActivo = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = AzulPrimario)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                        Text("Frecuencia", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                        var expandido by remember { mutableStateOf(false) }
                        val opciones = listOf("Cada 4 horas", "Cada 6 horas", "Cada 8 horas", "Cada 12 horas", "Una vez al día", "Personalizar")
                        ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = !expandido }) {
                            OutlinedTextField(
                                value = modelo.frecuencia, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                                opciones.forEach { opcion ->
                                    DropdownMenuItem(text = { Text(opcion, fontFamily = FontFamily.Default) }, onClick = { modelo.frecuencia = opcion; expandido = false })
                                }
                            }
                        }
                    }
                }

                modelo.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Default) }

                Button(
                    onClick = { mostrarConfirmacion = true },
                    enabled = !modelo.cargando,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                ) {
                    if (modelo.cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar cambios", fontSize = 16.sp, fontFamily = FontFamily.Default)
                    }
                }
            }
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Confirmar actualización", fontFamily = FontFamily.Default) },
            text = { Text("¿Estás seguro de que deseas guardar los cambios?", fontFamily = FontFamily.Default) },
            confirmButton = {
                TextButton(onClick = { mostrarConfirmacion = false; modelo.actualizarMedicamento(medicamentoId) }) {
                    Text("Guardar", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar", fontFamily = FontFamily.Default) }
            }
        )
    }
}
