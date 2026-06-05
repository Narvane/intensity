package br.com.narvane.intensity.experience.web

import br.com.narvane.intensity.experience.application.ExperienceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/intensity/api/v1/experiences")
@Tag(name = "Experiencias", description = "Catalogo de experiencias inusitadas e sorteio por intensidade")
class IntensityExperienceController(
    private val experienceService: ExperienceService
) {
    @GetMapping
    @Operation(summary = "Listar experiencias da caixinha ativa", description = "Retorna os resumos das experiencias registradas na caixinha selecionada.")
    fun list(): ResponseEntity<List<ExperienceSummaryResponse>> =
        ResponseEntity.ok(experienceService.listSummaries())

    @GetMapping("/box/{boxId}")
    @Operation(summary = "Listar experiencias por recurso de caixa", description = "Retorna experiencias de uma caixa para o fluxo CONNECT com filtros opcionais.")
    fun listByBox(
        @PathVariable boxId: UUID,
        @RequestParam(required = false) intensity: Int?,
        @RequestParam(required = false) maxIntensity: Int?
    ): ResponseEntity<List<ExperienceSummaryResponse>> {
        return ResponseEntity.ok(experienceService.listSummariesForConnect(boxId, intensity, maxIntensity))
    }

    @GetMapping("/box/{boxId}/{id}")
    @Operation(summary = "Detalhar experiencia por recurso de caixa", description = "Retorna uma experiencia da caixa no fluxo CONNECT.")
    fun getByBoxAndId(
        @PathVariable boxId: UUID,
        @PathVariable id: UUID
    ): ResponseEntity<ExperienceResponse> {
        return ResponseEntity.ok(experienceService.getDetailForConnect(boxId, id))
    }

    @GetMapping("/activate")
    @Deprecated("Use GET /experiences/box/{boxId} e selecao local no client")
    @Operation(summary = "Sortear experiencia", description = "Sorteia uma experiencia da caixinha por filtro de intensidade (qualquer, exata ou maxima).")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Experiencia sorteada"),
            ApiResponse(responseCode = "400", description = "Filtro invalido ou sem experiencias disponiveis")
        ]
    )
    fun activate(
        @Parameter(description = "ID da caixinha de experiencias", required = true)
        @RequestParam boxId: UUID,
        @Parameter(description = "Intensidade exata (1 a 5)")
        @RequestParam(required = false) intensity: Int?,
        @Parameter(description = "Intensidade maxima (1 a 5)")
        @RequestParam(required = false) maxIntensity: Int?
    ): ResponseEntity<ExperienceResponse> {
        return ResponseEntity.ok(experienceService.activate(boxId, intensity, maxIntensity))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar experiencia", description = "Retorna o conteudo completo da experiencia se o autor estiver autenticado no contexto correto.")
    fun getById(@PathVariable id: UUID): ResponseEntity<ExperienceResponse> =
        ResponseEntity.ok(experienceService.getDetail(id))

    @PostMapping
    @Operation(summary = "Criar experiencia", description = "Registra uma nova experiencia na caixinha selecionada.")
    @ApiResponse(responseCode = "201", description = "Experiencia criada")
    fun create(@Valid @RequestBody request: ExperienceUpsertRequest): ResponseEntity<ExperienceResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(experienceService.create(request))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar experiencia", description = "Atualiza os dados de uma experiencia ja cadastrada.")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ExperienceUpsertRequest
    ): ResponseEntity<ExperienceResponse> {
        return ResponseEntity.ok(experienceService.update(id, request))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover experiencia", description = "Exclui uma experiencia da caixinha ativa.")
    @ApiResponse(responseCode = "204", description = "Experiencia removida")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        experienceService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
