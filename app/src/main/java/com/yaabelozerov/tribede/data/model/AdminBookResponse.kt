package com.yaabelozerov.tribede.data.model

import com.yaabelozerov.tribede.domain.model.AdminBookingUI
import com.yaabelozerov.tribede.domain.model.BookStatus
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Serializable
data class AdminBookResponse(
    val id: String,
    val createdAt: String,
    val start: String,
    val end: String,
    val description: String,
    val zoneId: String,
    val zoneName: String,
    val officeSeatId: String?,
    val officeSeatNumber: String?,
    val status: Int,
    val user: UserDto
)

fun AdminBookResponse.toDomainModel() = AdminBookingUI(
    id = id,
    createdAt = LocalDateTime.ofInstant(Instant.parse(createdAt), ZoneId.systemDefault()),
    start = LocalDateTime.ofInstant(Instant.parse(start), ZoneId.systemDefault()),
    end = LocalDateTime.ofInstant(Instant.parse(end), ZoneId.systemDefault()),
    description = description,
    zoneName = zoneName,
    officeSeatNumber = officeSeatNumber,
    status = BookStatus.entries.getOrElse(status) { BookStatus.ACTIVE },
    username = user.name,
    userPhotoUrl = user.avatarUrl ?: ""
)