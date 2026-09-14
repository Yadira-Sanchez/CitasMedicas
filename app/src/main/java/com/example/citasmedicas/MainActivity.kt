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
import com.example.citasmedicas.ui.medicamentos.PantallaNuevoMedicamento

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitasMedicasTheme {
                // Controlamos qué pantalla se ve con esta variable
                var pantallaActual by remember { mutableStateOf("Login") }

                when (pantallaActual) {
                    "Login" -> PantallaLogin(
                        alEntrar = { pantallaActual = "Inicio" },
                        irARegistro = { pantallaActual = "Registro" }
                    )
                    "Registro" -> PantallaRegistro(
                        alRegistrar = { pantallaActual = "Inicio" },
                        irALogin = { pantallaActual = "Login" }
                    )
                    "Inicio" -> PantallaInicio(
                        alAgregarMedicamento = { pantallaActual = "NuevoMedicamento" },
                        alCerrarSesion = { pantallaActual = "Login" }
                    )
                    "NuevoMedicamento" -> PantallaNuevoMedicamento(
                        alGuardar = { pantallaActual = "Inicio" },
                        alVolver = { pantallaActual = "Inicio" }
                    )
                }
            }
        }
    }
}
