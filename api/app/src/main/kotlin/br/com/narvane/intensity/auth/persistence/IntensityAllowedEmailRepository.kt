package br.com.narvane.intensity.auth.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface IntensityAllowedEmailRepository : JpaRepository<IntensityAllowedEmailEntity, String>
