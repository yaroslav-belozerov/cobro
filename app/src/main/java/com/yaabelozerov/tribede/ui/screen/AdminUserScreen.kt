package com.yaabelozerov.tribede.ui.screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.UserRole
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminUserScreen(vm: AdminViewModel = viewModel(), onNavigateToDetailed: () -> Unit) {
    val state = vm.state.collectAsState().value
    val scope = rememberCoroutineScope()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth()) {
                Text(
                    "Клиенты",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
                IconButton(modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(top = 4.dp),
                    onClick = {
                        scope.launch {
                            Application.dataStore.apply {
                                saveIsAdmin(false)
                                saveToken("")
                            }
                        }
                    }) { Icon(Icons.AutoMirrored.Default.Logout, contentDescription = "logout") }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
        }
        items(state.users) { user ->
            UserCard(user) {
                println("Selected ${user.name}")
                vm.selectCurrent(user)
                onNavigateToDetailed()
            }
        }
    }
}

@Composable
fun UserCard(model: UserDto, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row {
                Box(Modifier.size(50.dp)) {
                    AsyncImage(
                        model = model.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.size(16.dp))
                Column {
                    Text(model.name, style = MaterialTheme.typography.titleMedium)
                    Text(model.email)
                }
            }
            Text(
                when (UserRole.entries[model.role]) {
                    UserRole.ADMIN -> "Администратор"
                    UserRole.CLIENT -> "Клиент"
                    UserRole.INTERNAL -> "Внутренний пользователь"
                }, color = MaterialTheme.colorScheme.tertiary
            )
        }
    }

}