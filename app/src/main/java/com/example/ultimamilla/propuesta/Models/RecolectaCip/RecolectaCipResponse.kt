
package com.example.ultimamilla.propuesta.Models.RecolectaCip

import kotlinx.serialization.Serializable

@Serializable
data class RecolectaCipResponse(
    val return_code: Int,
    val return_message: String,
    val result: List<List<RespuestaRecolecta>>,
    val errors: String
)

@Serializable
data class RespuestaRecolecta(
    val Error: String,
    val Mensaje: String
)
