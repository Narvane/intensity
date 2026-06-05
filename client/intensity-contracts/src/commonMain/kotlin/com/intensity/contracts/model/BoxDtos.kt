package com.intensity.contracts.model

import kotlinx.serialization.Serializable

@Serializable
data class BoxDto(
    val id: String,
    val groupId: String,
    val name: String,
    val boxType: String = "outings_friends",
    val createdAt: String
)

@Serializable
data class BoxCreateRequestDto(
    val name: String,
    val boxType: String? = null
)
