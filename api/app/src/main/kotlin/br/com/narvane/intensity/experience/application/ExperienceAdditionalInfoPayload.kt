package br.com.narvane.intensity.experience.application

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * JSON serializado antes da cifragem em [additional_info_cipher].
 */
data class ExperienceAdditionalInfoPayload(
    @JsonProperty("involvesEveryone")
    val involvesEveryone: String,
    @JsonProperty("othersWouldAccept")
    val othersWouldAccept: String,
    @JsonProperty("mildDiscomfort")
    val mildDiscomfort: String
)
