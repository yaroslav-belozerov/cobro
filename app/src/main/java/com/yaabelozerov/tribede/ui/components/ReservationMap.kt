package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.UserRole
import kotlinx.coroutines.flow.collectLatest

enum class SpaceType {
  OFFICE,
  TALKROOM,
  OPEN,
  MISC
}

data class CoworkingPlace(
    val name: String,
    val spaces: List<CoworkingSpace>,
)

data class Seat(
    val id: Long,
    val position: Pos,
)

data class CoworkingSpace(
    val id: Long,
    val name: String,
    val currentPeople: Int,
    val maxPeople: Int,
    val type: SpaceType,
    val color: Color,
    val position: Pos,
    val tags: List<String>,
    val seats: List<Seat>? = null,
    val isCompanyRestricted: Boolean,
)

data class Pos(val x: Float, val y: Float, val width: Float, val height: Float)

private val colors =
    listOf(
        Color.Red.copy(0.3f),
        Color.Green.copy(0.3f),
        Color.Blue.copy(0.3f),
        Color.Yellow.copy(0.3f),
        Color.Magenta.copy(0.3f),
        Color.Cyan.copy(0.3f))

@Composable
fun ReservationMap(list: List<CoworkingSpace>) {
  var width = 0
  var height = 0
  var chosenIndex: Long = -1
  Box(
      Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
          .fillMaxWidth()
          .aspectRatio(1f)) {
        Canvas(
            Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(12.dp)
                .fillMaxSize()
                .onPlaced {
                    width = it.size.width
                    height = it.size.height
                }
                .pointerInput(Unit) {
                  detectTapGestures(
                      onTap = { offset ->
                        val x = offset.x / width
                        val y = offset.y / height
                        list.forEachIndexed { index, it ->
                          if (it.position.x <= x &&
                              x <= (it.position.width + it.position.x) &&
                              it.position.y <= y &&
                              y <= (it.position.height + it.position.y)) {
                            chosenIndex = (if (it.id == chosenIndex) -1 else it.id)
                            println("clicked $index")
                          }
                        }
                      })
                }
                .onPlaced {
                  width = it.size.width
                  height = it.size.height
                }) {
              list.forEach {
                val color = it.color
                drawRoundRect(
                    color,
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    topLeft = Offset(it.position.x * width, it.position.y * height),
                    size =
                        Size(
                            width = it.position.width * width,
                            height = it.position.height * height))
                if (it.id == chosenIndex) {
                  drawRoundRect(
                      Color.Black,
                      style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                      cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                      topLeft = Offset(it.position.x * width, it.position.y * height),
                      size =
                          Size(
                              width = it.position.width * width,
                              height = it.position.height * height))
                }
                if (it.seats != null) {
                  it.seats.forEach { seat ->
                    drawCircle(
                        color = Color.Blue,
                        center = Offset(seat.position.x * width, seat.position.y * height),
                        radius = 5.dp.toPx())
                  }
                }
              }
            }
      }
}

