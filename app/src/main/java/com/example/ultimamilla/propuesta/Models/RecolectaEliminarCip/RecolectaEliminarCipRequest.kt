package com.example.ultimamilla.propuesta.Models.RecolectaEliminarCip

import kotlinx.serialization.Serializable

@Serializable
data class RecolectaEliminarCipRequest(
    val FolioRecolecta: String,
    val usuariocancela: String,
    val cip: String
)