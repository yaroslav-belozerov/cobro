package com.yaabelozerov.tribede.data

import com.yaabelozerov.tribede.data.model.ActionDTO
import com.yaabelozerov.tribede.data.model.AdminBookResponse
import com.yaabelozerov.tribede.data.model.AdminPhotoResponse
import com.yaabelozerov.tribede.data.model.BookRequestDTO
import com.yaabelozerov.tribede.data.model.BookResponseDTO
import com.yaabelozerov.tribede.data.model.ConfirmQr
import com.yaabelozerov.tribede.data.model.LoginDto
import com.yaabelozerov.tribede.data.model.QrConfirmResponse
import com.yaabelozerov.tribede.data.model.QrDto
import com.yaabelozerov.tribede.data.model.RegisterDto
import com.yaabelozerov.tribede.data.model.RescheduleBody
import com.yaabelozerov.tribede.data.model.SeatDto
import com.yaabelozerov.tribede.data.model.TokenDto
import com.yaabelozerov.tribede.data.model.UserDto
import com.yaabelozerov.tribede.data.model.UserPassportDTO
import com.yaabelozerov.tribede.data.model.ZoneDto
import com.yaabelozerov.tribede.ui.components.Decoration
import com.yaabelozerov.tribede.ui.screen.RequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File

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
                parameter("seat-id", it)
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
        println()
        httpClient.post {
            url("/book/$zoneId")
            header("Authorization", "Bearer $token")
            setBody(body)
            seatId?.let {
                parameter("seat-id", it)
            }
        }.body()
    }

    suspend fun rescheduleBook(token: String, body: RescheduleBody, id: String) {
        try {
            httpClient.patch {
                url("/book/$id/reschedule")
                header("Authorization", "Bearer $token")
                setBody(body)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun getQrCode(token: String, bookId: String): Result<QrDto> = runCatching {
        httpClient.get {
            url("/book/$bookId/qr")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getAdminBookings(token: String): Result<List<AdminBookResponse>> = runCatching {
        httpClient.get {
            url("/admin/active")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getAdminUsers(token: String): Result<List<UserDto>> = runCatching {
        httpClient.get {
            url("/user/all")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getAdminPassport(token: String, id: String): Result<UserPassportDTO> = runCatching {
        httpClient.get {
            url("/user/$id/passport")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getAdminPhoto(token: String, id: String): Result<AdminPhotoResponse> = runCatching {
        httpClient.get {
            url("/user/$id/verification-photo")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getActions(token: String): Result<List<ActionDTO>> = kotlin.runCatching {
        httpClient.get {
            url("/request/today")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun sendPassport(token: String, passportDTO: UserPassportDTO, id: String) {
        try {
            httpClient.post {
                url("/user/$id/passport")
                header("Authorization", "Bearer $token")
                setBody(passportDTO)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun markAction(id: String, token: String, status: Int) {
        try {
            httpClient.patch {
                url("/request/$id/mark")
                parameter("status", status)
                header("Authorization", "Bearer $token")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun sendPhoto(file: File, id: String, token: String): Result<String> = runCatching {
        httpClient.post {
            url("/user/$id/verification-photo")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            file.readBytes(),
                            Headers.build {
                                append("Content-Type", "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                            }
                        )
                    }
                )
            )
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun confirmQr(token: String, body: ConfirmQr): Result<QrConfirmResponse> = kotlin.runCatching {
        httpClient.patch {
            url("/confirm-qr")
            header("Authorization", "Bearer $token")
            setBody(body)
        }.body()
    }


    suspend fun deleteBook(token: String, id: String) {
        try {
            httpClient.patch {
                url("/book/$id/cancel")
                header("Authorization", "Bearer $token")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun uploadImage(file: File, token: String): Result<String> = runCatching {
        httpClient.post {
            url("user/upload")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            file.readBytes(),
                            Headers.build {
                                append("Content-Type", "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                            }
                        )
                    }
                )
            )
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun validateId(token: String, id: String, from: String, to: String, seatId: String?): Result<String> = runCatching {
        httpClient.get {
            url("book/$id/validate")
            parameter("from", from)
            parameter("to", to)
            header("Authorization", "Bearer $token")
            seatId?.let {
                parameter("seatId", it)
            }
        }.body()
    }

    suspend fun postDecor(token: String, body: Decoration): Result<String> = kotlin.runCatching {
        httpClient.post {
            url("decorations")
            header("Authorization", "Bearer $token")
            setBody(body)
        }.body()
    }

    suspend fun getDecor(token: String): Result<List<Decoration>> = kotlin.runCatching {
        httpClient.get {
            url("decorations")
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun sendRequest(token: String, body: RequestDto): Result<String> = kotlin.runCatching {
        httpClient.post {
            url("request")
            header("Authorization", "Bearer $token")
            setBody(body)
        }.body()
    }
}
