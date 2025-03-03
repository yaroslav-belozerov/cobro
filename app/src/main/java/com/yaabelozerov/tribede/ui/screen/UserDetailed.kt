package com.yaabelozerov.tribede.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel

@Composable
fun UserDetailed(vm: AdminViewModel = viewModel(), onBack: () -> Unit) {
    val user = vm.state.collectAsState().value.currentUser
    val passport = vm.state.collectAsState().value.currentPassport
    println("screen" + user.toString())
    println(passport)
//    BackHandler {
//        onBack()
//    }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        user?.let {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Пользователь", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(100.dp)) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    Column {
                        Text(user.name, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.size(8.dp))
                        Text(user.email, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
            item {
                Text("Роль: ${user.role}")
            }

        }
        passport?.let {
            item {
                Text("Паспорт", style = MaterialTheme.typography.headlineMedium) // TODO Нарисовать юай для паспорта
            }
            item {
                HorizontalDivider()
            }
        }
        user?.books?.let {
            item {
                Text("Бронирования", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.size(12.dp))
                HorizontalDivider()
                Spacer(Modifier.size(4.dp))
            }
            items(it) { book ->
                AdminBookCardForBookUI(model = book, onMove = {}, onDelete = vm::deleteBooking)
                Spacer(Modifier.size(12.dp))
                HorizontalDivider()
                Spacer(Modifier.size(4.dp))
            }
        }
    }

}