package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.yaabelozerov.tribede.ui.components.MyButton
import com.yaabelozerov.tribede.ui.components.MyTextField

@Composable
fun AuthScreen(
    onLogin: (LoginDto) -> Unit,
    onRegister: (RegisterDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    var hasAccount by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    Crossfade(hasAccount) { acc ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text(
                "cobro",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (acc) {
                var loginDTO by remember { mutableStateOf(LoginDto("", "")) }
                val isEmailValid = remember(loginDTO.email.length) {
                    loginDTO.email.matches(Regex("^[^@]+@[^@]+\\.[^@]+\$"))
                }
                var typedUsername by remember { mutableStateOf(false) }
                MyTextField(
                    loginDTO.email,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "E-mail",
                    enabled = !loading,
                    onValueChange = {
                        loginDTO = loginDTO.copy(email = it)
                        typedUsername = true
                    },
                    singleLine = true,
                    isError = !isEmailValid && typedUsername,
                    errorText = if (!isEmailValid && typedUsername) "Запишите e-mail в формате email@example.com" else null,
                )
                val isPasswordValid =
                    remember(loginDTO.password.length) { (loginDTO.password.length in 8..255) }
                var typedPassword by remember { mutableStateOf(false) }
                MyTextField(
                    loginDTO.password,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Пароль",
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        loginDTO = loginDTO.copy(password = it)
                        typedPassword = true
                    },
                    singleLine = true,
                    isError = !isPasswordValid && typedPassword,
                    errorText = if (!isPasswordValid && typedPassword) "Пароль должен содержать от 8 до 50 символов" else null,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        shape = MaterialTheme.shapes.small,
                        onClick = { hasAccount = false },
                        enabled = !loading
                    ) {
                        Text("Регистрация")
                    }
                    if (!loading) MyButton(
                        text = "Войти",
                        onClick = {
                            onLogin(loginDTO)
                            loading = true
                        },
                        enabled = !loading && isEmailValid && isPasswordValid,
                    ) else CircularProgressIndicator()
                }
            } else {
                var registerDTO by remember {
                    mutableStateOf(
                        RegisterDto(
                            name = "", surname = "", email = "", password = ""
                        )
                    )
                }
                MyTextField(registerDTO.name,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Имя",
                    enabled = !loading,
                    singleLine = true,
                    onValueChange = { registerDTO = registerDTO.copy(name = it) })
                MyTextField(
                    registerDTO.surname,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Фамилия",
                    enabled = !loading,
                    singleLine = true,
                    onValueChange = { registerDTO = registerDTO.copy(surname = it) },
                )
                val isEmailValid =
                    remember(registerDTO.email.length) {
                        registerDTO.email.matches(Regex("^[^@]+@[^@]+\\.[^@]+\$"))
                    }
                var typedUsername by remember { mutableStateOf(false) }
                MyTextField(
                    registerDTO.email,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "E-mail",
                    enabled = !loading,
                    onValueChange = {
                        registerDTO = registerDTO.copy(email = it)
                        typedUsername = true
                    },
                    singleLine = true,
                    isError = !isEmailValid && typedUsername,
                    errorText = if (!isEmailValid && typedUsername) "Логин должен содержать от 5 до 50 символов" else null,
                )
                val isPasswordValid =
                    remember(registerDTO.password.length) { (registerDTO.password.length in 8..255) }
                var typedPassword by remember { mutableStateOf(false) }
                MyTextField(
                    registerDTO.password,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Пароль",
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        registerDTO = registerDTO.copy(password = it)
                        typedPassword = true
                    },
                    singleLine = true,
                    isError = !isPasswordValid && typedPassword,
                    errorText = if (!isPasswordValid && typedPassword) "Пароль должен содержать от 8 до 50 символов" else null,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = { hasAccount = true },
                        enabled = !loading,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Вход")
                    }
                    if (!loading) MyButton(
                        text = "Зарегистрироваться",
                        onClick = {
                            onRegister(registerDTO)
                            loading = true
                        },
                        enabled = !loading && isEmailValid && isPasswordValid,
                    ) else CircularProgressIndicator()
                }
            }
        }
    }
}
