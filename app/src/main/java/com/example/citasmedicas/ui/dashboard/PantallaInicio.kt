package com.example.citasmedicas.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.citas.TarjetaCitaItem
import com.example.citasmedicas.ui.theme.AzulPrimario
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
    alDetalleMedicamento: (String) -> Unit,
    alDetalleCita: (String) -> Unit,
    alVerHistorial: () -> Unit,
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
            viewModel.cargarCitasPorFecha(
                fecha = uiState.fechaSeleccionada
            )
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
                            contentDescription = "Salir"
                        ) 
                    },
                    label = { 
                        Text(
                            text = "Salir"
                        ) 
                    }
                )
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp
                    )
            ) {
                FloatingActionButton(
                    onClick = { /* Asistente IA */ },
                    containerColor = Color(0xFF1B3E5F),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(
                            alignment = Alignment.BottomStart
                        )
                        .size(
                            size = 56.dp
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy, 
                        contentDescription = "Asistente IA"
                    )
                }

                Box(
                    modifier = Modifier.align(
                        alignment = Alignment.BottomEnd
                    )
                ) {
                    FloatingActionButton(
                        onClick = { 
                            menuAbierto = true 
                        },
                        containerColor = Color(0xFF4DD0E1),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(
                            size = 56.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add, 
                            contentDescription = "Agregar", 
                            modifier = Modifier.size(
                                size = 32.dp
                            )
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
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = "Ver Historial"
                                ) 
                            },
                            onClick = {
                                menuAbierto = false
                                alVerHistorial()
                            },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.History, 
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
                .padding(
                    paddingValues = padding
                )
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = 20.dp
            )
        ) {
            item { 
                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                ) 
            }

            // Encabezado
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
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF03A9F4),
                        modifier = Modifier.size(
                            size = 52.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person, 
                            contentDescription = null, 
                            tint = Color.White, 
                            modifier = Modifier.padding(
                                all = 8.dp
                            )
                        )
                    }
                }
            }

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
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Search, 
                            contentDescription = null, 
                            tint = Color.Gray
                        ) 
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrimario.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // Calendario Superior (RF12)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 12.dp
                    ),
                    contentPadding = PaddingValues(
                        vertical = 8.dp
                    )
                ) {
                    items(
                        items = uiState.diasSemana
                    ) { fecha ->
                        DiaItem(
                            fecha = fecha,
                            estaSeleccionado = fecha == uiState.fechaSeleccionada,
                            alSeleccionar = { 
                                viewModel.seleccionarFecha(
                                    fecha = fecha
                                ) 
                            }
                        )
                    }
                }
            }

            // Actividades del día (Vista Unificada RF12)
            item {
                Text(
                    text = "Actividades del día", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold
                )
            }

            val sinActividades = uiState.listaCitasDelDia.isEmpty() && 
                               uiState.listaMedicamentosDelDia.isEmpty()

            if (sinActividades) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 32.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay actividades para esta fecha", 
                            color = Color.Gray, 
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // Listado de Medicamentos con nuevo diseño profesional
                items(
                    items = uiState.listaMedicamentosDelDia
                ) { med ->
                    TarjetaMedicamentoItem(
                        med = med,
                        onClick = {
                            medicamentoSeleccionado = med
                            mostrarBottomSheet = true
                        },
                        onTomarClick = {
                            viewModel.actualizarEstadoMedicamento(
                                medicamento = med,
                                nuevoEstado = if (med.yaFueTomado) "Pasar" else "Tomar"
                            )
                        }
                    )
                }
            }
            // Listado de Citas Médicas
            items(
                items = uiState.listaCitasDelDia
            ) { cita ->
                TarjetaCitaItem(
                    cita = cita,
                    onClick = {
                        alDetalleCita(cita.id)
                    }
                )
            }

            item { 
                Spacer(
                    modifier = Modifier.height(
                        height = 100.dp
                    )
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
            sheetState = estadoSheet,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp, 
                        vertical = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp
                ),
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
                Spacer(
                    modifier = Modifier.height(
                        height = 8.dp
                    )
                )
                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(
                            medicamento = med, 
                            nuevoEstado = "Tomar"
                        )
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4DB6AC)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            height = 56.dp
                        ),
                    shape = RoundedCornerShape(
                        size = 16.dp
                    )
                ) {
                    Text(
                        text = "Tomar", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                }
                Button(
                    onClick = {
                        viewModel.actualizarEstadoMedicamento(
                            medicamento = med, 
                            nuevoEstado = "Pasar"
                        )
                        mostrarBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            height = 56.dp
                        ),
                    shape = RoundedCornerShape(
                        size = 16.dp
                    )
                ) {
                    Text(
                        text = "Pasar", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp, 
                        color = Color.Black
                    )
                }
                TextButton(
                    onClick = {
                        mostrarBottomSheet = false
                        alDetalleMedicamento(med.id)
                    }
                ) {
                    Text(text = "Ver detalles completos")
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
    val diaNombre = fecha.dayOfWeek.getDisplayName(
        TextStyle.SHORT, 
        localeEs
    )
    val diaNumero = fecha.dayOfMonth.toString()

    Card(
        onClick = alSeleccionar,
        modifier = Modifier
            .width(
                width = 64.dp
            )
            .height(
                height = 80.dp
            ),
        shape = RoundedCornerShape(
            size = 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) AzulPrimario else Color.White
        ),
        border = if (!estaSeleccionado) {
            BorderStroke(
                width = 1.dp, 
                color = Color.LightGray.copy(alpha = 0.2f)
            )
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (estaSeleccionado) 8.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = 12.dp
                ),
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
                        .size(
                            size = 4.dp
                        )
                        .background(
                            color = Color.White, 
                            shape = CircleShape
                        )
                )
            } else {
                Spacer(
                    modifier = Modifier.height(
                        height = 4.dp
                    )
                )
            }
        }
    }
}

