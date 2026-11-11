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
import androidx.navigation.NavHostController
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
import com.google.firebase.auth.FirebaseUser

object Destinos {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val MEDICAMENTOS = "medicamentos"
    const val CITAS = "citas"
    const val HISTORIAL = "historial"
    const val PERFIL = "perfil"
    const val NUEVA_CITA = "nueva_cita"
    const val NUEVO_MEDICAMENTO = "nuevo_medicamento"

    const val DETALLE_MEDICAMENTO = "detalle_medicamento/{medicamentoId}"
    const val EDITAR_MEDICAMENTO = "editar_medicamento/{medicamentoId}"
    const val DETALLE_CITA = "detalle_cita/{citaId}"
    const val EDITAR_CITA = "editar_cita/{citaId}"
    const val ALERTA_MEDICAMENTO = "alerta_medicamento/{medicamentoId}"

    fun detalleMedicamento(id: String) = "detalle_medicamento/$id"
    fun editarMedicamento(id: String) = "editar_medicamento/$id"
    fun detalleCita(id: String) = "detalle_cita/$id"
    fun editarCita(id: String) = "editar_cita/$id"
    fun alertaMedicamento(id: String) = "alerta_medicamento/$id"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GestorDeNotificaciones(this)
        enableEdgeToEdge()

