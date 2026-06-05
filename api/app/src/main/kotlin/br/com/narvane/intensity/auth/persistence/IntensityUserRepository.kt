package br.com.narvane.intensity.auth.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface IntensityUserRepository : JpaRepository<IntensityUserEntity, UUID> {
    fun findByEmail(email: String): Optional<IntensityUserEntity>
    fun findAllByOrderByNameAsc(): List<IntensityUserEntity>
}
