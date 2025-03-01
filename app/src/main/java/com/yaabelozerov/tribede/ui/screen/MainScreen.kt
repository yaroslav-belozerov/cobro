package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.ui.components.ReservationMap
import com.yaabelozerov.tribede.ui.components.Timeline
import com.yaabelozerov.tribede.ui.components.toSpace
import com.yaabelozerov.tribede.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var chosenBookingId by remember { mutableStateOf(state.zones.firstOrNull()?.id) }
    var visible by remember { mutableStateOf(false) }
    LazyColumn {
        item {
            Column {
                ReservationMap(chosenBookingId ?: "", {
                    visible = it.isNotEmpty(); if (it.isNotEmpty()) {
                    chosenBookingId = if (it == chosenBookingId) {
                        ""
                    } else {
                        it
                    }
                }
                }, state.zones.map { it.toSpace() })
                AnimatedVisibility(visible) {
                    Column {
                        state.zones.find { it.id == chosenBookingId }?.let {
                            Text(it.name, style = MaterialTheme.typography.titleLarge)
                            Text(it.description)
                            Text("0 / ${it.capacity}")
                        }
                    }
                }
            }
        }
        item {
            Text("Забронировать", style = MaterialTheme.typography.headlineMedium)
            val datePickerState = rememberDateRangePickerState()
//            DateRangePicker(datePickerState)
            Timeline(emptyList())
        }
    }
}