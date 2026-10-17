package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoDetalleMedicamento(
    medicamento: Medicamento,
    alCerrar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Cabecera: Nombre y botón cerrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = medicamento.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                color = AzulPrimario
            )
            IconButton(onClick = alCerrar) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Badge de Tipo (Pastilla, Jarabe, etc.)
        AssistChip(
            onClick = { },
            label = { Text(medicamento.tipo, fontFamily = FontFamily.Default) },
            leadingIcon = {
                val icono = when (medicamento.tipo) {
                    "Pastilla" -> Icons.Default.Medication
                    "Jarabe", "Gotas" -> Icons.Default.Opacity
                    "Inyección" -> Icons.Default.Vaccines
                    else -> Icons.Default.Medication
                }
                Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = AzulPrimario,
                leadingIconContentColor = AzulPrimario
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Información detallada en tarjeta
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ItemDetalle(
                    icono = Icons.Default.Straighten,
                    titulo = "Dosis",
                    valor = medicamento.dosis
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                ItemDetalle(
                    icono = Icons.Default.Repeat,
                    titulo = "Frecuencia",
                    valor = medicamento.frecuencia
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                ItemDetalle(
                    icono = Icons.Default.Schedule,
                    titulo = "Horario Programado",
                    valor = "Primera toma a las ${medicamento.horaToma}"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                ItemDetalle(
                    icono = Icons.Default.CalendarToday,
                    titulo = "Duración del Tratamiento",
                    valor = if (medicamento.fechaFin.isEmpty()) {
                        "Inicia el ${medicamento.fechaInicio}"
                    } else {
                        "Del ${medicamento.fechaInicio} al ${medicamento.fechaFin}"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de acción (Editar y Eliminar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* Lógica de edición RF06 futura */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Editar", fontFamily = FontFamily.Default)
            }

            Button(
                onClick = { /* Lógica de eliminación RF07 futura */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar", fontFamily = FontFamily.Default)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ItemDetalle(icono: ImageVector, titulo: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
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
