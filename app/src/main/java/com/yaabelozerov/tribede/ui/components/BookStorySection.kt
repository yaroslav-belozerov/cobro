package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
fun BookCard(model: BookResponseDTO, onClick: (String) -> Unit = {}, onMove: (String) -> Unit) {
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
                if (status == BookStatus.CANCELLED) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text("Отменено", color = MaterialTheme.colorScheme.tertiary)
                    }
                } else if (status == BookStatus.ENDED) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.EventAvailable, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text("Завершено", color = MaterialTheme.colorScheme.tertiary)
                    }
                } else if (status == BookStatus.ACTIVE) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text("В процессе", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
            if (minutes == 0L) {
                Text("$hours ч", style = MaterialTheme.typography.titleLarge)
            } else {
                Text("$hours ч $minutes мин", style = MaterialTheme.typography.titleLarge)
            }
        }
        Spacer(Modifier.weight(1f))
        if (status == BookStatus.PENDING) {
            FloatingActionButton(onClick = { onClick(model.id) }, shape = RoundedCornerShape(4.dp),
                content = { Icon(Icons.Filled.QrCode, null) },
                elevation = FloatingActionButtonDefaults.elevation(0.dp))
        }
        if (status != BookStatus.ACTIVE) {
            FloatingActionButton(
                onClick = { onMove(model.id) }, shape = RoundedCornerShape(6.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) { Icon(Icons.Filled.Update, null) }
        }
        Spacer(Modifier.width(16.dp))
    }
}