package br.com.narvane.intensity.group.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

data class GroupMemberPk(
    var groupId: UUID? = null,
    var userId: UUID? = null
) : Serializable

@Entity
@Table(name = "group_members", schema = "intensity")
@IdClass(GroupMemberPk::class)
class IntensityGroupMemberEntity(
    @Id
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID
)
