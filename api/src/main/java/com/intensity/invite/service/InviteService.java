package com.intensity.invite.service;

import com.intensity.platform.common.AuthPrincipal;
import com.intensity.platform.common.exception.ApiException;
import com.intensity.invite.dto.AcceptInviteResponse;
import com.intensity.invite.dto.InvitePreviewResponse;
import com.intensity.invite.dto.InviteResponse;
import com.intensity.invite.entity.Invite;
import com.intensity.invite.entity.InviteStatus;
import com.intensity.invite.repository.InviteRepository;
import com.intensity.group.dto.GroupMemberResponse;
import com.intensity.group.entity.Group;
import com.intensity.group.entity.GroupParticipant;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.ParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class InviteService {

	private final InviteRepository inviteRepository;
	private final InviteFactory inviteFactory;
	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final ParticipantRepository participantRepository;
	private final InviteCodeGenerator inviteCodeGenerator;

	public InviteService(
			InviteRepository inviteRepository,
			InviteFactory inviteFactory,
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository,
			ParticipantRepository participantRepository,
			InviteCodeGenerator inviteCodeGenerator) {
		this.inviteRepository = inviteRepository;
		this.inviteFactory = inviteFactory;
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.participantRepository = participantRepository;
		this.inviteCodeGenerator = inviteCodeGenerator;
	}

	@Transactional
	public InviteResponse create(UUID groupId, AuthPrincipal principal) {
		Group group = ensureGroupExists(groupId);
		ensureGroupMember(groupId, principal);

		Participant creator = participantRepository
				.findById(principal.participantId())
				.orElseThrow(() -> unauthorized());

		Invite invite = inviteFactory.createNew(group, creator);
		inviteRepository.save(invite);

		return toResponse(invite, Instant.now());
	}

	@Transactional(readOnly = true)
	public List<InviteResponse> listActive(UUID groupId, AuthPrincipal principal) {
		ensureGroupExists(groupId);
		ensureGroupMember(groupId, principal);

		Instant now = Instant.now();
		return inviteRepository.findByGroup_IdAndStatusOrderByCreatedAtDesc(groupId, InviteStatus.ACTIVE).stream()
				.filter(invite -> invite.effectiveStatus(now) == InviteStatus.ACTIVE)
				.map(invite -> toResponse(invite, now))
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

		Invite invite = resolveInvite(code, linkToken);
		ensureUsableForPreview(invite);

		return toPreview(invite, Instant.now());
	}

	@Transactional
	public AcceptInviteResponse accept(UUID inviteId, AuthPrincipal principal) {
		Invite invite = ensureInviteExists(inviteId);
		Instant now = Instant.now();
		ensureUsableForAcceptance(invite, now);

		UUID groupId = invite.getGroup().getId();
		if (groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(
				groupId, principal.participantId())) {
			throw new ApiException(
					HttpStatus.CONFLICT,
					"ALREADY_GROUP_MEMBER",
					"You are already a member of this group.");
		}

		Participant acceptor = participantRepository
				.findById(principal.participantId())
				.orElseThrow(this::unauthorized);

		groupParticipantRepository.save(new GroupParticipant(invite.getGroup(), acceptor.getId()));
		invite.markAccepted(acceptor, now);

		return new AcceptInviteResponse(groupId, true);
	}

	@Transactional
	public void revoke(UUID inviteId, AuthPrincipal principal) {
		Invite invite = ensureInviteExists(inviteId);
		ensureGroupMember(invite.getGroup().getId(), principal);
		ensureActiveForMutation(invite, Instant.now());
		invite.markRevoked();
	}

	private Invite resolveInvite(String code, UUID linkToken) {
		if (code != null && !code.isBlank()) {
			return inviteRepository
					.findByCode(Invite.normalizeCode(code))
					.orElseThrow(this::inviteNotFound);
		}

		return inviteRepository
				.findByLinkToken(linkToken)
				.orElseThrow(this::inviteNotFound);
	}

	private void ensureUsableForPreview(Invite invite) {
		Instant now = Instant.now();
		InviteStatus effectiveStatus = invite.effectiveStatus(now);
		if (effectiveStatus == InviteStatus.ACTIVE) {
			return;
		}

		persistExpiredIfNeeded(invite, now);
		throw inviteGone(effectiveStatus);
	}

	private void ensureUsableForAcceptance(Invite invite, Instant now) {
		InviteStatus effectiveStatus = invite.effectiveStatus(now);
		if (effectiveStatus == InviteStatus.ACTIVE) {
			return;
		}

		persistExpiredIfNeeded(invite, now);
		throw inviteGone(effectiveStatus);
	}

	private void ensureActiveForMutation(Invite invite, Instant now) {
		InviteStatus effectiveStatus = invite.effectiveStatus(now);
		if (effectiveStatus == InviteStatus.ACTIVE) {
			return;
		}

		persistExpiredIfNeeded(invite, now);
		throw inviteGone(effectiveStatus);
	}

	private void persistExpiredIfNeeded(Invite invite, Instant now) {
		if (invite.getStatus() == InviteStatus.ACTIVE && now.isAfter(invite.getExpiresAt())) {
			invite.markExpired();
		}
	}

	private InvitePreviewResponse toPreview(Invite invite, Instant now) {
		List<GroupMemberResponse> members = listGroupMembers(invite.getGroup().getId());
		return new InvitePreviewResponse(
				invite.getId(),
				invite.getGroup().getId(),
				members,
				invite.getExpiresAt(),
				invite.effectiveStatus(now));
	}

	private InviteResponse toResponse(Invite invite, Instant now) {
		return new InviteResponse(
				invite.getId(),
				invite.getGroup().getId(),
				invite.getCode(),
				invite.getLinkToken(),
				invite.getExpiresAt(),
				invite.effectiveStatus(now),
				invite.getCreatedAt());
	}

	private List<GroupMemberResponse> listGroupMembers(UUID groupId) {
		List<UUID> participantIds = groupParticipantRepository.findParticipantIdsByGroupId(groupId);
		return participantRepository.findAllById(participantIds).stream()
				.sorted(Comparator.comparing(Participant::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.map(participant -> new GroupMemberResponse(participant.getId(), participant.getDisplayName()))
				.toList();
	}

	private Group ensureGroupExists(UUID groupId) {
		return groupRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
	}

	private Invite ensureInviteExists(UUID inviteId) {
		return inviteRepository
				.findById(inviteId)
				.orElseThrow(this::inviteNotFound);
	}

	private void ensureGroupMember(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() == com.intensity.platform.common.AccessMode.EXPERIENCE_BOX) {
			if (!principal.canAccessExperienceBoxGroup(groupId)) {
				throw forbidden();
			}
			return;
		}

		if (!groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(
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
