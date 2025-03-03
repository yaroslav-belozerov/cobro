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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yaabelozerov.tribede.domain.model.BookStatus
import com.yaabelozerov.tribede.domain.model.BookingUI


@Composable
fun Timeline(
    bookings: List<BookingUI>,
    startHour: Int = 10, // Начало рабочего дня (9:00)
    endHour: Int = 22, // Конец рабочего дня (22:00)
) {
    val totalMinutes = (endHour - startHour) * 60
    val minuteWidth =
        (LocalConfiguration.current.screenWidthDp.toFloat() - 48) / totalMinutes.toFloat()
    Log.d("timeline", "bookings: $bookings")
    val bgColor = MaterialTheme.colorScheme.onBackground
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(40.dp)
        ) {
            // Фон таймлайна
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = bgColor,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 3f
                )
            }

            // Отображение бронирований
            bookings.filter { it.status != BookStatus.CANCELLED }.forEach { booking ->
                val startMinutes = booking.start.hour * 60 + booking.start.minute
                val endMinutes = booking.end.hour * 60 + booking.end.minute
                val startOffset = (startMinutes - startHour * 60) * minuteWidth
                val endOffset =
                    (endMinutes - startHour * 60) * minuteWidth
                Log.d("timeline 2", "startOffset: $startOffset, endOffset: $endOffset")
                Log.d(
                    "timeline 3", "start hour: ${booking.start.hour}, end hour: ${booking.end.hour}"
                )
                Box(
                    modifier = Modifier
                        .offset(x = startOffset.dp, y = 10.dp)
                        .width((endOffset - startOffset).dp)
                        .height(24.dp)
                        .clip(shape = MaterialTheme.shapes.extraSmall)
                        .background(Color(0xFF80CED7)),
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(bgColor, radius = 10f, center = Offset(0f, size.height / 2))
                drawCircle(bgColor, radius = 10f, center = Offset(size.width, size.height / 2))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (hour in startHour..endHour) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(1.5.dp)
                            .background(color = Color.Gray)
                    )
                    Text(text = "$hour", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
