package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.domain.model.BookStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun BookCard(model: BookResponseDTO) {
    val startDateTime = LocalDateTime.ofInstant(Instant.parse(model.start), ZoneId.systemDefault())
    val endDateTime = LocalDateTime.ofInstant(Instant.parse(model.end), ZoneId.systemDefault())
    var minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime)
    val hours = minutes / 60
    minutes %= 60

    val status = BookStatus.entries.getOrElse(model.status) { BookStatus.ACTIVE }


    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(model.zoneName, style = MaterialTheme.typography.titleMedium)
//            model.seat?.let {
//                Text(it, style = MaterialTheme.typography.titleSmall)
//            }
            Text("${startDateTime.toLocalDate()} в" +
                    " ${startDateTime.hour.toString().padStart(2, '0')}:" +
                    startDateTime.minute.toString().padStart(2, '0')
            )

            if (status == BookStatus.PENDING) {
                Row {
                    Icon(Icons.Filled.HourglassTop, contentDescription = null)
                    Text("Ждём вас")
                }
            } else {
                Row {
                    Icon(Icons.Filled.EventAvailable, contentDescription = null)
                    Text("Завершено")
                }
            }
        }
        Spacer(Modifier.weight(1f))
        if (minutes == 0L) {
            Text("$hours ч", style = MaterialTheme.typography.headlineLarge)
        } else {
            Text("$hours ч $minutes мин", style = MaterialTheme.typography.headlineLarge)
        }

    }
}