package com.intensity.contracts.model

import kotlinx.serialization.Serializable

@Serializable
data class ExperienceSummaryDto(
    val id: String,
    val intensity: Int,
    val createdBy: String,
    val createdAt: String,
    val descriptionMd5: String
)
