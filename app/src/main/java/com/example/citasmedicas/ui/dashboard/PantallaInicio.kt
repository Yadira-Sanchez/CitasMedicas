package com.example.citasmedicas.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario
import com.example.citasmedicas.ui.theme.VerdeClaro
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    viewModel: DashboardViewModel = viewModel(),
    alAgregarMedicamento: () -> Unit,
    alVerMedicamentos: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val uiState = viewModel.uiState

    // Obtenemos el ID del usuario actual de Firebase
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Cada vez que cambie el usuario (o al entrar a la pantalla), recargamos la información
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.cargarDatos()
        }
    }

    var medicamentoSeleccionado by remember { mutableStateOf<Medicamento?>(null) }
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    val estadoSheet = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Inicio") }
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = alVerMedicamentos,
                        icon = { Icon(Icons.Default.Medication, null) },
                        label = { Text("Medicinas") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* RF12: Navegar a Citas */ },
                        icon = { Icon(Icons.Default.Event, null) },
                        label = { Text("Citas") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = alCerrarSesion,
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Perfil") }
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                // Cabecera: Saludo y Foto
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Hola,",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                uiState.nombreUsuario,
                                color = AzulPrimario,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.LightGray,
                            modifier = Modifier.size(48.dp).clickable { /* RF21: Perfil */ }
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        placeholder = { Text("Buscar medicamentos o citas...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = { Icon(Icons.Default.Search, null) }
                    )
                }

                // Selector de Fechas
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Calendario",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Ver todo",
                                color = AzulPrimario,
                                modifier = Modifier.clickable { /* RF12 */ }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.diasSemana) { fecha ->
                                DiaItem(
                                    fecha = fecha,
                                    estaSeleccionado = fecha == uiState.fechaSeleccionada,
                                    alSeleccionar = { viewModel.seleccionarFecha(fecha) }
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Tomas de Hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.listaMedicamentos.isEmpty()) {
                    item {
                        Text(
                            "No tienes medicamentos programados para hoy.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(uiState.listaMedicamentos) { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    medicamentoSeleccionado = med
                                    mostrarBottomSheet = true
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Medication,
                                    contentDescription = null,
                                    tint = if (med.yaFueTomado) VerdeClaro else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        med.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${med.dosis} • ${med.horaToma}",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                if (med.yaFueTomado) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Tomado", tint = VerdeClaro)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Próxima Cita",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                val cita = uiState.proximaCita
                if (cita != null) {
                    item {
                        TarjetaCita(cita = cita)
                    }
                } else {
                    item {
                        Text(
                            "No tienes citas próximas.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item { Spacer(Modifier.height(140.dp)) }
            }
        }

        // Botón Flotante (+): Esquina inferior derecha (Alignment.BottomEnd) con margen de 16.dp según AGENTS.md
        FloatingActionButton(
            onClick = alAgregarMedicamento,
            containerColor = VerdeClaro,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 130.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir Medicamento")
        }

        // Botón Flotante de Chatbot IA: Esquina inferior izquierda (Alignment.BottomStart) con margen de 16.dp según AGENTS.md
        FloatingActionButton(
            onClick = { /* RF23: Abrir Asistente IA */ },
            containerColor = AzulPrimario,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 130.dp, start = 16.dp)
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = "Asistente IA")
        }
    }

    // ModalBottomSheet interactivo para actualizar el estado en Firestore (RF04)
    if (mostrarBottomSheet && medicamentoSeleccionado != null) {
        val med = medicamentoSeleccionado!!
        ModalBottomSheet(
            onDismissRequest = { mostrarBottomSheet = false },
            sheetState = estadoSheet
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = med.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrimario
                )
                Text(
                    text = "¿Qué acción deseas realizar para esta toma (${med.horaToma})?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Tomar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeClaro),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Tomar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Pasar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Pasar", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Reprogramar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrimario),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Reprogramar", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DiaItem(fecha: LocalDate, estaSeleccionado: Boolean, alSeleccionar: () -> Unit) {
    val localeEs = Locale.forLanguageTag("es-ES")
    val diaNombre = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, localeEs)
    val diaNumero = fecha.dayOfMonth.toString()

    Card(
        onClick = alSeleccionar,
        modifier = Modifier.width(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) AzulPrimario else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                diaNombre.uppercase(localeEs),
                style = MaterialTheme.typography.labelMedium,
                color = if (estaSeleccionado) Color.White else Color.Gray
            )
            Text(
                diaNumero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (estaSeleccionado) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun TarjetaCita(cita: CitaMedica) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AzulPrimario, contentColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        cita.doctor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        cita.especialidad,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(Icons.Default.Event, contentDescription = null)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "${cita.fecha} • ${cita.hora}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    cita.ubicacion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reprogramar")
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AzulPrimario),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}