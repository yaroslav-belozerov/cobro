package com.yaabelozerov.tribede.ui.screen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.ui.components.ScanQR
import com.yaabelozerov.tribede.ui.viewmodels.AdminViewModel

fun String.validQR(): Boolean {
    return this.length == 10 && this.all { it.isDigit() }
}

@Composable
fun QrPage(
    hasPermission: Boolean, vm: AdminViewModel = viewModel(), goBack: () -> Unit
) {
    val invalidStr = "Некорректный код"
    val validStr = "Подошло!"
    var text by remember { mutableStateOf(invalidStr) }
    Box(modifier = Modifier.fillMaxSize()) {

        if (hasPermission) ScanQR(modifier = Modifier.padding(32.dp).border(4.dp, if (text.validQR()) MaterialTheme.colorScheme.primary else Color.Transparent, MaterialTheme.shapes.medium).clip(
            MaterialTheme.shapes.medium), hasPermission, onScan = {
            text = it
        })
        Column(
            Modifier.align(Alignment.BottomCenter).padding(0.dp, 0.dp, 0.dp, 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (text.validQR()) validStr else invalidStr, fontSize = 18.sp
            )
            Button(onClick = {
                vm.confirmQr(text)
                goBack() }, enabled = text.validQR()) {
                Text(
                    text =  "Подтвердить",
                    fontSize = 18.sp
                )
            }
            TextButton(onClick = goBack) {
                Text("Назад")
            }
        }
    }
}