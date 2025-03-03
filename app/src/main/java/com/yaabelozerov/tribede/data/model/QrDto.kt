package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class QrDto(
    val code: String,
    val ttl: Long
)

@Serializable
data class ConfirmQr(
    val code: String
)

@Serializable
data class QrConfirmResponse(
    val bookId: String,
    val needsPassport: Boolean,
    val userId: String
)