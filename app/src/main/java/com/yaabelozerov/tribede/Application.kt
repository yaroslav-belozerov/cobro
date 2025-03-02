package com.yaabelozerov.tribede

import android.app.Application
import com.yaabelozerov.tribede.data.ApiClient
import com.yaabelozerov.tribede.data.DataStore
import com.yaabelozerov.tribede.data.Net

class Application : Application() {
  override fun onCreate() {
    super.onCreate()
    app = this
  }

  companion object {
    lateinit var app: Application

    val dataStore by lazy { DataStore(app) }
    val apiClient by lazy { ApiClient(Net.apiClient) }
  }
}
