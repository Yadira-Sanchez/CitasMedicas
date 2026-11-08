package com.example.citasmedicas.ui.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citasmedicas.data.CitaMedica
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.data.MensajeChat
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChatbotModal(
    alCerrar: () -> Unit,
    medicamentos: List<Medicamento>,
    citas: List<CitaMedica>,
    viewModel: AsistenteViewModel = viewModel()
) {
    val mensajes by viewModel.mensajes.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    var textoPregunta by remember { 
        mutableStateOf(
            value = ""
        ) 
    }
    val listState = rememberLazyListState()

    // Auto-scroll al último mensaje cuando la lista cambia o llega respuesta
    LaunchedEffect(
        key1 = mensajes.size
    ) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(
                index = mensajes.size - 1
            )
        }
    }

    Dialog(
        onDismissRequest = alCerrar,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight(
                    fraction = 0.85f
                )
                .fillMaxWidth()
                .imePadding()
                .padding(
                    all = 16.dp
                ),
            shape = RoundedCornerShape(
                size = 28.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Cabecera del Chat
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = AzulPrimario
                        )
                        .padding(
                            horizontal = 16.dp, 
                            vertical = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(
                                alpha = 0.2f
                            ),
                            modifier = Modifier.size(
                                size = 40.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(
                                    all = 8.dp
                                )
                            )
                        }
                        Spacer(
                            modifier = Modifier.width(
                                width = 12.dp
                            )
                        )
                        Column {
                            Text(
                                text = "Vitalis AI",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "En línea",
                                color = Color.White.copy(
                                    alpha = 0.8f
                                ),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { 
                                viewModel.limpiarHistorial() 
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Limpiar Historial",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = alCerrar
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Cuerpo del Chat con Scroll Automático
                Box(
                    modifier = Modifier
                        .weight(
                            weight = 1f
                        )
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        )
                ) {
                    if (mensajes.isEmpty()) {
                        Column(
                            modifier = Modifier.align(
                                alignment = Alignment.Center
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(
                                    size = 64.dp
                                )
                            )
                            Spacer(
                                modifier = Modifier.height(
                                    height = 16.dp
                                )
                            )
                            Text(
                                text = "¿En qué puedo ayudarte hoy?",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            space = 12.dp
                        )
                    ) {
                        items(
                            items = mensajes
                        ) { mensaje ->
                            GloboChat(
                                mensaje = mensaje
                            )
                        }

                        if (estaCargando) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(
                                            size = 20.dp
                                        ),
                                        strokeWidth = 2.dp,
                                        color = AzulPrimario
                                    )
                                    Spacer(
                                        modifier = Modifier.width(
                                            width = 8.dp
                                        )
                                    )
                                    Text(
                                        text = "Vitalis está pensando...",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // Barra de Entrada (Ajustada para teclado)
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                all = 16.dp
                            )
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textoPregunta,
                            onValueChange = { 
                                textoPregunta = it 
                            },
                            placeholder = { 
                                Text(
                                    text = "Escribe tu consulta..."
                                ) 
                            },
                            modifier = Modifier.weight(
                                weight = 1f
                            ),
                            shape = RoundedCornerShape(
                                size = 24.dp
                            ),
                            maxLines = 3
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = 8.dp
                            )
                        )
                        FloatingActionButton(
                            onClick = {
                                if (textoPregunta.isNotBlank()) {
                                    viewModel.enviarMensaje(
                                        pregunta = textoPregunta,
                                        medicamentos = medicamentos,
                                        citas = citas
                                    )
                                    textoPregunta = ""
                                }
                            },
                            containerColor = AzulPrimario,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(
                                size = 48.dp
                            ),
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GloboChat(
    mensaje: MensajeChat
) {
    val alineacion = if (mensaje.esDelUsuario) Alignment.CenterEnd else Alignment.CenterStart
    val colorBurbuja = if (mensaje.esDelUsuario) AzulPrimario else Color(0xFFF1F3F4)
    val colorTexto = if (mensaje.esDelUsuario) Color.White else Color.Black
    val forma = if (mensaje.esDelUsuario) {
        RoundedCornerShape(
            topStart = 16.dp, 
            topEnd = 16.dp, 
            bottomStart = 16.dp, 
            bottomEnd = 4.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 16.dp, 
            topEnd = 16.dp, 
            bottomStart = 4.dp, 
            bottomEnd = 16.dp
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .align(
                    alignment = alineacion
                )
                .widthIn(
                    max = 280.dp
                ),
            shape = forma,
            color = colorBurbuja
        ) {
            Text(
                text = mensaje.texto,
                modifier = Modifier.padding(
                    horizontal = 12.dp, 
                    vertical = 8.dp
                ),
                color = colorTexto,
                fontSize = 15.sp
            )
        }
    }
}
