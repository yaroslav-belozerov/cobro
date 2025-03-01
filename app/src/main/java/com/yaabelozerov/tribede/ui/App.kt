package com.yaabelozerov.tribede.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yaabelozerov.tribede.data.model.UserRole
import com.yaabelozerov.tribede.ui.components.ReservationMapScreen
import com.yaabelozerov.tribede.ui.screen.UserScreen
import com.yaabelozerov.tribede.ui.util.Nav
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel

@Composable
fun App(modifier: Modifier = Modifier, navCtrl: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    val userState by userViewModel.state.collectAsState()
    val role = userState.user?.role?.let { UserRole.entries.getOrNull(it) }
    NavHost(navCtrl, startDestination = Nav.BOOK.route, modifier = modifier) {
        composable(Nav.BOOK.route) {
            role?.let { ReservationMapScreen(it) }
        }
        composable(Nav.USER.route) {
            UserScreen(userViewModel)
        }
    }
}