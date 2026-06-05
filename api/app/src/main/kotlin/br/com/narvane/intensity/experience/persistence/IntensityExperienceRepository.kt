package br.com.narvane.intensity.experience.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface IntensityExperienceRepository : JpaRepository<IntensityExperienceEntity, UUID> {
    fun findAllByCreatedBy(createdBy: UUID): List<IntensityExperienceEntity>
    fun findAllByBoxIdOrderByCreatedAtDesc(boxId: UUID): List<IntensityExperienceEntity>
    fun findAllByBoxIdAndIntensityOrderByCreatedAtDesc(boxId: UUID, intensity: Int): List<IntensityExperienceEntity>
    fun findAllByBoxIdAndIntensityLessThanEqualOrderByCreatedAtDesc(boxId: UUID, maxIntensity: Int): List<IntensityExperienceEntity>

    @Query(
        value = "SELECT * FROM intensity.experiences WHERE box_id = :boxId ORDER BY RANDOM() LIMIT 1",
        nativeQuery = true
    )
    fun drawAnyInBox(@Param("boxId") boxId: UUID): IntensityExperienceEntity?

    @Query(
        value = "SELECT * FROM intensity.experiences WHERE box_id = :boxId AND intensity = :intensity ORDER BY RANDOM() LIMIT 1",
        nativeQuery = true
    )
    fun drawByIntensityInBox(@Param("boxId") boxId: UUID, @Param("intensity") intensity: Int): IntensityExperienceEntity?

    @Query(
        value = "SELECT * FROM intensity.experiences WHERE box_id = :boxId AND intensity <= :maxIntensity ORDER BY RANDOM() LIMIT 1",
        nativeQuery = true
    )
    fun drawByMaxIntensityInBox(@Param("boxId") boxId: UUID, @Param("maxIntensity") maxIntensity: Int): IntensityExperienceEntity?
}
