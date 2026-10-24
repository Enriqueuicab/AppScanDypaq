package com.example.ultimamilla.propuesta.Screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ultimamilla.propuesta.Models.Principal.PrincipalResponse
import com.example.ultimamilla.propuesta.Models.Principal.Recolecta

@Composable
fun RecolectaScreen(
    principalResponse: PrincipalResponse?,
    modifier: Modifier = Modifier,
    onRegistrarCip: (Recolecta) -> Unit
) {
    val viaje = principalResponse
        ?.result
        ?.viaje
        ?.firstOrNull()

    val recolectas = principalResponse
        ?.result
        ?.recolectas
        .orEmpty()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (viaje != null) {
            ViajeHeader(viaje = viaje)
        }

        RecolectasScreen(
            recolectas = recolectas,
            onRegistrarCip = onRegistrarCip,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RecolectasScreen(
    recolectas: List<Recolecta>,
    modifier: Modifier = Modifier,
    onRegistrarCip: (Recolecta) -> Unit
) {
    var pestañaSeleccionada by remember {
        mutableIntStateOf(0)
    }

    val titulos = listOf("Pendientes", "Recolectadas")

    val recolectasFiltradas = when (pestañaSeleccionada) {
        0 -> recolectas.filter { it.Estatus.trim() == "A" }
        1 -> recolectas.filter { it.Estatus.trim() == "T" }
        else -> emptyList()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScrollableTabRow(
            selectedTabIndex = pestañaSeleccionada
        ) {
            titulos.forEachIndexed { index, titulo ->
                Tab(
                    selected = pestañaSeleccionada == index,
                    onClick = {
                        pestañaSeleccionada = index
                    },
                    text = {
                        Text(titulo)
                    }
                )
            }
        }

        ListaRecolectas(
            recolectas = recolectasFiltradas,
            modifier = Modifier.weight(1f),
            onRegistrarCip = onRegistrarCip,
        )
    }
}

@Composable
fun ListaRecolectas(
    recolectas: List<Recolecta>,
    modifier: Modifier = Modifier,
    onRegistrarCip: (Recolecta) -> Unit
) {
    var recolectaSeleccionada by remember {
        mutableStateOf<Recolecta?>(null)
    }

    if (recolectas.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay recolectas en esta categoría.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = recolectas,
                key = { it.FolioRecolecta }
            ) { recolecta ->
                RecolectaCard(
                    recolecta = recolecta,
                    onClick = {
                        recolectaSeleccionada = recolecta
                    }
                )
            }
        }
    }

    recolectaSeleccionada?.let { recolecta ->
        DetalleRecolectaModal(
            recolecta = recolecta,
            onDismiss = {
                recolectaSeleccionada = null
            },
            onRegistrarCip = onRegistrarCip
        )
    }
}

@Composable
fun RecolectaCard(
    recolecta: Recolecta,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recolecta.FolioRecolecta.trim(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = when (recolecta.Estatus.trim()) {
                        "A" -> "Pendiente"
                        "T" -> "Recolectada"
                        else -> recolecta.Estatus
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = recolecta.RazonSocial.trim(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = recolecta.Direccion.trim(),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${recolecta.Piezas} pieza(s)",
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = recolecta.TipoPaquete.trim(),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${recolecta.Peso} kg • ${recolecta.Medidas.trim()}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecolectaModal(
    recolecta: Recolecta,
    onDismiss: () -> Unit,
    onRegistrarCip: (Recolecta) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                )
        ) {
            Text(
                text = recolecta.FolioRecolecta.trim(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = recolecta.RazonSocial.trim(),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Identificación",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega("Folio de ruta", recolecta.FolioRuta)
            DatoEntrega("Folio de recolecta", recolecta.FolioRecolecta)
            DatoEntrega(
                "Estatus",
                when (recolecta.Estatus.trim()) {
                    "A" -> "Asignado / Pendiente"
                    "T" -> "Terminado / Recolectado"
                    else -> recolecta.Estatus
                }
            )
            DatoEntrega("Realizado", recolecta.Realizado)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cliente",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega("Cliente", recolecta.Cliente)
            DatoEntrega("Razón social", recolecta.RazonSocial)
            DatoEntrega("Contacto", recolecta.Contacto)
            DatoEntrega("Teléfono", recolecta.Telefono)
            DatoEntrega("Dirección", recolecta.Direccion)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Paquete",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega("Piezas", recolecta.Piezas.toString())
            DatoEntrega("Tipo de paquete", recolecta.TipoPaquete)
            DatoEntrega("Contenido", recolecta.Contenido.toString())
            DatoEntrega("Descripción del contenido", recolecta.ContenidoDesc)
            DatoEntrega("Peso", "${recolecta.Peso} kg")
            DatoEntrega("Medidas", recolecta.Medidas)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Horario de recolección",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega("Tipo de horario", recolecta.TipoHorarioRecolecta)
            DatoEntrega("Horario inicial", recolecta.HorarioInicial)
            DatoEntrega("Horario final", recolecta.HorarioFinal)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ubicación",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega("Latitud", recolecta.Latitud)
            DatoEntrega("Longitud", recolecta.Longitud)

            Spacer(modifier = Modifier.height(12.dp))

            if (recolecta.Estatus.trim() == "A") {
                Button(
                    onClick = {
                        onRegistrarCip(recolecta)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar recolecta CIP")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar")
            }
        }
    }
}