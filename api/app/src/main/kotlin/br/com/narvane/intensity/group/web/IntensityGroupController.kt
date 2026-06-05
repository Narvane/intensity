package br.com.narvane.intensity.group.web

import br.com.narvane.intensity.security.AccessMode
import br.com.narvane.intensity.security.IntensityCurrentAccess
import br.com.narvane.intensity.group.application.IntensityGroupService
import br.com.narvane.intensity.shared.web.ApiException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/intensity/api/v1/groups")
@Tag(name = "Grupos", description = "Descoberta e selecao de grupos de convivencia")
class IntensityGroupController(
    private val groupService: IntensityGroupService,
    private val currentAccess: IntensityCurrentAccess
) {
    @GetMapping
    @Operation(summary = "Listar grupos do usuario", description = "Retorna os grupos dos quais o usuario autenticado participa.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Grupos retornados"),
            ApiResponse(responseCode = "403", description = "Modo de acesso invalido")
        ]
    )
    fun list(): ResponseEntity<List<GroupDetailResponse>> {
        val principal = currentAccess.principal()
        if (principal.accessMode != AccessMode.CURATE || principal.userId == null) {
            throw ApiException(HttpStatus.FORBIDDEN, "Somente o modo proponente lista grupos")
        }
        return ResponseEntity.ok(groupService.listGroupsForUser(principal.userId))
    }
}
