package com.yaabelozerov.tribede.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.domain.model.AdminBookingUI
import com.yaabelozerov.tribede.domain.model.BookStatus
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel

@Composable
fun MainAdminScreen(vm: AdminViewModel = viewModel()) {
    val state = vm.state.collectAsState().value
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text("Все бронирования", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
        }
        itemsIndexed(state.bookings) { index, model ->
            AdminBookCard(model, onDelete = vm::deleteBooking)
            if (index != state.bookings.size - 1) {
                Spacer(Modifier.size(12.dp))
                HorizontalDivider()
                Spacer(Modifier.size(4.dp))
            }
        }
    }
}

@Composable
fun AdminBookCard(
    model: AdminBookingUI,
    onMove: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onScan: (String) -> Unit = {}
) {
    // только Pending или Active


    Column(Modifier.fillMaxWidth()) {
        Text(model.zoneName, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                model.officeSeatNumber?.let {
                    Text("Место $it", style = MaterialTheme.typography.titleSmall)
                }
                Text(
                    "${model.start.toLocalDate()}"
                )

                Text(
                    "c  ${model.start.hour.toString().padStart(2, '0')}:" +
                            model.start.minute.toString().padStart(2, '0')
                )

                Text(
                    "до ${model.end.hour.toString().padStart(2, '0')}:" +
                            model.end.minute.toString().padStart(2, '0')
                )

                if (model.status == BookStatus.PENDING) {
                    Row {
                        Icon(Icons.Filled.HourglassTop, contentDescription = null)
                        Text("В ожидании")
                    }
                } else {
                    Row {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Text("В процессе")
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            if (model.status != BookStatus.ACTIVE) {
                FloatingActionButton(
                    onClick = { onMove(model.id) }, shape = RoundedCornerShape(6.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) { Icon(Icons.Filled.Update, null) }
                FloatingActionButton(
                    onClick = { onDelete(model.id) }, shape = RoundedCornerShape(6.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) { Icon(Icons.Filled.Cancel, null) }
                FloatingActionButton(
                    onClick = { onScan(model.id) }, shape = RoundedCornerShape(6.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) { Icon(Icons.Filled.QrCode, null) }
            }
            Spacer(Modifier.width(16.dp))


        }
    }
}