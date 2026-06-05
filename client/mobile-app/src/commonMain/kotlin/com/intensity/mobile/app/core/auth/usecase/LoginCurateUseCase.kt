package com.intensity.mobile.app.core.auth.usecase

import com.intensity.contracts.model.AuthResponseDto
import com.intensity.mobile.app.core.auth.port.AuthGatewayPort

class LoginCurateUseCase(
    private val gateway: AuthGatewayPort
) {
    suspend fun execute(email: String, password: String): AuthResponseDto {
        return gateway.loginCurate(email.trim(), password)
    }
}
