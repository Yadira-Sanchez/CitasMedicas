package com.example.citasmedicas.ui.medicamentos

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DialogoConfirmarEliminacion(
    nombreMedicamento: String,
    alConfirmar: () -> Unit,
    alDescartar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alDescartar,
        title = {
            Text(
                text = "Eliminar medicamento",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar $nombreMedicamento? Esta acción borrará el registro de tu base de datos y no se podrá deshacer.",
                fontFamily = FontFamily.Default
            )
        },
        confirmButton = {
            Button(
                onClick = alConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar", fontFamily = FontFamily.Default)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = alDescartar) {
                Text("Cancelar", fontFamily = FontFamily.Default)
            }
        }
    )
}
