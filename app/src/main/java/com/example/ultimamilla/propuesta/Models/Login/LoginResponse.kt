package com.example.ultimamilla.propuesta.Models.Login

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val return_code: Int,
    val return_message: String,
    val result: ResponseLogin,
    val errors: String
)

@Serializable
data class ResponseLogin(
    val mensaje: String,
    val oficina: String,
    val usuario: String,
    val empresa: String,
    val token: String,
    val perfil: String
)