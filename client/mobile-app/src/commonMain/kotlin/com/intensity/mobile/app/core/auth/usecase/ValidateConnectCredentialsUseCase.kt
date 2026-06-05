package com.intensity.mobile.app.core.auth.usecase

import com.intensity.contracts.model.ConnectMemberCredentialRequestDto
import com.intensity.mobile.app.core.auth.model.ConnectCredential

class ValidateConnectCredentialsUseCase {
    fun execute(credentials: List<ConnectCredential>): List<ConnectMemberCredentialRequestDto> {
        val mapped = credentials.map {
            ConnectMemberCredentialRequestDto(
                email = it.email.trim(),
                password = it.password
            )
        }
        require(mapped.none { it.email.isBlank() || it.password.isBlank() }) {
            "missing_credentials"
        }
        return mapped
    }
}
