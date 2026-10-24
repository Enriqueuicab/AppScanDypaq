package com.example.ultimamilla.propuesta.Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.ultimamilla.propuesta.Models.Principal.Entrega
import com.example.ultimamilla.propuesta.Models.Principal.PrincipalResponse
import com.example.ultimamilla.propuesta.Models.Principal.Recolecta
import com.example.ultimamilla.propuesta.Models.Principal.Viaje
import com.example.ultimamilla.propuesta.Shared.TokenShared
import com.example.ultimamilla.propuesta.Shared.UserShared
import com.example.ultimamilla.propuesta.Shared.OficinaShared
import com.example.ultimamilla.propuesta.viewModel.PrincipalViewModel

import kotlinx.coroutines.launch
@Composable
fun UltimaMillaApp(
    viewModel: PrincipalViewModel = viewModel(),
    onLogout: () -> Unit
) {

    val context = LocalContext.current

    var mostrarModalFolio by remember {
        mutableStateOf(true)
    }

    var folio by remember {
        mutableStateOf("")
    }

    val principalResponse by
    viewModel.principalResponse.observeAsState()

    /*
     * Cuando recibimos una respuesta del servidor,
     * podemos continuar con la pantalla principal.
     */
    LaunchedEffect(principalResponse) {
        if (principalResponse == null) {
            return@LaunchedEffect
        }
        if (principalResponse?.errors != "ERROR Folio de ruta no existe") {
            mostrarModalFolio = false
        }
    }



    if (mostrarModalFolio) {

        FolioDialog(

            folio = folio,

            onFolioChange = {
                folio = it
            },

            onContinuar = {
                if (folio.isNotBlank()) {
                    viewModel.obtenerRuta(
                        folio = folio,
                        context = context
                    )
                }
            }
        )
    }


    UltimaMillaScreen(
        mostrarContenido = !mostrarModalFolio,
        principalResponse = principalResponse,
        onLogout = onLogout
    )
}

@Composable
fun FolioDialog(
    folio: String,
    onFolioChange: (String) -> Unit,
    onContinuar: () -> Unit
) {

    AlertDialog(
        onDismissRequest = {
            // No permitir cerrar sin ingresar folio
        },

        title = {
            Text(
                text = "Ingresar folio de ruta",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            OutlinedTextField(
                value = folio,
                onValueChange = onFolioChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Folio de ruta")
                },
                singleLine = true
            )
        },

        confirmButton = {

            Button(
                onClick = onContinuar,
                enabled = folio.isNotBlank()
            ) {
                Text("Continuar")
            }
        }
    )
}

