package com.example.ultimamilla.propuesta.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import com.example.ultimamilla.propuesta.Models.Consulta.FaltanteResumen
import com.example.ultimamilla.propuesta.viewModel.ConsultaRecolecta.ConsultaRecoViewModel
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

    val locationManager = remember {
        LocationManager(context.applicationContext)
    }

    LaunchedEffect(folioRecolecta) {
        consultaViewModel.obtenerConsulta(
            folioRecolecta = folioRecolecta,
            context = context
        )
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
        }
        Log.e("CIP_DEBUG_1", "CIP recibido: $cip")
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

            // Tarjeta con los datos recibidos desde la pantalla principal
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

            // Sección independiente para los CIPs
            Text(
                text = "Registro de CIPs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = {
                    // PASO 1: El usuario presionó el botón para abrir el escáner.
                    Log.d("RECOLECTA_CIP", "Botón presionado. Abriendo escáner...")

                    // Configuración del lector de códigos de barras.
                    val opciones = ScanOptions().apply {
                        setPrompt("Escanea el código CIP")
                        setBeepEnabled(true)
                        setOrientationLocked(false)

                        // Solo acepta códigos de barras del formato CODE_39.
                        setDesiredBarcodeFormats(ScanOptions.CODE_39)
                    }

                    // PASO 2: Lanzar la actividad del escáner.
                    barcodeLauncher.launch(opciones)

                    Log.d("RECOLECTA_CIP", "Solicitud para abrir el escáner enviada")
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
                    Text("Escanear y enviar CIP")
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
                    text = "La ubicación se obtendrá al enviar.",
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
    var mostrarRegistrados by remember { mutableStateOf(true) }

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
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Registrados")
                        Text(
                            text = cipsRegistrados.size.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Faltantes")
                        Text(
                            text = totalFaltantes.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            HorizontalDivider()

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = mostrarRegistrados,
                    onClick = { mostrarRegistrados = true },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 2
                    )
                ) {
                    Text("Registrados (${cipsRegistrados.size})")
                }

                SegmentedButton(
                    selected = !mostrarRegistrados,
                    onClick = { mostrarRegistrados = false },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 2
                    )
                ) {
                    Text("Faltantes ($totalFaltantes)")
                }
            }

            if (mostrarRegistrados) {

                if (cipsRegistrados.isEmpty()) {
                    Text(
                        text = "Todavía no hay CIPs registrados.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    cipsRegistrados.forEach { cip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = cip,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

            } else {

                if (cipsFaltantes.isEmpty()) {
                    Text(
                        text = "¡No hay CIPs pendientes!",
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    cipsFaltantes.forEach { faltante ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = faltante.CIP,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Text(
                                text = "Guía: ${faltante.Guia}  |  Pieza: ${faltante.Pieza}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
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

