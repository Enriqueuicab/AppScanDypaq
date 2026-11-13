package com.example.ultimamilla.propuesta.Models.documentar

import kotlinx.serialization.Serializable

@Serializable
data class DocumentoRequest(
    val FolioRecolecta: String,
    val latitud: String,
    val longitud: String,
    val usuariocaptura: String
)