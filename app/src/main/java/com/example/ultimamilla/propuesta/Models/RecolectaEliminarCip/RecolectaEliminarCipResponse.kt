package com.example.ultimamilla.propuesta.Models.RecolectaEliminarCip

import kotlinx.serialization.Serializable

@Serializable
data class RecolectaEliminarCipResponse(
    val return_code: Int,
    val return_message: String,
    val result: List<List<RecolectaResult>>,
    val errors: String
)

@Serializable
data class RecolectaResult(
    val Error: String,
    val Mensaje: String
)