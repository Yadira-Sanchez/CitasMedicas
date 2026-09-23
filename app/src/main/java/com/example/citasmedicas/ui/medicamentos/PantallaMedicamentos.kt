package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.dashboard.DashboardViewModel
import com.example.citasmedicas.ui.theme.AzulPrimario

@Composable
fun PantallaMedicamentos(
    alAnadirMedicina: () -> Unit,
    alNavegarA: (String) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        bottomBar = {
            Column {
                // Botón de Acción Principal (Estilo Medisafe)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Button(
                        onClick = alAnadirMedicina,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                    ) {
                        Text(
                            "Añadir una medicina",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Barra de Navegación Inferior
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        selected = false,
                        onClick = { alNavegarA("inicio") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Medication, contentDescription = "Medicinas") },
                        label = { Text("Medicinas") },
                        selected = true,
                        onClick = { /* Ya estamos aquí */ }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Citas") },
                        label = { Text("Citas") },
                        selected = false,
                        onClick = { alNavegarA("citas") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = { alNavegarA("perfil") }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Encabezado Sutil
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Medicamentos activos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )

            if (uiState.listaMedicamentos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tienes medicamentos registrados.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(uiState.listaMedicamentos) { medicamento ->
                        ElementoMedicamentoMedisafe(medicamento)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ElementoMedicamentoMedisafe(medicamento: Medicamento) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono representativo
        val icono = when (medicamento.tipo) {
            "Pastilla" -> Icons.Default.Medication
            "Jarabe", "Gotas" -> Icons.Default.Opacity
            "Inyección" -> Icons.Default.Vaccines
            else -> Icons.Default.Medication
        }

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = AzulPrimario,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        // Textos centralizados
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = medicamento.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Próximo recordatorio: hoy, ${medicamento.horaToma}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
