package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.model.ConfirmQr
import com.yaabelozerov.tribede.ui.components.EnterPassportWidget
import com.yaabelozerov.tribede.ui.components.ScanQR
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun String.validQR(): Boolean {
    return this.length == 10 && this.all { it.isDigit() }
}

@Composable
fun QrPage(
    hasPermission: Boolean, vm: AdminViewModel = viewModel(), goBack: () -> Unit,
    navigateToUser: () -> Unit
) {
    val invalidStr = "Некорректный код"
    val validStr = "Подошло!"
    var text by remember { mutableStateOf(invalidStr) }

    var showPassportWidget by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var finalCode by remember { mutableStateOf("") }


    AnimatedVisibility(showPassportWidget) {
        if (showPassportWidget) {
            EnterPassportWidget(
                onDismissRequest = { showPassportWidget = false },
                onSend = { pass, img ->
                    vm.sendPassportAndImage(pass, img, userId)
                    val user = vm.state.value.users.find { it.id == userId }
                    user?.let {
                        vm.selectCurrent(user)
                    }
                    vm.viewModelScope.launch {
                        Application.dataStore.getToken().first().let { token ->
                            Application.apiClient.confirmQr(token, ConfirmQr(finalCode))
                                .exceptionOrNull()?.printStackTrace()
                        }
                    }
                },
                navigateToUser
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        if (hasPermission) ScanQR(modifier = Modifier
            .padding(32.dp)
            .border(
                4.dp,
                if (text.validQR()) MaterialTheme.colorScheme.primary else Color.Transparent,
                MaterialTheme.shapes.medium
            )
            .clip(
                MaterialTheme.shapes.medium
            ), hasPermission, onScan = {
            text = it
        })
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(0.dp, 0.dp, 0.dp, 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (text.validQR()) validStr else invalidStr, fontSize = 18.sp
            )
            Button(onClick = {
                vm.viewModelScope.launch {
                    Application.dataStore.getToken().first().let { token ->
                        val res = Application.apiClient.confirmQr(token, ConfirmQr(text))
                        res.getOrNull()?.let { r ->
                            if (r.needsPassport) {
                                println("qr page $r")
                                showPassportWidget = true
                                userId = r.userId
                                finalCode = text
                            } else {
                                userId = r.userId
                                val user = vm.state.value.users.find { it.id == userId }
                                user?.let {
                                    vm.selectCurrent(user)
                                }
                                navigateToUser()
                            }

                        }
                        res.exceptionOrNull()?.printStackTrace()
                    }
                }
            }, enabled = text.validQR()) {
                Text(
                    text = "Подтвердить",
                    fontSize = 18.sp
                )
            }
            TextButton(
                onClick = goBack
            ) {
                Text("Назад")
            }
        }
    }
}