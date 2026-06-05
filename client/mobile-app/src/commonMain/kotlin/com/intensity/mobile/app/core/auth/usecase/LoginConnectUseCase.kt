package com.intensity.mobile.app.core.auth.usecase

import com.intensity.contracts.model.AuthResponseDto
import com.intensity.contracts.model.ConnectMemberCredentialRequestDto
import com.intensity.mobile.app.core.auth.port.AuthGatewayPort

class LoginConnectUseCase(
    private val gateway: AuthGatewayPort
) {
    suspend fun execute(credentials: List<ConnectMemberCredentialRequestDto>): AuthResponseDto {
        return gateway.loginConnect(credentials)
    }
}
