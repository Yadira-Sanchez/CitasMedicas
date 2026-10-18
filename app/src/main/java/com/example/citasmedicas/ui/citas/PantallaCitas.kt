package com.example.citasmedicas.ui.citas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCitas(
    alNavegarA: (String) -> Unit,
    alNuevaCita: () -> Unit,
    modelo: CitaViewModel = viewModel()
) {
    val citas = modelo.listaCitas

    LaunchedEffect(
        key1 = Unit
    ) {
        modelo.obtenerCitas()
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Botón Principal de Acción
                Button(
                    onClick = { 
                        alNuevaCita() 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp, 
                            vertical = 12.dp
                        )
                        .height(
                            height = 56.dp
                        ),
                    shape = RoundedCornerShape(
                        size = 16.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrimario
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(
                        modifier = Modifier.width(
                            width = 8.dp
                        )
                    )
                    Text(
                        text = "Agregar nueva cita",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
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
                        selected = false,
                        onClick = {
                            alNavegarA("inicio")
                        }
                    )
                    NavigationBarItem(
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
                        },
                        selected = false,
                        onClick = {
                            alNavegarA("medicamentos")
                        }
                    )
                    NavigationBarItem(
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
                        },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
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
                        },
                        selected = false,
                        onClick = {
                            alNavegarA("perfil")
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    paddingValues = padding
                )
                .padding(
                    horizontal = 20.dp
                )
        ) {
            Spacer(
                modifier = Modifier.height(
                    height = 24.dp
                )
            )

            // Encabezado
            Text(
                text = "AGENDA DE SALUD",
                style = MaterialTheme.typography.labelLarge,
                color = AzulPrimario,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 4.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis Citas Médicas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(
                        size = 48.dp
                    )
                ) {
                    IconButton(
                        onClick = { /* Acción Notificaciones */ }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = AzulPrimario,
                            modifier = Modifier.size(
                                size = 24.dp
                            )
                        )
                    }
                }
            }

            // Chips de Filtro
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp
                )
            ) {
                item {
                    FilterChip(
                        selected = true,
                        onClick = { },
                        label = { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Próximas"
                                )
                                Spacer(
                                    modifier = Modifier.width(
                                        width = 6.dp
                                    )
                                )
                                Surface(
                                    color = Color.White.copy(
                                        alpha = 0.3f
                                    ),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "${citas.size}",
                                        modifier = Modifier.padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        ),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(
                            size = 24.dp
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrimario,
                            selectedLabelColor = Color.White
                        ),
                        border = null
                    )
                }
            }

            if (citas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes citas programadas.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 16.dp
                    ),
                    contentPadding = PaddingValues(
                        bottom = 16.dp
                    )
                ) {
                    items(
                        items = citas
                    ) { cita ->
                        TarjetaCitaItem(
                            cita = cita
                        )
                    }
                    
                    item {
                        // Tarjeta Informativa Inferior
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 8.dp
                                ),
                            shape = RoundedCornerShape(
                                size = 12.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF0F7FF)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    all = 16.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = AzulPrimario,
                                    modifier = Modifier.size(
                                        size = 20.dp
                                    )
                                )
                                Spacer(
                                    modifier = Modifier.width(
                                        width = 12.dp
                                    )
                                )
                                Text(
                                    text = "Recuerda presentarte 15 minutos antes de la hora con tu documento de identidad.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AzulPrimario,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(
                                height = 80.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaCitaItem(
    cita: CitaMedica
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
        )
    ) {
        Column {
            // Línea superior de color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = 3.dp
                    )
                    .background(
                        color = Color(0xFF2196F3)
                    )
            )

            Column(
                modifier = Modifier.padding(
                    all = 16.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(
                            weight = 1f
                        )
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                size = 12.dp
                            ),
                            color = Color(0xFFF0F5FF),
                            modifier = Modifier.size(
                                size = 52.dp
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = AzulPrimario,
                                    modifier = Modifier.size(
                                        size = 26.dp
                                    )
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.width(
                                width = 12.dp
                            )
                        )
                        Column {
                            Text(
                                text = cita.especialidad.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (cita.doctor.isEmpty()) "Doctor pendiente" else cita.doctor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E)
                            )
                            Text(
                                text = "● Confirmación previa",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                    
                    // Badge de Estado
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(
                            size = 12.dp
                        )
                    ) {
                        Text(
                            text = "Programada",
                            modifier = Modifier.padding(
                                horizontal = 8.dp, 
                                vertical = 4.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp
                    )
                ) {
                    // Contenedor Fecha
                    Surface(
                        modifier = Modifier.weight(
                            weight = 1f
                        ),
                        color = Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(
                            size = 8.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                all = 10.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = AzulPrimario,
                                modifier = Modifier.size(
                                    size = 16.dp
                                )
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    width = 8.dp
                                )
                            )
                            Text(
                                text = cita.fecha,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Contenedor Hora
                    Surface(
                        modifier = Modifier.weight(
                            weight = 1f
                        ),
                        color = Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(
                            size = 8.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                all = 10.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = AzulPrimario,
                                modifier = Modifier.size(
                                    size = 16.dp
                                )
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    width = 8.dp
                                )
                            )
                            Text(
                                text = "${cita.hora} hrs",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(
                        height = 12.dp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(
                                size = 16.dp
                            )
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = 4.dp
                            )
                        )
                        Text(
                            text = cita.lugar,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Surface(
                        color = Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(
                            size = 4.dp
                        )
                    ) {
                        Text(
                            text = "Módulo A",
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                )

                // Botonera Inferior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 10.dp
                    )
                ) {
                    Button(
                        onClick = { /* Acción Reprogramar */ },
                        modifier = Modifier
                            .weight(
                                weight = 1f
                            )
                            .height(
                                height = 44.dp
                            ),
                        shape = RoundedCornerShape(
                            size = 12.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F7FA),
                            contentColor = Color.DarkGray
                        ),
                        elevation = null,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Reprogramar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    
                    Button(
                        onClick = { /* Acción Ver Detalles */ },
                        modifier = Modifier
                            .weight(
                                weight = 1f
                            )
                            .height(
                                height = 44.dp
                            ),
                        shape = RoundedCornerShape(
                            size = 12.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF0F7FF),
                            contentColor = AzulPrimario
                        ),
                        elevation = null,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Ver detalles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
