package com.intensity.experience.service;

import com.intensity.box.entity.Box;
import com.intensity.box.repository.BoxRepository;
import com.intensity.common.AuthPrincipal;
import com.intensity.common.exception.ApiException;
import com.intensity.experience.dto.CreateExperienceRequest;
import com.intensity.experience.dto.CreateExperiencesBatchRequest;
import com.intensity.experience.dto.ExperienceParametersDto;
import com.intensity.experience.dto.ExperienceResponse;
import com.intensity.experience.entity.Experience;
import com.intensity.experience.entity.ExperienceType;
import com.intensity.experience.repository.ExperienceRepository;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.ParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExperienceService {

	private final ExperienceRepository experienceRepository;
	private final BoxRepository boxRepository;
	private final ParticipantRepository participantRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final SealService sealService;
	private final ExperienceVisibilityPolicy visibilityPolicy;

	public ExperienceService(
			ExperienceRepository experienceRepository,
			BoxRepository boxRepository,
			ParticipantRepository participantRepository,
			GroupParticipantRepository groupParticipantRepository,
			SealService sealService,
			ExperienceVisibilityPolicy visibilityPolicy) {
		this.experienceRepository = experienceRepository;
		this.boxRepository = boxRepository;
		this.participantRepository = participantRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.sealService = sealService;
		this.visibilityPolicy = visibilityPolicy;
	}

	@Transactional(readOnly = true)
	public List<ExperienceResponse> listByBox(UUID boxId, AuthPrincipal principal) {
		Box box = ensureBoxExists(boxId);
		ensureCanAccessBox(box, principal);

		return experienceRepository.findAllByBox_IdOrderByCreatedAtDesc(boxId).stream()
				.map(experience -> toResponse(experience, principal.participantId(), principal))
				.toList();
	}

	@Transactional
	public ExperienceResponse create(UUID boxId, CreateExperienceRequest request, AuthPrincipal principal) {
		Box box = ensureBoxExists(boxId);
		ensureCanAccessBox(box, principal);
		Participant author = requireAuthor(principal);

		Experience experience = persistNew(box, author, request);
		return toResponse(experience, principal.participantId(), principal);
	}

	@Transactional
	public List<ExperienceResponse> createBatch(
			UUID boxId, CreateExperiencesBatchRequest request, AuthPrincipal principal) {
		Box box = ensureBoxExists(boxId);
		ensureCanAccessBox(box, principal);
		Participant author = requireAuthor(principal);

		return request.experiences().stream()
				.map(item -> persistNew(box, author, item))
				.map(experience -> toResponse(experience, principal.participantId(), principal))
				.toList();
	}

	private Experience persistNew(Box box, Participant author, CreateExperienceRequest request) {
		String seal = sealService.computeFromDescription(request.description());
		Experience experience = new Experience(
				box,
				author,
				request.description(),
				request.reflection(),
				request.intensity(),
				request.parameters().effort(),
				request.parameters().unpredictability(),
				request.parameters().novelty(),
				resolveType(request.type()),
				seal);

		return experienceRepository.save(experience);
	}

	private Participant requireAuthor(AuthPrincipal principal) {
		return participantRepository
				.findById(principal.participantId())
				.orElseThrow(() -> new ApiException(
						HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token."));
	}

	private ExperienceType resolveType(ExperienceType type) {
		return type == null ? ExperienceType.NONE : type;
	}

	@Transactional
	public ExperienceResponse update(
			UUID experienceId, CreateExperienceRequest request, AuthPrincipal principal) {
		Experience experience = ensureExperienceExists(experienceId);
		ensureCanAccessBox(experience.getBox(), principal);
		ensureAuthor(experience, principal.participantId());

		String seal = sealService.computeFromDescription(request.description());
		experience.updateContent(
				request.description(),
				request.reflection(),
				request.intensity(),
				request.parameters().effort(),
				request.parameters().unpredictability(),
				request.parameters().novelty(),
				resolveType(request.type()),
				seal);

		return toResponse(experience, principal.participantId(), principal);
	}

	@Transactional
	public void delete(UUID experienceId, AuthPrincipal principal) {
		Experience experience = ensureExperienceExists(experienceId);
		ensureCanAccessBox(experience.getBox(), principal);
		ensureAuthor(experience, principal.participantId());
		experienceRepository.delete(experience);
	}

	private Box ensureBoxExists(UUID boxId) {
		return boxRepository
				.findById(boxId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "BOX_NOT_FOUND", "Box not found."));
	}

	private Experience ensureExperienceExists(UUID experienceId) {
		return experienceRepository
				.findById(experienceId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "EXPERIENCE_NOT_FOUND", "Experience not found."));
	}

	private void ensureCanAccessBox(Box box, AuthPrincipal principal) {
		UUID groupId = box.getGroup().getId();

		if (principal.accessMode() == com.intensity.common.AccessMode.EXPERIENCE_BOX) {
			if (!groupId.equals(principal.groupId())) {
				throw forbidden();
			}
			return;
		}

		if (!groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(
				groupId, principal.participantId())) {
			throw forbidden();
		}
	}

	private void ensureAuthor(Experience experience, UUID participantId) {
		if (!experience.getAuthor().getId().equals(participantId)) {
			throw new ApiException(
					HttpStatus.FORBIDDEN,
					"NOT_AUTHOR",
					"Only the author can change this experience.");
		}
	}

	private ExperienceResponse toResponse(
			Experience experience, UUID viewerId, AuthPrincipal principal) {
		boolean fullAccess = visibilityPolicy.hasFullContent(
				principal, experience.getAuthor().getId(), viewerId);
		ExperienceParametersDto parameters = new ExperienceParametersDto(
				experience.getEffort(), experience.getUnpredictability(), experience.getNovelty());

		return new ExperienceResponse(
				experience.getId(),
				experience.getBox().getId(),
				experience.getAuthor().getId(),
				experience.getAuthor().getDisplayName(),
				fullAccess ? experience.getDescription() : null,
				fullAccess ? experience.getReflection() : null,
				experience.getIntensity(),
				parameters,
				experience.getType(),
				experience.getSeal(),
				!fullAccess,
				experience.getCreatedAt(),
				experience.getUpdatedAt());
	}

	private ApiException forbidden() {
		return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
	}
}
