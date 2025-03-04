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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.domain.model.ActionUI
import com.yaabelozerov.tribede.ui.util.Actions
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsScreen(vm: AdminViewModel) {
    val actions = vm.state.collectAsState().value.actions
    PullToRefreshBox(
        isRefreshing = vm.state.collectAsState().value.isLoading,
        onRefresh = { vm.fetchData() }
    ) {
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
                ActionCard(it, { vm.fetchData() })
            }
        }
    }

}

@Composable
fun ActionCard(action: ActionUI, update: () -> Unit) {
    var act = Actions.OTHER
    if (action.actionNumber != null) {
        act = Actions.entries[action.actionNumber]
    }

    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(act.icon, null)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(act.title, style = MaterialTheme.typography.titleMedium)
                Text(action.text)
                Text("от ${action.createdAt.split("T").take(1)[0]}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = action.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(action.username, overflow = TextOverflow.Ellipsis)
                }
            }
            Checkbox(
                checked = action.status == 2,
                onCheckedChange = {
                    scope.launch {
                        Application.dataStore.getToken().first().let { token ->
                            if (action.status == 2) {
                                Application.apiClient.markAction(action.id, token, 0)
                                update()
                            } else {
                                Application.apiClient.markAction(action.id, token, 2)
                                update()
                            }
                        }
                    }
                },
            )

        }

    }

}

