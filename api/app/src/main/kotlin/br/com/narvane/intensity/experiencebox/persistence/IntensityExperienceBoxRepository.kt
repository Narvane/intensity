package br.com.narvane.intensity.experiencebox.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IntensityExperienceBoxRepository : JpaRepository<IntensityExperienceBoxEntity, UUID> {
    fun findAllByGroupIdOrderByCreatedAtDesc(groupId: UUID): List<IntensityExperienceBoxEntity>
    fun findByIdAndGroupId(id: UUID, groupId: UUID): IntensityExperienceBoxEntity?
}
