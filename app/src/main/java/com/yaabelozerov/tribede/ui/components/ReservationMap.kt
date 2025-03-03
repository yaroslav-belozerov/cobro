package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.data.model.SeatDto
import com.yaabelozerov.tribede.data.model.UserRole
import com.yaabelozerov.tribede.data.model.ZoneDto
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

enum class SpaceType {
    OFFICE, TALKROOM, OPEN, MISC
}

fun ZoneDto.toSpace(seats: List<SeatDto>): CoworkingSpace {
    val spaceType =
        SpaceType.entries.find { it.name.lowercase() == type.lowercase() } ?: SpaceType.MISC
    return CoworkingSpace(
        id = id,
        name = name,
        currentPeople = 0,
        maxPeople = capacity,
        type = spaceType,
        color = spaceType.color(),
        position = Pos(xCoordinate, yCoordinate, width, height),
        tags = zoneTags.map { it.tag.toString() },
        isCompanyRestricted = cls != null,
        description = description,
        seats = seats
    )
}

fun SpaceType.color() = when (this) {
    SpaceType.OFFICE -> Color(0xFF93e6ca)
    SpaceType.TALKROOM -> Color(0xff59b8e3)
    SpaceType.OPEN -> Color(0xFF93e6e5)
    SpaceType.MISC -> Color(0xffe0e0e0)
}

data class CoworkingSpace(
    val id: String,
    val name: String,
    val description: String,
    val currentPeople: Int,
    val maxPeople: Long,
    val type: SpaceType,
    val color: Color,
    val position: Pos,
    val tags: List<String>,
    val seats: List<SeatDto>,
    val isCompanyRestricted: Boolean,
)

@Serializable
data class Decoration(
    val name: String? = null,
    val type: String,
    val x: Float,
    val y: Float,
    val width: Float? = null,
    val height: Float? = null,
)

data class Pos(val x: Float, val y: Float, val width: Float, val height: Float)

