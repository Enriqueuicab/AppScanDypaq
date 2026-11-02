package com.example.ultimamilla.propuesta.Models.RecolectaCip

import kotlinx.serialization.Serializable

@Serializable
data class RecolectaCipRequest(
    val FolioEmbarqueR: String,
    val FolioRecolecta: String,
    val latitud: String,
    val longitud: String,
    val usuariocaptura: String,
    val cip: String
)