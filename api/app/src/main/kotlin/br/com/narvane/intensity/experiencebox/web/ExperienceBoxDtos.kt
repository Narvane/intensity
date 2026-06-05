package br.com.narvane.intensity.experiencebox.web

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.UUID

data class ExperienceBoxResponse(
    @field:Schema(example = "8c4e12d6-4ac3-4aa8-b4a6-5ef2e16da527")
    val id: UUID,
    @field:Schema(example = "5ed0740c-4f6b-4f2e-bfda-2628bba7f58a")
    val groupId: UUID,
    @field:Schema(example = "Noite de conexao")
    val name: String,
    @field:Schema(example = "connection_moments")
    val boxType: String,
    val createdAt: LocalDateTime
)

data class ExperienceBoxCreateRequest(
    @field:Schema(example = "Noite de conexao")
    @field:NotBlank(message = "Nome e obrigatorio")
    val name: String,
    /** Tipo nivel 2; omitido ou invalido => outings_friends */
    @field:Schema(example = "connection_moments")
    val boxType: String? = null
)
