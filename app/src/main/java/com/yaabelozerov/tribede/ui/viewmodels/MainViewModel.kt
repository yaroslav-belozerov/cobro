package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.data.model.ZoneDto
import com.yaabelozerov.tribede.data.model.toDomainModel
import com.yaabelozerov.tribede.domain.model.BookingUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val zones: List<ZoneDto> = emptyList(),
    val currentBookings: List<BookingUI> = emptyList()
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

    fun getBookings(zoneId: String, seatId: String?) {
        viewModelScope.launch {
            Application.dataStore.getToken().distinctUntilChanged().collect { token ->
                val result = api.getBookings(token, zoneId, seatId)
                result.getOrNull()?.let {
                    _state.update { state ->
                        state.copy(currentBookings = it.map { it.toDomainModel() })
                    }
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}