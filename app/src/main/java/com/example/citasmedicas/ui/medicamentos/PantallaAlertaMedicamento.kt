package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.ui.theme.AzulPrimario
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAlertaMedicamento(
    medicamentoId: String,
    alConfirmar: () -> Unit,
    alPosponer: () -> Unit,
    alOmitir: () -> Unit,
    modelo: MedicamentoViewModel = viewModel()
) {
    val medicamento = modelo.medicamentoSeleccionado
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(
        key1 = medicamentoId
    ) {
        modelo.obtenerMedicamentoPorId(
            id = medicamentoId
        )
    }

    // Observar mensajes de éxito desde el ViewModel para mostrarlos en el Snackbar
    LaunchedEffect(
        key1 = modelo.mensajeExito
    ) {
        modelo.mensajeExito?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it
                )
            }
            modelo.mensajeExito = null
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    paddingValues = padding
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            if (medicamento == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AzulPrimario
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            all = 24.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Cabecera con Icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(
                                size = 32.dp
                            )
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = 8.dp
                            )
                        )
                        Text(
                            text = "Alarma de Medicamento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(
                            height = 32.dp
                        )
                    )

                    Text(
                        text = "¡Hora de tomar tu medicamento!",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Default
                    )

                    Spacer(
                        modifier = Modifier.height(
                            height = 16.dp
                        )
                    )

                    Text(
                        text = medicamento.nombre,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Default
                    )

                    Spacer(
                        modifier = Modifier.height(
                            height = 24.dp
                        )
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(
                            size = 16.dp
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                all = 24.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Dosis exacta:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Default
                            )
                            Text(
                                text = "${medicamento.dosis} (${medicamento.tipo})",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Default
                            )
                            
                            Spacer(
                                modifier = Modifier.height(
                                    height = 16.dp
                                )
                            )
                            
                            Text(
                                text = "Instrucciones:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Default
                            )
                            Text(
                                text = "Toma programada para las ${medicamento.horaToma}",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(
                            height = 48.dp
                        )
                    )

                    // RF17: Botón Confirmar Toma
                    Button(
                        onClick = {
                            modelo.confirmarToma(
                                medicamentoId = medicamento.id,
                                alTerminar = {
                                    scope.launch {
                                        delay(1500) // Esperar a que el usuario vea el snackbar
                                        alConfirmar()
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                height = 64.dp
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(
                            size = 32.dp
                        )
                    ) {
                        Text(
                            text = "Confirmar Toma",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(
                            height = 16.dp
                        )
                    )

                    // RF18: Botón Posponer Recordatorio
                    Button(
                        onClick = {
                            modelo.posponerRecordatorio(
                                medicamento = medicamento,
                                minutos = 10,
                                alTerminar = {
                                    scope.launch {
                                        delay(1500)
                                        alPosponer()
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                height = 64.dp
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF57C00)
                        ),
                        shape = RoundedCornerShape(
                            size = 32.dp
                        )
                    ) {
                        Text(
                            text = "Posponer 10 min",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(
                            height = 24.dp
                        )
                    )

                    // RF17: Enlace Omitir Toma
                    TextButton(
                        onClick = {
                            modelo.omitirToma(
                                medicamentoId = medicamento.id,
                                alTerminar = {
                                    scope.launch {
                                        delay(1000)
                                        alOmitir()
                                    }
                                }
                            )
                        }
                    ) {
                        Text(
                            text = "Omitir esta dosis",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.Default
                        )
                    }
                }
            }
        }
    }
}
