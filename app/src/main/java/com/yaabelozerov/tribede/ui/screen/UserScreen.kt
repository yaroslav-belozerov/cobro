package com.yaabelozerov.tribede.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun UserScreen(vm: UserViewModel) {
  val uiState by vm.state.collectAsState()
  val scope = rememberCoroutineScope()
  uiState.user?.let { userInfo ->
    LazyColumn(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            Text("Профиль", style = MaterialTheme.typography.headlineMedium)
            AsyncImage(
                model = userInfo.avatarUrl,
                contentDescription = "avatar",
                modifier = Modifier.size(100.dp).clip(CircleShape).clickable { vm.onPickMedia() },
                contentScale = ContentScale.Crop)
          }
          item { Text(userInfo.name, style = MaterialTheme.typography.headlineSmall) }
          item { Text(userInfo.email) }
          //            item {
          //                Text(UserRole.entries[userInfo.role].name)
          //            }
          if (userInfo.books != null) {
            userInfo.books?.let {
              item {
                Row {
                  Text("Мои брониирования")
                  Box(modifier = Modifier.weight(1f).height(2.dp).background(color = Color.Gray))
                }
              }
              items(userInfo.books.filter { BookStatus.entries[it.status] == BookStatus.PENDING }) {
                BookCard(it)
              }
              item {
                Row {
                  Text("История ")
                  Box(modifier = Modifier.weight(1f).height(2.dp).background(color = Color.Gray))
                }
              }
              items(userInfo.books.filter { BookStatus.entries[it.status] != BookStatus.PENDING }) {
                BookCard(it)
              }
            }
          } else {
            item {
              Text(
                  "Здесь будут ваши бронирования",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimary)
            }
          }

          item {
            TextButton(
                onClick = {
                  scope.launch {
                    Application.dataStore.apply {
                      saveToken("")
                      saveIsAdmin(false)
                    }
                  }
                }) {
                  Text("Выйти")
                }
          }
        }
  }
}
