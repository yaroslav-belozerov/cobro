package com.yaabelozerov.tribede.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.model.BookRequestDTO
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.data.model.UserRole
import com.yaabelozerov.tribede.domain.model.BookStatus
import com.yaabelozerov.tribede.ui.components.BookCard
import com.yaabelozerov.tribede.ui.components.ClickableBookCard
import com.yaabelozerov.tribede.ui.components.MyButton
import com.yaabelozerov.tribede.ui.components.MyTextField
import com.yaabelozerov.tribede.ui.components.QrShowWidget
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(vm: UserViewModel) {
    val uiState by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showQrDialog by remember { mutableStateOf(false) }

    var showMoveModal by remember { mutableStateOf(false) }
    var currentMovedBook by remember { mutableStateOf<BookResponseDTO?>(null) }
    var moveTimeDialogOpen by remember { mutableStateOf(false) }

    val currentDate = LocalDate.now()
    val currentMillis = Instant.now().toEpochMilli()
    val currentDateMillis =
        currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            // Разрешаем выбор только дат, которые не раньше текущей
            return utcTimeMillis >= currentDateMillis
        }

        override fun isSelectableYear(year: Int): Boolean {
            // Разрешаем выбор только текущего и будущих годов
            return year >= currentDate.year
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentMillis, selectableDates = selectableDates
    )

    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { uriNotNull ->
                scope.launch {
                    vm.onMediaPicker(Application.app.applicationContext, uriNotNull)
                }
            }
        }

    if (showQrDialog) {
        QrShowWidget(
            onDismissRequest = { showQrDialog = false }, qrCode = uiState.qrString
        )
    }
    uiState.user?.let { userInfo ->
        PullToRefreshBox(isRefreshing = uiState.isLoading, onRefresh = { vm.fetchUserInfo() }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            "Профиль",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                        IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = {
                            scope.launch {
                                Application.dataStore.apply {
                                    saveToken("")
                                    saveIsAdmin(false)
                                }
                            }
                        }) {
                            Icon(
                                Icons.AutoMirrored.Default.Logout, contentDescription = "logout"
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    AsyncImage(
                        model = userInfo.avatarUrl,
                        contentDescription = "avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable {
                                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentScale = ContentScale.Crop
                    )
                }
                item { Text(userInfo.name, style = MaterialTheme.typography.headlineSmall) }
                item { Text(userInfo.email, style = MaterialTheme.typography.titleLarge) }
                if (UserRole.entries[userInfo.role] == UserRole.INTERNAL) {
                    item {
                        Text("Внутренний пользователь", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
                userInfo.books?.let {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Мои брони", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.size(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(color = Color.Gray)

                                )
                            }

                        }
                    }
                    val pending =
                        userInfo.books.filter { BookStatus.entries[it.status] == BookStatus.PENDING }
                    itemsIndexed(pending) { index, book ->
                        ClickableBookCard(book, { vm.getQr(it); showQrDialog = true }, {
                            currentMovedBook = book
                            showMoveModal = true
                        },
                            { vm.deleteBook(book.id)})
                        if (index != pending.size - 1) {
                            Spacer(Modifier.size(14.dp))
                            HorizontalDivider()
                            Spacer(Modifier.size(4.dp))
                        }

                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Text("История", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.size(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(color = Color.Gray)

                                )
                            }
                        }
                    }
                    items(userInfo.books.filter { BookStatus.entries[it.status] != BookStatus.PENDING }) {
                        ClickableBookCard(it, { vm.getQr(it) }, null, null)
                        Spacer(Modifier.size(14.dp))
                        HorizontalDivider()
                        Spacer(Modifier.size(4.dp))
                    }
                } ?: item {
                    Text(
                        "Здесь будут ваши бронирования",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        if (showMoveModal) MoveBottomSheet(datePickerState, onDismiss = {
            showMoveModal = false; datePickerState.selectedDateMillis = Instant.now().toEpochMilli()
        }, onOpen = { moveTimeDialogOpen = true })
        if (moveTimeDialogOpen) {
            currentMovedBook?.let { curr ->
                MovingDialog(onDismiss = { moveTimeDialogOpen = false },
                    chosenDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    } ?: LocalDateTime.now(),
                    movingSuccess = null,
                    onValidate = { from, to, callback ->
                        vm.validateBook(
                            curr.zoneId, from, to, curr.officeSeatId, callback
                        )
                    },
                    onClick = { from, to ->
                        moveTimeDialogOpen = false
                        showMoveModal = false
                        vm.move(from, to, curr.id)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveBottomSheet(datePickerState: DatePickerState, onDismiss: () -> Unit, onOpen: () -> Unit) {
    ModalBottomSheet(sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), onDismissRequest = {
        onDismiss()
    }, containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        Column {
            DatePicker(
                datePickerState, colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ), title = null
            )
            MyButton(onClick = {
                onOpen()
            }, text = "Перенести", modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp))
        }
    }
}

@Composable
fun MovingDialog(
    onDismiss: () -> Unit,
    chosenDate: LocalDateTime,
    movingSuccess: Boolean?,
    onValidate: (LocalDateTime, LocalDateTime, (Boolean) -> Unit) -> Unit,
    onClick: (LocalDateTime, LocalDateTime) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                movingSuccess.let { isSuccess ->
                    when (isSuccess) {
                        true -> {
                            Spacer(Modifier.height(16.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Перенос на ${chosenDate.toLocalDate()} успешен",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            MyButton(onClick = onDismiss, text = "OK")
                        }

                        false -> {
                            Spacer(Modifier.height(16.dp))
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Не удалось перенести",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            MyButton(onClick = onDismiss, text = "OK")
                        }

                        null -> {
                            Text(
                                "Перенос на ${chosenDate.toLocalDate()}",
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
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp, vertical = 4.dp
                                    ),
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
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp, vertical = 4.dp
                                    ),
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
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp, vertical = 4.dp
                                    ),
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
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp, vertical = 4.dp
                                    ),
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

                                var errorMessage by remember { mutableStateOf("") }

                                val from = chosenDate.withHour(hours[hourStartPager.currentPage])
                                    .withMinute(minutes[minuteStartPager.currentPage])
                                val to = chosenDate.withHour(hours[hourEndPager.currentPage])
                                    .withMinute(minutes[minuteEndPager.currentPage])

                                val deltaMins =
                                    (hours[hourEndPager.currentPage] - hours[hourStartPager.currentPage]) * 60 + (minutes[minuteEndPager.currentPage] - minutes[minuteStartPager.currentPage])
                                val delta = if (deltaMins >= 60) {
                                    "${deltaMins / 60} ч." + if (deltaMins % 60 > 0) " ${deltaMins % 60} мин." else ""
                                } else "$deltaMins мин."

                                if (from.isBefore(LocalDateTime.now()) || to.isBefore(LocalDateTime.now())) {
                                    errorMessage = "Время должно быть в будущем"
                                }
                                if (deltaMins < 0) {
                                    errorMessage = "Время конца должно быть после времени начала"
                                }
                                var ok by remember { mutableStateOf(false) }
                                val notSpinnin =
                                    (!minuteEndPager.isScrollInProgress && !minuteStartPager.isScrollInProgress && !hourStartPager.isScrollInProgress && !hourEndPager.isScrollInProgress)
                                LaunchedEffect(notSpinnin) {
                                    if (notSpinnin) {
                                        ok = false
                                        onValidate(
                                            from.minusHours(3),
                                            to.minusHours(3),
                                        ) {
                                            ok = it
                                        }
                                    }
                                }
                                val enabled =
                                    deltaMins > 0 && from.isAfter(LocalDateTime.now()) && from.isAfter(
                                        LocalDateTime.now()
                                    ) && ok && notSpinnin
                                MyButton(
                                    onClick = {
                                        onClick(
                                            from.minusHours(3),
                                            to.minusHours(3),
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = enabled && ok,
                                    text = if (enabled) "Подтвердить ($delta)" else "Нельзя",
                                    icon = if (enabled) Icons.Default.CheckCircle else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
