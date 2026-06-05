package com.intensity.contracts.model

import kotlinx.serialization.Serializable

@Serializable
data class ExperienceReflectionDto(
    val involvesEveryone: String,
    val othersWouldAccept: String,
    val mildDiscomfort: String
)

@Serializable
data class ExperienceResonanceDto(
    val effortStars: Int,
    val opennessStars: Int,
    val noveltyStars: Int
)

@Serializable
data class ExperienceUpsertRequestDto(
    val description: String,
    val intensity: Int,
    val involvesEveryoneJustification: String,
    val othersWouldAcceptJustification: String,
    val mildDiscomfortJustification: String,
    val effortStars: Int,
    val opennessStars: Int,
    val noveltyStars: Int
)

@Serializable
data class ExperienceDto(
    val id: String,
    val description: String,
    val intensity: Int,
    val createdBy: String,
    val createdAt: String,
    val descriptionMd5: String,
    val additionalInfo: ExperienceReflectionDto? = null,
    val parameters: ExperienceResonanceDto? = null
)
