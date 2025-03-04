package com.yaabelozerov.tribede.data.model

import com.yaabelozerov.tribede.domain.model.ActionUI
import kotlinx.serialization.Serializable

@Serializable
data class ActionDTO(
    val id: String,
    val text: String,
    val actionNumber: Int?,
    val createdAt: String,
    val status: Int,
    val additionalInfo: String,
    val bookId: String,
    val book: Book1,
)

@Serializable
data class Book1(
    val id: String,
    val createdAt: String,
    val start: String,
    val end: String,
    val userId: String,
    val user: User,
    val description: String,
    val status: Int,
)

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val avatarUrl: String,
)

fun ActionDTO.toDomainModel() = ActionUI(
    id = id,
    text = text,
    actionNumber = actionNumber,
    createdAt = createdAt,
    status = status,
    additionalInfo = additionalInfo,
    username = book.user.name,
    avatarUrl = book.user.avatarUrl
)