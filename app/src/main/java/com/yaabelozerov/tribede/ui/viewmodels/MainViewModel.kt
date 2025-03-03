package com.yaabelozerov.tribede.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.model.BookRequestDTO
import com.yaabelozerov.tribede.data.model.RescheduleBody
import com.yaabelozerov.tribede.data.model.toDomainModel
import com.yaabelozerov.tribede.domain.model.BookingUI
import com.yaabelozerov.tribede.ui.App
import com.yaabelozerov.tribede.ui.components.CoworkingSpace
import com.yaabelozerov.tribede.ui.components.Decoration
import com.yaabelozerov.tribede.ui.components.toSpace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class MainState(
    val zones: List<CoworkingSpace> = emptyList(),
    val currentBookings: List<BookingUI> = emptyList(),
    val decor: List<Decoration> = emptyList()
)

class MainViewModel(private val api: ApiClient = ApiClient()): ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        fetchZones()
        fetchDecor()
    }

    fun validateBook(zoneId: String, from: LocalDateTime, to: LocalDateTime, seatId: String?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            Application.dataStore.getToken().first().let { token ->
                if (api.validateId(
                    token = token,
                    from = from.toString(),
                    id = zoneId,
                    seatId = seatId,
                    to = to.toString()
                ).also { it.exceptionOrNull()?.printStackTrace() }.isSuccess) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
    }

    private fun fetchZones() {
        viewModelScope.launch {
            Application.dataStore.getToken().distinctUntilChanged().collect { token ->
                val result = api.getZones(token)
                result.getOrNull()?.let {
                    val zones = it.map {
                        println("zone $it")
                        if (it.type == "Office") {
                            val seats = api.getSeatsForOfficeZone(token, it.id).also { it.exceptionOrNull()?.printStackTrace() }
                            it.toSpace(seats.getOrNull() ?: emptyList()) //TODO потом починить
                        } else {
                            it.toSpace(emptyList())
                        }
                    }
                    _state.update { state ->
                        state.copy(zones = zones)
                    }
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    private fun fetchDecor() {
        viewModelScope.launch {
            Application.dataStore.getToken().distinctUntilChanged().collect { token ->
                val result = api.getDecor(token)
                result.getOrNull()?.let { res ->
                    _state.update { it.copy(decor = res) }
                    println(res)
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    fun getBookings(zoneId: String, seatId: String?) {
        viewModelScope.launch {
            Application.dataStore.getToken().first().let { token ->
                val result = api.getBookings(token, zoneId, seatId)
                result.getOrNull()?.let {
                    _state.update { state ->
                        state.copy(currentBookings = it.map { it.toDomainModel() })
                    }
                    Log.d("getBook", "getBookings: $it")
                } ?: _state.update { it.copy(currentBookings = emptyList()) }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    fun book(req: BookRequestDTO, zoneId: String, seatId: String?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            Application.dataStore.getToken().first().let { token ->
                println("book $token $req $zoneId $seatId")
                val res = api.postBook(token, req, zoneId, seatId).also { it.exceptionOrNull()?.printStackTrace() }
                getBookings(zoneId, seatId)
                callback(res.isSuccess)
            }
        }
    }

    fun resetModal() {
        _state.update { it.copy(currentBookings = emptyList()) }
    }
}