package com.yaabelozerov.tribede.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import com.yaabelozerov.tribede.ui.components.MyButton
import com.yaabelozerov.tribede.ui.components.MyTextField
import com.yaabelozerov.tribede.ui.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
) {
  var hasAccount by remember { mutableStateOf(true) }
  val state = vm.state.collectAsState().value
  Scaffold { innerPadding ->
    Crossfade(hasAccount) { acc ->
      Column(
          modifier = modifier.padding(innerPadding).fillMaxSize().padding(24.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      ) {
        Text(
            "cobro",
            style =
                MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black, fontSize = 72.sp, fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(bottom = 12.dp))
        if (acc) {
          var loginDTO by remember { mutableStateOf(LoginDto("", "")) }
          val isEmailValid =
              remember(loginDTO.email.length) {
                loginDTO.email.matches(Regex("^[^@]+@[^@]+\\.[^@]+\$"))
              }
          var typedUsername by remember { mutableStateOf(false) }
          MyTextField(
              loginDTO.email,
              modifier = Modifier.fillMaxWidth(),
              placeholder = "E-mail",
              enabled = !state.isLoading,
              onValueChange = {
                loginDTO = loginDTO.copy(email = it)
                typedUsername = true
              },
              singleLine = true,
              isError = !isEmailValid && typedUsername,
              errorText =
                  if (!isEmailValid && typedUsername) "Запишите e-mail в формате email@example.com"
                  else null,
          )
          val isPasswordValid =
              remember(loginDTO.password.length) { (loginDTO.password.length in 8..255) }
          MyTextField(
              loginDTO.password,
              modifier = Modifier.fillMaxWidth(),
              placeholder = "Пароль",
              enabled = !state.isLoading,
              visualTransformation = PasswordVisualTransformation(),
              onValueChange = { loginDTO = loginDTO.copy(password = it) },
              singleLine = true,
              isError = !isPasswordValid,
              errorText =
                  if (!isPasswordValid) "Пароль должен содержать от 8 до 50 символов" else null,
          )
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(
                shape = MaterialTheme.shapes.small,
                onClick = { hasAccount = false },
                enabled = !state.isLoading) {
                  Text("Регистрация")
                }
            if (!state.isLoading)
                MyButton(
                    text = "Войти",
                    onClick = { vm.login(loginDTO) },
                    enabled = isEmailValid && isPasswordValid,
                )
            else CircularProgressIndicator()
          }
        } else {
          var registerDTO by remember {
            mutableStateOf(RegisterDto(name = "", email = "", password = ""))
          }
          MyTextField(
              registerDTO.name,
              modifier = Modifier.fillMaxWidth(),
              placeholder = "Имя",
              enabled = !state.isLoading,
              singleLine = true,
              onValueChange = { registerDTO = registerDTO.copy(name = it) })
          val isEmailValid =
              remember(registerDTO.email.length) {
                registerDTO.email.matches(Regex("^[^@]+@[^@]+\\.[^@]+\$"))
              }
          MyTextField(
              registerDTO.email,
              modifier = Modifier.fillMaxWidth(),
              placeholder = "E-mail",
              enabled = !state.isLoading,
              onValueChange = { registerDTO = registerDTO.copy(email = it) },
              singleLine = true,
              isError = !isEmailValid,
              errorText = if (!isEmailValid) "Логин должен содержать от 5 до 50 символов" else null,
          )
          val isPasswordValid =
              remember(registerDTO.password.length) { (registerDTO.password.length in 8..255) }
          MyTextField(
              registerDTO.password,
              modifier = Modifier.fillMaxWidth(),
              placeholder = "Пароль",
              enabled = !state.isLoading,
              visualTransformation = PasswordVisualTransformation(),
              onValueChange = { registerDTO = registerDTO.copy(password = it) },
              singleLine = true,
              isError = !isPasswordValid,
              errorText =
                  if (!isPasswordValid) "Пароль должен содержать от 8 до 50 символов" else null,
          )
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(
                onClick = { hasAccount = true },
                enabled = !state.isLoading,
                shape = MaterialTheme.shapes.small) {
                  Text("Вход")
                }
            if (!state.isLoading)
                MyButton(
                    text = "Зарегистрироваться",
                    onClick = { vm.register(registerDTO) },
                    enabled = isEmailValid && isPasswordValid,
                )
            else CircularProgressIndicator()
          }
        }
        if (state.error != null) {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(state.error, color = MaterialTheme.colorScheme.error)
          }
        }
      }
    }
  }
}
