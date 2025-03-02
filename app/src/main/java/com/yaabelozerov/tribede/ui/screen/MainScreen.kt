package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.data.model.BookRequestDTO
import com.yaabelozerov.tribede.data.model.SeatDto
import com.yaabelozerov.tribede.ui.components.CoworkingSpace
import com.yaabelozerov.tribede.ui.components.MyButton
import com.yaabelozerov.tribede.ui.components.MyTextField
import com.yaabelozerov.tribede.ui.components.ReservationMap
import com.yaabelozerov.tribede.ui.components.SpaceType
import com.yaabelozerov.tribede.ui.components.Timeline
import com.yaabelozerov.tribede.ui.viewmodels.MainViewModel
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel
import java.lang.Math.pow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = viewModel(), userVm: UserViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var isBookingDialogOpen by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val bookingsForToday = remember(state.currentBookings, datePickerState.selectedDateMillis) {
        state.currentBookings.filter {
            it.start.toLocalDate() == LocalDateTime.ofInstant(datePickerState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it)
            } ?: Instant.now(), ZoneId.systemDefault()).toLocalDate()
        }
    }
    var chosenZone by remember { mutableStateOf<CoworkingSpace?>(null) }
    var chosenSeat by remember { mutableStateOf<SeatDto?>(null) }
    state.zones.takeIf { it.isNotEmpty() }?.let { zones ->
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
                    chosenZone, {
                        if (chosenZone == it) {
                            chosenZone = null
                        } else {
                            chosenZone = it
                            if (it.type != SpaceType.OFFICE) vm.getBookings(zoneId = it.id, seatId = null)
                        }
                    }, zones
                )
                if (chosenZone != null) ModalBottomSheet(onDismissRequest = {
                    chosenZone = null
                    chosenSeat = null
                    vm.resetModal()
                    datePickerState.selectedDateMillis = Instant.now().toEpochMilli()
                }, containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val radius = 17
                            chosenZone?.let { zone ->
                                var width by remember { mutableIntStateOf(0) }
                                var height by remember { mutableIntStateOf(0) }
                                if (zone.type == SpaceType.OFFICE) Canvas(Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(0.5f)
                                    .aspectRatio(zone.position.width / zone.position.height)
                                    .clip(MaterialTheme.shapes.large)
                                    .background(zone.color)
                                    .onPlaced { layout ->
                                        width = layout.size.width
                                        height = layout.size.height
                                    }.pointerInput(Unit) {
                                        detectTapGestures(onTap = { offset ->
                                            val x = offset.x
                                            val y = offset.y 
                                            zone.seats.forEach {
                                                val pointX = (it.x - zone.position.x) * width / zone.position.width
                                                val pointY = (it.y - zone.position.y) * height / zone.position.height
                                                val distance = sqrt(((x - pointX).pow(2) + (y - pointY).pow(2)))
                                                if (distance < radius.dp.toPx()) {
                                                    if (it.id == chosenSeat?.id) {
                                                        chosenSeat = null
                                                    } else {
                                                        chosenSeat = it
                                                        vm.getBookings(zoneId = zone.id, seatId = it.id)
                                                    }
                                                }
                                            }
                                        })
                                    }) {
                                    zone.seats.forEach { seat ->
                                        drawCircle(
                                            color = Color.Blue, center = Offset(
                                                (seat.x - zone.position.x) * width / zone.position.width,
                                                (seat.y - zone.position.y) * height / zone.position.height
                                            ), radius = radius.dp.toPx()
                                        )
                                        if (seat.id == chosenSeat?.id) {
                                            drawCircle(
                                                color = Color.Black, center = Offset(
                                                    (seat.x - zone.position.x) * width / zone.position.width,
                                                    (seat.y - zone.position.y) * height / zone.position.height
                                                ), radius = radius.dp.toPx(), style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                        }
                                    }
                                }

                                Text(zone.name, style = MaterialTheme.typography.headlineMedium)
                                Text(zone.description)
                                Text(zone.run { "Свободно ${maxPeople - 0} из $maxPeople" })
                                if (zone.type != SpaceType.OFFICE || chosenSeat != null) MyButton(
                                    onClick = { isBookingDialogOpen = true },
                                    text = "Забронировать",
                                    icon = Icons.Default.EditCalendar,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        if (chosenZone?.type != SpaceType.OFFICE) {
                            Box(Modifier.padding(horizontal = 12.dp, vertical = 24.dp)) {
                                Timeline(bookingsForToday)
                            }
                            DatePicker(
                                datePickerState, colors = DatePickerDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ), title = null
                            )
                        } else {
                            if (chosenSeat != null) {
                                Box(Modifier.padding(horizontal = 12.dp, vertical = 24.dp)) {
                                    Timeline(bookingsForToday)
                                }
                                DatePicker(
                                    datePickerState, colors = DatePickerDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    ), title = null
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isBookingDialogOpen) {
            BookingDialog(LocalDateTime.ofInstant(datePickerState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it)
            } ?: Instant.now(), ZoneId.systemDefault()),
                onDismiss = { isBookingDialogOpen = false },
                onClick = { req ->
                    chosenZone?.id?.let {
                        vm.book(
                            req = req, zoneId = it, seatId = chosenSeat?.id
                        ) {
                            userVm.fetchUserInfo()
                        }
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