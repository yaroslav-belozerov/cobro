package com.yaabelozerov.tribede.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.tribede.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.data.model.UserDto
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
import java.time.ZoneId
import java.util.UUID

data class UserState(
    val user: UserDto? = null,
    val qrString: String? = null
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

    private val mediaChoose = MutableStateFlow<(() -> Unit)?>(null)
    fun setMediaChoose(f: () -> Unit) {
        mediaChoose.update { f }
    }



    fun onPickMedia() {
        mediaChoose.value?.invoke()
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

    fun getQr(bookId: String) {
        viewModelScope.launch {
            dataStore.getToken().first().let { token ->
                apiClient.getQrCode(token, bookId).getOrNull()?.let {
                    _state.update { state ->
                        state.copy(qrString = it.code)
                    }
                }
            }
        }
    }

    fun fetchUserInfo() {
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