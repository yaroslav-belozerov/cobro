package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ActionDTO(
    val id: String,
    val text: String,
    val actionNumber: Int?,
    val createdAt: String,
    val status: Long,
    val additionalInfo: String,
    val bookId: String,
)