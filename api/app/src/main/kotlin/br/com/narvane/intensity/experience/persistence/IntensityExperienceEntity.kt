package br.com.narvane.intensity.experience.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "experiences", schema = "intensity")
class IntensityExperienceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "description_cipher", nullable = false, columnDefinition = "TEXT")
    var descriptionCipher: String,

    @Column(name = "description_md5", nullable = false, length = 32)
    var descriptionMd5: String,

    @Column(nullable = false)
    var intensity: Int,

    @Column(name = "created_by", nullable = false)
    var createdBy: UUID,

    @Column(name = "box_id", nullable = false)
    var boxId: UUID,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "effort_stars")
    var effortStars: Int? = null,

    @Column(name = "openness_stars")
    var opennessStars: Int? = null,

    @Column(name = "novelty_stars")
    var noveltyStars: Int? = null,

    @Column(name = "additional_info_cipher", columnDefinition = "TEXT")
    var additionalInfoCipher: String? = null
)
