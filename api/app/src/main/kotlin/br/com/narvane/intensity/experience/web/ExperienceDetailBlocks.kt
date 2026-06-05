package br.com.narvane.intensity.experience.web

import io.swagger.v3.oas.annotations.media.Schema

data class ExperienceReflectionBlock(
    @field:Schema(example = "Todo mundo consegue participar com conforto.")
    val involvesEveryone: String,
    @field:Schema(example = "Sim, a proposta e acessivel e respeita os limites do grupo.")
    val othersWouldAccept: String,
    @field:Schema(example = "Existe um leve desconforto inicial, mas sem exposicao excessiva.")
    val mildDiscomfort: String
)

data class ExperienceResonanceBlock(
    @field:Schema(example = "3")
    val effortStars: Int,
    @field:Schema(example = "4")
    val opennessStars: Int,
    @field:Schema(example = "2")
    val noveltyStars: Int
)
