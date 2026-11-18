package com.example.ultimamilla.propuesta.viewModel.RecolectaCipViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.RecolectaEliminarCip.RecolectaEliminarCipRequest
import com.example.ultimamilla.propuesta.Models.RecolectaEliminarCip.RecolectaEliminarCipResponse
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch

class RecolectaEliminaCipViewModel: ViewModel() {
    private val _recolectaEliminarCip = MutableLiveData<RecolectaEliminarCipResponse>()
    val recolectaEliminarCip: LiveData<RecolectaEliminarCipResponse?> get() = _recolectaEliminarCip

    fun obtenerRecolectaEliminarCip(
        FolioRecolecta: String,
        usuariocancela: String,
        cip: String,
        context: Context
    ){
        try{
            viewModelScope.launch{
                val server = ServerShared(context).getServerURL()
                val api = RetrofitClient.create(server, context)
                val request = RecolectaEliminarCipRequest(
                    FolioRecolecta,
                    usuariocancela,
                    cip
                )
                val response = api.obtenerRecolectaEliminarCip(request)

                if(response.return_message == "OK"){
                    _recolectaEliminarCip.postValue(response)
                    Toast.makeText(
                        context,
                        "${response.result.firstOrNull()?.firstOrNull()?.Mensaje}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    Toast.makeText(
                        context,
                        "${response.return_message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

         }
        }
        catch (e: Exception){
            Log.e("RecolectaEliminaCipViewModel", "Error al obtener la recolecta", e)
            Toast.makeText(
                context,
                "Error al obtener la recolecta",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}