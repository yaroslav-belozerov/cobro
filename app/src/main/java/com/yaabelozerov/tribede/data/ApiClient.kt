package com.yaabelozerov.tribede.data

import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import com.yaabelozerov.tribede.data.model.TokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.utils.io.InternalAPI

class ApiClient(private val httpClient: HttpClient) {
  suspend fun login(query: LoginDto): Result<TokenDto> = runCatching {
    httpClient
        .post {
          url("/auth/sign-up")
          setBody(query)
        }
        .body()
  }

  suspend fun register(query: RegisterDto): Result<TokenDto> = runCatching {
    httpClient
        .post {
          url("/auth/sign-up")
          setBody(query)
        }
        .body()
  }
}
