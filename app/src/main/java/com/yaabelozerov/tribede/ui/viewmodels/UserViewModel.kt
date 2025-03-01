package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserState(
    val name: String = "",
    val email: String = ""
)

class UserViewModel(private val dataStore: DataStore = Application.dataStore, private val apiClient: ApiClient = ApiClient()): ViewModel() {
    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.getToken().first().takeIf { it.isNotEmpty() }?.let {
                val result = apiClient.getUser(it)
                result.getOrNull()?.let {
                    _state.update {
                        UserState(name = it.name, email = it.email)
                    }
                } ?: kotlin.run {
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
        }
    }
}