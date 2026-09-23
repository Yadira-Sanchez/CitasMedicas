package com.example.citasmedicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.citasmedicas.ui.theme.CitasMedicasTheme
import com.example.citasmedicas.ui.auth.PantallaLogin
import com.example.citasmedicas.ui.auth.PantallaRegistro
import com.example.citasmedicas.ui.dashboard.PantallaInicio
import com.example.citasmedicas.ui.medicamentos.PantallaMedicamentos
import com.example.citasmedicas.ui.medicamentos.PantallaNuevoMedicamento
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitasMedicasTheme {
                val auth = FirebaseAuth.getInstance()


                var usuarioFirebase by remember { mutableStateOf(auth.currentUser) }

                DisposableEffect(auth) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        usuarioFirebase = firebaseAuth.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose { auth.removeAuthStateListener(listener) }
                }


                var pantallaActual by remember { mutableStateOf("Inicio") }


                if (usuarioFirebase == null) {
                    when (pantallaActual) {
                        "Registro" -> PantallaRegistro(
                            alRegistrar = { pantallaActual = "Inicio" },
                            irALogin = { pantallaActual = "Login" }
                        )
                        else -> PantallaLogin(
                            alEntrar = { pantallaActual = "Inicio" },
                            irARegistro = { pantallaActual = "Registro" }
                        )
                    }
                } else {
                    when (pantallaActual) {
                        "Inicio" -> PantallaInicio(
                            alAgregarMedicamento = { pantallaActual = "NuevoMedicamento" },
                            alVerMedicamentos = { pantallaActual = "Medicamentos" },
                            alCerrarSesion = { auth.signOut() }
                        )

                        "Medicamentos" -> PantallaMedicamentos(
                            alAnadirMedicina = { pantallaActual = "NuevoMedicamento" },
                            alNavegarA = { destino ->
                                when (destino) {
                                    "inicio" -> pantallaActual = "Inicio"
                                    "perfil" -> auth.signOut()
                                    else -> pantallaActual = "Inicio"
                                }
                            }
                        )

                        "NuevoMedicamento" -> PantallaNuevoMedicamento(
                            alGuardar = { pantallaActual = "Inicio" },
                            alVolver = { pantallaActual = "Inicio" }
                        )

                        else -> PantallaInicio(
                            alAgregarMedicamento = { pantallaActual = "NuevoMedicamento" },
                            alVerMedicamentos = { pantallaActual = "Medicamentos" },
                            alCerrarSesion = { auth.signOut() }
                        )
                    }
                }
            }
        }
    }
}