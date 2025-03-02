package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val books: List<BookResponseDTO>?,
    val avatarUrl: String?,
    val role: Int
)

enum class UserRole {
    ADMIN, CLIENT, INTERNAL
}

@Serializable
data class Book(
    val id: String,
    val createdAt: String,
    val start: String,
    val end: String,
    val userId: String,
    val user: String,
    val description: String,
    val status: Int,
    val zoneName: String,
)
