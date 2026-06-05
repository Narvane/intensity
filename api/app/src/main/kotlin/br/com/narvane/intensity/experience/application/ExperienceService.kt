package br.com.narvane.intensity.experience.application

import br.com.narvane.intensity.experiencebox.application.ExperienceBoxService
import br.com.narvane.intensity.experience.persistence.IntensityExperienceEntity
import br.com.narvane.intensity.experience.persistence.IntensityExperienceRepository
import br.com.narvane.intensity.experience.web.ExperienceResonanceBlock
import br.com.narvane.intensity.experience.web.ExperienceResponse
import br.com.narvane.intensity.experience.web.ExperienceSummaryResponse
import br.com.narvane.intensity.experience.web.ExperienceUpsertRequest
import br.com.narvane.intensity.experience.web.ExperienceReflectionBlock
import com.fasterxml.jackson.databind.ObjectMapper
import br.com.narvane.intensity.security.AccessMode
import br.com.narvane.intensity.security.IntensityCurrentAccess
import br.com.narvane.intensity.shared.web.ApiException
import br.com.narvane.intensity.group.application.IntensityGroupService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ExperienceService(
    private val experienceRepository: IntensityExperienceRepository,
    private val cryptoService: ExperienceCryptoService,
    private val currentAccess: IntensityCurrentAccess,
    private val groupService: IntensityGroupService,
    private val experienceBoxService: ExperienceBoxService,
    private val objectMapper: ObjectMapper
) {
    fun listSummariesForConnect(boxId: UUID, intensity: Int?, maxIntensity: Int?): List<ExperienceSummaryResponse> {
        currentAccess.requireMode(AccessMode.CONNECT)
        val groupId = currentAccess.principal().groupId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Grupo nao definido na sessao")
        experienceBoxService.requireBoxInGroup(boxId, groupId)
        validateSelectionFilter(intensity, maxIntensity)
        return when {
            intensity != null -> experienceRepository.findAllByBoxIdAndIntensityOrderByCreatedAtDesc(boxId, intensity)
            maxIntensity != null -> experienceRepository.findAllByBoxIdAndIntensityLessThanEqualOrderByCreatedAtDesc(boxId, maxIntensity)
            else -> experienceRepository.findAllByBoxIdOrderByCreatedAtDesc(boxId)
        }.map(::toSummary)
    }

    fun getDetailForConnect(boxId: UUID, id: UUID): ExperienceResponse {
        currentAccess.requireMode(AccessMode.CONNECT)
        val groupId = currentAccess.principal().groupId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Grupo nao definido na sessao")
        experienceBoxService.requireBoxInGroup(boxId, groupId)
        val entity = experienceRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (entity.boxId != boxId) {
            throw ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        return toResponse(entity)
    }

    fun listSummaries(): List<ExperienceSummaryResponse> {
        val principal = currentAccess.principal()
        if (principal.accessMode != AccessMode.CURATE) {
            throw ApiException(HttpStatus.FORBIDDEN, "Listagem disponivel apenas no modo proponente")
        }
        val boxId = principal.boxId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione uma caixinha de experiencias")
        val userId = currentAccess.curateUserId()
        assertCuratorAccessToExperienceBox(userId, boxId)
        return experienceRepository.findAllByBoxIdOrderByCreatedAtDesc(boxId).map(::toSummary)
    }

    fun getDetail(id: UUID): ExperienceResponse {
        val principal = currentAccess.principal()
        if (principal.accessMode != AccessMode.CURATE) {
            throw ApiException(HttpStatus.FORBIDDEN, "Detalhe disponivel apenas no modo proponente")
        }
        val boxId = principal.boxId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione uma caixinha de experiencias")
        val userId = currentAccess.curateUserId()
        assertCuratorAccessToExperienceBox(userId, boxId)
        val entity = experienceRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (entity.boxId != boxId) {
            throw ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (entity.createdBy != userId) {
            throw ApiException(HttpStatus.FORBIDDEN, "Apenas quem registrou pode ver o texto completo desta experiencia")
        }
        return toResponse(entity)
    }

    fun create(request: ExperienceUpsertRequest): ExperienceResponse {
        val curateUserId = currentAccess.curateUserId()
        val principal = currentAccess.principal()
        val boxId = principal.boxId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione uma caixinha de experiencias")
        assertCuratorAccessToExperienceBox(curateUserId, boxId)
        val normalized = request.description.trim()
        val entity = experienceRepository.save(
            IntensityExperienceEntity(
                descriptionCipher = cryptoService.encrypt(normalized),
                descriptionMd5 = cryptoService.md5(normalized),
                intensity = request.intensity,
                createdBy = curateUserId,
                boxId = boxId,
                effortStars = request.effortStars,
                opennessStars = request.opennessStars,
                noveltyStars = request.noveltyStars,
                additionalInfoCipher = encryptAdditionalInfo(request)
            )
        )
        return toResponse(entity)
    }

    fun update(id: UUID, request: ExperienceUpsertRequest): ExperienceResponse {
        val curateUserId = currentAccess.curateUserId()
        val principal = currentAccess.principal()
        val boxId = principal.boxId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione uma caixinha de experiencias")
        assertCuratorAccessToExperienceBox(curateUserId, boxId)
        val existing = experienceRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (existing.boxId != boxId) {
            throw ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (existing.createdBy != curateUserId) {
            throw ApiException(HttpStatus.FORBIDDEN, "Voce nao pode editar experiencias de outro autor")
        }

        val normalized = request.description.trim()
        existing.descriptionCipher = cryptoService.encrypt(normalized)
        existing.descriptionMd5 = cryptoService.md5(normalized)
        existing.intensity = request.intensity
        existing.effortStars = request.effortStars
        existing.opennessStars = request.opennessStars
        existing.noveltyStars = request.noveltyStars
        existing.additionalInfoCipher = encryptAdditionalInfo(request)
        return toResponse(experienceRepository.save(existing))
    }

    fun delete(id: UUID) {
        val curateUserId = currentAccess.curateUserId()
        val principal = currentAccess.principal()
        val boxId = principal.boxId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione uma caixinha de experiencias")
        assertCuratorAccessToExperienceBox(curateUserId, boxId)
        val existing = experienceRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (existing.boxId != boxId) {
            throw ApiException(HttpStatus.NOT_FOUND, "Experiencia nao encontrada")
        }
        if (existing.createdBy != curateUserId) {
            throw ApiException(HttpStatus.FORBIDDEN, "Voce nao pode remover experiencias de outro autor")
        }
        experienceRepository.delete(existing)
    }

    fun activate(boxId: UUID, intensity: Int?, maxIntensity: Int?): ExperienceResponse {
        currentAccess.requireMode(AccessMode.CONNECT)
        val principal = currentAccess.principal()
        val groupId = principal.groupId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Grupo nao definido na sessao")
        experienceBoxService.requireBoxInGroup(boxId, groupId)

        validateSelectionFilter(intensity, maxIntensity)

        val drawn = when {
            intensity != null -> experienceRepository.drawByIntensityInBox(boxId, intensity)
            maxIntensity != null -> experienceRepository.drawByMaxIntensityInBox(boxId, maxIntensity)
            else -> experienceRepository.drawAnyInBox(boxId)
        } ?: throw ApiException(HttpStatus.BAD_REQUEST, "Nao ha experiencias disponiveis para o filtro informado")
        return toResponse(drawn)
    }

    private fun validateSelectionFilter(intensity: Int?, maxIntensity: Int?) {
        if (intensity != null && maxIntensity != null) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Use apenas intensidade fixa ou intensidade maxima")
        }

        val validRange = 1..5
        if (intensity != null && intensity !in validRange) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Intensidade fixa deve estar entre 1 e 5")
        }
        if (maxIntensity != null && maxIntensity !in validRange) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Intensidade maxima deve estar entre 1 e 5")
        }
    }

    private fun assertCuratorAccessToExperienceBox(userId: UUID, boxId: UUID) {
        val groupId = currentAccess.principal().groupId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione um grupo")
        groupService.requireMember(groupId, userId)
        experienceBoxService.requireBoxInGroup(boxId, groupId)
    }

    private fun toSummary(entity: IntensityExperienceEntity): ExperienceSummaryResponse {
        return ExperienceSummaryResponse(
            id = entity.id ?: throw IllegalStateException("Experience sem id persistido"),
            intensity = entity.intensity,
            createdBy = entity.createdBy,
            createdAt = entity.createdAt,
            descriptionMd5 = entity.descriptionMd5
        )
    }

    private fun toResponse(entity: IntensityExperienceEntity): ExperienceResponse {
        val effort = entity.effortStars
        val openness = entity.opennessStars
        val novelty = entity.noveltyStars
        val parameters = if (effort != null && openness != null && novelty != null) {
            ExperienceResonanceBlock(
                effortStars = effort,
                opennessStars = openness,
                noveltyStars = novelty
            )
        } else {
            null
        }
        return ExperienceResponse(
            id = entity.id ?: throw IllegalStateException("Experience sem id persistido"),
            description = cryptoService.decrypt(entity.descriptionCipher),
            intensity = entity.intensity,
            createdBy = entity.createdBy,
            createdAt = entity.createdAt,
            descriptionMd5 = entity.descriptionMd5,
            additionalInfo = decryptAdditionalInfo(entity.additionalInfoCipher),
            parameters = parameters
        )
    }

    private fun encryptAdditionalInfo(request: ExperienceUpsertRequest): String {
        val payload = ExperienceAdditionalInfoPayload(
            involvesEveryone = request.involvesEveryoneJustification.trim(),
            othersWouldAccept = request.othersWouldAcceptJustification.trim(),
            mildDiscomfort = request.mildDiscomfortJustification.trim()
        )
        val json = objectMapper.writeValueAsString(payload)
        return cryptoService.encrypt(json)
    }

    private fun decryptAdditionalInfo(cipher: String?): ExperienceReflectionBlock? {
        if (cipher.isNullOrBlank()) {
            return null
        }
        return runCatching {
            val json = cryptoService.decrypt(cipher)
            val payload = objectMapper.readValue(json, ExperienceAdditionalInfoPayload::class.java)
            ExperienceReflectionBlock(
                involvesEveryone = payload.involvesEveryone,
                othersWouldAccept = payload.othersWouldAccept,
                mildDiscomfort = payload.mildDiscomfort
            )
        }.getOrNull()
    }
}
