
package com.example.ultimamilla.propuesta.Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ultimamilla.propuesta.Shared.OficinaShared
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.Shared.UserShared
import com.example.ultimamilla.propuesta.viewModel.LoginViewModel.LoginViewModel

private val AzulPrincipal = Color(0xFF1769AA)
private val FondoLogin = Color(0xFFF3F6FA)
private val TextoPrincipal = Color(0xFF183153)
private val TextoSecundario = Color(0xFF6B7C93)

@Composable
fun LoginScreen(
    onNextButtonClicked: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val userShared = remember {
        UserShared(context)
    }

    val oficinaStorage = remember {
        OficinaShared(context)
    }

    val serverShared = remember {
        ServerShared(context)
    }

    var username by rememberSaveable {
        mutableStateOf(userShared.getUser())
    }

    var passwd by rememberSaveable {
        mutableStateOf("")
    }

    var oficina by remember {
        mutableStateOf(oficinaStorage.getOficina())
    }

    var serverUrl by rememberSaveable {
        mutableStateOf(serverShared.getServerURL())
    }

    var isServerEnabled by rememberSaveable {
        mutableStateOf(false)
    }

    var showServerChangeDialog by remember {
        mutableStateOf(false)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val loginResponse by viewModel.loginResponse.observeAsState()
    val loadingStatus = viewModel.loadingStatus.value

    LaunchedEffect(Unit) {
        if (serverUrl.isBlank()) {
            val defaultServerURL =
                "https://rastreomovil-sandbox.dypaq.com/"

            serverShared.saveServerURL(defaultServerURL)
            serverUrl = defaultServerURL

            Log.d(
                "LoginScreen",
                "URL por defecto guardada: $defaultServerURL"
            )
        }

        oficina = oficinaStorage.getOficina()

        Log.d(
            "LoginScreen",
            "Usuario recuperado: $username"
        )

        Log.d(
            "LoginScreen",
            "Oficina recuperada: $oficina"
        )
    }

    // Procesar la respuesta del login fuera de la composición.
    LaunchedEffect(loginResponse, loadingStatus) {
        if (loadingStatus == "true" && loginResponse != null) {
            userShared.saveUser(username)

            oficina = oficinaStorage.getOficina()

            Log.d(
                "LoginScreen",
                "Usuario guardado: $username"
            )

            Log.d(
                "LoginScreen",
                "Oficina almacenada: $oficina"
            )

            username = ""
            passwd = ""
            isServerEnabled = false

            viewModel.loadingStatus.value = "false"

            onNextButtonClicked()
        }
    }

    if (showServerChangeDialog) {
        AlertDialog(
            onDismissRequest = {
                showServerChangeDialog = false
            },
            title = {
                Text("Cambiar servidor")
            },
            text = {
                Text(
                    "¿Quieres habilitar la edición de la URL del servidor?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showServerChangeDialog = false
                        isServerEnabled = true
                    }
                ) {
                    Text("Sí, cambiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showServerChangeDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    LoginCompact(
        modifier = modifier,
        username = username,
        passwd = passwd,
        serverUrl = serverUrl,
        isServerEnabled = isServerEnabled,
        keyboardController = keyboardController,
        onUsernameChange = {
            username = it
        },
        onPasswordChange = {
            passwd = it
        },
        onServerUrlChange = {
            serverUrl = it
        },
        onLogin = {
            keyboardController?.hide()

            viewModel.loginPostRequest(
                username.trim(),
                passwd,
                context
            )
        },
        onServerLongPress = {
            showServerChangeDialog = true
        },
        onServerSave = {
            val url = serverUrl.trim()

            if (url.isNotBlank()) {
                serverShared.saveServerURL(url)
                serverUrl = url
                isServerEnabled = false

                Toast.makeText(
                    context,
                    "Servidor actualizado",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Ingresa una URL válida",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
}

@Composable
fun LoginCompact(
    username: String,
    passwd: String,
    serverUrl: String,
    isServerEnabled: Boolean,
    keyboardController: SoftwareKeyboardController?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onServerUrlChange: (String) -> Unit,
    onLogin: () -> Unit,
    onServerLongPress: () -> Unit,
    onServerSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoLogin)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 24.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Encabezado
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(AzulPrincipal)
                .padding(
                    horizontal = 24.dp,
                    vertical = 17.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "UM",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Última Milla",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Gestión de entregas y recolecciones",
            fontSize = 13.sp,
            color = TextoSecundario
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Tarjeta de acceso
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Bienvenido",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Text(
                    text = "Ingresa tus credenciales para continuar.",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Usuario")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrincipal,
                        focusedLabelColor = AzulPrincipal,
                        cursorColor = AzulPrincipal
                    )
                )

                OutlinedTextField(
                    value = passwd,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Contraseña")
                    },
                    singleLine = true,
                    visualTransformation =
                        PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onLogin()
                            keyboardController?.hide()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrincipal,
                        focusedLabelColor = AzulPrincipal,
                        cursorColor = AzulPrincipal
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Button(
                    onClick = {
                        onLogin()
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrincipal
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "→",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Configuración secundaria del servidor
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onServerLongPress()
                        }
                    )
                },
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFE7EDF5)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Configuración del servidor",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isServerEnabled) {
                        "Modifica la dirección y guarda los cambios."
                    } else {
                        "Mantén pulsado aquí para cambiar el servidor."
                    },
                    fontSize = 12.sp,
                    color = TextoSecundario
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = onServerUrlChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("URL del servidor")
                    },
                    enabled = isServerEnabled,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrincipal,
                        focusedLabelColor = AzulPrincipal
                    )
                )

                if (isServerEnabled) {
                    TextButton(
                        onClick = onServerSave,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Guardar servidor")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Última Milla · Operaciones",
            fontSize = 12.sp,
            color = TextoSecundario
        )
    }
}
