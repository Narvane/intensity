package com.intensity.mobile.app.core.auth.model

import kotlin.random.Random

data class ConnectCredential(
    val key: Long = Random.nextLong(),
    val email: String,
    val password: String
)
