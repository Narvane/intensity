package br.com.narvane.intensity.group.application

import br.com.narvane.intensity.auth.persistence.IntensityUserRepository
import br.com.narvane.intensity.group.persistence.IntensityGroupEntity
import br.com.narvane.intensity.group.persistence.IntensityGroupMemberEntity
import br.com.narvane.intensity.group.persistence.IntensityGroupMemberRepository
import br.com.narvane.intensity.group.persistence.IntensityGroupRepository
import br.com.narvane.intensity.group.web.ParticipantSnippetResponse
import br.com.narvane.intensity.group.web.GroupDetailResponse
import br.com.narvane.intensity.shared.web.ApiException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class IntensityGroupService(
    private val groupRepository: IntensityGroupRepository,
    private val groupMemberRepository: IntensityGroupMemberRepository,
    private val userRepository: IntensityUserRepository
) {
    @Transactional
    fun resolveOrCreateGroup(participantUserIds: List<UUID>): IntensityGroupEntity {
        val fingerprint = IntensityGroupFingerprint.fromUserIds(participantUserIds)
        groupRepository.findByFingerprint(fingerprint)?.let { return it }
        val group = groupRepository.save(
            IntensityGroupEntity(fingerprint = fingerprint)
        )
        val sid = group.id ?: throw IllegalStateException("Grupo sem id")
        participantUserIds.forEach { uid ->
            groupMemberRepository.save(IntensityGroupMemberEntity(groupId = sid, userId = uid))
        }
        return group
    }

    fun requireMember(groupId: UUID, userId: UUID) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw ApiException(HttpStatus.FORBIDDEN, "Voce nao participa deste grupo")
        }
    }

    fun listGroupsForUser(userId: UUID): List<GroupDetailResponse> {
        val groups = groupRepository.findGroupsForUser(userId)
        return groups.map { group ->
            val sid = group.id ?: throw IllegalStateException("Grupo sem id")
            val memberRows = groupMemberRepository.findAllByGroupId(sid)
            val participants = memberRows.mapNotNull { row ->
                userRepository.findById(row.userId).orElse(null)?.let { u ->
                    val pid = u.id ?: return@let null
                    ParticipantSnippetResponse(id = pid, name = u.name)
                }
            }.sortedBy { it.name.lowercase() }
            GroupDetailResponse(
                id = sid,
                participants = participants
            )
        }
    }
}
