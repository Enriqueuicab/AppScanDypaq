package com.example.ultimamilla.propuesta.Models.Perfil

import kotlinx.serialization.Serializable

@Serializable
data class viajeResponse(
    val return_code: Int,
    val return_message: String,
    val result: List<responseViaje>,
    val errors: String
)

@Serializable
data class responseViaje(
    val Folio: String,
    val Ruta: String,
    val FechaCaptura: String,
    val Operador: String,
    val OperadorNombre: String,
    val Vehiculo: String,
    val VehiculoDesc: String,
    val Column1: String,
    val Column2: Boolean

)