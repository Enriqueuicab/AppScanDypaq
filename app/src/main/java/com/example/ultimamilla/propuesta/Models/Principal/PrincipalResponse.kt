package com.example.ultimamilla.propuesta.Models.Principal

import kotlinx.serialization.Serializable

@Serializable
data class PrincipalResponse(
    val return_code: Int,
    val return_message: String,
    val result: ResponsePrincipal,
    val errors: String
)

@Serializable
data class ResponsePrincipal(
    val viaje: List<Viaje>,
    val entregas: List<Entrega>?,
    val recolectas: List<Recolecta>?
)

@Serializable
data class Viaje(
    val Folio: String? = null,
    val Ruta: String? = null,
    val FechaCaptura: String? = null,
    val Operador: String? = null,
    val OperadorNombre: String? = null,
    val Vehiculo: String? = null,
    val VehiculoDesc: String? = null,
    val ResponsableEmbarque: String? = null,
    val ResponsableEmbarqueNombre: String? = null,
    val Error: String? = null
)

@Serializable
data class Entrega(
    val FolioRuta: String,
    val Guia: String,
    val CIP: String,
    val EstaEntregada: Int,
    val Cantidad: Int,
    val Partida: Int,
    val TipoPaquete: String,
    val peso: Double,
    val largo: Double,
    val ancho: Double,
    val alto: Double,
    val FacturarA: String,
    val RazonSocialD: String,
    val DireccionD: String,
    val TelefonoD: String,
    val PorCobrar: Int,
    val ReferenciaEnvio: String,
    val TieneAcuse: Int
)

@Serializable
data class Recolecta(
    val FolioRuta: String,
    val FolioRecolecta: String,
    val Estatus: String,
    val Realizado: String? = null,
    val Cliente: String,
    val RazonSocial: String,
    val Direccion: String,
    val Telefono: String,
    val Contacto: String,
    val Latitud: String,
    val Longitud: String,
    val Piezas: Int,
    val TipoPaquete: String,
    val Contenido: Int,
    val ContenidoDesc: String,
    val Peso: Int,
    val Medidas: String,
    val TipoHorarioRecolecta: String,
    val HorarioInicial: String,
    val HorarioFinal: String
)
