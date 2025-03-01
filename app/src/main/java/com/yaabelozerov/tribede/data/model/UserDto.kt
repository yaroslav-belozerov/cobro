package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val city: String?,
    val books: List<Book>?,
    val role: UserRole
)

enum class UserRole {
    REGULAR,
    INTERNAL,
    ADMIN
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
    val status: BookStatus,
)

enum class BookStatus {
    ACTIVE,
    INACTIVE,
    PENDING
}