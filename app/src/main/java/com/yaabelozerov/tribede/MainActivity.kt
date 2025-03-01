package com.yaabelozerov.tribede

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.tribede.ui.App
import com.yaabelozerov.tribede.ui.theme.AppTheme
import com.yaabelozerov.tribede.ui.util.Nav
import com.yaabelozerov.tribede.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authVM = AuthViewModel(Application.apiClient, Application.dataStore)

        setContent {
            val navCtrl = rememberNavController()
            val current =
                Nav.entries.find { navCtrl.currentBackStackEntryAsState().value?.destination?.route == it.route }

            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (navCtrl.currentBackStackEntry?.destination?.route != Nav.AUTH.route) {
                            BottomAppBar {
                                Nav.entries.forEach {
                                    val selected = it == current
                                    it.icon?.let { ic -> // если иконка добавлена в файл Nav,
                                        // то он отобразит её в Bottom bar
                                        NavigationBarItem(
                                            icon = {
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
                        }

                    }
                ) { innerPadding ->
                    App(Modifier.padding(innerPadding).windowInsetsPadding(WindowInsets.ime), navCtrl)
                }
            }
        }
    }
}