@Composable
fun UltimaMillaScreen(
    mostrarContenido: Boolean,
    principalResponse: PrincipalResponse?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    var pantallaActual by remember {
        mutableIntStateOf(0)
    }

    var recolectaParaCip by remember {
        mutableStateOf<Recolecta?>(null)
    }

    val recolectaSeleccionada = recolectaParaCip

    if (recolectaSeleccionada != null) {
        RecolectaCipScreen(
            folioEmbarqueInicial = recolectaSeleccionada.FolioRuta.trim(),
            folioRecolectaInicial = recolectaSeleccionada.FolioRecolecta.trim(),
            plazaInicial = OficinaShared(context).getOficina(),
            usuarioInicial = UserShared(context).getUser(),
            onRegresar = {
                recolectaParaCip = null
            }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                pantallaActual = pantallaActual,
                onSeleccionar = {
                    pantallaActual = it

                    scope.launch {
                        drawerState.close()
                    }
                },
                onLogout = {
                    TokenShared(context).eliminarToken()
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    pantallaActual = pantallaActual,
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            bottomBar = {
                BarraNavegacion(
                    seleccion = pantallaActual,
                    onSeleccion = {
                        pantallaActual = it
                    }
                )
            }
        ) { paddingValues ->

            when (pantallaActual) {

                0 -> {
                    UltimaMillaContenido(
                        principalResponse = principalResponse,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                1 -> {
                    RecolectaScreen(
                        modifier = Modifier.padding(paddingValues),
                        principalResponse = principalResponse,
                        onRegistrarCip = {
                                recolecta ->
                            recolectaParaCip = recolecta
                        }
                    )
                }

                2 -> {
                    PerfilScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun UltimaMillaContenido(
    principalResponse: PrincipalResponse?,
    modifier: Modifier = Modifier
) {
    val viaje = principalResponse
        ?.result
        ?.viaje
        ?.firstOrNull()

    val entregas = principalResponse
        ?.result
        ?.entregas
        .orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        if (viaje != null) {
            ViajeHeader(
                viaje = viaje
            )
        }

        EntregasScreen(
            entregas = entregas,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BarraSuperior(
    pantallaActual: Int,
    onMenuClick: () -> Unit
) {

    val titulo = when (pantallaActual) {
        0 -> "Última Milla"
        1 -> "Recolecta"
        2 -> "Dashboard"
        else -> "Última Milla"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top=30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onMenuClick
        ) {

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Abrir menú",
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = titulo,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ViajeHeader(
    viaje: Viaje
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = viaje.Folio
                    ?.trim()
                    .orEmpty(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = viaje.Ruta
                    ?.trim()
                    .orEmpty(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = viaje.OperadorNombre
                    ?.trim()
                    .orEmpty(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = viaje.VehiculoDesc
                    ?.trim()
                    .orEmpty(),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = viaje.FechaCaptura
                    ?.trim()
                    .orEmpty(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EntregasScreen(
    entregas: List<Entrega>,
    modifier: Modifier = Modifier
) {
    var pestañaSeleccionada by remember {
        mutableIntStateOf(0)
    }

    val entregasFiltradas = when (pestañaSeleccionada) {

        0 -> entregas.filter {
            it.EstaEntregada == 0
        }

        1 -> entregas.filter {
            it.EstaEntregada == 1
        }

        2 -> entregas.filter {
            it.PorCobrar == 1
        }

        else -> emptyList()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        ScrollableTabRow(
            selectedTabIndex = pestañaSeleccionada
        ) {

            listOf(
                "Pendientes",
                "Entregados",
                "FxC"
            ).forEachIndexed { index, titulo ->

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

        ListaEntregas(
            entregas = entregasFiltradas,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ListaEntregas(
    entregas: List<Entrega>,
    modifier: Modifier = Modifier
) {
    var entregaSeleccionada by remember {
        mutableStateOf<Entrega?>(null)
    }

    if (entregas.isEmpty()) {

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay entregas en esta categoría.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    } else {

        LazyColumn(
            modifier = modifier,

            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp
            )
        ) {

            items(entregas) { entrega ->

                EntregaCard(
                    entrega = entrega,
                    onClick = {
                        entregaSeleccionada = entrega
                    }
                )
            }
        }
    }

    entregaSeleccionada?.let { entrega ->

        DetalleEntregaModal(
            entrega = entrega,
            onDismiss = {
                entregaSeleccionada = null
            }
        )
    }
}

@Composable
fun EntregaCard(
    entrega: Entrega,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                onClick()
            }
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = entrega.Guia
                        ?.trim()
                        .orEmpty(),

                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.weight(1f)
                )

                if (entrega.EstaEntregada == 1) {

                    Text(
                        text = "Entregada",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = entrega.RazonSocialD
                    ?.trim()
                    .orEmpty(),

                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = entrega.DireccionD
                    ?.trim()
                    .orEmpty(),

                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,

                maxLines = 2
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "${entrega.Cantidad} pieza(s)",
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = entrega.TipoPaquete
                        ?.trim()
                        .orEmpty(),

                    fontSize = 13.sp
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "${entrega.peso} kg  •  " +
                            "${entrega.largo} × " +
                            "${entrega.ancho} × " +
                            "${entrega.alto} cm",

                fontSize = 12.sp,

                color = MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEntregaModal(
    entrega: Entrega,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                )
        ) {

            Text(
                text = entrega.Guia
                    ?.trim()
                    .orEmpty(),

                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = entrega.RazonSocialD
                    ?.trim()
                    .orEmpty(),

                fontSize = 15.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Identificación",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega(
                "Guía",
                entrega.Guia
            )

            DatoEntrega(
                "CIP",
                entrega.CIP
            )

            DatoEntrega(
                "Partida",
                entrega.Partida.toString()
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Paquete",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega(
                "Cantidad",
                entrega.Cantidad.toString()
            )

            DatoEntrega(
                "Tipo de paquete",
                entrega.TipoPaquete
            )

            DatoEntrega(
                "Peso",
                "${entrega.peso} kg"
            )

            DatoEntrega(
                "Largo",
                "${entrega.largo} cm"
            )

            DatoEntrega(
                "Ancho",
                "${entrega.ancho} cm"
            )

            DatoEntrega(
                "Alto",
                "${entrega.alto} cm"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Destino",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega(
                "Razón social",
                entrega.RazonSocialD
            )

            DatoEntrega(
                "Dirección",
                entrega.DireccionD
            )

            DatoEntrega(
                "Teléfono",
                entrega.TelefonoD
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Información adicional",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            DatoEntrega(
                "Facturar a",
                entrega.FacturarA
            )

            DatoEntrega(
                "Referencia de envío",
                entrega.ReferenciaEnvio
            )

            DatoEntrega(
                "Por cobrar",
                if (entrega.PorCobrar == 1) {
                    "Sí"
                } else {
                    "No"
                }
            )

            DatoEntrega(
                "Tiene acuse",
                if (entrega.TieneAcuse == 1) {
                    "Sí"
                } else {
                    "No"
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
fun DatoEntrega(
    titulo: String,
    valor: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {

        Text(
            text = titulo,
            fontSize = 12.sp,
            color = MaterialTheme
                .colorScheme
                .onSurfaceVariant
        )

        Text(
            text = valor
                ?.trim()
                .orEmpty(),

            fontSize = 15.sp
        )
    }
}

// Barras de navegacion
@Composable
fun BarraNavegacion(
    seleccion: Int,
    onSeleccion: (Int) -> Unit,
) {

    NavigationBar {

        NavigationBarItem(

            selected = seleccion == 0,

            onClick = {
                onSeleccion(0)
            },

            icon = {
                Icon(
                    imageVector = Icons.Default.DeliveryDining,
                    contentDescription = "Entrega"
                )
            },

            label = {
                Text("Entregas")
            }
        )

        NavigationBarItem(

            selected = seleccion == 1,

            onClick = {
                onSeleccion(1)
            },

            icon = {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "Recolecta"
                )
            },

            label = {
                Text("Recolectas")
            }
        )

        NavigationBarItem(

            selected = seleccion == 2,

            onClick = {
                onSeleccion(2)
            },

            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Dashboard"
                )
            },

            label = {
                Text("Dashboard")
            }
        )
    }
}

@Composable
fun MenuLateral(
    pantallaActual: Int,
    onSeleccionar: (Int) -> Unit,
    onLogout: () -> Unit
) {

    ModalDrawerSheet {

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Última Milla",
            modifier = Modifier.padding(
                horizontal = 24.dp
            ),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        NavigationDrawerItem(

            label = {
                Text("Entregas")
            },

            selected = pantallaActual == 0,

            onClick = {
                onSeleccionar(0)
            },

            icon = {
                Icon(
                    Icons.Default.DeliveryDining,
                    contentDescription = null
                )
            }
        )

        NavigationDrawerItem(

            label = {
                Text("Recolecion")
            },

            selected = pantallaActual == 1,

            onClick = {
                onSeleccionar(1)
            },

            icon = {
                Icon(
                    Icons.Default.LocalShipping,
                    contentDescription = null
                )
            }
        )

        NavigationDrawerItem(

            label = {
                Text("Dashboard")
            },

            selected = pantallaActual == 2,

            onClick = {
                onSeleccionar(2)
            },

            icon = {
                Icon(
                    Icons.Default.Dashboard,
                    contentDescription = null
                )
            }
        )

        NavigationDrawerItem(
            label = {
                Text("Salir")
            },

            selected = false,

            onClick = {
                onLogout()
            },

            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null
                )
            }
        )

    }
}