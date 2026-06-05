package br.com.narvane.intensity.experiencebox.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import br.com.narvane.intensity.experiencebox.domain.ExperienceBoxTypeCodes
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "experience_boxes", schema = "intensity")
class IntensityExperienceBoxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(name = "box_type", nullable = false, length = 64)
    var boxType: String = ExperienceBoxTypeCodes.DEFAULT,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
