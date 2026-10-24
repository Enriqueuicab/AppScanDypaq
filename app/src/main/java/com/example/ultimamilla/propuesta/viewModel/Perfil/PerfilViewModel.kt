package com.example.ultimamilla.propuesta.viewModel.Perfil

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.Perfil.viajeRequest
import com.example.ultimamilla.propuesta.Models.Perfil.viajeResponse
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.Shared.UserShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch

class PerfilViewModel(): ViewModel() {
    private val _perfilResponse = MutableLiveData<viajeResponse>()
    val perfilResponse: LiveData<viajeResponse?>get() = _perfilResponse


    fun obtenerPerfil(
        usuario: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val server = ServerShared(context).getServerURL()
                val api = RetrofitClient.create(server, context)

                val request = viajeRequest(usuario = usuario)

                Log.d("PerfilViewModel", "Usuario enviado: '$usuario'")

                val response = api.obtenerViajes(request)

                Log.d("PerfilViewModel", "Código: ${response.return_code}")
                Log.d("PerfilViewModel", "Mensaje: ${response.return_message}")
                Log.d("PerfilViewModel", "Cantidad de registros: ${response.result.size}")
                Log.d("PerfilViewModel", "Resultado: ${response.result}")

                _perfilResponse.postValue(response)

                if (response.result.isNotEmpty()) {
                    Log.d("PerfilViewModel", "Se encontraron registros del viaje")
                } else {
                    Log.d("PerfilViewModel", "La API devolvió una lista vacía")
                }

            } catch (e: Exception) {
                Log.e("PerfilViewModel", "Error al obtener el perfil", e)
            }
        }
    }
}