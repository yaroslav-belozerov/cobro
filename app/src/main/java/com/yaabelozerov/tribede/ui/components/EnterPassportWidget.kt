package com.yaabelozerov.tribede.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.model.UserPassportDTO
import kotlinx.coroutines.launch

@Composable
fun EnterPassportWidget(
    onDismissRequest: () -> Unit,
    onSend: (UserPassportDTO, Uri) -> Unit,
    navigateToUser: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var showCamera by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    var passport by remember {
        mutableStateOf(
            UserPassportDTO(
                "", "", "", "", "", "", "", ""
            )
        )
    }
    Dialog(onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showCamera) {
                    Box(
                        Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth().padding(top = 16.dp).clip(MaterialTheme.shapes.small)
                    ) {
                        CameraCapture(
                            onImageCaptured = { capturedImageUri = it; showCamera = false },
                            lifecycleOwner = lifecycleOwner
                        )
                    }
                } else {
                    Text("Ввести паспорт", style = MaterialTheme.typography.headlineSmall)
                    Row {
                        MyOutlinedTextField(value = passport.serial,
                            onValueChange = { passport = passport.copy(serial = it) },
                            modifier = Modifier.width(130.dp),
                            singleLine = true,
                            placeholder = { Text("Серия") })
                        Spacer(Modifier.width(4.dp))
                        MyOutlinedTextField(value = passport.number,
                            onValueChange = { passport = passport.copy(number = it) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = { Text("Номер") })
                    } // TODO cделать визуал трансформ
                    MyOutlinedTextField(value = passport.firstname,
                        onValueChange = { passport = passport.copy(firstname = it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Имя") })
                    MyOutlinedTextField(value = passport.lastname,
                        onValueChange = { passport = passport.copy(lastname = it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Фамилия") })
                    MyOutlinedTextField(value = passport.middlename,
                        onValueChange = { passport = passport.copy(middlename = it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Отчество") })
                    MyOutlinedTextField(value = passport.birthday,
                        onValueChange = { passport = passport.copy(birthday = it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Дата рождения") })
                    TextButton(onClick = onDismissRequest) { Text("Закрыть") }
                    MyButton(
                        onClick = { showCamera = true },
                        text = if (showCamera) "Ввести данные" else "Сделать фото"
                    )
                }
                MyButton(
                    onClick = { capturedImageUri?.let {
                        onSend(passport, it); navigateToUser()
                    } },
                    text = "Отправить"
                )
            }
        }
    }
}