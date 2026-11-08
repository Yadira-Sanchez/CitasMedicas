package com.example.citasmedicas.ui.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
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
import com.example.citasmedicas.data.MensajeChat
import com.example.citasmedicas.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAsistente(
    viewModel: AsistenteViewModel = viewModel(),
    alVolver: () -> Unit
) {
    val uiState = viewModel.uiState
    val scrollState = rememberLazyListState()

    // Auto-scroll al último mensaje cuando la lista cambia
    LaunchedEffect(
        key1 = uiState.listaMensajes.size,
        key2 = uiState.estaEscribiendo
    ) {
        if (uiState.listaMensajes.isNotEmpty()) {
            scrollState.animateScrollToItem(
                index = uiState.listaMensajes.size
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AzulPrimario.copy(alpha = 0.1f),
                            modifier = Modifier.size(size = 40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = AzulPrimario,
                                modifier = Modifier.padding(all = 8.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier.width(width = 12.dp)
                        )
                        Column {
                            Text(
                                text = "Asistente Virtual",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "En línea",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = alVolver
                    ) {
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
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.textoMensaje,
                        onValueChange = { 
                            viewModel.actualizarTexto(it) 
                        },
                        placeholder = { 
                            Text(
                                text = "Escribe un mensaje..."
                            ) 
                        },
                        modifier = Modifier.weight(weight = 1f),
                        shape = RoundedCornerShape(size = 24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulPrimario,
                            unfocusedContainerColor = Color(0xFFF5F7FA),
                            focusedContainerColor = Color(0xFFF5F7FA),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    FloatingActionButton(
                        onClick = { 
                            viewModel.enviarMensaje() 
                        },
                        containerColor = AzulPrimario,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(size = 48.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar"
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .background(color = Color(0xFFF8F9FA))
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 12.dp)
            ) {
                items(
                    items = uiState.listaMensajes
                ) { mensaje ->
                    BurbujaMensaje(
                        mensaje = mensaje
                    )
                }

                if (uiState.estaEscribiendo) {
                    item {
                        Text(
                            text = "El asistente está escribiendo...",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BurbujaMensaje(
    mensaje: MensajeChat
) {
    val alineacion = if (mensaje.esDelUsuario) Alignment.CenterEnd else Alignment.CenterStart
    val colorBurbuja = if (mensaje.esDelUsuario) AzulPrimario else Color.White
    val colorTexto = if (mensaje.esDelUsuario) Color.White else Color.Black
    val forma = if (mensaje.esDelUsuario) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alineacion
    ) {
        Surface(
            color = colorBurbuja,
            shape = forma,
            shadowElevation = 1.dp
        ) {
            Text(
                text = mensaje.texto,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = colorTexto,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}
