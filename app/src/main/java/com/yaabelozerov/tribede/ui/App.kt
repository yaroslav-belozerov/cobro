package com.yaabelozerov.tribede.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.ui.screen.MainAdminScreen
import com.yaabelozerov.tribede.ui.screen.MainScreen
import com.yaabelozerov.tribede.ui.screen.UserScreen
import com.yaabelozerov.tribede.ui.util.Nav
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel

@Composable
fun App(modifier: Modifier = Modifier, navCtrl: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    val userState by userViewModel.state.collectAsState()
    NavHost(navCtrl, startDestination = Nav.BOOK.route, modifier = modifier) {
        composable(Nav.BOOK.route) {
            Application.dataStore.getIsAdmin().collectAsState(null).value.let { isAdmin ->
                if (isAdmin != null) {
                    if (isAdmin) {
                        userState.user?.let {
                            MainAdminScreen()
                        }
                    } else {
                        userState.user?.let {
                            MainScreen()
                        }
                    }
                }
            }
        }
        composable(Nav.USER.route) {
            UserScreen(userViewModel)
        }
    }
}