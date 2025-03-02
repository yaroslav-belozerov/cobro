package com.yaabelozerov.tribede.data.model

import com.yaabelozerov.tribede.domain.model.BookStatus
import com.yaabelozerov.tribede.domain.model.BookingUI
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.serialization.Serializable

@Serializable
data class BookResponseDTO(
    val id: String,
    val createdAt: String,
    val start: String,
    val end: String,
    val description: String,
    val zoneId: String,
    val zoneName: String,
    val officeSeatId: String?,
    val officeSeatNumber: String?,
    val status: Int
)

fun BookResponseDTO.toDomainModel(): BookingUI =
    BookingUI(
        id = id,
        createdAt = LocalDateTime.ofInstant(Instant.parse(createdAt), ZoneId.of("UTC")),
        start = LocalDateTime.ofInstant(Instant.parse(start), ZoneId.of("UTC")),
        end = LocalDateTime.ofInstant(Instant.parse(end), ZoneId.of("UTC")),
        description = description,
        status = BookStatus.entries.getOrElse(status) { BookStatus.ACTIVE }
    )


@Serializable
data class BookRequestDTO(
    val from: String,
    val to: String,
    val description: String
)

