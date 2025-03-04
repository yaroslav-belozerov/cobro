package com.yaabelozerov.tribede.domain.model

data class ActionUI(
    val id: String,
    val text: String,
    val actionNumber: Int?,
    val createdAt: String,
    var status: Int,
    val username: String,
    val additionalInfo: String,
    val avatarUrl: String
)


