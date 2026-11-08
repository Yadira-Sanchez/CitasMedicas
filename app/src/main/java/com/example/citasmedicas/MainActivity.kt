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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.citasmedicas.ui.historial.PantallaHistorial
import com.example.citasmedicas.ui.perfil.PantallaPerfil
import com.example.citasmedicas.util.GestorDeNotificaciones
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GestorDeNotificaciones(this)

        enableEdgeToEdge()
        setContent {
            CitasMedicasTheme {
                val auth = FirebaseAuth.getInstance()
                val navController = rememberNavController()
                var usuarioFirebase by remember { 
                    mutableStateOf(auth.currentUser) 
                }
                var mostrarDialogoPermiso by remember { 
                    mutableStateOf(false) 
                }

                LaunchedEffect(
                    key1 = intent
                ) {
                    val pantalla = intent.getStringExtra("PANTALLA")
                    val medId = intent.getStringExtra("MEDICAMENTO_ID")
                    val citaId = intent.getStringExtra("CITA_ID")

                    if (pantalla == "ALERTA" && medId != null) {
                        navController.navigate("alerta_medicamento/$medId")
                    } else if (pantalla == "DETALLE_CITA" && citaId != null) {
                        navController.navigate("detalle_cita/$citaId")
                    }
                }

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

                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { padding ->
                    Box(
                        modifier = Modifier.fillMaxSize()
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
                                        navController.navigate("medicamentos") {
                                            popUpTo("inicio") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    alVerCitas = {
                                        navController.navigate("citas") {
                                            popUpTo("inicio") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    alNuevaCita = {
                                        navController.navigate("nueva_cita")
                                    },
                                    alDetalleMedicamento = { id: String ->
                                        navController.navigate("detalle_medicamento/$id")
                                    },
                                    alDetalleCita = { id: String ->
                                        navController.navigate("detalle_cita/$id")
                                    },
                                    alVerHistorial = {
                                        navController.navigate("historial")
                                    },
                                    alCerrarSesion = {
                                        navController.navigate("perfil") {
                                            popUpTo("inicio") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
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
                                        navController.navigate(destino) {
                                            popUpTo("inicio") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
                                    alNavegarA = { ruta ->
                                        navController.navigate(ruta) {
                                            popUpTo("inicio") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    alNuevaCita = {
                                        navController.navigate("nueva_cita")
                                    },
                                    alDetalleCita = { idCita -> 
                                        navController.navigate("detalle_cita/$idCita")
                                    }

                                )
                            }

                            composable(
                                route = "historial"
                            ) {
                                PantallaHistorial(
                                    alVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(
                                route = "perfil"
                            ) {
                                PantallaPerfil(
                                    alVolver = {
                                        navController.popBackStack()
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
                                route = "detalle_cita/{citaId}",
                                arguments = listOf(
                                    navArgument("citaId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("citaId") ?: ""
                                PantallaDetalleCita(
                                    citaId = id,
                                    alEditar = {
                                        navController.navigate("editar_cita/$id")
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
                                route = "editar_cita/{citaId}",
                                arguments = listOf(
                                    navArgument("citaId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("citaId") ?: ""
                                PantallaEditarCita(
                                    citaId = id,
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

                        if (mostrarDialogoPermiso) {
                            AlertDialog(
                                onDismissRequest = { mostrarDialogoPermiso = false },
                                title = { Text("Permiso de Alarmas Exactas") },
                                text = { Text("Para recibir notificaciones de tus medicamentos a la hora exacta, necesitamos este permiso.") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            mostrarDialogoPermiso = false
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                val intent = Intent().apply {
                                                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                                    data = Uri.parse("package:${packageName}")
                                                }
                                                startActivity(intent)
                                            }
                                        }
                                    ) {
                                        Text("Ir a Ajustes")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { mostrarDialogoPermiso = false }) {
                                        Text("Ahora no")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
