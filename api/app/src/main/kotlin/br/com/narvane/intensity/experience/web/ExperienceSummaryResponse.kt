package br.com.narvane.intensity.experience.web

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class ExperienceSummaryResponse(
    @field:Schema(example = "b8cad9c0-86eb-4a51-ae9a-3e5f2e30ce5e")
    val id: UUID,
    @field:Schema(example = "3")
    val intensity: Int,
    @field:Schema(example = "2c978d72-7c8f-4a86-9ccd-c98a7874602c")
    val createdBy: UUID,
    val createdAt: LocalDateTime,
    @field:Schema(example = "16f83f5bb7a0f7448f95ec8264f3f618")
    val descriptionMd5: String
)
