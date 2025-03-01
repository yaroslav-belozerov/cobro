package com.yaabelozerov.tribede.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.ui.components.ReservationMapPreview
import com.yaabelozerov.tribede.ui.screen.AuthScreen
import com.yaabelozerov.tribede.ui.screen.UserScreen
import com.yaabelozerov.tribede.ui.util.Nav
import kotlinx.serialization.Serializable

@Composable
fun App(modifier: Modifier = Modifier, navCtrl: NavHostController) {
    NavHost(navCtrl, startDestination = "user") {
        composable(Nav.BOOK.route) {
            LazyColumn(modifier) {
                item {
                    ReservationMapPreview()
                }
            }
        }
        composable(Nav.USER.route) {
            UserScreen(modifier)
        }
    }
}