package com.example.ultimamilla.propuesta.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ultimamilla.propuesta.Location.LocationManager
import com.example.ultimamilla.propuesta.viewModel.RecolectaCipViewModel.RecolectaCipViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ultimamilla.propuesta.Models.Consulta.FaltanteResumen
import com.example.ultimamilla.propuesta.Models.RecolectaCip.RecolectaCipResponse
import com.example.ultimamilla.propuesta.Models.RecolectaEliminarCip.RecolectaEliminarCipResponse
import com.example.ultimamilla.propuesta.viewModel.ConsultaRecolecta.ConsultaRecoViewModel
import com.example.ultimamilla.propuesta.viewModel.RecolectaCipViewModel.RecolectaEliminaCipViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecolectaCipScreen(
    folioEmbarqueInicial: String,
    folioRecolectaInicial: String,
    plazaInicial: String,
    usuarioInicial: String,
    viewModel: RecolectaCipViewModel = viewModel(),
    consultaViewModel: ConsultaRecoViewModel = viewModel(),
    eliminacionViewModel: RecolectaEliminaCipViewModel = viewModel(),
    onRegresar: () -> Unit = {}
) {
    val context = LocalContext.current
    val consulta by consultaViewModel.consultaReco.observeAsState()

    val folioEmbarque = folioEmbarqueInicial
    val folioRecolecta = folioRecolectaInicial
    val plaza = plazaInicial
    val usuario = usuarioInicial

    var cipPendiente by remember {
        mutableStateOf<String?>(null)
    }

    var procesandoCip by remember {
        mutableStateOf(false)
    }

    // Nueva variable: controla el modal de operaciones.
    var mostrarOpcionesCip by remember {
        mutableStateOf(false)
    }

    // Nueva variable: identifica si se agrega o elimina.
    var operacionCip by remember {
        mutableStateOf("AGREGAR")
    }

    val locationManager = remember {
        LocationManager(context.applicationContext)
    }

    LaunchedEffect(folioRecolecta) {
        consultaViewModel.obtenerConsulta(
            folioRecolecta = folioRecolecta,
            context = context
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observerAgregar = Observer<RecolectaCipResponse?> { response ->
            if (response != null) {
                procesandoCip = false

                if (response.return_message == "OK") {
                    consultaViewModel.obtenerConsulta(
                        folioRecolecta = folioRecolecta.trim(),
                        context = context
                    )
                }
            }
        }

        val observerEliminar = Observer<RecolectaEliminarCipResponse?> { response ->
            if (response?.return_message == "OK") {
                procesandoCip = false

                consultaViewModel.obtenerConsulta(
                    folioRecolecta = folioRecolecta.trim(),
                    context = context
                )
            }
        }

        viewModel.recolectaCip.observe(lifecycleOwner, observerAgregar)
        eliminacionViewModel.recolectaEliminarCip.observe(lifecycleOwner, observerEliminar)

        onDispose {
            viewModel.recolectaCip.removeObserver(observerAgregar)
            eliminacionViewModel.recolectaEliminarCip.removeObserver(observerEliminar)
        }
    }

    fun enviarCipConUbicacion(cip: String) {
        procesandoCip = true

        Log.e("CIP_DEBUG_2", "Iniciando obtención de ubicación")

        locationManager.obtenerUbicacion(
            onSuccess = { ubicacion ->

                Log.e(
                    "CIP_DEBUG_2",
                    """
                    Ubicación obtenida:
                    Latitud: ${ubicacion.latitude}
                    Longitud: ${ubicacion.longitude}

                    Datos que se enviarán:
                    FolioEmbarqueR: '${folioEmbarque.trim()}'
                    FolioRecolecta: '${folioRecolecta.trim()}'
                    Plaza: '${plaza.trim()}'
                    Usuario: '${usuario.trim()}'
                    CIP: '$cip'
                    """.trimIndent()
                )

                Log.e("CIP_DEBUG_2", "Invocando al ViewModel ahora")

                viewModel.obtenerCipRecolecta(
                    FolioEmbarqueR = folioEmbarque.trim(),
                    FolioRecolecta = folioRecolecta.trim(),
                    latitud = ubicacion.latitude.toString(),
                    longitud = ubicacion.longitude.toString(),
                    usuariocaptura = usuario.trim(),
                    cip = cip,
                    context = context
                )
            },
            onError = { error ->
                Log.e(
                    "CIP_DEBUG_2",
                    "Falló la ubicación: ${error.message}",
                    error
                )

                procesandoCip = false

                Toast.makeText(
                    context,
                    error.message ?: "No se pudo obtener la ubicación",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    val permisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->

        val concedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        val cip = cipPendiente

        if (concedido && cip != null) {
            enviarCipConUbicacion(cip)
        } else {
            cipPendiente = null

            Toast.makeText(
                context,
                "Se necesita permiso de ubicación para enviar el CIP",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->

        Log.e(
            "CIP_DEBUG_1",
            "CALLBACK EJECUTADO. Contents=${result.contents}"
        )

        val cip = result.contents?.trim()

        if (cip.isNullOrBlank()) {

            Log.e("CIP_DEBUG_1", "El escáner devolvió un CIP vacío o cancelado")

            Toast.makeText(
                context,
                "Escaneo cancelado",
                Toast.LENGTH_SHORT
            ).show()

            return@rememberLauncherForActivityResult

        } else {

            // Diferenciar la operación seleccionada.
            if (operacionCip == "AGREGAR") {

                val tienePermiso =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                if (tienePermiso) {
                    enviarCipConUbicacion(cip)
                } else {
                    cipPendiente = cip

                    permisoUbicacion.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

            } else if (operacionCip == "ELIMINAR") {

                procesandoCip = true

                eliminacionViewModel.obtenerRecolectaEliminarCip(
                    FolioRecolecta = folioRecolecta.trim(),
                    usuariocancela = usuario.trim(),
                    cip = cip,
                    context = context
                )
            }
        }

        Log.e("CIP_DEBUG_1", "CIP recibido: $cip")
    }

    // Modal para seleccionar la operación.
    if (mostrarOpcionesCip) {

        AlertDialog(
            onDismissRequest = {
                mostrarOpcionesCip = false
            },
            title = {
                Text("Operación de CIP")
            },
            text = {
                Text("Selecciona qué deseas hacer con el CIP.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        operacionCip = "AGREGAR"
                        mostrarOpcionesCip = false

                        val opciones = ScanOptions().apply {
                            setPrompt("Escanea el CIP para agregar")
                            setBeepEnabled(true)
                            setOrientationLocked(false)
                            setDesiredBarcodeFormats(ScanOptions.CODE_39)
                        }

                        barcodeLauncher.launch(opciones)
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            operacionCip = "ELIMINAR"
                            mostrarOpcionesCip = false

                            val opciones = ScanOptions().apply {
                                setPrompt("Escanea el CIP para eliminar")
                                setBeepEnabled(true)
                                setOrientationLocked(false)
                                setDesiredBarcodeFormats(ScanOptions.CODE_39)
                            }

                            barcodeLauncher.launch(opciones)
                        }
                    ) {
                        Text(
                            "Eliminar",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    TextButton(
                        onClick = {
                            mostrarOpcionesCip = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar recolecta") },
                navigationIcon = {
                    TextButton(onClick = onRegresar) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Encabezado
            Text(
                text = "Información de la recolecta",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Verifica los datos antes de registrar los CIPs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Tarjeta con los datos de operación
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Datos de operación",
                        style = MaterialTheme.typography.titleMedium
                    )

                    DatoOperacion(
                        titulo = "Folio de embarque",
                        valor = folioEmbarque
                    )

                    DatoOperacion(
                        titulo = "Folio de recolecta",
                        valor = folioRecolecta
                    )

                    DatoOperacion(
                        titulo = "Plaza",
                        valor = plaza
                    )

                    DatoOperacion(
                        titulo = "Usuario de captura",
                        valor = usuario
                    )
                }
            }

            // Sección de CIPs
            Text(
                text = "Registro de CIPs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = {
                    mostrarOpcionesCip = true
                },
                enabled = !procesandoCip,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {

                if (procesandoCip) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(Modifier.width(8.dp))
                    Text("Procesando CIP...")

                } else {

                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear CIP"
                    )

                    Spacer(Modifier.width(8.dp))
                    Text("Escanear CIP")
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "La ubicación se obtendrá al agregar un CIP.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (consulta != null && consulta!!.returnCode == 200) {

                ListadoCips(
                    cipsRegistrados = consulta!!.result.listaSimpleCips,
                    cipsFaltantes = consulta!!.result.faltantesResumen,
                    conteoRecolectados = consulta!!.result.conteoRecolectados
                )

            } else if (consulta == null) {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

            } else {

                Text(
                    text = "No se pudo obtener la información de la recolecta.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Composable
fun ListadoCips(
    cipsRegistrados: List<String>,
    cipsFaltantes: List<FaltanteResumen>,
    conteoRecolectados: Int
) {
    var mostrarRegistrados by remember { mutableStateOf<Boolean?>(null) }

    val totalFaltantes = cipsFaltantes.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen de CIPs",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Contador de registrados
                Card(
                    onClick = { mostrarRegistrados = true },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text("Registrados")

                        Text(
                            text = cipsRegistrados.size.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                // Contador de faltantes
                Card(
                    onClick = { mostrarRegistrados = false },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text("Faltantes")

                        Text(
                            text = totalFaltantes.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }
    }

    // Modal de información
    if (mostrarRegistrados != null) {

        val esRegistrado = mostrarRegistrados == true

        val lista = if (esRegistrado) {
            cipsRegistrados
        } else {
            cipsFaltantes
        }

        AlertDialog(
            onDismissRequest = {
                mostrarRegistrados = null
            }
            ,
            title = {
                Text(
                    text = if (esRegistrado) {
                        "CIPs registrados"
                    } else {
                        "CIPs faltantes"
                    }
                )
            },
            text = {
                if (lista.isEmpty()) {
                    Text(
                        text = if (esRegistrado) {
                            "Todavía no hay CIPs registrados."
                        } else {
                            "¡No hay CIPs pendientes!"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(lista.size) { index ->

                            if (esRegistrado) {
                                val cip = cipsRegistrados[index]

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(Modifier.width(10.dp))

                                    Text(text = cip)
                                }

                            } else {
                                val faltante = cipsFaltantes[index]

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )

                                        Spacer(Modifier.width(10.dp))

                                        Text(text = faltante.CIP)
                                    }

                                    Text(
                                        text = "Guía: ${faltante.Guia} | Pieza: ${faltante.Pieza}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 34.dp, top = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { mostrarRegistrados = null }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}


@Composable
fun DatoOperacion(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = valor.ifBlank { "No disponible" },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

