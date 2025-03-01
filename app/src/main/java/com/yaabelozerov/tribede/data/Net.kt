package com.yaabelozerov.tribede.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Net {
  private const val BACK_URL = "http://prod-team-21-ml8gb3lr.final.prodcontest.ru/"

  val apiClient = HttpClient {
    install(Logging) { level = LogLevel.BODY }
    install(ContentNegotiation) {
      json(
          json =
              Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
              })
    }
    defaultRequest {
      contentType(ContentType.Application.Json)
      url(BACK_URL)
    }
    expectSuccess = true
  }
}
