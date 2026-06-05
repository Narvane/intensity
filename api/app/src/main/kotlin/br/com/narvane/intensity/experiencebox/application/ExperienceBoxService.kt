package br.com.narvane.intensity.experiencebox.application

import br.com.narvane.intensity.experiencebox.domain.ExperienceBoxTypeCodes
import br.com.narvane.intensity.experiencebox.persistence.IntensityExperienceBoxEntity
import br.com.narvane.intensity.experiencebox.persistence.IntensityExperienceBoxRepository
import br.com.narvane.intensity.experiencebox.web.ExperienceBoxResponse
import br.com.narvane.intensity.shared.web.ApiException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ExperienceBoxService(
    private val experienceBoxRepository: IntensityExperienceBoxRepository
) {
    fun listForGroup(groupId: UUID): List<ExperienceBoxResponse> {
        return experienceBoxRepository.findAllByGroupIdOrderByCreatedAtDesc(groupId).map(::toResponse)
    }

    @Transactional
    fun createInGroup(groupId: UUID, name: String, boxTypeRaw: String? = null): ExperienceBoxResponse {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Nome da caixinha de experiencias e obrigatorio")
        }
        val boxType = ExperienceBoxTypeCodes.normalize(boxTypeRaw)
        val saved = experienceBoxRepository.save(
            IntensityExperienceBoxEntity(groupId = groupId, name = trimmed, boxType = boxType)
        )
        return toResponse(saved)
    }

    fun requireBoxInGroup(boxId: UUID, groupId: UUID): IntensityExperienceBoxEntity {
        return experienceBoxRepository.findByIdAndGroupId(boxId, groupId)
            ?: throw ApiException(HttpStatus.NOT_FOUND, "Caixinha de experiencias nao encontrada neste grupo")
    }

    private fun toResponse(entity: IntensityExperienceBoxEntity): ExperienceBoxResponse {
        return ExperienceBoxResponse(
            id = entity.id ?: throw IllegalStateException("Box sem id"),
            groupId = entity.groupId,
            name = entity.name,
            boxType = entity.boxType,
            createdAt = entity.createdAt
        )
    }
}
