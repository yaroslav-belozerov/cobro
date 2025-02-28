package com.yaabelozerov.tribede.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStore(context: Context) {
    private val Context.dataStore by preferencesDataStore("preferences")
    private val dataStore = context.dataStore

    private val tokenKey = stringPreferencesKey("token")

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[tokenKey] = token
        }
    }
    fun getToken() = dataStore.data.map { it[tokenKey] ?: "" }
}
