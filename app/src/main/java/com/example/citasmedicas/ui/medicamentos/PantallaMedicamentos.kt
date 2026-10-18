package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val snackbarHostState = remember { 
        SnackbarHostState() 
    }

    LaunchedEffect(
        key1 = medicamentoViewModel.eliminacionExitosa
    ) {
        if (medicamentoViewModel.eliminacionExitosa) {
            snackbarHostState.showSnackbar(
                message = "Medicamento eliminado correctamente."
            )
        }
    }

    LaunchedEffect(
        key1 = medicamentoViewModel.mensajeError
    ) {
        medicamentoViewModel.mensajeError?.let {
            snackbarHostState.showSnackbar(
                message = it
            )
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState
            ) 
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Botón Principal de Acción
                Button(
                    onClick = { 
                        alAnadirMedicina() 
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
                        text = "Agregar nuevo medicamento",
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
                text = "TRATAMIENTOS",
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
                    text = "Medicamentos activos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
            }

            // Filtros por Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp
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
                            Text(
                                text = "Todos (${uiState.listaMedicamentos.size})"
                            ) 
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

            Spacer(
                modifier = Modifier.height(
                    height = 12.dp
                )
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
                    verticalArrangement = Arrangement.spacedBy(
                        space = 16.dp
                    ),
                    contentPadding = PaddingValues(
                        bottom = 24.dp
                    )
                ) {
                    items(
                        items = uiState.listaMedicamentos
                    ) { medicamento ->
                        ElementoMedicamentoItem(
                            medicamento = medicamento,
                            onClick = { 
                                alVerDetalle(medicamento.id) 
                            }
                        )
                    }
                }
            }
        }
    }
}
