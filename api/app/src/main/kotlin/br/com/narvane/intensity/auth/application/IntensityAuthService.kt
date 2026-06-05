package br.com.narvane.intensity.auth.application

import br.com.narvane.intensity.auth.persistence.IntensityAllowedEmailRepository
import br.com.narvane.intensity.auth.persistence.IntensityUserEntity
import br.com.narvane.intensity.auth.persistence.IntensityUserRepository
import br.com.narvane.intensity.auth.web.AuthResponse
import br.com.narvane.intensity.auth.web.CurateLoginRequest
import br.com.narvane.intensity.auth.web.ConnectLoginRequest
import br.com.narvane.intensity.auth.web.RegisteredUserResponse
import br.com.narvane.intensity.auth.web.RegisterRequest
import br.com.narvane.intensity.experiencebox.application.ExperienceBoxService
import br.com.narvane.intensity.security.AccessMode
import br.com.narvane.intensity.security.AppPrincipal
import br.com.narvane.intensity.security.IntensityJwtService
import br.com.narvane.intensity.shared.web.ApiException
import br.com.narvane.intensity.group.application.IntensityGroupService
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IntensityAuthService(
    private val allowedEmailRepository: IntensityAllowedEmailRepository,
    private val userRepository: IntensityUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val intensityJwtService: IntensityJwtService,
    private val intensityGroupService: IntensityGroupService,
    private val experienceBoxService: ExperienceBoxService
) {
    fun register(request: RegisterRequest): AuthResponse {
        val email = request.email.trim().lowercase()
        if (userRepository.findByEmail(email).isPresent) {
            throw ApiException(HttpStatus.CONFLICT, "Ja existe usuario com este email")
        }

        allowedEmailRepository.findById(email).orElseThrow {
            ApiException(HttpStatus.FORBIDDEN, "Este email nao esta liberado para cadastro")
        }

        val saved = userRepository.save(
            IntensityUserEntity(
                name = request.name.trim(),
                email = email,
                passwordHash = passwordEncoder.encode(request.password)
            )
        )

        return AuthResponse(
            token = null,
            accessMode = null,
            userId = saved.id
        )
    }

    fun listRegisteredUsers(): List<RegisteredUserResponse> {
        return userRepository.findAllByOrderByNameAsc().map { user ->
            RegisteredUserResponse(
                id = user.id ?: throw IllegalStateException("Usuario sem id persistido"),
                name = user.name,
                email = user.email
            )
        }
    }

    fun loginCurate(request: CurateLoginRequest): AuthResponse {
        val email = request.email.trim().lowercase()
        val user = userRepository.findByEmail(email).orElseThrow {
            ApiException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas")
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ApiException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas")
        }

        val uid = user.id ?: throw IllegalStateException("Usuario sem id")
        val token = intensityJwtService.createCurateToken(uid, user.email)
        return AuthResponse(
            token = token,
            accessMode = AccessMode.CURATE.name,
            userId = uid
        )
    }

    fun loginConnect(request: ConnectLoginRequest): AuthResponse {
        val byEmail = request.credentials.associateBy { it.email.trim().lowercase() }
        if (byEmail.size != request.credentials.size) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Emails duplicados no login")
        }

        val resolvedUsers = mutableListOf<IntensityUserEntity>()
        for ((emailNorm, cred) in byEmail) {
            val user = userRepository.findByEmail(emailNorm).orElseThrow {
                ApiException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas")
            }
            if (!passwordEncoder.matches(cred.password, user.passwordHash)) {
                throw ApiException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas")
            }
            resolvedUsers.add(user)
        }

        val sortedUsers = resolvedUsers.sortedBy { it.id }
        val participantIds = sortedUsers.map { u -> u.id ?: throw IllegalStateException("Usuario sem id") }
        val group = intensityGroupService.resolveOrCreateGroup(participantIds)
        val groupId = group.id ?: throw IllegalStateException("Grupo sem id")
        val token = intensityJwtService.createConnectToken(
            participantUserIds = participantIds,
            participantEmails = sortedUsers.map { it.email },
            groupId = groupId
        )
        return AuthResponse(
            token = token,
            accessMode = AccessMode.CONNECT.name,
            userId = null,
            participantUserIds = participantIds,
            groupId = groupId
        )
    }

    fun selectCurateGroup(principal: AppPrincipal, groupId: UUID): AuthResponse {
        if (principal.accessMode != AccessMode.CURATE) {
            throw ApiException(HttpStatus.FORBIDDEN, "Sessao invalida")
        }
        val userId = principal.userId ?: throw ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao identificado")
        intensityGroupService.requireMember(groupId, userId)
        val user = userRepository.findById(userId).orElseThrow {
            ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado")
        }
        val token = intensityJwtService.createCurateToken(userId, user.email, groupId, null)
        return AuthResponse(
            token = token,
            accessMode = AccessMode.CURATE.name,
            userId = userId,
            groupId = groupId,
            boxId = null
        )
    }

    fun selectCurateExperienceBox(principal: AppPrincipal, boxId: UUID): AuthResponse {
        if (principal.accessMode != AccessMode.CURATE) {
            throw ApiException(HttpStatus.FORBIDDEN, "Sessao invalida")
        }
        val userId = principal.userId ?: throw ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao identificado")
        val groupId = principal.groupId
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "Selecione um grupo antes da caixinha")
        intensityGroupService.requireMember(groupId, userId)
        val box = experienceBoxService.requireBoxInGroup(boxId, groupId)
        val bid = box.id ?: throw IllegalStateException("Box sem id")
        val user = userRepository.findById(userId).orElseThrow {
            ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado")
        }
        val token = intensityJwtService.createCurateToken(userId, user.email, groupId, bid)
        return AuthResponse(
            token = token,
            accessMode = AccessMode.CURATE.name,
            userId = userId,
            groupId = groupId,
            boxId = bid,
            experienceBoxType = box.boxType
        )
    }
}
