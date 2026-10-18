package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.citasmedicas.ui.dashboard.DashboardViewModel
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMedicamentos(
    alAnadirMedicina: () -> Unit,
    alVerDetalle: (String) -> Unit,
    alNavegarA: (String) -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel(),
    medicamentoViewModel: MedicamentoViewModel = viewModel()
) {
    val uiState = dashboardViewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(medicamentoViewModel.eliminacionExitosa) {
        if (medicamentoViewModel.eliminacionExitosa) {
            snackbarHostState.showSnackbar("Medicamento eliminado correctamente.")
        }
    }

    LaunchedEffect(medicamentoViewModel.mensajeError) {
        medicamentoViewModel.mensajeError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(hostState = snackbarHostState) 
        },
        bottomBar = {
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
                    selected = true,
                    onClick = { }
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
                    selected = false,
                    onClick = { 
                        alNavegarA("citas") 
                    }
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            
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
                        text = "No tienes medicamentos registrados.", 
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), 
                    verticalArrangement = Arrangement.Top
                ) {
                    items(uiState.listaMedicamentos) { medicamento ->
                        ElementoMedicamentoItem(
                            medicamento = medicamento,
                            onClick = { 
                                alVerDetalle(medicamento.id) 
                            }
                        )
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
fun ElementoMedicamentoItem(
    medicamento: Medicamento, 
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        Spacer(
            modifier = Modifier.width(20.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