@Composable
fun ReservationMap(
    userRole: UserRole?,
    chosen: CoworkingSpace?,
    currentChosenType: SpaceType?,
    onClick: (CoworkingSpace) -> Unit,
    list: List<CoworkingSpace>,
    decor: List<Decoration>,
) {
    val bgColor = MaterialTheme.colorScheme.onBackground
    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }
    val toiletPainter = rememberVectorPainter(Icons.Default.Wc)
    val entrancePainter = rememberVectorPainter(Icons.AutoMirrored.Default.ArrowLeft)
    val peoplePainter = rememberVectorPainter(Icons.Default.People)

    val ffm = LocalFontFamilyResolver.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val tm = TextMeasurer(
        defaultFontFamilyResolver = ffm,
        defaultDensity = density,
        defaultLayoutDirection = layoutDirection,
    )

    Canvas(Modifier
        .fillMaxWidth()
        .aspectRatio(1.4f)
        .padding(horizontal = 12.dp)
        .onPlaced {
            width = it.size.width
            height = it.size.width
        }
        .pointerInput(Unit) {
            detectTapGestures(onTap = { offset ->
                val x = offset.x / width
                val y = offset.y / height
                println("$x, $y")
                list.forEach {
                    if (it.position.x <= x && x <= (it.position.width + it.position.x) && it.position.y <= y && y <= (it.position.height + it.position.y)) {
                        if (it.type != SpaceType.MISC && !(userRole == UserRole.CLIENT && it.isCompanyRestricted)) {
                            onClick(it)
                        }
                    }
                }
            })
        }) {
        list.forEach { zone ->
            val isAllowed = !(userRole == UserRole.CLIENT && zone.isCompanyRestricted)
            val color =
                if (isAllowed) zone.color.copy(currentChosenType?.let { if (it == zone.type) 1f else 0.5f } ?: 1f)
                else Color(0xffe0e0e0)
            drawRoundRect(
                color, cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()), topLeft = Offset(
                    (zone.position.x + 0.005f) * width, (zone.position.y + 0.005f) * height
                ), size = Size(
                    width = (zone.position.width - 0.01f) * width,
                    height = (zone.position.height - 0.01f) * height
                )
            )
            if (zone == chosen) {
                drawRoundRect(
                    bgColor,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    topLeft = Offset(
                        (zone.position.x + 0.01f) * width, (zone.position.y + 0.01f) * height
                    ),
                    size = Size(
                        width = (zone.position.width - 0.02f) * width,
                        height = (zone.position.height - 0.02f) * height
                    )
                )
            }
            if (currentChosenType == zone.type && !(zone.isCompanyRestricted && userRole == UserRole.CLIENT)) {
                drawRoundRect(
                    Color(0xff2f3c99),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    topLeft = Offset(
                        (zone.position.x + 0.01f) * width, (zone.position.y + 0.01f) * height
                    ),
                    size = Size(
                        width = (zone.position.width - 0.02f) * width,
                        height = (zone.position.height - 0.02f) * height
                    )
                )
            }
            zone.seats.forEach { seat ->
                drawCircle(color = Color(0xFFD1603D).copy(if (currentChosenType?.let { it == SpaceType.OFFICE } != false) 1f else 0.5f),
                    center = Offset(seat.x * width, seat.y * height),
                    radius = 6.dp.toPx())
            }

            decor.forEach {
                when (it.type) {
                    "Icon" -> translate(left = it.x * width, top = it.y * height) {
                        when (it.name) {
                            "toilet" -> {
                                with(toiletPainter) {
                                    draw(
                                        size = Size(24.dp.toPx(), 24.dp.toPx()),
                                        colorFilter = ColorFilter.tint(bgColor.copy(if (currentChosenType == SpaceType.MISC || currentChosenType == null) 1f else 0.25f))
                                    )
                                }
                            }

                            "entrance_left" -> {
                                with(entrancePainter) {
                                    draw(
                                        size = Size(48.dp.toPx(), 48.dp.toPx()),
                                        colorFilter = ColorFilter.tint(Color(0xFF0ea600).copy(if (currentChosenType != null) 0.25f else 1f))
                                    )
                                }
                            }
                        }
                    }

                    "Rectangle" -> it.width?.let { w ->
                        it.height?.let { h ->
                            drawRoundRect(
                                color = when (it.name) {
                                    "door" -> Color(0xFF0c0e17)
                                    else -> Color.Red
                                }.copy(if (currentChosenType != null) 0.05f else 1f),
                                size = Size(w * width, h * height),
                                topLeft = Offset(it.x * width, it.y * height),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        }
                    }

                    else -> Unit
                }
            }

            if (zone.type != SpaceType.MISC && zone.type != SpaceType.OFFICE) {
                with(peoplePainter) {
                    translate(zone.position.x * width + 8.dp.toPx(),
                        zone.position.let { it.y + it.height } * height - 32.dp.toPx()) {
                        draw(
                            size = Size(24.dp.toPx(), 24.dp.toPx()),
                            colorFilter = ColorFilter.tint(bgColor.copy(if (currentChosenType != null) if (currentChosenType == zone.type) 1f else 0.5f else 1f))
                        )
                    }
                }
                val tmr = tm.measure("${zone.maxPeople}")
                drawText(tmr,
                    topLeft = Offset(zone.position.x * width + 36.dp.toPx(),
                        zone.position.let { it.y + it.height } * height - tmr.size.height - 12.dp.toPx()),
                    color = bgColor)
            }
        }
    }
}

