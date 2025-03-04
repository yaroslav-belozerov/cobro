package com.yaabelozerov.tribede.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.ui.screen.AdminUserScreen
import com.yaabelozerov.tribede.ui.screen.ActionsScreen
import com.yaabelozerov.tribede.ui.screen.MainAdminScreen
import com.yaabelozerov.tribede.ui.screen.MainScreen
import com.yaabelozerov.tribede.ui.screen.QrPage
import com.yaabelozerov.tribede.ui.screen.UserDetailed
import com.yaabelozerov.tribede.ui.screen.UserScreen
import com.yaabelozerov.tribede.ui.util.Nav
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel
import com.yaabelozerov.tribede.ui.viewmodels.UserViewModel

@Composable
fun App(
    modifier: Modifier = Modifier,
    navCtrl: NavHostController,
    hasCameraPermission: Boolean,
    askForPermission: () -> Unit,
) {
    val userViewModel: UserViewModel = viewModel()
    val userState by userViewModel.state.collectAsState()
    val adminVM: AdminViewModel = viewModel()

    NavHost(navCtrl, startDestination = Nav.BOOK.route, modifier = modifier, enterTransition = {
        fadeIn(
            tween(200)
        )
    }, exitTransition = {
        fadeOut(tween(200))
    }) {
        composable(Nav.BOOK.route) {
            Application.dataStore.getIsAdmin().collectAsState(null).value.let { isAdmin ->
                if (isAdmin != null) {
                    if (isAdmin) {
                        userState.user?.let {
                            MainAdminScreen(vm = adminVM, navigateToScan = {
                                askForPermission()
                                navCtrl.navigate(Nav.SCAN.route)
                            })
                        }
                    } else {
                        userState.user?.let {
                            MainScreen(userVm = userViewModel)
                        }
                    }
                }
            }
        }
        composable(Nav.USER.route) {
            Application.dataStore.getIsAdmin().collectAsState(null).value.let { isAdmin ->
                if (isAdmin != null) {
                    if (isAdmin) {
                        userState.user?.let {
                            AdminUserScreen(vm = adminVM) { navCtrl.navigate(Nav.USER_DETAILED.route) }
                        }
                    } else {
                        userState.user?.let {
                            UserScreen(userViewModel)
                        }
                    }
                }
            }
        }

        composable(Nav.SCAN.route, enterTransition = { fadeIn() }, exitTransition = { fadeOut() }

        ) {

            QrPage(hasCameraPermission,
                goBack = { navCtrl.navigateUp() },
                vm = adminVM,
                navigateToUser = { navCtrl.navigate(Nav.USER_DETAILED.route) })
        }

        composable(Nav.USER_DETAILED.route) {
            UserDetailed(vm = adminVM, onBack = { navCtrl.navigateUp() })
        }

        composable("actions") {
            ActionsScreen(adminVM)
        }
    }
}