package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPassportDTO(
    val id: String,
    val serial: String,
    val number: String,
    val firstname: String,
    val lastname: String,
    val middlename: String,
    val createdOn: String,
    val birthday: String,
)

