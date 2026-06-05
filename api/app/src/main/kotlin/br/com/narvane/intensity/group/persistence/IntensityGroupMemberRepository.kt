package br.com.narvane.intensity.group.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IntensityGroupMemberRepository : JpaRepository<IntensityGroupMemberEntity, GroupMemberPk> {
    fun existsByGroupIdAndUserId(groupId: UUID, userId: UUID): Boolean
    fun findAllByGroupId(groupId: UUID): List<IntensityGroupMemberEntity>
}
