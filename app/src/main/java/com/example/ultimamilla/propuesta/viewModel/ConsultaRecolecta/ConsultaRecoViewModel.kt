package com.example.ultimamilla.propuesta.viewModel.ConsultaRecolecta

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.Consulta.ConsultaRequest
import com.example.ultimamilla.propuesta.Models.Consulta.ConsultaResponse
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch

class ConsultaRecoViewModel(): ViewModel(){
    private val _consultaReco = MutableLiveData<ConsultaResponse?>()
    val consultaReco: LiveData<ConsultaResponse?>get() = _consultaReco

    fun obtenerConsulta(
        folioRecolecta: String,
        context: Context
    ){
        viewModelScope.launch{
           try {
               val server = ServerShared(context).getServerURL()
               val api = RetrofitClient.create(server, context)

               val request = ConsultaRequest(FolioRecolecta = folioRecolecta)

               val response = api.obtenerConsulta(request)
               _consultaReco.postValue(response)

               if(response.returnMessage == "OK"){
                   Toast.makeText(
                       context,
                       "Folio de Recolecta Entontrado",
                       Toast.LENGTH_SHORT
                   ).show()
               }else{
                   Toast.makeText(
                       context,
                       "Folio de Recolecta No Encontrado",
                       Toast.LENGTH_SHORT
                   ).show()
               }

           }
           catch (e: Exception){
               Log.e("ConsultaRecoViewModel", "Error al obtener la consulta", e)
           }
        }
    }

}