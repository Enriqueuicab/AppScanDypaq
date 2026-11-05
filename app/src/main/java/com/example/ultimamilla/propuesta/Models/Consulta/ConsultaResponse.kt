package com.example.ultimamilla.propuesta.Models.Consulta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultaResponse(
    @SerialName("return_code")
    val returnCode: Int,

    @SerialName("return_message")
    val returnMessage: String,

    val result: ConsultaResult,

    val errors: String
)

@Serializable
data class ConsultaResult(
    val recolecta: Recolecta,
    val conteoRecolectados: Int,
    val listaSimpleCips: List<String>,
    val detallePorGuia: List<DetallePorGuia>,
    val faltantesResumen: List<FaltanteResumen>
)

@Serializable
data class Recolecta(
    val Folio: String,
    val Plaza: Int,
    val FechaProgramada: String,
    val Ruta: String,
    val Cliente: String,
    val Piezas: Int,
    val TipoPaquete: String,
    val TipoHorarioRecolecta: String,
    val HorarioInicial: String,
    val HorarioFinal: String,
    val TipoRecoleccion: String,
    val Latitud: String,
    val Longitud: String,
    val ObservacionesDeRecolecta: String,
    val Estatus: String
)

@Serializable
data class DetallePorGuia(
    val Folio: String,
    val Guia: String,
    val CIP: String,
    val Pieza: Int
)

@Serializable
data class FaltanteResumen(
    val Guia: String,
    val CIP: String,
    val Pieza: Int
)