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
    val status: Int
)

fun BookResponseDTO.toDomainModel(): BookingUI =
    BookingUI(
        id = id,
        createdAt = LocalDateTime.ofInstant(Instant.parse(createdAt), ZoneId.systemDefault()),
        start = LocalDateTime.ofInstant(Instant.parse(start), ZoneId.systemDefault()),
        end = LocalDateTime.ofInstant(Instant.parse(end), ZoneId.systemDefault()),
        description = description,
        status = BookStatus.entries.getOrElse(status) { BookStatus.ACTIVE }
    )
