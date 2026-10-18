package com.example.citasmedicas.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.citas.TarjetaCitaItem
import com.example.citasmedicas.ui.theme.AzulPrimario
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    var menuAbierto by remember { 
        mutableStateOf(false) 
    }

    LaunchedEffect(
        key1 = userId,
        key2 = uiState.fechaSeleccionada
    ) {
        if (userId != null) {
            viewModel.cargarCitasPorFecha(uiState.fechaSeleccionada)
        }
    }

    var medicamentoSeleccionado by remember { 
        mutableStateOf<Medicamento?>(null) 
    }
    var mostrarBottomSheet by remember { 
        mutableStateOf(false) 
    }
    val estadoSheet = rememberModalBottomSheetState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
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
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrimario,
                        selectedTextColor = AzulPrimario,
                        indicatorColor = Color(0xFFF0F5FF)
                    )
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
                    .padding(horizontal = 24.dp)
            ) {
                // Botón del asistente/robot (Izquierda)
                FloatingActionButton(
                    onClick = { /* RF23: Asistente IA */ },
                    containerColor = Color(0xFF1B3E5F),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Asistente IA"
                    )
                }

                // Botón flotante (+) Turquesa (Derecha)
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    FloatingActionButton(
                        onClick = { 
                            menuAbierto = true 
                        },
                        containerColor = Color(0xFF4DD0E1), // Turquesa
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { 
                            menuAbierto = false 
                        }
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
                .padding(paddingValues = padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(space = 20.dp)
        ) {
            item {
                Spacer(
                    modifier = Modifier.height(height = 16.dp)
                )
            }

            // Encabezado del Usuario
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Hola,",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = uiState.nombreUsuario,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                    }
                    
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF03A9F4),
                            modifier = Modifier.size(size = 52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(all = 8.dp)
                            )
                        }
                        // Punto indicador de estado
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = Color(0xFF4CAF50),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }

            // Barra de Búsqueda
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = "Buscar medicamentos o citas...",
                            color = Color.LightGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(size = 16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedBorderColor = AzulPrimario
                    ),
                    singleLine = true
                )
            }

            // Sección Calendario
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
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { alVerCitas() }
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(height = 12.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(space = 10.dp)
                    ) {
                        items(items = uiState.diasSemana) { fecha ->
                            DiaItem(
                                fecha = fecha,
                                estaSeleccionado = fecha == uiState.fechaSeleccionada,
                                alSeleccionar = { viewModel.seleccionarFecha(fecha) }
                            )
                        }
                    }
                }
            }

            // Sección "Tomas de Hoy"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tomas de Hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(
                        modifier = Modifier.width(width = 8.dp)
                    )
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(size = 12.dp)
                    ) {
                        Text(
                            text = "${uiState.listaMedicamentos.size} hoy",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = AzulPrimario,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(
                        modifier = Modifier.weight(weight = 1f)
                    )
                    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.forLanguageTag("es-ES"))
                    Text(
                        text = uiState.fechaSeleccionada.format(formatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
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
                items(items = uiState.listaMedicamentos) { med ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                medicamentoSeleccionado = med
                                mostrarBottomSheet = true
                            },
                        shape = RoundedCornerShape(size = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(all = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(size = 48.dp)
                                    .background(
                                        color = Color(0xFFE1F5FE),
                                        shape = RoundedCornerShape(size = 12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val icon = when(med.tipo) {
                                    "Gotas" -> Icons.Default.WaterDrop
                                    "Jarabe" -> Icons.Default.MedicalServices
                                    else -> Icons.Default.Medication
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = AzulPrimario,
                                    modifier = Modifier.size(size = 24.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(width = 16.dp)
                            )

                            Column(
                                modifier = Modifier.weight(weight = 1f)
                            ) {
                                Text(
                                    text = med.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = med.dosis,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    if (!med.yaFueTomado) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = Color(0xFFFFF9C4),
                                            shape = RoundedCornerShape(size = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Schedule,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(size = 12.dp),
                                                    tint = Color(0xFFF57C00)
                                                )
                                                Text(
                                                    text = " ${med.horaToma}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFFF57C00),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = " • ${med.horaToma}",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }

                            // Botón de estado circular
                            if (med.yaFueTomado) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF4DB6AC), // Verde menta
                                    modifier = Modifier.size(size = 32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completado",
                                        tint = Color.White,
                                        modifier = Modifier.padding(all = 6.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(size = 32.dp)
                                        .border(
                                            width = 2.dp,
                                            color = Color.LightGray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
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
                items(items = uiState.listaCitasDelDia) { cita ->
                    TarjetaCitaItem(cita = cita)
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(height = 100.dp)
                )
            }
        }
    }

    if (mostrarBottomSheet && medicamentoSeleccionado != null) {
        val med = medicamentoSeleccionado!!
        ModalBottomSheet(
            onDismissRequest = { mostrarBottomSheet = false },
            sheetState = estadoSheet,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = med.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrimario
                )
                Text(
                    text = "¿Qué acción deseas realizar para esta toma (${med.horaToma})?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Tomar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC)),
                    modifier = Modifier.fillMaxWidth().height(height = 56.dp),
                    shape = RoundedCornerShape(size = 16.dp)
                ) {
                    Text(text = "Tomar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(med, "Pasar")
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.fillMaxWidth().height(height = 56.dp),
                    shape = RoundedCornerShape(size = 16.dp)
                ) {
                    Text(text = "Pasar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                }

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
        modifier = Modifier.width(width = 64.dp).height(height = 80.dp),
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) Color(0xFF0D47A1) else Color.White
        ),
        border = if (!estaSeleccionado) BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.2f)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (estaSeleccionado) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = diaNombre.uppercase(localeEs),
                style = MaterialTheme.typography.labelSmall,
                color = if (estaSeleccionado) Color.White.copy(alpha = 0.7f) else Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = diaNumero,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (estaSeleccionado) Color.White else Color.Black
            )
            if (estaSeleccionado) {
                Box(
                    modifier = Modifier
                        .size(size = 4.dp)
                        .background(color = Color.White, shape = CircleShape)
                )
            } else {
                Spacer(modifier = Modifier.height(height = 4.dp))
            }
        }
    }
}
