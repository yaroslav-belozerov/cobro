package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.RegisterDto

@Composable
fun AuthPage(modifier: Modifier, onLogin: (LoginDto) -> Unit, onRegister: (RegisterDto) -> Unit) {
    var hasAccount by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    Crossfade(hasAccount) { acc ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("cobro", fontSize = 64.sp, fontWeight = FontWeight.Bold)
            if (acc) {
                var loginDTO by remember { mutableStateOf(LoginDto("", "")) }
                val isEmailValid =
                    remember(loginDTO.email.length) { (loginDTO.email.length in 5..50) }
                var typedUsername by remember { mutableStateOf(false) }
                OutlinedTextField(
                    loginDTO.email,
                    label = { Text("E-mail") },
                    enabled = !loading,
                    onValueChange = {
                        loginDTO = loginDTO.copy(email = it)
                        typedUsername = true
                    },
                    singleLine = true,
                    isError = !isEmailValid && typedUsername,
                    supportingText = { if (!isEmailValid && typedUsername) Text("Запишите e-mail в формате email@example.com") },
                    shape = MaterialTheme.shapes.medium
                )
                val isPasswordValid =
                    remember(loginDTO.password.length) { (loginDTO.password.length in 8..255) }
                var typedPassword by remember { mutableStateOf(false) }
                OutlinedTextField(
                    loginDTO.password,
                    label = { Text("Пароль") },
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        loginDTO = loginDTO.copy(password = it)
                        typedPassword = true
                    },
                    singleLine = true,
                    isError = !isPasswordValid && typedPassword,
                    supportingText = { if (!isPasswordValid && typedPassword) Text("Пароль должен содержать от 8 до 50 символов") },
                    shape = MaterialTheme.shapes.medium
                )
                if (!loading) Button(onClick = {
                    onLogin(loginDTO)
                    loading = true
                }, enabled = !loading && isEmailValid && isPasswordValid) {
                    Text("Войти")
                } else CircularProgressIndicator()
                TextButton(onClick = { hasAccount = false }, enabled = !loading) {
                    Text("Регистрация")
                }
            } else {
                var registerDTO by remember {
                    mutableStateOf(
                        RegisterDto(
                            name = "", surname = "", email = "", password = ""
                        )
                    )
                }
                OutlinedTextField(
                    registerDTO.name,
                    label = { Text("Имя") },
                    enabled = !loading,
                    singleLine = true,
                    onValueChange = { registerDTO = registerDTO.copy(name = it) },
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    registerDTO.surname,
                    label = { Text("Фамилия") },
                    enabled = !loading,
                    singleLine = true,
                    onValueChange = { registerDTO = registerDTO.copy(surname = it) },
                    shape = MaterialTheme.shapes.medium
                )
                val isEmailValid =
                    remember(registerDTO.email.length) { (registerDTO.email.length in 5..50) }
                var typedUsername by remember { mutableStateOf(false) }
                OutlinedTextField(
                    registerDTO.email,
                    label = { Text("E-mail") },
                    enabled = !loading,
                    onValueChange = {
                        registerDTO = registerDTO.copy(email = it)
                        typedUsername = true
                    },
                    singleLine = true,
                    isError = !isEmailValid && typedUsername,
                    supportingText = { if (!isEmailValid && typedUsername) Text("Логин должен содержать от 5 до 50 символов") },
                    shape = MaterialTheme.shapes.medium
                )
                val isPasswordValid =
                    remember(registerDTO.password.length) { (registerDTO.password.length in 8..255) }
                var typedPassword by remember { mutableStateOf(false) }
                OutlinedTextField(
                    registerDTO.password,
                    label = { Text("Пароль") },
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        registerDTO = registerDTO.copy(password = it)
                        typedPassword = true
                    },
                    singleLine = true,
                    isError = !isPasswordValid && typedPassword,
                    supportingText = { if (!isPasswordValid && typedPassword) Text("Пароль должен содержать от 8 до 50 символов") },
                    shape = MaterialTheme.shapes.medium
                )
                if (!loading) Button(onClick = {
                    onRegister(registerDTO)
                    loading = true
                }, enabled = !loading && isEmailValid && isPasswordValid) {
                    Text("Зарегистрироваться")
                } else CircularProgressIndicator()
                TextButton(onClick = { hasAccount = true }, enabled = !loading) {
                    Text("Вход")
                }
            }
        }
    }
}
