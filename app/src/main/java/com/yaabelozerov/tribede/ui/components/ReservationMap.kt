package com.yaabelozerov.tribede.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.data.model.SeatDto
import com.yaabelozerov.tribede.data.model.ZoneDto
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
        color = when (spaceType) {
            SpaceType.OFFICE -> Color(0xFFCCDBDC)
            SpaceType.TALKROOM -> Color(0xff1e6585)
            SpaceType.OPEN -> Color(0xFF80CED7)
            SpaceType.MISC -> Color(0xFF5F6062).copy(0.1f)
        },
        position = Pos(xCoordinate, yCoordinate, width, height),
        tags = zoneTags.map { it.tag.toString() },
        isCompanyRestricted = cls != null,
        description = description,
        seats = seats
    )
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
    val id: String,
    val type: String,
    val x: Long,
    val y: Long,
)

data class Pos(val x: Float, val y: Float, val width: Float, val height: Float)

@Composable
fun ReservationMap(
    chosen: CoworkingSpace?,
    onClick: (CoworkingSpace) -> Unit,
    list: List<CoworkingSpace>,
    decor: List<Decoration>
) {
    val bgColor = MaterialTheme.colorScheme.onBackground
    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }

    Canvas(Modifier
        .fillMaxWidth()
        .aspectRatio(1.3f)
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
                        if (it.type != SpaceType.MISC) {
                            onClick(it)
                        }
                    }
                }
            })
        }) {
        list.forEach {
            val color = it.color
            drawRoundRect(
                color, cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()), topLeft = Offset(
                    (it.position.x + 0.005f) * width, (it.position.y + 0.005f) * height
                ), size = Size(
                    width = (it.position.width - 0.01f) * width,
                    height = (it.position.height - 0.01f) * height
                )
            )
            if (it == chosen) {
                drawRoundRect(
                    bgColor,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    topLeft = Offset(
                        (it.position.x + 0.01f) * width, (it.position.y + 0.01f) * height
                    ),
                    size = Size(
                        width = (it.position.width - 0.02f) * width,
                        height = (it.position.height - 0.02f) * height
                    )
                )
            }
            it.seats.forEach { seat ->
                drawCircle(
                    color = Color(0xFFD1603D),
                    center = Offset(seat.x * width, seat.y * height),
                    radius = 6.dp.toPx()
                )
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
        id, { id = it }, lst, emptyList()
    )
}
