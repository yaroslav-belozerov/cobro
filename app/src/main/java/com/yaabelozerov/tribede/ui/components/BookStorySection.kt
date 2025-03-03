package com.yaabelozerov.tribede.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.domain.model.BookStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCard(model: BookResponseDTO, onQr: (String) -> Unit = {}, onLongClick: () -> Unit = {}) {
    val startDateTime = LocalDateTime.ofInstant(Instant.parse(model.start), ZoneId.systemDefault())
    val endDateTime = LocalDateTime.ofInstant(Instant.parse(model.end), ZoneId.systemDefault())
    var minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime)
    val hours = minutes / 60
    minutes %= 60

    val status = BookStatus.entries.getOrElse(model.status) { BookStatus.ACTIVE }


    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.combinedClickable(onLongClick = onLongClick) {  }) {
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
            FloatingActionButton(onClick = { onQr(model.id) }, shape = RoundedCornerShape(4.dp),
                content = { Icon(Icons.Filled.QrCode, null) },
                elevation = FloatingActionButtonDefaults.elevation(0.dp))
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
fun ClickableBookCard(
    model: BookResponseDTO,
    onQr: (String) -> Unit,
    onMove: ((String) -> Unit)?,
    onDelete: (() -> Unit)?
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)) {
        BookCard(model, onQr, onLongClick = { isMenuVisible = true })
        AnimatedVisibility(
            isMenuVisible,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp)
        ) {
            DropdownMenu(
                expanded = isMenuVisible,
                onDismissRequest = { isMenuVisible = false }, // Закрыть меню при нажатии вне его
            ) {
                onDelete?.let {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Cancel, null)
                                Spacer(Modifier.size(8.dp))
                                Text("Отменить")
                            }

                        },
                        onClick = {
                            onDelete()
                            isMenuVisible = false // Закрыть меню после выбора
                        }
                    )
                }
                onMove?.let {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Update, null)
                                Spacer(Modifier.size(8.dp))
                                Text("Перенести")
                            }

                        },
                        onClick = {
                            onMove(model.id)
                            isMenuVisible = false // Закрыть меню после выбора
                        }
                    )
                }


            }
        }

    }
}