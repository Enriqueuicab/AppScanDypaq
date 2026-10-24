package com.example.ultimamilla.propuesta.viewModel.LoginViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.Login.LoginRequest
import com.example.ultimamilla.propuesta.Models.Login.LoginResponse
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.Shared.TokenShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val loadingStatus = mutableStateOf("false")
    val tokenAlmacenar = mutableStateOf("")

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    @SuppressLint("SuspiciousIndentation")
    fun loginPostRequest(
        user: String,
        pwd: String,
        context: Context
    ) {

        if (user.isBlank() || pwd.isBlank()) {

            Toast.makeText(
                context,
                "Faltan campos por ingresar",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            viewModelScope.launch {

                try {

                    Toast.makeText(
                        context,
                        "VALIDANDO USUARIO",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 1. Obtener el servidor guardado
                    val server = ServerShared(context).getServerURL()

                    // 2. Crear la instancia de Retrofit
                    val api = RetrofitClient.create(
                        server,
                        context
                    )

                    // 3. Crear el objeto que se enviará
                    val loginRequest = LoginRequest(
                        user = user,
                        pwd = pwd
                    )

                    // 4. Realizar la petición
                    val loginResponse = api.login(loginRequest)

                    // 5. Guardar la respuesta
                    _loginResponse.postValue(loginResponse)

                    // 6. Revisar resultado
                    if (loginResponse.result.mensaje == "VALIDO") {

                        loadingStatus.value = "true"

                        val token = loginResponse.result.token.trim()

                        Log.d(
                            "LoginViewModel",
                            "Token recibido: ${!token.isNullOrBlank()}"
                        )

                        TokenShared(context).guardarToken(token)

                        Log.d(
                            "LoginViewModel",
                            "Token guardado: ${TokenShared(context).obtenerToken() != null}"
                        )

                        tokenAlmacenar.value = token

                        Toast.makeText(
                            context,
                            "USUARIO VÁLIDO",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d(
                            "LoginViewModel",
                            "Login exitoso. Usuario: ${loginResponse.result.usuario}"
                        )

                    } else {

                        loadingStatus.value = "false"

                        Toast.makeText(
                            context,
                            loginResponse.result.mensaje,
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d(
                            "LoginViewModel",
                            "Login rechazado: ${loginResponse.result.mensaje}"
                        )
                    }

                } catch (e: Exception) {

                    loadingStatus.value = "false"

                    Toast.makeText(
                        context,
                        "Error en la solicitud. Intente nuevamente.",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.e(
                        "LoginViewModel",
                        "Excepción: ${e.message}",
                        e
                    )
                }
            }
        }
    }
}