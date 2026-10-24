package com.example.ultimamilla.propuesta.Models.RecolectaCip

import kotlinx.serialization.Serializable

@Serializable
data class RecolectaCipResponse(
    val return_code: Int,
    val return_message: String,
    val result: List<ResponseRecolecta>,
    val errors: String
)
@Serializable
data class ResponseRecolecta(
    val resultado: List<respuesta>
)
@Serializable
data class respuesta(
    val Error: String,
    val Mensaje: String
)