package com.yaabelozerov.tribede.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
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
    val bookings: List<AdminBookingUI> = emptyList()

)

class AdminViewModel(private val api: ApiClient = Application.apiClient): ViewModel() {
    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    init {
        fetchData()
    }

     fun deleteBooking(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Application.dataStore.getToken().first().let { token ->
                api.deleteBook(token, id)
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            Application.dataStore.getToken().first().let { token ->
                val result = api.getAdminBookings(token)
                result.getOrNull()?.let {
                    println(it)
                    _state.update { state ->
                        state.copy(bookings = it.map { it.toDomainModel() })
                    }
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}
