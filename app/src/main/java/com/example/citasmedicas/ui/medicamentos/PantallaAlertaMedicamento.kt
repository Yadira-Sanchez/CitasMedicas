package com.example.citasmedicas.ui.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun PantallaAlertaMedicamento(
    medicamentoId: String,
    alConfirmar: () -> Unit,
    alPosponer: () -> Unit,
    alOmitir: () -> Unit,
    modelo: MedicamentoViewModel = viewModel()
) {
    val medicamento = modelo.medicamentoSeleccionado

    LaunchedEffect(
        key1 = medicamentoId
    ) {
        modelo.obtenerMedicamentoPorId(
            id = medicamentoId
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
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

                Button(
                    onClick = {
                        modelo.confirmarTomaAlerta(
                            medicamento = medicamento,
                            alTerminar = alConfirmar
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

                Button(
                    onClick = {
                        modelo.posponerTomaAlerta(
                            medicamento = medicamento,
                            alTerminar = alPosponer
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

                TextButton(
                    onClick = {
                        modelo.omitirTomaAlerta(
                            alTerminar = alOmitir
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
