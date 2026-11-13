package com.example.ultimamilla.propuesta.Models.documentar

import kotlinx.serialization.Serializable

@Serializable
data class DocumentoResponse(
    val return_code: Int,
    val return_message: String,
    val result: List<List<DocumentoResult>>,
    val errors: String
)

@Serializable
data class DocumentoResult(
    val id: Int,
    val WP_IdControl: Int,
    val Column1: Int,
    val Existente: String,
    val ErrorMsg: String
)