package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import com.yaabelozerov.tribede.data.model.UserRole
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val token: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val displayAdminChoice: Boolean = false,
)

class AuthViewModel(
    private val api: ApiClient = ApiClient(),
    private val dataStore: DataStore = Application.dataStore,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun login(dto: LoginDto) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = api.login(dto)
            result.getOrNull()?.let {
                val user = api.getUser(it.token).also { it.exceptionOrNull()?.printStackTrace() }.getOrNull() ?: return@let
                if (user.role == UserRole.ADMIN.ordinal) {
                    dataStore.saveToken(it.token)
                    _state.update { it.copy(displayAdminChoice = true) }
                } else {
                    dataStore.saveToken(it.token)
                    _state.update { it.copy(error = null) }
                }
            }
            result.exceptionOrNull()?.let {
                it.printStackTrace()
                when (it) {
                    is ClientRequestException -> _state.update { it.copy(error = "Неправильный логин или пароль") }
                    else -> _state.update { it.copy(error = "Что-то пошло не так") }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun register(dto: RegisterDto) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = api.register(dto)
            result.getOrNull()?.let {
                _state.update { it.copy(error = null) }
                dataStore.saveToken(it.token)
            } ?: _state.update { it.copy(error = "Что-то пошло не так") }
            result.exceptionOrNull()?.printStackTrace()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun closeAdminChoice() {
        _state.update { it.copy(displayAdminChoice = false) }
    }

}
