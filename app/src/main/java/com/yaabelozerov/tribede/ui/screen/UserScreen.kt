package com.yaabelozerov.tribede.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.domain.model.BookStatus
import com.yaabelozerov.tribede.ui.components.BookCard
import com.yaabelozerov.tribede.ui.components.QrShowWidget
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(vm: UserViewModel) {
    val uiState by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showQrDialog by remember { mutableStateOf(false) }

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
            onDismissRequest = { showQrDialog = false},
            qrCode = uiState.qrString
        )
    }
    uiState.user?.let { userInfo ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { vm.fetchUserInfo() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Box(Modifier.fillMaxWidth()) {
                        Text("Профиль", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.align(
                            Alignment.Center))
                        IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = {
                            scope.launch {
                                Application.dataStore.apply {
                                    saveToken("")
                                    saveIsAdmin(false)
                                }
                            }
                        }) { Icon(Icons.AutoMirrored.Default.Logout, contentDescription = "logout") }
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
                item { Text(userInfo.email) }
                //            item {
                //                Text(UserRole.entries[userInfo.role].name)
                //            }
                userInfo.books?.let {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Мои бронирования", style = MaterialTheme.typography.headlineSmall)
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
                    val pending = userInfo.books.filter { BookStatus.entries[it.status] == BookStatus.PENDING }
                    itemsIndexed(pending) { index, book ->
                        BookCard(book, { vm.getQr(it); showQrDialog = true })
                        if (index != pending.size - 1) {
                            Spacer(Modifier.size(14.dp))
                            HorizontalDivider()
                            Spacer(Modifier.size(4.dp))
                        }

                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                        BookCard(it,  { vm.getQr(it) })
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
    }
}
