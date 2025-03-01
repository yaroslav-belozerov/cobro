package com.yaabelozerov.tribede.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Booking(
    val startTime: Int, // Время начала бронирования (в миллисекундах)
    val endTime: Int,   // Время окончания бронирования (в миллисекундах)
    val isBooked: Boolean // Занято или свободно
)

@Composable
fun Timeline(
    bookings: List<Booking>,
    startHour: Int = 9, // Начало рабочего дня (9:00)
    endHour: Int = 18 // Конец рабочего дня (18:00)
) {
  val totalMinutes = (endHour - startHour) * 60
  val minuteWidth = (LocalConfiguration.current.screenWidthDp.toFloat() - 16) / totalMinutes.toFloat()
    Log.d("timeline", "current: ${LocalConfiguration.current.screenWidthDp}")
    Log.d("timeline", "Timeline: minuteWidth: $minuteWidth, totalMinutes: $totalMinutes")

    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(40.dp)) {
            // Фон таймлайна
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2f)
            }

            // Отображение бронирований
            bookings.forEach { booking ->
                val startOffset = ((booking.startTime - startHour * 60) * minuteWidth)
                val endOffset = ((booking.endTime - startHour * 60) * minuteWidth)
                Log.d("timeline", "startOffset: $startOffset, endOffset: $endOffset")

                Box(
                    modifier =
                    Modifier
                        .offset(x = startOffset.dp, y = 10.dp)
                        .width((endOffset - startOffset).dp)
                        .height(30.dp)
                        .clip(shape = RoundedCornerShape(3.dp))
                        .background(if (booking.isBooked) Color.Red else Color.Green),

                    )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (hour in startHour..endHour) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(16.dp)
                ) {
                    Box(
                        modifier = Modifier.height(6.dp).width(1.5.dp).background(color = Color.Gray)
                    )
                    Text(
                        text = "$hour",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

            }
        }
    }

}

@Preview
@Composable
fun TimeLinePreview() {
    Timeline(listOf(
        Booking(startTime = 9 * 60, endTime = 10 * 60, isBooked = true),
        Booking(startTime = 10 * 60, endTime = 12 * 60, isBooked = false),
        Booking(startTime = 14 * 60, endTime = 16 * 60, isBooked = true)
    ))
}