@Preview
@Composable
fun ReservationMapPreview() {
    val lst = listOf(
        CoworkingSpace(
            id = "0",
            name = "Переговорная 1",
            currentPeople = 0,
            maxPeople = 6,
            type = SpaceType.TALKROOM,
            color = Color(0xFF003249),
            position = Pos(0.7f, 0.50f, 0.30f, 0.15f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            seats = emptyList(),
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "1",
            name = "Переговорная 2",
            currentPeople = 0,
            maxPeople = 6,
            type = SpaceType.TALKROOM,
            color = Color(0xFF003249),
            position = Pos(0.55f, 0.50f, 0.15f, 0.15f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            seats = emptyList(),
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "2",
            name = "Общее пространство",
            currentPeople = 2,
            maxPeople = 4,
            type = SpaceType.OPEN,
            color = Color(0xFF80CED7),
            position = Pos(0.0f, 0.50f, 0.55f, 0.25f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            seats = emptyList(),
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "3",
            name = "Офис 1",
            seats = listOf(
                SeatDto("0", 0.865f, 0.335f),
                SeatDto("1", 0.935f, 0.335f),
                SeatDto("2", 0.935f, 0.395f),
                SeatDto("4", 0.865f, 0.395f),
            ),
            currentPeople = 2,
            maxPeople = 4,
            type = SpaceType.OFFICE,
            color = Color(0xFFCCDBDC),
            position = Pos(0.83f, 0.30f, 0.14f, 0.14f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "4",
            name = "Офис 2",
            currentPeople = 2,
            maxPeople = 4,
            seats = listOf(
                SeatDto("5", 0.665f, 0.335f),
                SeatDto("6", 0.735f, 0.335f),
                SeatDto("7", 0.665f, 0.395f),
                SeatDto("8", 0.735f, 0.395f),
            ),
            type = SpaceType.OFFICE,
            color = Color(0xFFCCDBDC),
            position = Pos(0.63f, 0.30f, 0.14f, 0.14f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "5",
            name = "Офис 3",
            currentPeople = 2,
            maxPeople = 4,
            seats = listOf(
                SeatDto("9", 0.465f, 0.335f),
                SeatDto("10", 0.535f, 0.335f),
                SeatDto("11", 0.465f, 0.395f),
                SeatDto("12", 0.535f, 0.395f),
            ),
            type = SpaceType.OFFICE,
            color = Color(0xFFCCDBDC),
            position = Pos(0.43f, 0.30f, 0.14f, 0.14f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "6",
            name = "Офис 4",
            currentPeople = 2,
            maxPeople = 4,
            seats = listOf(
                SeatDto("13", 0.735f, 0.135f),
                SeatDto("14", 0.810f, 0.135f),
                SeatDto("15", 0.890f, 0.135f),
                SeatDto("16", 0.965f, 0.135f),
            ),
            type = SpaceType.OFFICE,
            color = Color(0xFFCCDBDC),
            position = Pos(0.7f, 0.10f, 0.30f, 0.20f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "7",
            name = "Офис 5",
            currentPeople = 2,
            maxPeople = 4,
            seats = listOf(
                SeatDto("17", 0.465f, 0.135f),
                SeatDto("18", 0.530f, 0.135f),
                SeatDto("19", 0.600f, 0.135f),
                SeatDto("20", 0.665f, 0.135f),
            ),
            type = SpaceType.OFFICE,
            color = Color(0xFFCCDBDC),
            position = Pos(0.43f, 0.10f, 0.27f, 0.20f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "8",
            name = "тупняк",
            currentPeople = 2,
            maxPeople = 4,
            type = SpaceType.MISC,
            color = Color(0xFF6F7378),
            position = Pos(0.25f, 0.25f, 0.18f, 0.19f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            seats = emptyList(),
            isCompanyRestricted = false
        ),
        CoworkingSpace(
            id = "9",
            name = "общак 2",
            currentPeople = 2,
            maxPeople = 4,
            type = SpaceType.OPEN,
            color = Color(0xFF80CED7),
            position = Pos(0.0f, 0.20f, 0.25f, 0.24f),
            tags = listOf("tag1", "tag2"),
            description = "asdsadsd",
            seats = emptyList(),
            isCompanyRestricted = false
        ),
    )
    var id by remember { mutableStateOf<CoworkingSpace?>(null) }
    ReservationMap(
        UserRole.ADMIN, id, null, { id = it }, lst, listOf(
            Decoration(
                type = "Icon", name = "toilet", x = 0.265f, y = 0.355f, width = null, height = null
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.125f,
                y = 0.42f,
                width = 0.07f,
                height = 0.015f
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.35f,
                y = 0.42f,
                width = 0.05f,
                height = 0.015f
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.775f,
                y = 0.28f,
                width = 0.05f,
                height = 0.015f
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.575f,
                y = 0.28f,
                width = 0.05f,
                height = 0.015f
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.2f,
                y = 0.505f,
                width = 0.07f,
                height = 0.015f
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.59f,
                y = 0.505f,
                width = 0.07f,
                height = 0.015f
            ), Decoration(
                type = "Rectangle",
                name = "door",
                x = 0.75f,
                y = 0.505f,
                width = 0.07f,
                height = 0.015f
            ), Decoration(
                type = "Icon", name = "entrance_left", x = 0.94f, y = 0.4f
            )
        )
    )
}
