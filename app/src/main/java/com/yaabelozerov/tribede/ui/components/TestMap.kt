package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CoworkingMap() {
  var selectedZone by remember { mutableStateOf<Int?>(null) }

  Box(modifier = Modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      if (selectedZone == null) {
        // Отрисовка всех зон
        drawRect(color = Color.LightGray, topLeft = Offset(100f, 100f), size = Size(300f, 200f))
        drawRect(color = Color.LightGray, topLeft = Offset(450f, 100f), size = Size(200f, 200f))
      } else {
        // Отрисовка выбранной зоны на весь экран
        drawRect(
            color = Color.LightGray, topLeft = Offset(0f, 0f), size = Size(size.width, size.height))
      }
    }

    // Обработка нажатий
    if (selectedZone == null) {
      Box(
          modifier =
              Modifier.offset(100.dp, 100.dp)
                  .size(300.dp, 200.dp)
                  .clickable { selectedZone = 1 }
                  .background(Color.Transparent))

      Box(
          modifier =
              Modifier.offset(450.dp, 100.dp)
                  .size(200.dp, 200.dp)
                  .clickable { selectedZone = 2 }
                  .background(Color.Transparent))
    } else {
      // Кнопка для возврата
      Button(onClick = { selectedZone = null }, modifier = Modifier.align(Alignment.TopEnd)) {
        Text("Назад")
      }
    }
  }
}
