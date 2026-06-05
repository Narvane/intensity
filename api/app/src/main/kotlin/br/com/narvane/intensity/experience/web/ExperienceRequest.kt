package br.com.narvane.intensity.experience.web

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ExperienceUpsertRequest(
    @field:Schema(example = "Fazer um jantar colaborativo com todo mundo cozinhando juntos.")
    @field:NotBlank(message = "Descricao e obrigatoria")
    @field:Size(max = 1000, message = "Descricao deve ter no maximo 1000 caracteres")
    val description: String,
    @field:Schema(example = "3")
    @field:Min(value = 1, message = "Intensidade invalida")
    @field:Max(value = 5, message = "Intensidade invalida")
    val intensity: Int,
    /** Opcional no fluxo atual (reflexao unica em [othersWouldAcceptJustification]). Pode ser vazio. */
    @field:Schema(example = "Todo mundo participa da preparacao e consegue contribuir.")
    @field:Size(max = 2000, message = "Justificativa muito longa")
    val involvesEveryoneJustification: String,
    @field:Schema(example = "A atividade e leve, inclusiva e cabe no ritmo do grupo.")
    @field:NotBlank(message = "Justifique se todos topariam a experiencia (reflexao obrigatoria)")
    @field:Size(max = 2000, message = "Justificativa muito longa")
    val othersWouldAcceptJustification: String,
    /** Opcional no fluxo atual. Pode ser vazio. */
    @field:Schema(example = "Pode gerar um pouco de vergonha no inicio, mas de forma segura.")
    @field:Size(max = 2000, message = "Justificativa muito longa")
    val mildDiscomfortJustification: String,
    @field:Schema(example = "3")
    @field:Min(value = 1, message = "Compromisso invalido")
    @field:Max(value = 5, message = "Compromisso invalido")
    val effortStars: Int,
    @field:Schema(example = "4")
    @field:Min(value = 1, message = "Abertura emocional invalida")
    @field:Max(value = 5, message = "Abertura emocional invalida")
    val opennessStars: Int,
    @field:Schema(example = "2")
    @field:Min(value = 1, message = "Novidade invalida")
    @field:Max(value = 5, message = "Novidade invalida")
    val noveltyStars: Int
)
