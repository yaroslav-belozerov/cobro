package com.yaabelozerov.tribede.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yaabelozerov.tribede.data.model.UserPassportDTO

@Composable
fun EnterPassportWidget(
    onDismissRequest: () -> Unit,
    onSend: (UserPassportDTO) -> Unit,
    navigateToUser: () -> Unit,

) {
    var passport by remember {
        mutableStateOf(UserPassportDTO(
            "", "", "", "", "", "", "", "")) }
    Dialog(onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ввести паспорт", style = MaterialTheme.typography.headlineSmall)
                Row {
                    MyOutlinedTextField(
                        value = passport.serial,
                        onValueChange = { passport = passport.copy(serial = it) },
                        modifier = Modifier.width(130.dp),
                        singleLine = true,
                        placeholder = { Text("Серия") }
                    )
                    Spacer(Modifier.width(4.dp))
                    MyOutlinedTextField(
                        value = passport.number,
                        onValueChange = { passport = passport.copy(number = it) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("Номер") }
                    )
                } // TODO cделать визуал трансформ
                MyOutlinedTextField(
                    value = passport.firstname,
                    onValueChange = { passport = passport.copy(firstname = it)},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Имя") }
                )
                MyOutlinedTextField(
                    value = passport.lastname,
                    onValueChange = { passport = passport.copy(lastname = it)},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Фамилия") }
                )
                MyOutlinedTextField(
                    value = passport.middlename,
                    onValueChange = { passport = passport.copy(middlename = it)},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Отчество") }
                )
                MyOutlinedTextField(
                    value = passport.birthday,
                    onValueChange = { passport = passport.copy(birthday = it)},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Дата рождения") }
                )
                Row(Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismissRequest) { Text("Закрыть") }
                    MyButton(onClick = { onSend(passport); navigateToUser() }, text = "Подтвердить", modifier = Modifier.weight(1f))
                }

            }
        }
    }
}