        setContent {
            CitasMedicasTheme {
                val auth = FirebaseAuth.getInstance()
                val navController = rememberNavController()

                var usuarioFirebase by remember { mutableStateOf(auth.currentUser) }
                var mostrarDialogoPermiso by remember { mutableStateOf(false) }

                // Escuchar notificaciones (Intents)
                LaunchedEffect(key1 = intent) {
                    val pantalla = intent.getStringExtra("PANTALLA")
                    val medId = intent.getStringExtra("MEDICAMENTO_ID")
                    val citaId = intent.getStringExtra("CITA_ID")

                    if (pantalla == "ALERTA" && medId != null) {
                        navController.navigate(Destinos.alertaMedicamento(medId))
                    } else if (pantalla == "DETALLE_CITA" && citaId != null) {
                        navController.navigate(Destinos.detalleCita(citaId))
                    }
                }

                // Verificar permisos de alarmas exactas (Android 12+)
                LaunchedEffect(key1 = Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (!alarmManager.canScheduleExactAlarms()) {
                            mostrarDialogoPermiso = true
                        }
                    }
                }

                // Vigilar estado de sesión de Firebase
                DisposableEffect(key1 = auth) {
                    val listener = FirebaseAuth.AuthStateListener { usuarioFirebase = it.currentUser }
                    auth.addAuthStateListener(listener)
                    onDispose { auth.removeAuthStateListener(listener) }
                }

                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { padding ->
                    Box(modifier = Modifier.fillMaxSize()) {

                        // Llamada al mapa de navegación limpio
                        NavegacionApp(
                            navController = navController,
                            usuarioFirebase = usuarioFirebase,
                            auth = auth
                        )

                        // Diálogo de permisos
                        if (mostrarDialogoPermiso) {
                            AlertDialog(
                                onDismissRequest = {
                                    mostrarDialogoPermiso = false
                                },
                                title = {
                                    Text(
                                        text = "Permiso de Alarmas Exactas"
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Para recibir notificaciones a la hora exacta, necesitamos este permiso."
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            mostrarDialogoPermiso = false
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                startActivity(
                                                    Intent(
                                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                                        Uri.parse("package:$packageName")
                                                    )
                                                )
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
                                            text = "Ahora no"
                                        )
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

@Composable
fun NavegacionApp(
    navController: NavHostController,
    usuarioFirebase: FirebaseUser?,
    auth: FirebaseAuth
) {
    NavHost(
        navController = navController,
        startDestination = if (usuarioFirebase == null) Destinos.LOGIN else Destinos.INICIO
    ) {

        // --- AUTENTICACIÓN ---
        composable(route = Destinos.LOGIN) {
            PantallaLogin(
                alEntrar = {
                    navController.navigate(Destinos.INICIO) {
                        popUpTo(Destinos.LOGIN) { inclusive = true }
                    }
                },
                irARegistro = {
                    navController.navigate(Destinos.REGISTRO)
                }
            )
        }

        composable(route = Destinos.REGISTRO) {
            PantallaRegistro(
                alRegistrar = {
                    navController.navigate(Destinos.INICIO) {
                        popUpTo(Destinos.LOGIN) { inclusive = true }
                    }
                },
                irALogin = {
                    navController.navigate(Destinos.LOGIN)
                }
            )
        }

        // --- DASHBOARD / INICIO ---
        composable(route = Destinos.INICIO) {
            PantallaInicio(
                alAgregarMedicamento = {
                    navController.navigate(Destinos.NUEVO_MEDICAMENTO)
                },
                alVerMedicamentos = {
                    navController.navigate(Destinos.MEDICAMENTOS) {
                        popUpTo(Destinos.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alVerCitas = {
                    navController.navigate(Destinos.CITAS) {
                        popUpTo(Destinos.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alNuevaCita = {
                    navController.navigate(Destinos.NUEVA_CITA)
                },
                alDetalleMedicamento = { id ->
                    navController.navigate(Destinos.detalleMedicamento(id))
                },
                alDetalleCita = { id ->
                    navController.navigate(Destinos.detalleCita(id))
                },
                alVerHistorial = {
                    navController.navigate(Destinos.HISTORIAL)
                },
                alCerrarSesion = {
                    navController.navigate(Destinos.PERFIL) {
                        popUpTo(Destinos.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // --- MÓDULO MEDICAMENTOS ---
        composable(route = Destinos.MEDICAMENTOS) {
            PantallaMedicamentos(
                alAnadirMedicina = {
                    navController.navigate(Destinos.NUEVO_MEDICAMENTO)
                },
                alVerDetalle = { id ->
                    navController.navigate(Destinos.detalleMedicamento(id))
                },
                alVerHistorial = {
                    navController.navigate(Destinos.HISTORIAL)
                },
                alNavegarA = { destino ->
                    navController.navigate(destino) {
                        popUpTo(Destinos.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(route = Destinos.NUEVO_MEDICAMENTO) {
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
            route = Destinos.DETALLE_MEDICAMENTO,
            arguments = listOf(
                navArgument(
                    name = "medicamentoId"
                ) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("medicamentoId") ?: ""
            PantallaDetalleMedicamento(
                medicamentoId = id,
                alEditar = {
                    navController.navigate(Destinos.editarMedicamento(id))
                },
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destinos.EDITAR_MEDICAMENTO,
            arguments = listOf(
                navArgument(
                    name = "medicamentoId"
                ) {
                    type = NavType.StringType
                }
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
            route = Destinos.ALERTA_MEDICAMENTO,
            arguments = listOf(
                navArgument(
                    name = "medicamentoId"
                ) {
                    type = NavType.StringType
                }
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

        // --- MÓDULO CITAS ---
        composable(route = Destinos.CITAS) {
            PantallaCitas(
                alNavegarA = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Destinos.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alNuevaCita = {
                    navController.navigate(Destinos.NUEVA_CITA)
                },
                alDetalleCita = { id ->
                    navController.navigate(Destinos.detalleCita(id))
                }
            )
        }

        composable(route = Destinos.NUEVA_CITA) {
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
            route = Destinos.DETALLE_CITA,
            arguments = listOf(
                navArgument(
                    name = "citaId"
                ) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("citaId") ?: ""
            PantallaDetalleCita(
                citaId = id,
                alEditar = {
                    navController.navigate(Destinos.editarCita(id))
                },
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destinos.EDITAR_CITA,
            arguments = listOf(
                navArgument(
                    name = "citaId"
                ) {
                    type = NavType.StringType
                }
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

        // --- HISTORIAL Y PERFIL ---
        composable(route = Destinos.HISTORIAL) {
            PantallaHistorial(
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Destinos.PERFIL) {
            PantallaPerfil(
                alVolver = {
                    navController.popBackStack()
                },
                alNavegarA = { destino ->
                    navController.navigate(destino) {
                        popUpTo(Destinos.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alCerrarSesion = {
                    auth.signOut()
                    navController.navigate(Destinos.LOGIN) {
                        popUpTo(Destinos.INICIO) { inclusive = true }
                    }
                }
            )
        }
    }
}
