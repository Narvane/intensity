package br.com.narvane.intensity.auth.web

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class RegisterRequest(
    @field:Schema(example = "Gustavo")
    @field:NotBlank(message = "Nome e obrigatorio")
    val name: String,
    @field:Schema(example = "gustavo@narvane.com")
    @field:Email(message = "Email invalido")
    val email: String,
    @field:Schema(example = "minhaSenha123")
    @field:Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    val password: String
)

data class CurateLoginRequest(
    @field:Schema(example = "gustavo@narvane.com")
    @field:Email(message = "Email invalido")
    val email: String,
    @field:Schema(example = "minhaSenha123")
    @field:NotBlank(message = "Senha e obrigatoria")
    val password: String
)

data class ConnectMemberCredentialRequest(
    @field:Schema(example = "gustavo@narvane.com")
    @field:Email(message = "Email invalido")
    val email: String,
    @field:Schema(example = "minhaSenha123")
    @field:NotBlank(message = "Senha e obrigatoria")
    val password: String
)

data class ConnectLoginRequest(
    @field:NotEmpty(message = "Informe pelo menos um usuario com email e senha")
    @field:Valid
    @field:Schema(description = "Credenciais dos participantes que entram juntos no grupo ativo.")
    val credentials: List<ConnectMemberCredentialRequest>
)

data class SelectGroupRequest(
    @field:Schema(example = "2c978d72-7c8f-4a86-9ccd-c98a7874602c")
    @field:NotNull(message = "groupId e obrigatorio")
    val groupId: UUID
)

data class SelectBoxRequest(
    @field:Schema(example = "8c4e12d6-4ac3-4aa8-b4a6-5ef2e16da527")
    @field:NotNull(message = "boxId e obrigatorio")
    val boxId: UUID
)
