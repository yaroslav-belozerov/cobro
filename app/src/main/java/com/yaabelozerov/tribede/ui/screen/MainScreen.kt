package com.yaabelozerov.tribede.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.ui.components.Booking
import com.yaabelozerov.tribede.ui.components.ReservationMap
import com.yaabelozerov.tribede.ui.components.ReservationMapPreview
import com.yaabelozerov.tribede.ui.components.Timeline
import com.yaabelozerov.tribede.ui.components.toSpace
import com.yaabelozerov.tribede.ui.viewmodels.MainViewModel

@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var chosenBookingId by remember { mutableStateOf("") }
    LazyColumn {
        item {
            ReservationMap(chosenBookingId,
                { chosenBookingId = it },
                state.zones.map { it.toSpace() })
        }
        item { BookUi(emptyList()) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookUi(list: List<Booking>) {
    Column {
        Text("Забронировать", style = MaterialTheme.typography.headlineMedium)
        val datePickerState = rememberDateRangePickerState()
        DateRangePicker(datePickerState)
        Timeline(list)
    }
}