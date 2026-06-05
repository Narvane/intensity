package br.com.narvane.intensity.experiencebox.web

import br.com.narvane.intensity.security.AccessMode
import br.com.narvane.intensity.security.IntensityCurrentAccess
import br.com.narvane.intensity.experiencebox.application.ExperienceBoxService
import br.com.narvane.intensity.shared.web.ApiException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/intensity/api/v1/experience-boxes")
@Tag(name = "Caixinhas", description = "Gestao de caixinhas de experiencias por grupo")
class IntensityExperienceBoxController(
    private val experienceBoxService: ExperienceBoxService,
    private val currentAccess: IntensityCurrentAccess
) {
    @GetMapping
    @Operation(summary = "Listar caixinhas do grupo", description = "Retorna as caixinhas de experiencias visiveis no grupo ativo da sessao.")
    fun list(): ResponseEntity<List<ExperienceBoxResponse>> {
        val principal = currentAccess.principal()
        val groupId = when (principal.accessMode) {
            AccessMode.CONNECT -> principal.groupId
            AccessMode.CURATE -> principal.groupId
            else -> null
        } ?: throw ApiException(HttpStatus.BAD_REQUEST, "Grupo nao definido no token")
        return ResponseEntity.ok(experienceBoxService.listForGroup(groupId))
    }

    @PostMapping
    @Operation(summary = "Criar caixinha de experiencias", description = "Cria uma nova caixinha no grupo ativo, disponivel para experiencias compartilhadas.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Caixinha criada"),
            ApiResponse(responseCode = "403", description = "Apenas sessao compartilhada pode criar caixinha")
        ]
    )
    fun create(@Valid @RequestBody request: ExperienceBoxCreateRequest): ResponseEntity<ExperienceBoxResponse> {
        val principal = currentAccess.principal()
        if (principal.accessMode != AccessMode.CONNECT) {
            throw ApiException(HttpStatus.FORBIDDEN, "Somente o modo em grupo pode criar caixinhas de experiencias")
        }
        val groupId = principal.groupId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Grupo nao definido no token")
        val created = experienceBoxService.createInGroup(groupId, request.name, request.boxType)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
}
