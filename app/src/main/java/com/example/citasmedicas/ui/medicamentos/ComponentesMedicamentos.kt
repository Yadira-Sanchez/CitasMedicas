package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario

@Composable
fun DialogoConfirmarEliminacion(
    nombreMedicamento: String,
    alConfirmar: () -> Unit,
    alDescartar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alDescartar,
        title = {
            Text(
                text = "Eliminar medicamento",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar $nombreMedicamento? Esta acción borrará el registro de tu base de datos y no se podrá deshacer.",
                fontFamily = FontFamily.Default
            )
        },
        confirmButton = {
            Button(
                onClick = alConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Eliminar",
                    fontFamily = FontFamily.Default
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = alDescartar
            ) {
                Text(
                    text = "Cancelar",
                    fontFamily = FontFamily.Default
                )
            }
        }
    )
}

@Composable
fun ElementoMedicamentoItem(
    medicamento: Medicamento,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 6.dp
            ),
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
        Row(
            modifier = Modifier
                .padding(
                    all = 16.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor del ícono a la izquierda
            val iconoMedicina = when (medicamento.tipo) {
                "Gotas" -> Icons.Default.WaterDrop
                "Jarabe" -> Icons.Default.MedicalServices
                "Pastilla" -> Icons.Default.Medication
                else -> Icons.Default.Medication
            }

            Box(
                modifier = Modifier
                    .size(
                        size = 56.dp
                    )
                    .background(
                        color = Color(0xFFF0F5FF),
                        shape = RoundedCornerShape(
                            size = 12.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoMedicina,
                    contentDescription = null,
                    tint = AzulPrimario,
                    modifier = Modifier.size(
                        size = 28.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.width(
                    width = 16.dp
                )
            )

            // Contenido central
            Column(
                modifier = Modifier.weight(
                    weight = 1f
                )
            ) {
                Text(
                    text = medicamento.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "${medicamento.dosis} • ${medicamento.frecuencia}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(
                        height = 8.dp
                    )
                )

                // Etiqueta de horario (Badge/Píldora)
                val colorFondoBadge = if (medicamento.yaFueTomado) {
                    Color(0xFFF5F5F5) 
                } else {
                    Color(0xFFFFF9C4)
                }
                
                val colorTextoBadge = if (medicamento.yaFueTomado) {
                    Color(0xFF757575)
                } else {
                    Color(0xFFF57C00)
                }

                Surface(
                    color = colorFondoBadge,
                    shape = RoundedCornerShape(
                        size = 8.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (medicamento.yaFueTomado) Icons.Default.Check else Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(
                                size = 14.dp
                            ),
                            tint = colorTextoBadge
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = 4.dp
                            )
                        )
                        Text(
                            text = if (medicamento.yaFueTomado) "Completado hoy (${medicamento.horaToma})" else "Próximo: Hoy, ${medicamento.horaToma}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorTextoBadge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Botón de verificación a la derecha
            IconButton(
                onClick = { /* Lógica se mantiene igual */ },
                modifier = Modifier.size(
                    size = 40.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (medicamento.yaFueTomado) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                    modifier = Modifier.size(
                        size = 32.dp
                    )
                )
            }
        }
    }
}
