package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.data.model.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserState(
    val user: UserDto? = null
)

class UserViewModel(
    private val dataStore: DataStore = Application.dataStore,
    private val apiClient: ApiClient = ApiClient(),
) : ViewModel() {
    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            dataStore.getToken().distinctUntilChanged().collect {
                it.takeIf { it.isNotEmpty() }?.let {
                    val result = apiClient.getUser(it)
                    result.getOrNull()?.let {
                        _state.update { state ->
                            state.copy(user = it)
                        }
                    } ?: result.exceptionOrNull()?.let {
                        it.printStackTrace()
                        Application.dataStore.apply {
                            saveToken("")
                            saveIsAdmin(false)
                        }
                    }
                }
            }
        }
    }
}