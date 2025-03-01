package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.ui.components.ReservationMap
import com.yaabelozerov.tribede.ui.components.Timeline
import com.yaabelozerov.tribede.ui.components.toSpace
import com.yaabelozerov.tribede.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    state.zones.takeIf { it.isNotEmpty() }?.let { zones ->
        var chosenBook by remember { mutableStateOf(zones.first()) }
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Spacer(Modifier.size(24.dp))
            Text("Забронировать", style = MaterialTheme.typography.headlineMedium)
            Column {
                ReservationMap(if (expanded) chosenBook else null, {
                    if (chosenBook == it) {
                        expanded = !expanded
                    } else {
                        chosenBook = it
                        expanded = true
                    }
                }, zones)
                AnimatedVisibility(
                    expanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column {
                        Text(chosenBook.name, style = MaterialTheme.typography.titleLarge)
                        Text(chosenBook.description)
                        Text("0 / ${chosenBook.maxPeople}")
                        Timeline(emptyList())
                    }
                }
            }
        }
    }
}