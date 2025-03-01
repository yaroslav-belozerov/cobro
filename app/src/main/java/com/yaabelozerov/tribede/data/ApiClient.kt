package com.yaabelozerov.tribede.data

import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import com.yaabelozerov.tribede.data.model.TokenDto
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.ZoneDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.InternalAPI

class ApiClient(private val httpClient: HttpClient = Net.apiClient) {
    suspend fun login(query: LoginDto): Result<TokenDto> = runCatching {
        httpClient.post {
            url("/auth/sign-in")
            setBody(query)
        }.body()
    }

    suspend fun register(query: RegisterDto): Result<TokenDto> = runCatching {
        httpClient.post {
            url("/auth/sign-up")
            setBody(query)
        }.body()
    }

    suspend fun getUser(token: String): Result<UserDto> = runCatching {
        httpClient.get {
            url("/user")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getZones(token: String): Result<List<ZoneDto>> = runCatching {
        httpClient.get {
            url("/zone")
            header("Authorization", "Bearer $token")
        }.body()
    }
}
