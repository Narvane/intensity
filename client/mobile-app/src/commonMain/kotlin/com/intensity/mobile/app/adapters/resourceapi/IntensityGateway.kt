package com.intensity.mobile.app.adapters.resourceapi

import com.intensity.mobile.shared.IntensityApiClient
import com.intensity.contracts.model.AuthResponseDto
import com.intensity.contracts.model.BoxDto
import com.intensity.contracts.model.BoxCreateRequestDto
import com.intensity.contracts.model.ExperienceDto
import com.intensity.contracts.model.ExperienceUpsertRequestDto
import com.intensity.contracts.model.CurateLoginRequestDto
import com.intensity.contracts.model.ExperienceSummaryDto
import com.intensity.contracts.model.ConnectMemberCredentialRequestDto
import com.intensity.contracts.model.ConnectLoginRequestDto
import com.intensity.contracts.model.RegisteredUserDto
import com.intensity.contracts.model.RegisterRequestDto
import com.intensity.contracts.model.SelectBoxRequestDto
import com.intensity.contracts.model.SelectGroupRequestDto
import com.intensity.contracts.model.GroupDetailDto

class IntensityGateway {
    private var baseUrl: String = defaultBaseUrl()
    private var apiClient: IntensityApiClient = createApiClient(baseUrl)

    suspend fun register(name: String, email: String, password: String): AuthResponseDto {
        val response = apiClient.register(
            RegisterRequestDto(
                name = name,
                email = email,
                password = password
            )
        )
        if (response.userId == null) {
            throw IllegalStateException("registration response missing user id")
        }
        return response
    }

    suspend fun registeredUsers(): List<RegisteredUserDto> {
        return apiClient.registeredUsers()
    }

    suspend fun loginCurate(email: String, password: String): AuthResponseDto {
        return apiClient.loginCurate(CurateLoginRequestDto(email = email, password = password))
    }

    suspend fun loginConnect(credentials: List<ConnectMemberCredentialRequestDto>): AuthResponseDto {
        return apiClient.loginConnect(ConnectLoginRequestDto(credentials = credentials))
    }

    suspend fun selectCurateGroup(token: String, groupId: String): AuthResponseDto {
        return apiClient.selectCurateGroup(token, SelectGroupRequestDto(groupId = groupId))
    }

    suspend fun selectCurateExperienceBox(token: String, boxId: String): AuthResponseDto {
        return apiClient.selectCurateExperienceBox(token, SelectBoxRequestDto(boxId = boxId))
    }

    suspend fun listGroups(token: String): List<GroupDetailDto> = apiClient.listGroups(token)

    suspend fun listBoxes(token: String): List<BoxDto> = apiClient.listBoxes(token)

    suspend fun createBox(token: String, name: String, boxType: String? = null): BoxDto {
        return apiClient.createBox(token, BoxCreateRequestDto(name = name, boxType = boxType))
    }

    suspend fun listExperienceSummaries(token: String): List<ExperienceSummaryDto> =
        apiClient.listExperienceSummaries(token)

    suspend fun getExperience(token: String, experienceId: String): ExperienceDto =
        apiClient.getExperience(token, experienceId)

    suspend fun activateExperience(token: String, boxId: String, intensity: Int?, maxIntensity: Int?): ExperienceDto {
        return apiClient.activateExperience(token, boxId, intensity, maxIntensity)
    }

    suspend fun listConnectExperiences(
        token: String,
        boxId: String,
        intensity: Int?,
        maxIntensity: Int?
    ): List<ExperienceSummaryDto> {
        return apiClient.listConnectExperiences(token, boxId, intensity, maxIntensity)
    }

    suspend fun getConnectExperience(token: String, boxId: String, id: String): ExperienceDto {
        return apiClient.getConnectExperience(token, boxId, id)
    }

    suspend fun createExperience(token: String, request: ExperienceUpsertRequestDto): ExperienceDto {
        return apiClient.createExperience(token = token, request = request)
    }

    suspend fun updateExperience(token: String, id: String, request: ExperienceUpsertRequestDto): ExperienceDto {
        return apiClient.updateExperience(
            token = token,
            experienceId = id,
            request = request
        )
    }

    suspend fun deleteExperience(token: String, id: String) {
        apiClient.deleteExperience(token, id)
    }
}

expect fun defaultBaseUrl(): String

expect fun createApiClient(baseUrl: String): IntensityApiClient
