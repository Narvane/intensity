package br.com.narvane.intensity.auth.web

import br.com.narvane.intensity.auth.application.IntensityAuthService
import br.com.narvane.intensity.security.AppPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/intensity/api/v1/auth")
@Tag(name = "Autenticacao", description = "Cadastro, login e selecao de contexto de sessao do Intensity")
class IntensityAuthController(
    private val authService: IntensityAuthService
) {
    @GetMapping("/registered-users")
    @Operation(summary = "Listar usuarios cadastrados", description = "Retorna usuarios habilitados para compor grupos no login compartilhado.")
    fun registeredUsers(): ResponseEntity<List<RegisteredUserResponse>> {
        return ResponseEntity.ok(authService.listRegisteredUsers())
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastrar conta", description = "Cria uma nova conta de participante no Intensity.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Conta criada"),
            ApiResponse(responseCode = "409", description = "Email ja cadastrado"),
            ApiResponse(responseCode = "403", description = "Email nao liberado")
        ]
    )
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login/curate")
    @Operation(summary = "Login individual", description = "Inicia sessao individual para criar e gerenciar experiencias.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login efetuado"),
            ApiResponse(responseCode = "401", description = "Credenciais invalidas")
        ]
    )
    fun loginCurate(@Valid @RequestBody request: CurateLoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.loginCurate(request))
    }

    @PostMapping("/login/connect")
    @Operation(summary = "Login compartilhado", description = "Autentica multiplos participantes e define o grupo ativo da sessao.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Sessao de grupo criada"),
            ApiResponse(responseCode = "400", description = "Emails duplicados"),
            ApiResponse(responseCode = "401", description = "Credenciais invalidas")
        ]
    )
    fun loginConnect(@Valid @RequestBody request: ConnectLoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.loginConnect(request))
    }

    @PostMapping("/session/select-group")
    @Operation(summary = "Selecionar grupo", description = "No modo individual, define qual grupo sera usado para gerenciar caixinhas e experiencias.")
    @ApiResponse(responseCode = "200", description = "Grupo selecionado")
    fun selectCurateGroup(
        @Valid @RequestBody request: SelectGroupRequest,
        authentication: Authentication
    ): ResponseEntity<AuthResponse> {
        val principal = authentication.principal as AppPrincipal
        return ResponseEntity.ok(authService.selectCurateGroup(principal, request.groupId))
    }

    @PostMapping("/session/select-experience-box")
    @Operation(summary = "Selecionar caixinha de experiencias", description = "No modo individual, define a caixinha ativa para criar, listar e editar experiencias.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Caixinha selecionada"),
            ApiResponse(
                responseCode = "404",
                description = "Caixinha nao encontrada",
                content = [Content(schema = Schema(implementation = String::class))
                ]
            )
        ]
    )
    fun selectCurateExperienceBox(
        @Valid @RequestBody request: SelectBoxRequest,
        authentication: Authentication
    ): ResponseEntity<AuthResponse> {
        val principal = authentication.principal as AppPrincipal
        return ResponseEntity.ok(authService.selectCurateExperienceBox(principal, request.boxId))
    }
}
