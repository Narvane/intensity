package com.intensity.mobile.app.adapters.resourceapi

import com.intensity.mobile.shared.HttpClientEngineFactoryProvider
import com.intensity.mobile.shared.IntensityApiClient
import com.intensity.mobile.shared.IntensityHttpClientFactory
import io.ktor.client.engine.darwin.Darwin

// Alinhar com dev Android: API local na LAN. Para build de produAAo, troque pela URL pAblica.
actual fun defaultBaseUrl(): String = "http://192.168.0.210:8080/intensity2/api/v1"

actual fun createApiClient(baseUrl: String): IntensityApiClient {
    val httpClient = IntensityHttpClientFactory.create(
        HttpClientEngineFactoryProvider(Darwin)
    )
    return IntensityApiClient(httpClient = httpClient, baseUrl = baseUrl)
}
