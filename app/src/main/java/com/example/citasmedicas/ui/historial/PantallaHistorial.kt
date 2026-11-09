package com.example.citasmedicas.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.RegistroHistorial
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    citaId: String? = null,
    viewModel: HistorialViewModel = viewModel(),
    alVolver: () -> Unit
) {
    // Disparamos la carga cuando la pantalla se inicia o cambia el citaId
    LaunchedEffect(key1 = citaId) {
        viewModel.cargarHistorial(citaId)
    }

    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (citaId != null) "Historial de Cita" else "Historial de Actividades",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
        ) {
            if (uiState.estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AzulPrimario
                )
            } else if (uiState.listaHistorial.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No se encontraron registros.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.listaHistorial) { registro ->
                        TarjetaHistorialItem(registro = registro)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaHistorialItem(registro: RegistroHistorial) {
    val esMedicamento = registro.tipo == "MEDICAMENTO"
    val esTomado = registro.accion == "Tomado"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (esMedicamento) Color(0xFFE3F2FD) else Color(0xFFF1F8E9),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (esMedicamento) Icons.Default.Medication else Icons.Default.Event,
                    contentDescription = null,
                    tint = if (esMedicamento) AzulPrimario else Color(0xFF2E7D32),
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = registro.nombreActividad,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${registro.fecha} • ${registro.hora}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (esTomado) Icons.Default.CheckCircle else Icons.Default.RemoveCircle,
                    contentDescription = null,
                    tint = if (esTomado) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = registro.accion,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (esTomado) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
