package com.yaabelozerov.tribede

import android.app.Application
import com.yaabelozerov.tribede.data.DataStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        private lateinit var app: Application

        val dataStore by lazy { DataStore(app) }
    }
}