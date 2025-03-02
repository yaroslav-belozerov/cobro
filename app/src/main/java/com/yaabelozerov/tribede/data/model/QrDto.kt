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