@Preview
@Composable
fun ReservationMapPreview() {
  ReservationMap(
      listOf(
          CoworkingSpace(
              id = 0,
              name = "Переговорная 1",
              currentPeople = 0,
              maxPeople = 6,
              type = SpaceType.TALKROOM,
              color = Color(0xFF003249),
              position = Pos(0.7f, 0.50f, 0.30f, 0.15f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 1,
              name = "Переговорная 2",
              currentPeople = 0,
              maxPeople = 6,
              type = SpaceType.TALKROOM,
              color = Color(0xFF003249),
              position = Pos(0.55f, 0.50f, 0.15f, 0.15f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 2,
              name = "Общее пространство",
              currentPeople = 2,
              maxPeople = 4,
              type = SpaceType.OPEN,
              color = Color(0xFF80CED7),
              position = Pos(0.0f, 0.50f, 0.55f, 0.25f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 3,
              name = "Офис 1",
              seats =
                  listOf(
                      Seat(0, Pos(0.865f, 0.335f, 0.14f, 0.14f)),
                      Seat(1, Pos(0.935f, 0.335f, 0.14f, 0.14f)),
                      Seat(2, Pos(0.935f, 0.395f, 0.14f, 0.14f)),
                      Seat(4, Pos(0.865f, 0.395f, 0.14f, 0.14f)),
                  ),
              currentPeople = 2,
              maxPeople = 4,
              type = SpaceType.OFFICE,
              color = Color(0xFFCCDBDC),
              position = Pos(0.83f, 0.30f, 0.14f, 0.14f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 4,
              name = "Офис 2",
              currentPeople = 2,
              maxPeople = 4,
              seats =
                  listOf(
                      Seat(5, Pos(0.665f, 0.335f, 0.14f, 0.14f)),
                      Seat(6, Pos(0.735f, 0.335f, 0.14f, 0.14f)),
                      Seat(7, Pos(0.665f, 0.395f, 0.14f, 0.14f)),
                      Seat(8, Pos(0.735f, 0.395f, 0.14f, 0.14f)),
                  ),
              type = SpaceType.OFFICE,
              color = Color(0xFFCCDBDC),
              position = Pos(0.63f, 0.30f, 0.14f, 0.14f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 5,
              name = "Офис 3",
              currentPeople = 2,
              maxPeople = 4,
              seats =
                  listOf(
                      Seat(9, Pos(0.465f, 0.335f, 0.14f, 0.14f)),
                      Seat(10, Pos(0.535f, 0.335f, 0.14f, 0.14f)),
                      Seat(11, Pos(0.465f, 0.395f, 0.14f, 0.14f)),
                      Seat(12, Pos(0.535f, 0.395f, 0.14f, 0.14f)),
                  ),
              type = SpaceType.OFFICE,
              color = Color(0xFFCCDBDC),
              position = Pos(0.43f, 0.30f, 0.14f, 0.14f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 6,
              name = "Офис 4",
              currentPeople = 2,
              maxPeople = 4,
              seats =
                  listOf(
                      Seat(13, Pos(0.735f, 0.135f, 0.30f, 0.20f)),
                      Seat(14, Pos(0.810f, 0.135f, 0.30f, 0.20f)),
                      Seat(15, Pos(0.890f, 0.135f, 0.30f, 0.20f)),
                      Seat(16, Pos(0.965f, 0.135f, 0.30f, 0.20f)),
                  ),
              type = SpaceType.OFFICE,
              color = Color(0xFFCCDBDC),
              position = Pos(0.7f, 0.10f, 0.30f, 0.20f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 7,
              name = "Офис 5",
              currentPeople = 2,
              maxPeople = 4,
              seats =
                  listOf(
                      Seat(17, Pos(0.465f, 0.135f, 0.30f, 0.20f)),
                      Seat(18, Pos(0.530f, 0.135f, 0.30f, 0.20f)),
                      Seat(19, Pos(0.605f, 0.135f, 0.30f, 0.20f)),
                      Seat(20, Pos(0.675f, 0.135f, 0.30f, 0.20f)),
                  ),
              type = SpaceType.OFFICE,
              color = Color(0xFFCCDBDC),
              position = Pos(0.43f, 0.10f, 0.28f, 0.20f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 8,
              name = "тупняк",
              currentPeople = 2,
              maxPeople = 4,
              type = SpaceType.MISC,
              color = Color(0xFF6F7378),
              position = Pos(0.25f, 0.25f, 0.18f, 0.19f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
          CoworkingSpace(
              id = 9,
              name = "общак 2",
              currentPeople = 2,
              maxPeople = 4,
              type = SpaceType.OPEN,
              color = Color(0xFF80CED7),
              position = Pos(0.0f, 0.20f, 0.25f, 0.24f),
              tags = listOf("tag1", "tag2"),
              isCompanyRestricted = false),
      ))
}
