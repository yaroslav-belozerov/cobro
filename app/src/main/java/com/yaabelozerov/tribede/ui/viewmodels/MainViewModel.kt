package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.model.ZoneDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val zones: List<ZoneDto> = emptyList()
)

class MainViewModel(private val api: ApiClient = ApiClient()): ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        fetchZones()
    }

    private fun fetchZones() {
        viewModelScope.launch {
            Application.dataStore.getToken().distinctUntilChanged().collect { token ->
                val result = api.getZones(token)
                result.getOrNull()?.let {
                    _state.update { state ->
                        state.copy(zones = it)
                    }
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}