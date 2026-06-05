package com.intensity.mobile.shared

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText

/**
 * Maps Ktor HTTP failures to a short user-facing message (includes response body when present,
 * e.g. Spring validation JSON).
 */
suspend fun readableIntensityHttpError(throwable: Throwable): String {
    return when (throwable) {
        is ClientRequestException -> {
            val code = throwable.response.status.value
            val chunk = runCatching { throwable.response.bodyAsText() }
                .getOrNull()
                ?.trim()
                ?.take(400)
            if (!chunk.isNullOrBlank()) {
                "Servidor ($code): $chunk"
            } else {
                throwable.message ?: "Erro HTTP $code"
            }
        }
        is ServerResponseException -> {
            val code = throwable.response.status.value
            val chunk = runCatching { throwable.response.bodyAsText() }
                .getOrNull()
                ?.trim()
                ?.take(400)
            if (!chunk.isNullOrBlank()) {
                "Servidor ($code): $chunk"
            } else {
                throwable.message ?: "Erro no servidor ($code)"
            }
        }
        else -> throwable.message?.take(400)?.ifBlank { null } ?: "Erro desconhecido"
    }
}
