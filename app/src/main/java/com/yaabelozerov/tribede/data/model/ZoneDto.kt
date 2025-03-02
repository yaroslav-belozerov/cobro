package com.yaabelozerov.tribede.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZoneDto(
    val id: String,
    val name: String,
    val description: String,
    val capacity: Long,
    @SerialName("class") val cls: String?,
    val xCoordinate: Float,
    val yCoordinate: Float,
    val width: Float,
    val height: Float,
    val zoneTags: List<ZoneTag>,
    val type: String = ""
)

@Serializable
data class ZoneTag(
    val tag: Long,
)

@Serializable
data class SeatDto(
    val id: String,
    val x: Float,
    val y: Float,
)