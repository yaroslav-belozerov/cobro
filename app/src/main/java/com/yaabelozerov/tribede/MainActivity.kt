package com.yaabelozerov.tribede

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.tribede.ui.App
import com.yaabelozerov.tribede.ui.components.MyButton
import com.yaabelozerov.tribede.ui.screen.AuthScreen
import com.yaabelozerov.tribede.ui.theme.AppTheme
import com.yaabelozerov.tribede.ui.util.Nav
import com.yaabelozerov.tribede.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val token by Application.dataStore.getToken().collectAsState(null)
            val navCtrl = rememberNavController()
            val current =
                Nav.entries.find { navCtrl.currentBackStackEntryAsState().value?.destination?.route == it.route }
            val authVm: AuthViewModel = viewModel()
            val authState by authVm.state.collectAsState()
            val scope = rememberCoroutineScope()

            AppTheme {
                if (token == "") {
                    AuthScreen(authVm)
                } else if (token != null) {
                    if (authState.displayAdminChoice) {
                        Scaffold {
                            Column(
                                Modifier
                                    .padding(it)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MyButton(onClick = {
                                    scope.launch {
                                        Application.dataStore.saveIsAdmin(false)
                                        authVm.closeAdminChoice()
                                    }
                                }, text = "Войти как клиент")
                                TextButton(onClick = {
                                    scope.launch {
                                        Application.dataStore.saveIsAdmin(true)
                                        authVm.closeAdminChoice()
                                    }
                                }, shape = MaterialTheme.shapes.small) {
                                    Text("Войти как папочка")
                                }
                            }
                        }
                    } else {
                        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                            BottomAppBar {
                                Nav.entries.forEach {
                                    val selected = it == current
                                    it.icon?.let { ic -> // если иконка добавлена в файл Nav,
                                        // то он отобразит её в Bottom bar
                                        NavigationBarItem(icon = {
                                            Icon(
                                                if (selected) ic.selectedIcon else ic.unselectedIcon,
                                                null,
                                                Modifier.size(30.dp)
                                            )
                                        },
                                            onClick = { navCtrl.navigate(it.route) },
                                            selected = selected
                                        )
                                    }
                                }
                            }
                        }) { innerPadding ->
                            App(
                                Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .windowInsetsPadding(WindowInsets.ime), navCtrl
                            )
                        }
                    }
                }
            }
        }
    }
}
