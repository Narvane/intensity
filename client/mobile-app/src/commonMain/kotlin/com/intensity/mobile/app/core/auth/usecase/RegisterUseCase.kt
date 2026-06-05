package com.intensity.mobile.app.core.auth.usecase

import com.intensity.contracts.model.AuthResponseDto
import com.intensity.mobile.app.core.auth.port.AuthGatewayPort

class RegisterUseCase(
    private val gateway: AuthGatewayPort
) {
    suspend fun execute(name: String, email: String, password: String): AuthResponseDto {
        return gateway.register(name.trim(), email.trim(), password)
    }
}
