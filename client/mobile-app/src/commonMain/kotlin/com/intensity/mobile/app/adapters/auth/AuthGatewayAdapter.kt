package com.intensity.mobile.app.adapters.auth

import com.intensity.contracts.model.AuthResponseDto
import com.intensity.contracts.model.ConnectMemberCredentialRequestDto
import com.intensity.mobile.app.core.auth.port.AuthGatewayPort
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway

class AuthGatewayAdapter(
    private val gateway: IntensityGateway
) : AuthGatewayPort {
    override suspend fun loginCurate(email: String, password: String): AuthResponseDto {
        return gateway.loginCurate(email, password)
    }

    override suspend fun loginConnect(credentials: List<ConnectMemberCredentialRequestDto>): AuthResponseDto {
        return gateway.loginConnect(credentials)
    }

    override suspend fun register(name: String, email: String, password: String): AuthResponseDto {
        return gateway.register(name, email, password)
    }
}
