package com.intensity.convite.service;

import com.intensity.common.AuthPrincipal;
import com.intensity.common.exception.ApiException;
import com.intensity.convite.dto.AcceptInviteResponse;
import com.intensity.convite.dto.InvitePreviewResponse;
import com.intensity.convite.dto.InviteResponse;
import com.intensity.convite.entity.Convite;
import com.intensity.convite.entity.InviteStatus;
import com.intensity.convite.repository.ConviteRepository;
import com.intensity.grupo.dto.GroupMemberResponse;
import com.intensity.grupo.entity.Grupo;
import com.intensity.grupo.entity.GrupoParticipante;
import com.intensity.grupo.repository.GrupoParticipanteRepository;
import com.intensity.grupo.repository.GrupoRepository;
import com.intensity.participante.entity.Participante;
import com.intensity.participante.repository.ParticipanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ConviteService {

	private final ConviteRepository conviteRepository;
	private final ConviteFactory conviteFactory;
	private final GrupoRepository grupoRepository;
	private final GrupoParticipanteRepository grupoParticipanteRepository;
	private final ParticipanteRepository participanteRepository;
	private final InviteCodeGenerator inviteCodeGenerator;

	public ConviteService(
			ConviteRepository conviteRepository,
			ConviteFactory conviteFactory,
			GrupoRepository grupoRepository,
			GrupoParticipanteRepository grupoParticipanteRepository,
			ParticipanteRepository participanteRepository,
			InviteCodeGenerator inviteCodeGenerator) {
		this.conviteRepository = conviteRepository;
		this.conviteFactory = conviteFactory;
		this.grupoRepository = grupoRepository;
		this.grupoParticipanteRepository = grupoParticipanteRepository;
		this.participanteRepository = participanteRepository;
		this.inviteCodeGenerator = inviteCodeGenerator;
	}

	@Transactional
	public InviteResponse create(UUID groupId, AuthPrincipal principal) {
		Grupo grupo = ensureGroupExists(groupId);
		ensureGroupMember(groupId, principal);

		Participante creator = participanteRepository
				.findById(principal.participantId())
				.orElseThrow(() -> unauthorized());

		Convite convite = conviteFactory.createNew(grupo, creator);
		conviteRepository.save(convite);

		return toResponse(convite, Instant.now());
	}

	@Transactional(readOnly = true)
	public List<InviteResponse> listActive(UUID groupId, AuthPrincipal principal) {
		ensureGroupExists(groupId);
		ensureGroupMember(groupId, principal);

		Instant now = Instant.now();
		return conviteRepository.findByGrupo_IdAndStatusOrderByCreatedAtDesc(groupId, InviteStatus.ACTIVE).stream()
				.filter(convite -> convite.effectiveStatus(now) == InviteStatus.ACTIVE)
				.map(convite -> toResponse(convite, now))
				.toList();
	}

	@Transactional
	public InvitePreviewResponse validate(String code, UUID linkToken) {
		if ((code == null || code.isBlank()) && linkToken == null) {
			throw validationError("Provide either code or link token.");
		}

		if (code != null && !code.isBlank() && !inviteCodeGenerator.isValidFormat(code)) {
			throw validationError("Enter a valid 6-character invite code.");
		}

		Convite convite = resolveInvite(code, linkToken);
		ensureUsableForPreview(convite);

		return toPreview(convite, Instant.now());
	}

	@Transactional
	public AcceptInviteResponse accept(UUID inviteId, AuthPrincipal principal) {
		Convite convite = ensureInviteExists(inviteId);
		Instant now = Instant.now();
		ensureUsableForAcceptance(convite, now);

		UUID groupId = convite.getGrupo().getId();
		if (grupoParticipanteRepository.existsById_GrupoIdAndId_ParticipanteId(
				groupId, principal.participantId())) {
			throw new ApiException(
					HttpStatus.CONFLICT,
					"ALREADY_GROUP_MEMBER",
					"You are already a member of this group.");
		}

		Participante acceptor = participanteRepository
				.findById(principal.participantId())
				.orElseThrow(this::unauthorized);

		grupoParticipanteRepository.save(new GrupoParticipante(convite.getGrupo(), acceptor.getId()));
		convite.markAccepted(acceptor, now);

		return new AcceptInviteResponse(groupId, true);
	}

	@Transactional
	public void revoke(UUID inviteId, AuthPrincipal principal) {
		Convite convite = ensureInviteExists(inviteId);
		ensureGroupMember(convite.getGrupo().getId(), principal);
		ensureActiveForMutation(convite, Instant.now());
		convite.markRevoked();
	}

	private Convite resolveInvite(String code, UUID linkToken) {
		if (code != null && !code.isBlank()) {
			return conviteRepository
					.findByCode(Convite.normalizeCode(code))
					.orElseThrow(this::inviteNotFound);
		}

		return conviteRepository
				.findByLinkToken(linkToken)
				.orElseThrow(this::inviteNotFound);
	}

	private void ensureUsableForPreview(Convite convite) {
		Instant now = Instant.now();
		InviteStatus effectiveStatus = convite.effectiveStatus(now);
		if (effectiveStatus == InviteStatus.ACTIVE) {
			return;
		}

		persistExpiredIfNeeded(convite, now);
		throw inviteGone(effectiveStatus);
	}

	private void ensureUsableForAcceptance(Convite convite, Instant now) {
		InviteStatus effectiveStatus = convite.effectiveStatus(now);
		if (effectiveStatus == InviteStatus.ACTIVE) {
			return;
		}

		persistExpiredIfNeeded(convite, now);
		throw inviteGone(effectiveStatus);
	}

	private void ensureActiveForMutation(Convite convite, Instant now) {
		InviteStatus effectiveStatus = convite.effectiveStatus(now);
		if (effectiveStatus == InviteStatus.ACTIVE) {
			return;
		}

		persistExpiredIfNeeded(convite, now);
		throw inviteGone(effectiveStatus);
	}

	private void persistExpiredIfNeeded(Convite convite, Instant now) {
		if (convite.getStatus() == InviteStatus.ACTIVE && now.isAfter(convite.getExpiresAt())) {
			convite.markExpired();
		}
	}

	private InvitePreviewResponse toPreview(Convite convite, Instant now) {
		List<GroupMemberResponse> members = listGroupMembers(convite.getGrupo().getId());
		return new InvitePreviewResponse(
				convite.getId(),
				convite.getGrupo().getId(),
				members,
				convite.getExpiresAt(),
				convite.effectiveStatus(now));
	}

	private InviteResponse toResponse(Convite convite, Instant now) {
		return new InviteResponse(
				convite.getId(),
				convite.getGrupo().getId(),
				convite.getCode(),
				convite.getLinkToken(),
				convite.getExpiresAt(),
				convite.effectiveStatus(now),
				convite.getCreatedAt());
	}

	private List<GroupMemberResponse> listGroupMembers(UUID groupId) {
		List<UUID> participantIds = grupoParticipanteRepository.findParticipantIdsByGroupId(groupId);
		return participanteRepository.findAllById(participantIds).stream()
				.sorted(Comparator.comparing(Participante::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.map(participant -> new GroupMemberResponse(participant.getId(), participant.getDisplayName()))
				.toList();
	}

	private Grupo ensureGroupExists(UUID groupId) {
		return grupoRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
	}

	private Convite ensureInviteExists(UUID inviteId) {
		return conviteRepository
				.findById(inviteId)
				.orElseThrow(this::inviteNotFound);
	}

	private void ensureGroupMember(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() == com.intensity.common.AccessMode.EXPERIENCE_BOX) {
			if (!groupId.equals(principal.groupId())) {
				throw forbidden();
			}
			return;
		}

		if (!grupoParticipanteRepository.existsById_GrupoIdAndId_ParticipanteId(
				groupId, principal.participantId())) {
			throw forbidden();
		}
	}

	private ApiException inviteNotFound() {
		return new ApiException(HttpStatus.NOT_FOUND, "INVITE_NOT_FOUND", "Invite not found.");
	}

	private ApiException inviteGone(InviteStatus status) {
		String message = switch (status) {
			case EXPIRED -> "This invite has expired.";
			case REVOKED -> "This invite has been revoked.";
			case ACCEPTED -> "This invite has already been accepted.";
			default -> "This invite is no longer available.";
		};
		return new ApiException(HttpStatus.GONE, "INVITE_GONE", message);
	}

	private ApiException validationError(String message) {
		return new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", message);
	}

	private ApiException forbidden() {
		return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
	}

	private ApiException unauthorized() {
		return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token.");
	}
}
