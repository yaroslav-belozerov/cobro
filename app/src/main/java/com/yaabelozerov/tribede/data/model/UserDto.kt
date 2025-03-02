package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val city: String?,
    val books: List<Book>?,
    val avatarUrl: String,
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
    val roomName: String,
    val seat: String?
)
