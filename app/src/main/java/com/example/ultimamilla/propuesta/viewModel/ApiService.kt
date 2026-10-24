package com.example.ultimamilla.propuesta.viewModel

import com.example.ultimamilla.propuesta.Models.Login.LoginRequest
import com.example.ultimamilla.propuesta.Models.Login.LoginResponse
import com.example.ultimamilla.propuesta.Models.Perfil.viajeRequest
import com.example.ultimamilla.propuesta.Models.Perfil.viajeResponse
import com.example.ultimamilla.propuesta.Models.Principal.PrincipalRequest
import com.example.ultimamilla.propuesta.Models.Principal.PrincipalResponse
import com.example.ultimamilla.propuesta.Models.RecolectaCip.RecolectaCipRequest
import com.example.ultimamilla.propuesta.Models.RecolectaCip.RecolectaCipResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v2/rastreomovil_login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/v2/conductor/viajedetalle")
    suspend fun obtenerPrincipal(
        @Body request: PrincipalRequest
    ): PrincipalResponse

    @POST("api/v2/conductor/viajes")
    suspend fun obtenerViajes(
        @Body request: viajeRequest
    ): viajeResponse

    @POST("api/v2/rastreomovil_recolectar_cip")
    suspend fun obtenerCipRecolecta(
        @Body request: RecolectaCipRequest
    ): RecolectaCipResponse

}