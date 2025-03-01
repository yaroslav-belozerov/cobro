package com.yaabelozerov.tribede.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel

@Composable
fun UserScreen(vm: UserViewModel = viewModel()) {
    val uiState by vm.state.collectAsState()
    Text(uiState.name)
    Text(uiState.email)
}