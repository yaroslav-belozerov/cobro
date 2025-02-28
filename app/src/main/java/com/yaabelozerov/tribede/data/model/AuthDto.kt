package com.yaabelozerov.tribede.data.model

data class LoginDto(
    val email: String,
    val password: String
)

data class RegisterDto(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)