package com.intensity.mobile.shared

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object IntensityHttpClientFactory {
    fun create(engine: HttpClientEngineFactoryProvider): HttpClient {
        return HttpClient(engine.factory) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }
}

class HttpClientEngineFactoryProvider(
    val factory: io.ktor.client.engine.HttpClientEngineFactory<*>
)
