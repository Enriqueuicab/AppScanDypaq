package com.example.ultimamilla.propuesta.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultimamilla.propuesta.Models.Principal.PrincipalRequest
import com.example.ultimamilla.propuesta.Models.Principal.PrincipalResponse
import com.example.ultimamilla.propuesta.Shared.FolioShared
import com.example.ultimamilla.propuesta.Shared.ServerShared
import com.example.ultimamilla.propuesta.viewModel.Network.RetrofitClient
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.HttpException

class PrincipalViewModel : ViewModel() {

    private val _principalResponse =
        MutableLiveData<PrincipalResponse?>()

    val principalResponse: LiveData<PrincipalResponse?>
        get() = _principalResponse

    fun obtenerRuta(
        folio: String,
        context: Context
    ) {
        viewModelScope.launch {

            try {

                val server = ServerShared(context).getServerURL()

                val api = RetrofitClient.create(
                    server,
                    context
                )

                val request = PrincipalRequest(
                    folioembarquer = folio
                )
                val response = api.obtenerPrincipal(request)

                val body = response.result.viaje
                val body2 = response.result.entregas
                val body3 = response.result.recolectas

                Log.d(
                    "PrincipalViewModel",
                    "BODY PRINCIPAL: $body + $body2 + $body3"
                )

                _principalResponse.postValue(response)

                /*
                 * Solamente guardamos el folio si
                 * el servidor lo aceptó correctamente.
                 */
                if (response.return_message == "OK") {

                    FolioShared(context).saveFolio(folio)

                    Log.d(
                        "PrincipalViewModel",
                        "Folio guardado: $folio"
                    )
                    Toast.makeText(
                        context,
                        "Folio Encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    Toast.makeText(
                        context,
                        "Folio Invalido",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: HttpException) {

                Log.e(
                    "UltimaMillaViewModel",
                    "HTTP ${e.code()}"
                )

                Log.e(
                    "UltimaMillaViewModel",
                    "Error: ${e.response()?.errorBody()?.string()}"
                )
                Toast.makeText(
                    context,
                    "Folio Invalido",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {

                Log.e(
                    "UltimaMillaViewModel",
                    "Error obteniendo ruta",
                    e
                )
            }
        }
    }
}