package com.example.citasmedicas.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario
import com.example.citasmedicas.ui.theme.VerdeClaro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(alAgregarMedicamento: () -> Unit, alCerrarSesion: () -> Unit) {
    val medicamentos = listOf(
        Medicamento("Ibuprofeno", "Pastilla", "400mg", "Cada 8 horas", "08:00 AM", "", ""),
        Medicamento("Paracetamol", "Pastilla", "500mg", "Cada 6 horas", "06:00 AM", "", "", yaFueTomado = true)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = alAgregarMedicamento, containerColor = VerdeClaro) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Face, null) }, label = { Text("Medicamentos") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.DateRange, null) }, label = { Text("Citas") })
                NavigationBarItem(selected = false, onClick = alCerrarSesion, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Perfil") })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = AzulPrimario, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Hola, Carlos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Notifications, contentDescription = null, tint = AzulPrimario)
            }
            
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar medicamentos...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )

            Spacer(Modifier.height(20.dp))
            Text("Tomas de Hoy", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            
            LazyColumn {
                items(medicamentos) { med ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = if (med.yaFueTomado) Color.Gray else VerdeClaro, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(med.nombre, fontWeight = FontWeight.Bold)
                                Text("${med.dosis} • ${med.horaToma}", color = Color.Gray)
                            }
                            if (med.yaFueTomado) {
                                Text("Tomado", color = Color.Green)
                            } else {
                                Button(onClick = {}) { Text("Tomar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