@Composable
fun TarjetaMedicamentoItem(
    med: Medicamento,
    onClick: () -> Unit,
    onTomarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            size = 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Column {
            // Línea superior de color para identidad visual profesional
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = 3.dp
                    )
                    .background(
                        color = if (med.yaFueTomado) Color(0xFF4CAF50) else AzulPrimario
                    )
            )

            Column(
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contenedor del Icono Principal (Estilo Moderno)
                    Surface(
                        shape = RoundedCornerShape(
                            size = 12.dp
                        ),
                        color = if (med.yaFueTomado) Color(0xFFE8F5E9) else Color(0xFFF0F7FF),
                        modifier = Modifier
                            .size(
                                size = 52.dp
                            )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = if (med.yaFueTomado) Color(0xFF2E7D32) else AzulPrimario,
                                modifier = Modifier
                                    .size(
                                        size = 26.dp
                                    )
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .width(
                                width = 12.dp
                            )
                    )

                    // Textos Centrales Destacados
                    Column(
                        modifier = Modifier
                            .weight(
                                weight = 1f
                            )
                    ) {
                        Text(
                            text = med.tipo.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.Default
                        )
                        Text(
                            text = med.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E),
                            fontFamily = FontFamily.Default
                        )
                        Text(
                            text = "● Toma programada",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (med.yaFueTomado) Color(0xFF2E7D32) else Color(0xFFF57C00),
                            fontFamily = FontFamily.Default
                        )
                    }

                    // Etiqueta de Estado (Badge)
                    Surface(
                        color = if (med.yaFueTomado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(
                            size = 12.dp
                        )
                    ) {
                        Text(
                            text = if (med.yaFueTomado) "Tomado" else "Pendiente",
                            modifier = Modifier
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (med.yaFueTomado) Color(0xFF2E7D32) else Color(0xFFE65100),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .width(
                                width = 8.dp
                            )
                    )

                    // Checkbox de Acción Rápida (Simetría con Citas)
                    IconButton(
                        onClick = onTomarClick,
                        modifier = Modifier
                            .size(
                                size = 32.dp
                            )
                    ) {
                        if (med.yaFueTomado) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF4DB6AC),
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Tomado",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(
                                            all = 6.dp
                                        )
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 2.dp,
                                        color = Color.LightGray.copy(
                                            alpha = 0.5f
                                        ),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(
                            height = 16.dp
                        )
                )

                // Fila Inferior con Contenedores de Información de Apoyo
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp
                    )
                ) {
                    // Contenedor Dosis
                    Surface(
                        modifier = Modifier
                            .weight(
                                weight = 1f
                            ),
                        color = Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(
                            size = 8.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    all = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = null,
                                tint = AzulPrimario,
                                modifier = Modifier
                                    .size(
                                        size = 16.dp
                                    )
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(
                                        width = 8.dp
                                    )
                            )
                            Text(
                                text = med.dosis,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }

                    // Contenedor Frecuencia
                    Surface(
                        modifier = Modifier
                            .weight(
                                weight = 1f
                            ),
                        color = Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(
                            size = 8.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    all = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = AzulPrimario,
                                modifier = Modifier
                                    .size(
                                        size = 16.dp
                                    )
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(
                                        width = 8.dp
                                    )
                            )
                            Text(
                                text = med.frecuencia,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(
                            height = 12.dp
                        )
                )

                // Fila de Hora Resaltada (Estilo profesional)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (!med.yaFueTomado) Color(0xFFFFF3E0) else Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(
                            size = 8.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = if (!med.yaFueTomado) Color(0xFFE65100) else Color.Gray,
                                modifier = Modifier
                                    .size(
                                        size = 16.dp
                                    )
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(
                                        width = 4.dp
                                    )
                            )
                            Text(
                                text = "${med.horaToma} hrs",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (!med.yaFueTomado) Color(0xFFE65100) else Color.Gray,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
            }
        }
    }
}
