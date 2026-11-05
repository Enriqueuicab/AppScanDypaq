package com.example.ultimamilla.propuesta.Models.Consulta

import kotlinx.serialization.Serializable

@Serializable
data class ConsultaRequest(
    val FolioRecolecta: String
)