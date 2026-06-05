package br.com.narvane.intensity.group.web

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class ParticipantSnippetResponse(
    @field:Schema(example = "2c978d72-7c8f-4a86-9ccd-c98a7874602c")
    val id: UUID,
    @field:Schema(example = "Gustavo")
    val name: String
)

data class GroupDetailResponse(
    @field:Schema(example = "5ed0740c-4f6b-4f2e-bfda-2628bba7f58a")
    val id: UUID,
    val participants: List<ParticipantSnippetResponse>
)
