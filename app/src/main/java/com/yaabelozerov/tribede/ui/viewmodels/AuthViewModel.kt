package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.data.Net
import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val token: String = "",
    val error: String? = null
)

class AuthViewModel(private val api: ApiClient = ApiClient(), private val dataStore: DataStore = Application.dataStore): ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun login(dto: LoginDto) {
        viewModelScope.launch {
            val result = api.login(dto)
            if (result.isSuccess) {
                _state.update { it.copy(error = null) }
                result.getOrNull()?.let {
                    dataStore.saveToken(it.token)
                }
            } else {
                _state.update { it.copy(error = "Что-то пошло не так") }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    fun register(dto: RegisterDto) {
        viewModelScope.launch {
            println(dto)
            val result = api.register(dto)
            if (result.isSuccess) {
                _state.update { it.copy(error = null) }
                result.getOrNull()?.let {
                    dataStore.saveToken(it.token)
                }
            } else {
                result.exceptionOrNull()?.printStackTrace()
                _state.update { it.copy(error = "Что-то пошло не так") }
            }
        }
    }
}
