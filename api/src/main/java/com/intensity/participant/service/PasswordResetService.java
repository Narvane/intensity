package com.intensity.participant.service;

import com.intensity.platform.common.exception.ApiException;
import com.intensity.platform.email.EmailProperties;
import com.intensity.platform.email.EmailSender;
import com.intensity.participant.dto.ForgotPasswordRequest;
import com.intensity.participant.dto.ResetPasswordRequest;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.entity.PasswordResetToken;
import com.intensity.participant.repository.ParticipantRepository;
import com.intensity.participant.repository.PasswordResetTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PasswordResetService {

	private final ParticipantRepository participantRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailSender emailSender;
	private final EmailProperties emailProperties;

	public PasswordResetService(
			ParticipantRepository participantRepository,
			PasswordResetTokenRepository passwordResetTokenRepository,
			PasswordEncoder passwordEncoder,
			EmailSender emailSender,
			EmailProperties emailProperties) {
		this.participantRepository = participantRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailSender = emailSender;
		this.emailProperties = emailProperties;
	}

	@Transactional
	public void requestReset(ForgotPasswordRequest request) {
		String email = request.email().trim().toLowerCase();
		participantRepository.findByEmailIgnoreCase(email).ifPresent(this::issueResetEmail);
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		Instant now = Instant.now();
		PasswordResetToken resetToken = passwordResetTokenRepository
				.findByToken(request.token())
				.orElseThrow(this::invalidResetToken);

		if (!resetToken.isUsable(now)) {
			throw invalidResetToken();
		}

		Participant participant = resetToken.getParticipant();
		participant.changePasswordHash(passwordEncoder.encode(request.password()));
		resetToken.markUsed(now);
	}

	private void issueResetEmail(Participant participant) {
		Instant now = Instant.now();
		passwordResetTokenRepository.deleteUnusedByParticipantId(participant.getId());

		UUID rawToken = UUID.randomUUID();
		Instant expiresAt = now.plusSeconds(emailProperties.passwordResetExpirationSeconds());
		passwordResetTokenRepository.save(new PasswordResetToken(participant, rawToken, expiresAt, now));

		String resetUrl = buildResetUrl(rawToken);
		emailSender.send(
				participant.getEmail(),
				"Reset your Intensity password",
				buildHtmlBody(participant.getDisplayName(), resetUrl));
	}

	private String buildResetUrl(UUID token) {
		String base = emailProperties.appBaseUrl().replaceAll("/+$", "");
		return base + "/auth/reset-password?t=" + token;
	}

	private static String buildHtmlBody(String displayName, String resetUrl) {
		return """
				<p>Hi %s,</p>
				<p>We received a request to reset your Intensity password.</p>
				<p><a href="%s">Set a new password</a></p>
				<p>This link expires in one hour. If you did not request a reset, you can ignore this email.</p>
				""".formatted(escapeHtml(displayName), resetUrl);
	}

	private static String escapeHtml(String value) {
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;");
	}

	private ApiException invalidResetToken() {
		return new ApiException(
				HttpStatus.BAD_REQUEST,
				"INVALID_RESET_TOKEN",
				"This password reset link is invalid or has expired.");
	}
}
