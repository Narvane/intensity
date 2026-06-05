package br.com.narvane.intensity.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IntensityCurrentAccess {
    fun principal(): AppPrincipal {
        val auth = SecurityContextHolder.getContext().authentication
        return auth?.principal as? AppPrincipal ?: throw IllegalStateException("Unauthenticated principal")
    }

    fun requireMode(mode: AccessMode) {
        if (principal().accessMode != mode) {
            throw IllegalArgumentException("Sessao sem permissao para esta operacao")
        }
    }

    fun curateUserId(): UUID {
        val principal = principal()
        if (principal.accessMode != AccessMode.CURATE || principal.userId == null) {
            throw IllegalArgumentException("Somente sessao de Desafiar pode realizar esta operacao")
        }
        return principal.userId
    }
}
