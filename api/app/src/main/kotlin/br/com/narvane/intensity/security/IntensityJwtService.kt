package br.com.narvane.intensity.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

data class JwtPayload(
    val accessMode: AccessMode,
    val userId: UUID?,
    val participantUserIds: List<UUID>,
    val groupId: UUID?,
    val boxId: UUID?
)

@Service
class IntensityJwtService(
    @Value("\${security.jwt.secret}") private val secret: String,
    @Value("\${security.jwt.expiration-ms}") private val expirationMs: Long
) {
    private fun signingKey(): SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun createCurateToken(
        userId: UUID,
        email: String,
        groupId: UUID? = null,
        boxId: UUID? = null
    ): String {
        val now = Date()
        val expiresAt = Date(now.time + expirationMs)
        val builder = Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("accessMode", AccessMode.CURATE.name)
            .issuedAt(now)
            .expiration(expiresAt)
        if (groupId != null) {
            builder.claim("groupId", groupId.toString())
        }
        if (boxId != null) {
            builder.claim("boxId", boxId.toString())
        }
        return builder.signWith(signingKey()).compact()
    }

    fun createConnectToken(
        participantUserIds: List<UUID>,
        participantEmails: List<String>,
        groupId: UUID
    ): String {
        val now = Date()
        val expiresAt = Date(now.time + expirationMs)
        return Jwts.builder()
            .subject("connect-session")
            .claim("accessMode", AccessMode.CONNECT.name)
            .claim("participantUserIds", participantUserIds.map(UUID::toString))
            .claim("participantEmails", participantEmails)
            .claim("groupId", groupId.toString())
            .issuedAt(now)
            .expiration(expiresAt)
            .signWith(signingKey())
            .compact()
    }

    fun parsePayload(token: String): JwtPayload {
        val claims = claims(token)
        val accessMode = AccessMode.valueOf(claims["accessMode"].toString())
        val userId = claims.subject.takeIf { it != "connect-session" }?.let(UUID::fromString)
        val participantIds = claims["participantUserIds"] as? List<*> ?: emptyList<Any>()
        return JwtPayload(
            accessMode = accessMode,
            userId = userId,
            participantUserIds = participantIds.mapNotNull { runCatching { UUID.fromString(it.toString()) }.getOrNull() },
            groupId = uuidClaim(claims, "groupId"),
            boxId = uuidClaim(claims, "boxId")
        )
    }

    private fun uuidClaim(claims: Claims, key: String): UUID? {
        val raw = claims[key] ?: return null
        return runCatching { UUID.fromString(raw.toString()) }.getOrNull()
    }

    fun isValid(token: String): Boolean = runCatching {
        val expiration = claims(token).expiration
        expiration.after(Date())
    }.getOrDefault(false)

    private fun claims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
