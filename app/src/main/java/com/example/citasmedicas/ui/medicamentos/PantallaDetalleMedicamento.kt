package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(medicamentoId) {
        modelo.obtenerMedicamentoPorId(medicamentoId)
    }

    // Navegar atrás cuando se elimine con éxito
    LaunchedEffect(modelo.eliminacionExitosa) {
        if (modelo.eliminacionExitosa) {
            alVolver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle",
                        fontFamily = FontFamily.Default
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
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AzulPrimario
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                ContenidoDetalleMedicamento(
                    medicamento = medicamento,
                    alEditar = {
                        alEditar(medicamento.id)
                    },
                    alEliminar = {
                        mostrarDialogoEliminar = true
                    }
                )
            }
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
                        color = Color(0xFFE57373)
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
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = medicamento.nombre,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            color = AzulPrimario
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = medicamento.tipo,
                    fontFamily = FontFamily.Default
                )
            },
            leadingIcon = {
                val icono = when (medicamento.tipo) {
                    "Pastilla" -> Icons.Default.Medication
                    "Jarabe", "Gotas" -> Icons.Default.Opacity
                    "Inyección" -> Icons.Default.Vaccines
                    else -> Icons.Default.Medication
                }
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = AzulPrimario,
                leadingIconContentColor = AzulPrimario
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ItemDetalle(
                    icono = Icons.Default.Straighten,
                    titulo = "Dosis",
                    valor = medicamento.dosis
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp
                )
                ItemDetalle(
                    icono = Icons.Default.Repeat,
                    titulo = "Frecuencia",
                    valor = medicamento.frecuencia
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp
                )
                ItemDetalle(
                    icono = Icons.Default.Schedule,
                    titulo = "Horario Programado",
                    valor = "Primera toma a las ${medicamento.horaToma}"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp
                )
                ItemDetalle(
                    icono = Icons.Default.CalendarToday,
                    titulo = "Duración",
                    valor = if (medicamento.fechaFin.isEmpty()) "Inicia el ${medicamento.fechaInicio}" else "Del ${medicamento.fechaInicio} al ${medicamento.fechaFin}"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = alEditar,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = "Editar",
                    fontFamily = FontFamily.Default
                )
            }

            Button(
                onClick = alEliminar,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = "Eliminar",
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

@Composable
fun ItemDetalle(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        Column {
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = FontFamily.Default
            )
            Text(
                text = valor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Default,
                color = Color.Black
            )
        }
    }
}
