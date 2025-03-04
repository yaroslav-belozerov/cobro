package com.yaabelozerov.tribede.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.domain.model.AdminBookingUI
import com.yaabelozerov.tribede.domain.model.BookStatus
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAdminScreen(vm: AdminViewModel = viewModel(), navigateToScan: () -> Unit) {
    val state = vm.state.collectAsState().value
    var isRefreshing by remember { mutableStateOf(false) }
    PullToRefreshBox(state.isLoading, onRefresh = {vm.fetchData()}) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text("Все брони", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(state.zones) {
                            FilterChip(
                                selected = state.currentZones.contains(it),
                                onClick = {
                                    if (state.currentZones.contains(it)) {
                                        vm.deleteFilterZone(it)
                                    } else {
                                        vm.addFilterZone(it)
                                    }
                                },
                                label = { Text(it) },
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                }
                val filteredBookings = state.bookings.filter { booking ->
                    state.currentZones.isEmpty() || state.currentZones.contains(booking.zoneName)
                }
                itemsIndexed(filteredBookings) { index, model ->
                    AdminBookCard(model, onDelete = vm::deleteBooking)
                    if (index != filteredBookings.size - 1) {
                        Spacer(Modifier.size(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.size(4.dp))
                    }
                }
                item {
                    Spacer(Modifier.height(64.dp))
                }

            }

            FloatingActionButton(
                onClick = navigateToScan,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 24.dp, end = 24.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(Icons.Filled.QrCode, contentDescription = null, modifier = Modifier.padding(16.dp).size(36.dp))
            }
        }
    }

}

@Composable
fun AdminBookCard(
    model: AdminBookingUI,
    onMove: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},

) {
    // только Pending или Active


    Column(Modifier.fillMaxWidth()) {
        Text(model.zoneName, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                model.officeSeatNumber?.let {
                    Text("Место $it", style = MaterialTheme.typography.titleSmall)
                }
                Text(
                    "${model.start.toLocalDate()}"
                )

                Text(
                    "c  ${model.start.hour.toString().padStart(2, '0')}:" +
                            model.start.minute.toString().padStart(2, '0')
                )

                Text(
                    "до ${model.end.hour.toString().padStart(2, '0')}:" +
                            model.end.minute.toString().padStart(2, '0')
                )

                if (model.status == BookStatus.PENDING) {
                    Row {
                        Icon(Icons.Filled.HourglassTop, contentDescription = null)
                        Text("В ожидании")
                    }
                } else {
                    Row {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Text("В процессе")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(24.dp)) {
                        AsyncImage(model.userPhotoUrl, contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop)

                    }
                    Text(model.username, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.weight(1f))
            if (model.status != BookStatus.ACTIVE) {
                FloatingActionButton(
                    onClick = { onMove(model.id) }, shape = RoundedCornerShape(6.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) { Icon(Icons.Filled.Update, null) }
            }
            FloatingActionButton(
                onClick = { onDelete(model.id) }, shape = RoundedCornerShape(6.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) { Icon(Icons.Filled.Cancel, null) }



        }
    }
}

@Composable
fun AdminBookCardForBookUI(
    model: BookResponseDTO,
    onMove: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    ) {
    // только Pending или Active

    val startDateTime = LocalDateTime.ofInstant(Instant.parse(model.start), ZoneId.systemDefault())
    val endDateTime = LocalDateTime.ofInstant(Instant.parse(model.end), ZoneId.systemDefault())
    var minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime)
    val hours = minutes / 60
    minutes %= 60

    val status = BookStatus.entries.getOrElse(model.status) { BookStatus.ACTIVE }

    Column(Modifier.fillMaxWidth()) {
        Text(model.zoneName, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                model.officeSeatNumber?.let {
                    Text("Место $it", style = MaterialTheme.typography.titleSmall)
                }
                Text(
                    "${startDateTime.toLocalDate()}"
                )

                Text(
                    "c  ${startDateTime.hour.toString().padStart(2, '0')}:" +
                            startDateTime.minute.toString().padStart(2, '0')
                )

                Text(
                    "до ${endDateTime.hour.toString().padStart(2, '0')}:" +
                            endDateTime.minute.toString().padStart(2, '0')
                )

                if (status == BookStatus.PENDING) {
                    Row {
                        Icon(Icons.Filled.HourglassTop, contentDescription = null)
                        Text("Ждём вас")
                    }
                } else {
                    if (status == BookStatus.CANCELLED) {
                        Row {
                            Icon(Icons.Filled.EventBusy, contentDescription = null)
                            Text("Отменено")
                        }
                    } else if (status == BookStatus.ENDED) {
                        Row {
                            Icon(Icons.Filled.EventAvailable, contentDescription = null)
                            Text("Завершено")
                        }
                    } else if (status == BookStatus.ACTIVE) {
                        Row {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Text("В процессе")
                        }
                    }

                }
            }
            Spacer(Modifier.weight(1f))
            if (status != BookStatus.ACTIVE) {
                FloatingActionButton(
                    onClick = { onMove(model.id) }, shape = RoundedCornerShape(6.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) { Icon(Icons.Filled.Update, null) }
                FloatingActionButton(
                    onClick = { onDelete(model.id) }, shape = RoundedCornerShape(6.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) { Icon(Icons.Filled.Cancel, null) }

            }
        }
    }
}