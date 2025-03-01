package com.yaabelozerov.tribede.domain.model

import java.time.LocalDateTime

enum class BookStatus {
    PENDING, ACTIVE, CANCELLED, ENDED
}

data class BookingUI(
    val id: String,
    val createdAt: LocalDateTime,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String,
    val status: BookStatus
)
