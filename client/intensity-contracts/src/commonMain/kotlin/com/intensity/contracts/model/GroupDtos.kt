package com.intensity.contracts.model

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantSnippetDto(
    val id: String,
    val name: String
)

@Serializable
data class GroupDetailDto(
    val id: String,
    val participants: List<ParticipantSnippetDto>
)
