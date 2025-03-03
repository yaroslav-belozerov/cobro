package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.model.ConfirmQr
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.UserPassportDTO
import com.yaabelozerov.tribede.data.model.toDomainModel
import com.yaabelozerov.tribede.domain.model.AdminBookingUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminState(
    val zones: List<String> = emptyList(),
    val bookings: List<AdminBookingUI> = emptyList(),

    val isLoading: Boolean = false,
    val currentZones: List<String> = emptyList(),

    val users: List<UserDto> = emptyList(),
    val currentUser: UserDto? = null,
    val currentPassport: UserPassportDTO? = null

)

class AdminViewModel(private val api: ApiClient = Application.apiClient) : ViewModel() {
    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    init {
        println("im inited")
        fetchData()

    }

    fun addFilterZone(zone: String) {
        _state.update { it.copy(currentZones = it.currentZones + zone) }
    }

    fun deleteFilterZone(zone: String) {
        _state.update { it.copy(currentZones = it.currentZones - zone) }
    }

    fun deleteBooking(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Application.dataStore.getToken().first().let { token ->
                api.deleteBook(token, id)
                fetchData()
            }
        }
    }

    fun selectCurrent(user: UserDto) {
        _state.update { it.copy(currentUser = user) }
        println("vm ${_state.value.currentUser}")
        viewModelScope.launch(Dispatchers.IO) {
            Application.dataStore.getToken().first().let { token ->
                val result = api.getAdminPassport(token, user.id)
                result.getOrNull()?.let { res ->
                    _state.update { it.copy(currentPassport = res) }
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    fun confirmQr(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Application.dataStore.getToken().first().let { token ->
                api.confirmQr(token, ConfirmQr(code))
            }
            fetchData()
        }
    }

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            Application.dataStore.getToken().first().let { token ->
                val result = api.getAdminBookings(token)
                result.getOrNull()?.let {
                    println(it)
                    _state.update { state ->
                        state.copy(bookings = it.map { it.toDomainModel() })
                    }
                }
                result.exceptionOrNull()?.printStackTrace()
                val zones = api.getZones(token)
                zones.getOrNull()?.let {
                    _state.update { state ->
                        state.copy(zones = it.map { it.name }.sorted())
                    }
                }
                val users = api.getAdminUsers(token)
                users.getOrNull()?.let {
                    _state.update { state ->
                        state.copy(users = it)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
