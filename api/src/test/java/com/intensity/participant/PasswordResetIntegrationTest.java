package com.intensity.participant;

import com.intensity.AbstractMockMvcIntegrationTest;
import com.intensity.participant.entity.PasswordResetToken;
import com.intensity.participant.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordResetIntegrationTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void forgotPasswordAlwaysReturnsNoContent() throws Exception {
		long beforeUnknown = passwordResetTokenRepository.count();

		mockMvc.perform(post("/v1/auth/forgot-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "email": "nobody@example.com" }
								"""))
				.andExpect(status().isNoContent());

		assertThat(passwordResetTokenRepository.count()).isEqualTo(beforeUnknown);

		register("Maya", "maya-reset@example.com");

		mockMvc.perform(post("/v1/auth/forgot-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "email": "maya-reset@example.com" }
								"""))
				.andExpect(status().isNoContent());

		assertThat(requireUnusedToken("maya-reset@example.com")).isNotNull();
	}

	@Test
	void resetPasswordAllowsLoginWithNewPassword() throws Exception {
		register("Leo", "leo-reset@example.com");

		mockMvc.perform(post("/v1/auth/forgot-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "email": "leo-reset@example.com" }
								"""))
				.andExpect(status().isNoContent());

		UUID token = requireUnusedToken("leo-reset@example.com").getToken();

		mockMvc.perform(post("/v1/auth/reset-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "token": "%s",
								  "password": "new-password-99"
								}
								""".formatted(token)))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "leo-reset@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));

		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "leo-reset@example.com",
								  "password": "new-password-99"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessMode").value("EXPERIENCES"));
	}

	@Test
	void resetPasswordRejectsUsedOrUnknownToken() throws Exception {
		register("Nico", "nico-reset@example.com");

		mockMvc.perform(post("/v1/auth/forgot-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "email": "nico-reset@example.com" }
								"""))
				.andExpect(status().isNoContent());

		UUID token = requireUnusedToken("nico-reset@example.com").getToken();

		mockMvc.perform(post("/v1/auth/reset-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "token": "%s",
								  "password": "fresh-password-1"
								}
								""".formatted(token)))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/v1/auth/reset-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "token": "%s",
								  "password": "another-password"
								}
								""".formatted(token)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_RESET_TOKEN"));

		mockMvc.perform(post("/v1/auth/reset-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "token": "%s",
								  "password": "another-password"
								}
								""".formatted(UUID.randomUUID())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_RESET_TOKEN"));
	}

	@Test
	void resetPasswordRejectsExpiredToken() throws Exception {
		register("Expired", "expired-reset@example.com");

		mockMvc.perform(post("/v1/auth/forgot-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "email": "expired-reset@example.com" }
								"""))
				.andExpect(status().isNoContent());

		UUID token = requireUnusedToken("expired-reset@example.com").getToken();

		jdbcTemplate.update(
				"UPDATE password_reset_token SET expires_at = ? WHERE token = ?",
				Timestamp.from(Instant.now().minusSeconds(60)),
				token);

		mockMvc.perform(post("/v1/auth/reset-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "token": "%s",
								  "password": "too-late-password"
								}
								""".formatted(token)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_RESET_TOKEN"));
	}

	private PasswordResetToken requireUnusedToken(String email) {
		return passwordResetTokenRepository
				.findFirstByParticipant_EmailIgnoreCaseAndUsedAtIsNullOrderByCreatedAtDesc(email)
				.orElseThrow();
	}

	private void register(String displayName, String email) throws Exception {
		jdbcTemplate.update(
				"MERGE INTO allowlist_email (email) KEY (email) VALUES (?)",
				email.toLowerCase());

		mockMvc.perform(post("/v1/participants")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "displayName": "%s",
								  "email": "%s",
								  "password": "password123"
								}
								""".formatted(displayName, email)))
				.andExpect(status().isCreated());
	}
}

