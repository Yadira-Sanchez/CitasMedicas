package com.example.citasmedicas.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
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
import com.example.citasmedicas.data.RegistroHistorial
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    viewModel: HistorialViewModel = viewModel(),
    alVolver: () -> Unit
) {
    LaunchedEffect(
        key1 = Unit
    ) {
        viewModel.obtenerHistorial()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial de Recordatorios",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            alVolver()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
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
                .fillMaxSize()
                .padding(
                    paddingValues = padding
                )
        ) {
            if (viewModel.estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        alignment = Alignment.Center
                    ),
                    color = AzulPrimario
                )
            } else if (viewModel.listaHistorial.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes actividades registradas en el historial",
                        color = Color.Gray,
                        modifier = Modifier.padding(
                            horizontal = 32.dp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        all = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 12.dp
                    )
                ) {
                    items(
                        items = viewModel.listaHistorial
                    ) { registro ->
                        TarjetaHistorial(
                            registro = registro
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaHistorial(
    registro: RegistroHistorial
) {
    val esTomado = registro.estado == "Tomado"
    val colorEstado = if (esTomado) Color(0xFF2E7D32) else Color(0xFFF57C00)
    val iconoEstado = if (esTomado) Icons.Default.CheckCircle else Icons.Default.Warning

    Card(
        modifier = Modifier.fillMaxWidth(),
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
        Row(
            modifier = Modifier
                .padding(
                    all = 16.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(
                    weight = 1f
                )
            ) {
                Text(
                    text = registro.nombreActividad,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(
                    modifier = Modifier.height(
                        height = 4.dp
                    )
                )
                Text(
                    text = registro.fechaHora,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Surface(
                color = colorEstado.copy(
                    alpha = 0.1f
                ),
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
                        imageVector = iconoEstado,
                        contentDescription = null,
                        tint = colorEstado,
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
                        text = registro.estado,
                        color = colorEstado,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
