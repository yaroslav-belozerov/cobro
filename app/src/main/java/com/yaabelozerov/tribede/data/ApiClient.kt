package com.yaabelozerov.tribede.data

import com.yaabelozerov.tribede.data.model.BookRequestDTO
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.QrDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import com.yaabelozerov.tribede.data.model.SeatDto
import com.yaabelozerov.tribede.data.model.TokenDto
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.ZoneDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

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

    suspend fun getBookings(token: String, id: String, seatId: String?): Result<List<BookResponseDTO>> = runCatching {
        httpClient.get {
            url("/book/$id")
            header("Authorization", "Bearer $token")
            seatId?.let {
                parameter("seatId", it)
            }
        }.body()
    }

    suspend fun getSeatsForOfficeZone(token: String, zoneId: String): Result<List<SeatDto>> = runCatching {
        httpClient.get {
            url("/zone/office/$zoneId/seats")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun postBook(token: String, body: BookRequestDTO, zoneId: String, seatId: String?): Result<String> = runCatching {
        httpClient.post {
            url("/book/$zoneId")
            header("Authorization", "Bearer $token")
            setBody(body)
            seatId?.let {
                parameter("seatId", it)
            }
        }.body()
    }

    suspend fun getQrCode(token: String, bookId: String): Result<QrDto> = runCatching {
        httpClient.get {
            url("/book/$bookId/qr")
            header("Authorization", "Bearer $token")
        }.body()
    }
}
