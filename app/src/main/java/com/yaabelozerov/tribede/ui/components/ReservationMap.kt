package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.UserRole

data class CoworkingPlace(
    val name: String,
    val spaces: List<CoworkingSpace>,
)

data class CoworkingSpace(
    val name: String,
    val currentPeople: Int,
    val maxPeople: Int,
    val points: List<MapPoint>,
    val tags: List<String>,
    val isCompanyRestricted: Boolean,
)

data class MapPoint(
    val topLeft: Pair<Float, Float>,
    val size: Pair<Float, Float>,
)

private val colors = listOf(
    Color.Red.copy(0.3f),
    Color.Green.copy(0.3f),
    Color.Blue.copy(0.3f),
    Color.Yellow.copy(0.3f),
    Color.Magenta.copy(0.3f),
    Color.Cyan.copy(0.3f)
)

@Composable
fun ReservationMap(list: List<Pair<MapPoint, String>>) {
    var width = 0
    var height = 0
    var chosenIndex by remember { mutableIntStateOf(-1) }
    Box(Modifier
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .fillMaxWidth()
        .aspectRatio(1f)
    ) {
        Canvas(Modifier
            .padding(12.dp)
            .fillMaxSize()
            .onPlaced {
                width = it.size.width
                height = it.size.height
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    val x = offset.x / width
                    val y = offset.y / height
                    list.forEachIndexed { index, it ->
                        if (it.first.topLeft.first <= x && x <= (it.first.size.first + it.first.topLeft.first) && it.first.topLeft.second <= y && y <= (it.first.size.second + it.first.topLeft.second)) {
                            chosenIndex = (if (index == chosenIndex) -1 else index)
                            println("clicked $index")
                        }
                    }
                })
            }) {
            list.forEachIndexed { index, it ->
                val color = colors[index % colors.size]
                drawRoundRect(
                    color,
                    style = Fill,
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    topLeft = Offset(width * it.first.topLeft.first, height * it.first.topLeft.second),
                    size = Size(
                        width * (it.first.size.first), height * (it.first.size.second)
                    ),
                )
                if (index == chosenIndex) {
                    drawRoundRect(
                        Color.Black,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                        topLeft = Offset(width * it.first.topLeft.first, height * it.first.topLeft.second),
                        size = Size(
                            width * (it.first.size.first), height * (it.first.size.second)
                        ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ReservationMapPreview() {
    ReservationMap(
        listOf(
            Pair(MapPoint(Pair(0f, 0f), Pair(.4f, .2f)), "test"),
            Pair(MapPoint(Pair(0f, .2f), Pair(.4f, .5f)), "test"),
            Pair(MapPoint(Pair(0f, .7f), Pair(.3f, .3f)), "test"),
            Pair(MapPoint(Pair(.55f, 0f), Pair(.45f, .3f)), "test"),
            Pair(MapPoint(Pair(.55f, .3f), Pair(.45f, .4f)), "test"),
            Pair(MapPoint(Pair(.55f, .7f), Pair(.45f, .3f)), "test"),
        )
    )
}

@Composable
fun ReservationMapScreen(user: UserDto) {
    val role = user.role.let { UserRole.entries.getOrNull(it) }
    LazyColumn {
        item {
            role?.let {
                Text("Добро пожаловать, уважаемый ${
                    when (it) {
                        UserRole.ADMIN -> "администратор"
                        UserRole.CLIENT -> "клиент"
                        UserRole.INTERNAL -> "сотрудник"
                    }
                }", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(12.dp))
            }
        }
        item {
            ReservationMapPreview()
            user.books?.forEach {
                Text("${it.start} ${it.end}")
            }
        }
    }
}