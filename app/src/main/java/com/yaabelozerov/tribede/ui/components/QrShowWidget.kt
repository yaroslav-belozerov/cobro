package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage

@Composable
fun QrShowWidget(onDismissRequest: () -> Unit, qrCode: String?) {
    Dialog(onDismissRequest) {
        Card {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)) {

                Text("Вход", style = MaterialTheme.typography.headlineSmall)
                Box(Modifier.size(200.dp)) {
                    if (qrCode != null) {
                        val qrBitmap = generateQRCode(qrCode)
                        if (qrBitmap != null) {
                            AsyncImage(model = qrBitmap, contentDescription = "qr code",
                                modifier = Modifier.fillMaxSize())

                        }
                    }
                }
                if (qrCode != null) {
                    Text(qrCode, style = MaterialTheme.typography.titleLarge)
                }
                Text("Покажите этот код сотруднику коворкинга, чтобы войти",
                    modifier = Modifier.padding(start = 8.dp))
                Row {
                    Spacer(Modifier.weight(1f))
                    TextButton(onDismissRequest) { Text("Закрыть") }
                }


            }
        }
    }
}