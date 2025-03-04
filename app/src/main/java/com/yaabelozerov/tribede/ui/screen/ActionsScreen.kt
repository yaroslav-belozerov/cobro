package com.yaabelozerov.tribede.ui.screen

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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaabelozerov.tribede.data.model.ActionDTO
import com.yaabelozerov.tribede.domain.model.ActionUI
import com.yaabelozerov.tribede.ui.util.Actions
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel

@Composable
fun ActionsScreen(vm: AdminViewModel) {
    val actions = vm.state.collectAsState().value.actions
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
                    "Запросы",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
        }
        items(actions) {
            ActionCard(it)
        }
    }
}

@Composable
fun ActionCard(action: ActionUI) {
    var act = Actions.OTHER
    if (action.actionNumber != null) {
        act = Actions.entries[action.actionNumber]
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp)) {
            Icon(act.icon, null)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(act.title, style = MaterialTheme.typography.titleMedium)
                Text(action.text)
                Text("от ${action.createdAt}")
            }
        }

    }

}

