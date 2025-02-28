package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    val tl: Pair<Float, Float>,
    val br: Pair<Float, Float>,
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
    Canvas(Modifier
        .background(MaterialTheme.colorScheme.surfaceContainer).size(256.dp)
        .onPlaced {
            width = it.size.width
            height = it.size.height
        }) {
        list.forEachIndexed { index, it ->
            val color = colors[index % colors.size]
            drawRoundRect(
                color,
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                topLeft = Offset(width * it.first.tl.first, height * it.first.tl.second),
                size = Size(
                    width * (it.first.br.first - it.first.tl.first),
                    height * (it.first.br.second - it.first.tl.second)
                )
            )
        }
    }
}

@Preview
@Composable
fun ReservationMapPreview() {
    ReservationMap(listOf(
        Pair(MapPoint(Pair(0f, 0f), Pair(.4f, .2f)), "test"),
        Pair(MapPoint(Pair(0f, .2f), Pair(.4f, .5f)), "test"),
        Pair(MapPoint(Pair(0f, .5f), Pair(.3f, 1f)), "test"),
        Pair(MapPoint(Pair(.55f, 0f), Pair(1f, .3f)), "test"),
        Pair(MapPoint(Pair(.55f, .3f), Pair(1f, .7f)), "test"),
        Pair(MapPoint(Pair(.55f, .7f), Pair(1f, 1f)), "test"),
    ))
}