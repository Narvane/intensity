package br.com.narvane.intensity.auth.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "allowed_emails", schema = "intensity")
class IntensityAllowedEmailEntity(
    @Id
    @Column(nullable = false, unique = true)
    val email: String
)
