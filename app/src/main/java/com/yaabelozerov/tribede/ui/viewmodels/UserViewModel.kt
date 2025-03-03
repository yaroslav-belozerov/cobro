package com.yaabelozerov.tribede.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.data.model.RescheduleBody
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.toDomainModel
import com.yaabelozerov.tribede.domain.model.BookingUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

data class UserState(
    val user: UserDto? = null,
    val qrString: String? = null,
    val isLoading: Boolean = false,
    val books: List<BookingUI> = emptyList()
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

    fun onMediaPicker(app: Context, uri: Uri) {
        viewModelScope.launch {
            dataStore.getToken().first().let { token ->
                app.contentResolver.openInputStream(uri)?.use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    val dir = File(app.cacheDir, "images")
                    dir.mkdir()
                    val file = File(dir, UUID.randomUUID().toString() + ".jpg")
                    file.createNewFile()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, file.outputStream())
//                    val part = MultipartBody.Part.createFormData(
//                        "file", file.name, file.asRequestBody()
//                    )
                    apiClient.uploadImage(file, token).exceptionOrNull()?.printStackTrace()
                    file.delete()
                    fetchUserInfo()
                }
            }
        }
    }

    fun move(from: LocalDateTime, to: LocalDateTime, bookId: String) {
        viewModelScope.launch {
            Application.dataStore.getToken().first().let { token ->
                apiClient.rescheduleBook(token, RescheduleBody(
                    from = "$from:00.000Z",
                    to = "$to:00.000Z"
                ), bookId)
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            Application.dataStore.getToken().first().let { token ->
                apiClient.deleteBook(token, bookId)
            }
        }
    }

    fun getQr(bookId: String) {
        viewModelScope.launch {
            dataStore.getToken().first().let { token ->
                apiClient.getQrCode(token, bookId).also { it.exceptionOrNull()?.printStackTrace() }.getOrNull()?.let {
                    _state.update { state ->
                        state.copy(qrString = it.code)
                    }
                }
            }
        }
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            dataStore.getToken().distinctUntilChanged().collect { token ->
                _state.update { it.copy(isLoading = true) }
                token.takeIf { it.isNotEmpty() }?.let {
                    val result = apiClient.getUser(it)
                    result.getOrNull()?.let {
                        println("result: $it")
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
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun validateBook(zoneId: String, from: LocalDateTime, to: LocalDateTime, seatId: String?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            Application.dataStore.getToken().first().let { token ->
                if (apiClient.validateId(
                        token = token,
                        from = from.toString(),
                        id = zoneId,
                        seatId = seatId,
                        to = to.toString()
                    ).also { it.exceptionOrNull()?.printStackTrace() }.isSuccess) {
                    callback(true)
                    fetchUserInfo()
                } else {
                    callback(false)
                }
            }
        }
    }
}