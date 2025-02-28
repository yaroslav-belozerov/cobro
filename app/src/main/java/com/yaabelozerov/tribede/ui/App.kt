package com.yaabelozerov.tribede.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.ui.screen.AuthScreen
import kotlinx.serialization.Serializable

@Composable
fun App(modifier: Modifier = Modifier) {
    val isAuthenticated by Application.dataStore.getToken().collectAsState(null)
    val navCtrl = rememberNavController()
    if (isAuthenticated == "") {
        AuthScreen()
    } else if (isAuthenticated != null) {
        NavHost(navCtrl, startDestination = "reservations") {
            composable("reservations") {
                LazyColumn(modifier) {
                    item {
                    }
                }
            }
        }
    }
}