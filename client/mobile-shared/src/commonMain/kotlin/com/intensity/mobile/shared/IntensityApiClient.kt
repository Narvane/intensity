package com.intensity.mobile.shared

import com.intensity.contracts.model.AuthResponseDto
import com.intensity.contracts.model.BoxCreateRequestDto
import com.intensity.contracts.model.BoxDto
import com.intensity.contracts.model.ExperienceDto
import com.intensity.contracts.model.CurateLoginRequestDto
import com.intensity.contracts.model.ExperienceSummaryDto
import com.intensity.contracts.model.ExperienceUpsertRequestDto
import com.intensity.contracts.model.ConnectLoginRequestDto
import com.intensity.contracts.model.RegisterRequestDto
import com.intensity.contracts.model.RegisteredUserDto
import com.intensity.contracts.model.SelectBoxRequestDto
import com.intensity.contracts.model.SelectGroupRequestDto
import com.intensity.contracts.model.GroupDetailDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class IntensityApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    private val root: String = baseUrl.trimEnd('/')

    suspend fun register(request: RegisterRequestDto): AuthResponseDto {
        return httpClient.post("$root/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun registeredUsers(): List<RegisteredUserDto> {
        return httpClient.get("$root/auth/registered-users").body()
    }

    suspend fun loginCurate(request: CurateLoginRequestDto): AuthResponseDto {
        return httpClient.post("$root/auth/login/curate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun loginConnect(request: ConnectLoginRequestDto): AuthResponseDto {
        return httpClient.post("$root/auth/login/connect") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun selectCurateGroup(token: String, request: SelectGroupRequestDto): AuthResponseDto {
        return httpClient.post("$root/auth/session/select-group") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun selectCurateExperienceBox(token: String, request: SelectBoxRequestDto): AuthResponseDto {
        return httpClient.post("$root/auth/session/select-experience-box") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun listGroups(token: String): List<GroupDetailDto> {
        return httpClient.get("$root/groups") {
            bearer(token)
        }.body()
    }

    suspend fun listBoxes(token: String): List<BoxDto> {
        return httpClient.get("$root/experience-boxes") {
            bearer(token)
        }.body()
    }

    suspend fun createBox(token: String, request: BoxCreateRequestDto): BoxDto {
        return httpClient.post("$root/experience-boxes") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun listExperienceSummaries(token: String): List<ExperienceSummaryDto> {
        return httpClient.get("$root/experiences") {
            bearer(token)
        }.body()
    }

    suspend fun getExperience(token: String, experienceId: String): ExperienceDto {
        return httpClient.get("$root/experiences/$experienceId") {
            bearer(token)
        }.body()
    }

    suspend fun listConnectExperiences(
        token: String,
        boxId: String,
        intensity: Int?,
        maxIntensity: Int?
    ): List<ExperienceSummaryDto> {
        return httpClient.get("$root/experiences/box/$boxId") {
            bearer(token)
            if (intensity != null) {
                parameter("intensity", intensity)
            }
            if (maxIntensity != null) {
                parameter("maxIntensity", maxIntensity)
            }
        }.body()
    }

    suspend fun getConnectExperience(
        token: String,
        boxId: String,
        experienceId: String
    ): ExperienceDto {
        return httpClient.get("$root/experiences/box/$boxId/$experienceId") {
            bearer(token)
        }.body()
    }

    suspend fun createExperience(token: String, request: ExperienceUpsertRequestDto): ExperienceDto {
        return httpClient.post("$root/experiences") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateExperience(token: String, experienceId: String, request: ExperienceUpsertRequestDto): ExperienceDto {
        return httpClient.put("$root/experiences/$experienceId") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteExperience(token: String, experienceId: String) {
        httpClient.delete("$root/experiences/$experienceId") {
            bearer(token)
        }
    }

    suspend fun activateExperience(
        token: String,
        boxId: String,
        intensity: Int?,
        maxIntensity: Int?
    ): ExperienceDto {
        return httpClient.get("$root/experiences/activate") {
            bearer(token)
            parameter("boxId", boxId)
            if (intensity != null) {
                parameter("intensity", intensity)
            }
            if (maxIntensity != null) {
                parameter("maxIntensity", maxIntensity)
            }
        }.body()
    }
}

private fun io.ktor.client.request.HttpRequestBuilder.bearer(token: String) {
    headers.append("Authorization", "Bearer $token")
}
