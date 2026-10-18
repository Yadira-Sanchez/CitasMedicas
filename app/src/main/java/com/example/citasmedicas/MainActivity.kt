package com.example.citasmedicas

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.citasmedicas.ui.theme.CitasMedicasTheme
import com.example.citasmedicas.ui.auth.PantallaLogin
import com.example.citasmedicas.ui.auth.PantallaRegistro
import com.example.citasmedicas.ui.dashboard.PantallaInicio
import com.example.citasmedicas.ui.medicamentos.*
import com.example.citasmedicas.ui.citas.*
import com.example.citasmedicas.data.Medicamento
import com.example.citasmedicas.util.GestorDeNotificaciones
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canal de notificaciones al iniciar
        GestorDeNotificaciones(this)

        enableEdgeToEdge()
        setContent {
            CitasMedicasTheme {
                val auth = FirebaseAuth.getInstance()
                val navController = rememberNavController()
                var usuarioFirebase by remember { mutableStateOf(auth.currentUser) }
                var mostrarDialogoPermiso by remember { mutableStateOf(false) }

                // Procesar navegación desde notificación (Deep Link o Extras)
                LaunchedEffect(
                    key1 = intent
                ) {
                    val pantalla = intent.getStringExtra("PANTALLA")
                    val medId = intent.getStringExtra("MEDICAMENTO_ID")
                    if (pantalla == "ALERTA" && medId != null) {
                        navController.navigate("alerta_medicamento/$medId")
                    }
                }

                // Verificar permiso de alarmas exactas (Android 12+)
                LaunchedEffect(
                    key1 = Unit
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (!alarmManager.canScheduleExactAlarms()) {
                            mostrarDialogoPermiso = true
                        }
                    }
                }

                DisposableEffect(
                    key1 = auth
                ) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        usuarioFirebase = firebaseAuth.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose {
                        auth.removeAuthStateListener(listener)
                    }
                }

                Scaffold { padding ->
                    Box(
                        modifier = Modifier
                            .padding(
                                paddingValues = padding
                            )
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = if (usuarioFirebase == null) "login" else "inicio"
                        ) {
                            composable(
                                route = "login"
                            ) {
                                PantallaLogin(
                                    alEntrar = {
                                        navController.navigate("inicio") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    irARegistro = {
                                        navController.navigate("registro")
                                    }
                                )
                            }
                            composable(
                                route = "registro"
                            ) {
                                PantallaRegistro(
                                    alRegistrar = {
                                        navController.navigate("inicio") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    irALogin = {
                                        navController.navigate("login")
                                    }
                                )
                            }
                            composable(
                                route = "inicio"
                            ) {
                                PantallaInicio(
                                    alAgregarMedicamento = {
                                        navController.navigate("nuevo_medicamento")
                                    },
                                    alVerMedicamentos = {
                                        navController.navigate("medicamentos")
                                    },
                                    alVerCitas = {
                                        navController.navigate("citas")
                                    },
                                    alNuevaCita = {
                                        navController.navigate("nueva_cita")
                                    },
                                    alCerrarSesion = {
                                        auth.signOut()
                                        navController.navigate("login") {
                                            popUpTo("inicio") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(
                                route = "medicamentos"
                            ) {
                                PantallaMedicamentos(
                                    alAnadirMedicina = {
                                        navController.navigate("nuevo_medicamento")
                                    },
                                    alNavegarA = { destino ->
                                        navController.navigate(destino)
                                    },
                                    alVerDetalle = { id ->
                                        navController.navigate("detalle_medicamento/$id")
                                    }
                                )
                            }
                            composable(
                                route = "citas"
                            ) {
                                PantallaCitas(
                                    alNavegarA = { destino ->
                                        navController.navigate(destino)
                                    },
                                    alNuevaCita = {
                                        navController.navigate("nueva_cita")
                                    }
                                )
                            }
                            composable(
                                route = "detalle_medicamento/{medicamentoId}",
                                arguments = listOf(
                                    navArgument("medicamentoId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("medicamentoId") ?: ""
                                PantallaDetalleMedicamento(
                                    medicamentoId = id,
                                    alEditar = {
                                        navController.navigate("editar_medicamento/$id")
                                    },
                                    alVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                route = "editar_medicamento/{medicamentoId}",
                                arguments = listOf(
                                    navArgument("medicamentoId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("medicamentoId") ?: ""
                                PantallaEditarMedicamento(
                                    medicamentoId = id,
                                    alActualizar = {
                                        navController.popBackStack()
                                    },
                                    alVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                route = "nuevo_medicamento"
                            ) {
                                PantallaNuevoMedicamento(
                                    alGuardar = {
                                        navController.popBackStack()
                                    },
                                    alVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                route = "nueva_cita"
                            ) {
                                PantallaNuevaCita(
                                    alGuardar = {
                                        navController.popBackStack()
                                    },
                                    alVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                route = "alerta_medicamento/{medicamentoId}",
                                arguments = listOf(
                                    navArgument("medicamentoId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("medicamentoId") ?: ""
                                PantallaAlertaMedicamento(
                                    medicamentoId = id,
                                    alConfirmar = {
                                        navController.popBackStack()
                                    },
                                    alPosponer = {
                                        navController.popBackStack()
                                    },
                                    alOmitir = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }

                if (mostrarDialogoPermiso) {
                    AlertDialog(
                        onDismissRequest = {
                            mostrarDialogoPermiso = false
                        },
                        title = {
                            Text(
                                text = "Permiso de Alarmas"
                            )
                        },
                        text = {
                            Text(
                                text = "Para recordarte tus medicinas a tiempo, necesitamos el permiso de alarmas exactas."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    mostrarDialogoPermiso = false
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        val intentPermiso = Intent(
                                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                            Uri.parse("package:$packageName")
                                        )
                                        startActivity(intentPermiso)
                                    }
                                }
                            ) {
                                Text(
                                    text = "Ir a Ajustes"
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    mostrarDialogoPermiso = false
                                }
                            ) {
                                Text(
                                    text = "Cancelar"
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(
        intent: Intent
    ) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
