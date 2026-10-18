package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleMedicamento(
    medicamentoId: String,
    alEditar: (String) -> Unit,
    alVolver: () -> Unit,
    modelo: MedicamentoViewModel = viewModel()
) {
    val medicamento = modelo.medicamentoSeleccionado
    var mostrarDialogoEliminar by remember { 
        mutableStateOf(false) 
    }

    LaunchedEffect(
        key1 = medicamentoId
    ) {
        modelo.obtenerMedicamentoPorId(medicamentoId)
    }

    // Navegar atrás cuando se elimine con éxito
    LaunchedEffect(
        key1 = modelo.eliminacionExitosa
    ) {
        if (modelo.eliminacionExitosa) {
            alVolver()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalle del Medicamento",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
        if (medicamento == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues = padding
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AzulPrimario
                )
            }
        } else {
            ContenidoDetalleMedicamento(
                medicamento = medicamento,
                modifier = Modifier.padding(
                    paddingValues = padding
                ),
                alEditar = {
                    alEditar(medicamento.id)
                },
                alEliminar = {
                    mostrarDialogoEliminar = true
                }
            )
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEliminar = false
            },
            title = {
                Text(
                    text = "¿Eliminar medicamento?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta acción no se puede deshacer. Se cancelarán los recordatorios asociados."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modelo.eliminarMedicamento(medicamentoId)
                        mostrarDialogoEliminar = false
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                    }
                ) {
                    Text(
                        text = "Cancelar"
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoDetalleMedicamento(
    medicamento: Medicamento,
    modifier: Modifier = Modifier,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            )
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
    ) {
        // Chip de tipo
        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = medicamento.tipo,
                    color = AzulPrimario,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                val icono = when (medicamento.tipo) {
                    "Pastilla" -> Icons.Default.Medication
                    "Jarabe", "Gotas" -> Icons.Default.WaterDrop
                    "Inyección" -> Icons.Default.Vaccines
                    else -> Icons.Default.Medication
                }
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = AzulPrimario,
                    modifier = Modifier.size(
                        size = 18.dp
                    )
                )
            },
            shape = RoundedCornerShape(
                size = 12.dp
            ),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color(0xFFF0F7FF),
                labelColor = AzulPrimario
            ),
            border = null
        )

        Spacer(
            modifier = Modifier.height(
                height = 12.dp
            )
        )

        // Nombre del medicamento
        Text(
            text = medicamento.nombre,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.height(
                height = 24.dp
            )
        )

        // Tarjeta Destacada de Próxima Toma
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                size = 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = AzulPrimario
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        all = 20.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(
                        weight = 1f
                    )
                ) {
                    Text(
                        text = "PRÓXIMA TOMA PROGRAMADA",
                        color = Color.White.copy(
                            alpha = 0.8f
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = medicamento.horaToma,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = 8.dp
                            )
                        )
                        Text(
                            text = "Hoy",
                            color = Color.White.copy(
                                alpha = 0.8f
                            ),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(
                                bottom = 4.dp
                            )
                        )
                    }
                    Text(
                        text = "Dosis: ${medicamento.dosis}",
                        color = Color.White.copy(
                            alpha = 0.9f
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        size = 12.dp
                    ),
                    color = Color.White.copy(
                        alpha = 0.2f
                    ),
                    modifier = Modifier.size(
                        size = 48.dp
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(
                                size = 24.dp
                            )
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(
                height = 24.dp
            )
        )

        // Tarjeta Central de Detalles
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                size = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    vertical = 8.dp
                )
            ) {
                ItemDetalle(
                    icono = Icons.Default.Medication,
                    titulo = "Dosis recomendada",
                    valor = medicamento.dosis,
                    colorFondoIcono = Color(0xFFE3F2FD),
                    colorIcono = Color(0xFF1976D2)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 24.dp
                    ),
                    thickness = 0.5.dp,
                    color = Color.LightGray.copy(
                        alpha = 0.3f
                    )
                )
                ItemDetalle(
                    icono = Icons.Default.Refresh,
                    titulo = "Frecuencia",
                    valor = medicamento.frecuencia,
                    colorFondoIcono = Color(0xFFF3E5F5),
                    colorIcono = Color(0xFF7B1FA2)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 24.dp
                    ),
                    thickness = 0.5.dp,
                    color = Color.LightGray.copy(
                        alpha = 0.3f
                    )
                )
                ItemDetalle(
                    icono = Icons.Default.Schedule,
                    titulo = "Horario programado",
                    valor = "Primera toma a las ${medicamento.horaToma}",
                    colorFondoIcono = Color(0xFFE8F5E9),
                    colorIcono = Color(0xFF388E3C)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 24.dp
                    ),
                    thickness = 0.5.dp,
                    color = Color.LightGray.copy(
                        alpha = 0.3f
                    )
                )
                ItemDetalle(
                    icono = Icons.Default.CalendarToday,
                    titulo = "Duración",
                    valor = if (medicamento.fechaFin.isEmpty()) "Tratamiento continuo" else "Del ${medicamento.fechaInicio} al ${medicamento.fechaFin}",
                    colorFondoIcono = Color(0xFFFFFDE7),
                    colorIcono = Color(0xFFFBC02D)
                )
            }
        }

        Spacer(
            modifier = Modifier.weight(
                weight = 1f
            )
        )
        
        Spacer(
            modifier = Modifier.height(
                height = 32.dp
            )
        )

        // Botonera de Acción Inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp
            )
        ) {
            OutlinedButton(
                onClick = alEditar,
                modifier = Modifier
                    .weight(
                        weight = 1f
                    )
                    .height(
                        height = 56.dp
                    ),
                shape = RoundedCornerShape(
                    size = 16.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF0F7FF),
                    contentColor = AzulPrimario
                ),
                border = null
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(
                        width = 8.dp
                    )
                )
                Text(
                    text = "Editar",
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = alEliminar,
                modifier = Modifier
                    .weight(
                        weight = 1f
                    )
                    .height(
                        height = 56.dp
                    ),
                shape = RoundedCornerShape(
                    size = 16.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(
                        width = 8.dp
                    )
                )
                Text(
                    text = "Eliminar",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ItemDetalle(
    icono: ImageVector,
    titulo: String,
    valor: String,
    colorFondoIcono: Color,
    colorIcono: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                all = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(
                size = 44.dp
            ),
            shape = RoundedCornerShape(
                size = 12.dp
            ),
            color = colorFondoIcono
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(
                        size = 22.dp
                    )
                )
            }
        }
        
        Spacer(
            modifier = Modifier.width(
                width = 16.dp
            )
        )
        
        Column {
            Text(
                text = titulo,
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = valor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
