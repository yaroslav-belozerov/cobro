package com.yaabelozerov.tribede.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.ui.screen.AuthScreen
import kotlinx.serialization.Serializable

@Composable
fun App(modifier: Modifier = Modifier) {
    val isAuthenticated by Application.dataStore.getToken().collectAsState(null)
    if (isAuthenticated == "") {
        AuthScreen(onLogin = {}, onRegister = {})
    } else if (isAuthenticated != null) {
        LazyColumn(modifier) {
            item {
                Text("Header", style = MaterialTheme.typography.displayLarge)
                Text("Text")
            }
        }
    }
}