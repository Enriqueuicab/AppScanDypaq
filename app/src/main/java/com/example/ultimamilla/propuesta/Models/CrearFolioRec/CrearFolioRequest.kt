package com.example.ultimamilla.propuesta.Models.CrearFolioRec

import kotlinx.serialization.Serializable

@Serializable
data class CrearFolioRequest(
    val FolioEmbarqueR: String,
    val latitud: String,
    val longitud: String,
    val plaza: String,
    val usuariocaptura: String

)