package com.intensity.mobile.app.core.auth.port

import com.intensity.contracts.model.AuthResponseDto
import com.intensity.contracts.model.ConnectMemberCredentialRequestDto

interface AuthGatewayPort {
    suspend fun loginCurate(email: String, password: String): AuthResponseDto
    suspend fun loginConnect(credentials: List<ConnectMemberCredentialRequestDto>): AuthResponseDto
    suspend fun register(name: String, email: String, password: String): AuthResponseDto
}
