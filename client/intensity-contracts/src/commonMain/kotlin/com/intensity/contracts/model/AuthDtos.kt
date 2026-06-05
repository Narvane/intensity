package com.intensity.contracts.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class CurateLoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class ConnectMemberCredentialRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class ConnectLoginRequestDto(
    val credentials: List<ConnectMemberCredentialRequestDto>
)

@Serializable
data class AuthResponseDto(
    val token: String? = null,
    val accessMode: String? = null,
    val userId: String? = null,
    val participantUserIds: List<String> = emptyList(),
    val groupId: String? = null,
    val boxId: String? = null,
    val experienceBoxType: String? = null
)

@Serializable
data class SelectGroupRequestDto(
    val groupId: String
)

@Serializable
data class SelectBoxRequestDto(
    val boxId: String
)

@Serializable
data class RegisteredUserDto(
    val id: String,
    val name: String,
    val email: String
)
