package br.com.narvane.intensity.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class IntensityJwtAuthenticationFilter(
    private val intensityJwtService: IntensityJwtService
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI ?: return true
        return !path.startsWith("/intensity/")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header.isNullOrBlank() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix("Bearer ").trim()
        if (!intensityJwtService.isValid(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val payload = intensityJwtService.parsePayload(token)
        val principal = AppPrincipal(
            accessMode = payload.accessMode,
            userId = payload.userId,
            participantUserIds = payload.participantUserIds,
            groupId = payload.groupId,
            boxId = payload.boxId
        )
        val auth = UsernamePasswordAuthenticationToken(
            principal,
            null,
            listOf(SimpleGrantedAuthority("MODE_${payload.accessMode.name}"))
        )
        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
    }
}
