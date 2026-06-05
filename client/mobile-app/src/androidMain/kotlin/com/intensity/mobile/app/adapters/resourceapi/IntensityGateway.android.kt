package com.intensity.mobile.app.adapters.resourceapi

import com.intensity.mobile.app2.BuildConfig
import com.intensity.mobile.shared.HttpClientEngineFactoryProvider
import com.intensity.mobile.shared.IntensityApiClient
import com.intensity.mobile.shared.IntensityHttpClientFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun defaultBaseUrl(): String = BuildConfig.INTENSITY_API_BASE_URL

actual fun createApiClient(baseUrl: String): IntensityApiClient {
    val httpClient = IntensityHttpClientFactory.create(
        HttpClientEngineFactoryProvider(OkHttp)
    )
    return IntensityApiClient(httpClient = httpClient, baseUrl = baseUrl)
}
