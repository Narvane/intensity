package br.com.narvane.intensity.group.application

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

object IntensityGroupFingerprint {
    fun fromUserIds(userIds: List<UUID>): String {
        val sorted = userIds.map { it.toString() }.sorted().joinToString(",")
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(sorted.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { b -> "%02x".format(b) }
    }
}
