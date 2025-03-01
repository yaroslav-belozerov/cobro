package com.yaabelozerov.tribede.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.ui.components.ReservationMap
import com.yaabelozerov.tribede.ui.components.ReservationMapPreview
import com.yaabelozerov.tribede.ui.components.toSpace
import com.yaabelozerov.tribede.ui.viewmodels.MainViewModel

@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    LazyColumn {
        item {
            ReservationMap(state.zones.map { it.toSpace() })
        }
    }
}