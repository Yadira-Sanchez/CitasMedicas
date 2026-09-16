package com.example.citasmedicas.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario
import com.example.citasmedicas.ui.theme.VerdeClaro
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    viewModel: DashboardViewModel = viewModel(),
    alAgregarMedicamento: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val uiState = viewModel.uiState

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* RF23: Abrir Asistente IA */ },
                containerColor = AzulPrimario,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "Asistente IA")
            }
        },
        floatingActionButtonPosition = FabPosition.Start,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* RF17: Navegar a Medicamentos */ },
                    icon = { Icon(Icons.Default.Medication, null) },
                    label = { Text("Medicinas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* RF12: Navegar a Citas */ },
                    icon = { Icon(Icons.Default.Event, null) },
                    label = { Text("Citas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = alCerrarSesion,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // Cabecera: Saludo y Foto
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hola,", fontSize = 16.sp, color = Color.Gray)
                        Text(uiState.nombreUsuario, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        modifier = Modifier.size(48.dp).clickable { /* RF21: Perfil */ }
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            // Buscador
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Buscar medicamentos o citas...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
            }

            // Selector de Fechas
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Calendario", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Ver todo",
                            color = AzulPrimario,
                            modifier = Modifier.clickable { /* RF12 */ }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.diasSemana) { fecha ->
                            DiaItem(
                                fecha = fecha,
                                estaSeleccionado = fecha == uiState.fechaSeleccionada,
                                alSeleccionar = { viewModel.seleccionarFecha(fecha) }
                            )
                        }
                    }
                }
            }

            // Tomas de Hoy
            item {
                Text("Tomas de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            if (uiState.listaMedicamentos.isEmpty()) {
                item {
                    Text("No tienes medicamentos programados para hoy.", color = Color.Gray)
                }
            } else {
                items(uiState.listaMedicamentos) { med ->
                    TarjetaMedicamento(
                        medicamento = med,
                        alTomar = { viewModel.marcarComoTomado(med) }
                    )
                }
            }

            // Próxima Cita
            item {
                Text("Próxima Cita", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            val cita = uiState.proximaCita
            if (cita != null) {
                item {
                    TarjetaCita(cita = cita)
                }
            } else {
                item {
                    Text("No tienes citas próximas.", color = Color.Gray)
                }
            }
            
            item { Spacer(Modifier.height(80.dp)) } // Espacio para el FAB
        }
    }
}

@Composable
fun DiaItem(fecha: LocalDate, estaSeleccionado: Boolean, alSeleccionar: () -> Unit) {
    val localeEs = Locale.forLanguageTag("es-ES")
    val diaNombre = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, localeEs)
    val diaNumero = fecha.dayOfMonth.toString()

    Card(
        onClick = alSeleccionar,
        modifier = Modifier.width(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) AzulPrimario else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                diaNombre.uppercase(localeEs),
                fontSize = 12.sp,
                color = if (estaSeleccionado) Color.White else Color.Gray
            )
            Text(
                diaNumero,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (estaSeleccionado) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun TarjetaMedicamento(medicamento: Medicamento, alTomar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Medication,
                contentDescription = null,
                tint = if (medicamento.yaFueTomado) VerdeClaro else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(medicamento.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${medicamento.dosis} • ${medicamento.horaToma}", color = Color.Gray, fontSize = 14.sp)
            }
            if (medicamento.yaFueTomado) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Tomado", tint = VerdeClaro)
            } else {
                Button(
                    onClick = alTomar,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeClaro),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Tomar", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun TarjetaCita(cita: CitaMedica) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AzulPrimario),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cita.doctor, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(cita.especialidad, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                Icon(Icons.Default.Event, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("${cita.fecha} • ${cita.hora}", color = Color.White, fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(cita.ubicacion, color = Color.White, fontSize = 14.sp)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reprogramar")
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AzulPrimario),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}
