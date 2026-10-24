package com.example.ultimamilla.propuesta.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ultimamilla.propuesta.Shared.UserShared
import com.example.ultimamilla.propuesta.viewModel.Perfil.PerfilViewModel

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current

    val usuario = remember {
        UserShared(context).getUser()?.trim().orEmpty()
    }

    val perfilResponse by perfilViewModel.perfilResponse.observeAsState()

    LaunchedEffect(usuario) {
        if (usuario.isNotBlank()) {
            perfilViewModel.obtenerPerfil(usuario, context)
        }
    }

    val viaje = perfilResponse?.result?.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (usuario.isBlank()) {
            MensajePerfil(
                mensaje = "No se encontró un usuario guardado. Inicia sesión nuevamente."
            )
        } else if (perfilResponse == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Consultando información del perfil...")
            }
        } else if (viaje == null) {
            MensajePerfil(
                mensaje = perfilResponse?.return_message
                    ?.takeIf { it.isNotBlank() }
                    ?: "No se encontró información del operador."
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = viaje.OperadorNombre.ifBlank {
                            "Operador"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Usuario: $usuario",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            SeccionPerfil(titulo = "Información del viaje") {
                DatoPerfil("Folio", viaje.Folio)
                DatoPerfil("Ruta", viaje.Ruta)
                DatoPerfil("Fecha de captura", viaje.FechaCaptura)
            }

            SeccionPerfil(titulo = "Información del vehículo") {
                DatoPerfil("Vehículo", viaje.Vehiculo)
                DatoPerfil("Descripción", viaje.VehiculoDesc)
            }

            SeccionPerfil(titulo = "Información del operador") {
                DatoPerfil("Número de operador", viaje.Operador)
            }
        }
    }
}

@Composable
private fun SeccionPerfil(
    titulo: String,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            contenido()
        }
    }
}

@Composable
private fun DatoPerfil(
    titulo: String,
    valor: String
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = valor.ifBlank { "No disponible" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun MensajePerfil(
    mensaje: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = mensaje,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}