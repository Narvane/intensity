package br.com.narvane.intensity.group.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface IntensityGroupRepository : JpaRepository<IntensityGroupEntity, UUID> {
    fun findByFingerprint(fingerprint: String): IntensityGroupEntity?

    @Query(
        """
        SELECT DISTINCT s FROM IntensityGroupEntity s, IntensityGroupMemberEntity m
        WHERE m.groupId = s.id AND m.userId = :userId
        ORDER BY s.createdAt DESC
        """
    )
    fun findGroupsForUser(@Param("userId") userId: UUID): List<IntensityGroupEntity>
}
