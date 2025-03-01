package com.yaabelozerov.tribede.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.model.UserRole
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun UserScreen(vm: UserViewModel) {
    val uiState by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    uiState.user?.let { userInfo ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(userInfo.name)
            }
            item {
                Text(userInfo.email)
            }
            item {
                Text(UserRole.entries[userInfo.role].name)
            }
            item {
                TextButton(onClick = { scope.launch {
                    Application.dataStore.apply {
                        saveToken("")
                        saveIsAdmin(false)
                    }
                } }) { Text("Выйти") }
            }
        }
    }
}