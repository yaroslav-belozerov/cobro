package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class TokenDto(
    val token: String
)