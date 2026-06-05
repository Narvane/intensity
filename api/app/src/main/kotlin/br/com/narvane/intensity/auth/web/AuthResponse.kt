package br.com.narvane.intensity.auth.web

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AuthResponse(
    @field:Schema(description = "JWT de sessao", example = "eyJhbGciOiJIUzI1NiJ9...")
    val token: String?,
    @field:Schema(description = "Modo de acesso da sessao", example = "CURATE")
    val accessMode: String?,
    @field:Schema(description = "Usuario autenticado no modo individual", example = "2c978d72-7c8f-4a86-9ccd-c98a7874602c")
    val userId: UUID?,
    @field:Schema(description = "Participantes autenticados no modo compartilhado")
    val participantUserIds: List<UUID> = emptyList(),
    @field:Schema(description = "Grupo ativo da sessao", example = "5ed0740c-4f6b-4f2e-bfda-2628bba7f58a")
    val groupId: UUID? = null,
    @field:Schema(description = "Caixinha ativa da sessao", example = "8c4e12d6-4ac3-4aa8-b4a6-5ef2e16da527")
    val boxId: UUID? = null,
    /** Tipo nivel 2 da caixinha selecionada (modo proponente); null fora desse contexto. */
    @field:Schema(description = "Tipo contextual da caixinha ativa", example = "connection_moments")
    val experienceBoxType: String? = null
)

data class RegisteredUserResponse(
    @field:Schema(example = "2c978d72-7c8f-4a86-9ccd-c98a7874602c")
    val id: UUID,
    @field:Schema(example = "Gustavo")
    val name: String,
    @field:Schema(example = "gustavo@narvane.com")
    val email: String
)
