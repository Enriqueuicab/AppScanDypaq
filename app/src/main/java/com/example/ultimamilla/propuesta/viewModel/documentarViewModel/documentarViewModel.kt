package com.example.ultimamilla.propuesta.viewModel.documentarViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.documentar.DocumentoRequest
import com.example.ultimamilla.propuesta.Models.documentar.DocumentoResponse
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch

class documentarViewModel : ViewModel() {

    private val _documentar = MutableLiveData<DocumentoResponse?>()
    val documentar: LiveData<DocumentoResponse?> get() = _documentar

    fun obtenerDocumento(
        folioRecolecta: String,
        latitud: String,
        longitud: String,
        usuariocaptura: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val server = ServerShared(context).getServerURL()
                val api = RetrofitClient.create(server, context)

                val request = DocumentoRequest(
                    folioRecolecta,
                    latitud,
                    longitud,
                    usuariocaptura
                )

                val response = api.obtenerDocumento(request)
                _documentar.postValue(response)

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