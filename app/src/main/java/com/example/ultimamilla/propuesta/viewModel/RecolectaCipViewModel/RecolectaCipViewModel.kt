package com.example.ultimamilla.propuesta.viewModel.RecolectaCipViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.RecolectaCip.RecolectaCipRequest
import com.example.ultimamilla.propuesta.Models.RecolectaCip.RecolectaCipResponse
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RecolectaCipViewModel(): ViewModel(){
    private val _recolectaCip = MutableLiveData<RecolectaCipResponse>()
    val recolectaCip: LiveData<RecolectaCipResponse?>get() = _recolectaCip

    fun obtenerCipRecolecta(
        FolioEmbarqueR: String,
        FolioRecolecta: String,
        latitud: String,
        longitud: String,
        usuariocaptura: String,
        cip: String,
        context: Context
    ){
        viewModelScope.launch {
            try{
                val server = ServerShared(context).getServerURL()
                val api = RetrofitClient.create(server, context)

                val request = RecolectaCipRequest(
                    FolioEmbarqueR,
                    FolioRecolecta,
                    latitud,
                    longitud,
                    usuariocaptura,
                    cip
                )

                Log.d("CIP_REQUEST", "FolioEmbarqueR = '${request.FolioEmbarqueR}'")
                Log.d("CIP_REQUEST", "FolioRecolecta = '${request.FolioRecolecta}'")
                Log.d("CIP_REQUEST", "Latitud = '${request.latitud}'")
                Log.d("CIP_REQUEST", "Longitud = '${request.longitud}'")
                Log.d("CIP_REQUEST", "Usuario = '${request.usuariocaptura}'")
                Log.d("CIP_REQUEST", "CIP = '${request.cip}'")

                val response = api.obtenerCipRecolecta(request)
                val mensaje = response.result.firstOrNull()?.resultado?.firstOrNull()?.Mensaje

                if(response.return_message == "OK"){
                    Log.d("RecolectaCipViewModel", "Respuesta: ${response.result}")
                    _recolectaCip.postValue(response)
                    Toast.makeText(
                        context,
                        "${mensaje}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    Log.d("RecolectaCipViewModel", "Error: ${response.errors}")
                    Toast.makeText(
                        context,
                        "${response.return_message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } catch (e: HttpException) {
            val errorServidor = e.response()
                ?.errorBody()
                ?.string()

            Log.e(
                "CIP_DEBUG_3",
                """
        ERROR HTTP
        Código: ${e.code()}
        Mensaje: ${e.message()}
        Respuesta del servidor: $errorServidor
        """.trimIndent(),
                e
            )

            Toast.makeText(
                context,
                "Error HTTP ${e.code()}: ${errorServidor ?: e.message()}",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Log.e(
                "CIP_DEBUG_3",
                "Error inesperado: ${e.message}",
                e
            )
        }
        }
    }
}