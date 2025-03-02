package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.data.model.BookRequestDTO
import com.yaabelozerov.tribede.ui.components.MyButton
import com.yaabelozerov.tribede.ui.components.MyTextField
import com.yaabelozerov.tribede.ui.components.ReservationMap
import com.yaabelozerov.tribede.ui.components.SpaceType
import com.yaabelozerov.tribede.ui.components.Timeline
import com.yaabelozerov.tribede.ui.viewmodels.MainViewModel
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = viewModel(), userVm: UserViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var isBookingDialogOpen by remember { mutableStateOf(false) }
    var chosenDate by remember { mutableStateOf(LocalDateTime.now()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val bookingsForToday =
        remember(state.currentBookings) { state.currentBookings.filter { it.start.toLocalDate() == chosenDate.toLocalDate() } }
    state.zones.takeIf { it.isNotEmpty() }?.let { zones ->
        var chosenZone by remember { mutableStateOf(zones.first()) }
        var expanded by remember { mutableStateOf(false) }
        LaunchedEffect(datePickerState.selectedDateMillis) {
            chosenDate = LocalDateTime.ofInstant(datePickerState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it)
            } ?: Instant.now(), ZoneId.systemDefault())
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.size(24.dp))
            Text("Забронировать", style = MaterialTheme.typography.headlineMedium)
            Column(Modifier.fillMaxWidth()) {
                ReservationMap(
                    if (expanded) chosenZone else null, {
                        if (chosenZone == it) {
                            expanded = !expanded
                        } else {
                            chosenZone = it
                            expanded = true
                            vm.getBookings(zoneId = it.id, seatId = null)
                        }
                    }, zones
                )
                if (expanded) ModalBottomSheet(onDismissRequest = { expanded = false }) {
                    Column {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(chosenZone.name, style = MaterialTheme.typography.headlineMedium)
                            Text(chosenZone.description)
                            Text(chosenZone.run { "Свободно ${maxPeople - 0} из $maxPeople" })
                            if (chosenZone.type != SpaceType.OFFICE) MyButton(
                                onClick = { isBookingDialogOpen = true },
                                text = "Забронировать",
                                icon = Icons.Default.EditCalendar,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(Modifier.padding(horizontal = 12.dp, vertical = 24.dp)) {
                            Timeline(bookingsForToday)
                        }
                        DatePicker(
                            datePickerState, colors = DatePickerDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.background
                            ), title = null
                        )
                    }
                }
            }
        }

        if (isBookingDialogOpen) {
            BookingDialog(chosenDate,
                onDismiss = { isBookingDialogOpen = false },
                onClick = { req ->
                    vm.book(
                        req = req, zoneId = chosenZone.id, seatId = null
                    ) {
                        userVm.fetchUserInfo()
                    }
                })
        }
    }
}

@Composable
private fun BookingDialog(
    chosenDate: LocalDateTime,
    onDismiss: () -> Unit,
    onClick: (BookRequestDTO) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Бронь на ${chosenDate.toLocalDate()}",
                    style = MaterialTheme.typography.headlineMedium
                )
                val hours = (10..21).toList()
                val minutes = listOf(0, 15, 30, 45)
                val hourStartPager = rememberPagerState { hours.size }
                val minuteStartPager = rememberPagerState { minutes.size }

                Text("Начало", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerticalPager(
                        hourStartPager,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .height(56.dp)
                            .width(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim),
                    ) {
                        Row(
                            modifier = Modifier.height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                hours[it].toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    VerticalPager(
                        minuteStartPager,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .height(56.dp)
                            .width(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim),
                    ) {
                        Row(
                            modifier = Modifier.height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                minutes[it].toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                val hourEndPager = rememberPagerState { hours.size }
                val minuteEndPager = rememberPagerState { minutes.size }
                Text("Конец", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerticalPager(
                        hourEndPager,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .height(56.dp)
                            .width(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim),
                    ) {
                        Row(
                            modifier = Modifier.height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                hours[it].toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    VerticalPager(
                        minuteEndPager,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .height(56.dp)
                            .width(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim),
                    ) {
                        Row(
                            modifier = Modifier.height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                minutes[it].toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                Column {
                    var description by remember { mutableStateOf("") }
                    MyTextField(
                        description,
                        { description = it },
                        placeholder = "Комментарии к брони",
                        modifier = Modifier.fillMaxWidth()
                    )

                    val from = chosenDate.withHour(hours[hourStartPager.currentPage])
                        .withMinute(minutes[minuteStartPager.currentPage])
                    val to = chosenDate.withHour(hours[hourEndPager.currentPage])
                        .withMinute(minutes[minuteEndPager.currentPage])

                    val deltaMins =
                        (hours[hourEndPager.currentPage] - hours[hourStartPager.currentPage]) * 60 + (minutes[minuteEndPager.currentPage] - minutes[minuteStartPager.currentPage])
                    val delta = if (deltaMins >= 60) {
                        "${deltaMins / 60} ч." + if (deltaMins % 60 > 0) " ${deltaMins % 60} мин." else ""
                    } else "$deltaMins мин."
                    val enabled = deltaMins > 0
                    MyButton(
                        onClick = {
                            onClick(
                                BookRequestDTO(
                                    from = "$from:00.000Z",
                                    to = "$to:00.000Z",
                                    description = description
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        text = if (enabled) "Подтвердить ($delta)" else "Некорректный интервал",
                        icon = if (enabled) Icons.Default.CheckCircle else null
                    )
                }
            }
        }
    }
}