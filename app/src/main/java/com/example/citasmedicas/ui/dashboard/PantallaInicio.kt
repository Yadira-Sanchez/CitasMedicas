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
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.citas.TarjetaCitaItem
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
    alVerCitas: () -> Unit,
    alNuevaCita: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val uiState = viewModel.uiState
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var menuAbierto by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.cargarCitasPorFecha(uiState.fechaSeleccionada)
        }
    }

    var medicamentoSeleccionado by remember { mutableStateOf<Medicamento?>(null) }
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    val estadoSheet = rememberModalBottomSheetState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Inicio"
                        )
                    },
                    label = {
                        Text(
                            text = "Inicio"
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = alVerMedicamentos,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = "Medicinas"
                        )
                    },
                    label = {
                        Text(
                            text = "Medicinas"
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = alVerCitas,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Citas"
                        )
                    },
                    label = {
                        Text(
                            text = "Citas"
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = alCerrarSesion,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    },
                    label = {
                        Text(
                            text = "Perfil"
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // FAB Botón Chatbot IA (Izquierda)
                FloatingActionButton(
                    onClick = { /* RF23: Abrir Asistente IA */ },
                    containerColor = AzulPrimario,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Asistente IA"
                    )
                }

                // FAB Botón (+) con DropdownMenu (Derecha)
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    FloatingActionButton(
                        onClick = { menuAbierto = true },
                        containerColor = VerdeClaro,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar"
                        )
                    }

                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Agregar Medicamento"
                                )
                            },
                            onClick = {
                                menuAbierto = false
                                alAgregarMedicamento()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Medication,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Agregar Cita"
                                )
                            },
                            onClick = {
                                menuAbierto = false
                                alNuevaCita()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            // Cabecera: Saludo
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Hola,",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = uiState.nombreUsuario,
                            color = AzulPrimario,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Buscador
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = "Buscar medicamentos o citas..."
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                )
            }

            // Calendario
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calendario",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ver todo",
                            color = AzulPrimario,
                            modifier = Modifier.clickable {
                                alVerCitas()
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.diasSemana) { fecha ->
                            DiaItem(
                                fecha = fecha,
                                estaSeleccionado = fecha == uiState.fechaSeleccionada,
                                alSeleccionar = {
                                    viewModel.seleccionarFecha(fecha)
                                }
                            )
                        }
                    }
                }
            }

            // Tomas del día
            item {
                Text(
                    text = "Tomas del día",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.listaMedicamentos.isEmpty()) {
                item {
                    Text(
                        text = "No tienes medicamentos programados.",
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = if (med.yaFueTomado) VerdeClaro else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = med.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${med.dosis} • ${med.horaToma}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (med.yaFueTomado) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Tomado",
                                    tint = VerdeClaro
                                )
                            }
                        }
                    }
                }
            }

            // Citas para este día
            item {
                Text(
                    text = "Citas para este día",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.listaCitasDelDia.isEmpty()) {
                item {
                    Text(
                        text = "No tienes citas para la fecha seleccionada.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(uiState.listaCitasDelDia) { cita ->
                    TarjetaCitaItem(
                        cita = cita
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }

    if (mostrarBottomSheet && medicamentoSeleccionado != null) {
        val med = medicamentoSeleccionado!!
        ModalBottomSheet(
            onDismissRequest = {
                mostrarBottomSheet = false
            },
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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Tomar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeClaro
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Tomar",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Pasar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Pasar",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Reprogramar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AzulPrimario
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Reprogramar",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

@Composable
fun DiaItem(
    fecha: LocalDate,
    estaSeleccionado: Boolean,
    alSeleccionar: () -> Unit
) {
    val localeEs = Locale.forLanguageTag("es-ES")
    val diaNombre = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, localeEs)
    val diaNumero = fecha.dayOfMonth.toString()

    Card(
        onClick = alSeleccionar,
        modifier = Modifier.width(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) AzulPrimario else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = diaNombre.uppercase(localeEs),
                style = MaterialTheme.typography.labelMedium,
                color = if (estaSeleccionado) Color.White else Color.Gray
            )
            Text(
                text = diaNumero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (estaSeleccionado) Color.White else Color.Black
            )
        }
    }
}
