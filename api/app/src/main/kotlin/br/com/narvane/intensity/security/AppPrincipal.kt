package br.com.narvane.intensity.security

import java.util.UUID

data class AppPrincipal(
    val accessMode: AccessMode,
    val userId: UUID?,
    val participantUserIds: List<UUID>,
    val groupId: UUID? = null,
    val boxId: UUID? = null
)
