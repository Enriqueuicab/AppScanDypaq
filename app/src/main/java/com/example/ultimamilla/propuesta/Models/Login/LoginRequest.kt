package com.example.ultimamilla.propuesta.Models.Login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val user: String,
    val pwd: String
)