package com.intensity.platform.security;

import com.intensity.AbstractMockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Public auth routes must never answer INVALID_TOKEN, no matter how the
 * request is shaped. Historic regression: MVC matchers made a public POST
 * without Content-Type fall into authenticated() and return a misleading
 * INVALID_TOKEN from the entry point.
 */
class PublicRouteContractIntegrationTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void forgotPasswordWithoutContentTypeIsNeverInvalidToken() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/auth/forgot-password")
						.content("""
								{ "email": "someone@example.com" }
								"""))
				.andReturn();

		int status = result.getResponse().getStatus();
		String body = result.getResponse().getContentAsString();

		assertThat(status).isIn(204, 422);
		assertThat(body).doesNotContain("INVALID_TOKEN");
	}

	@Test
	void forgotPasswordWithGarbageBearerStillSucceeds() throws Exception {
		mockMvc.perform(post("/v1/auth/forgot-password")
						.header("Authorization", "Bearer not-a-real-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "email": "someone@example.com" }
								"""))
				.andExpect(status().isNoContent());
	}

	@Test
	void loginWithMalformedBodyReturnsValidationErrorNotInvalidToken() throws Exception {
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("not json at all"))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void loginWithWrongCredentialsReturnsInvalidCredentialsNotInvalidToken() throws Exception {
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "ghost@example.com",
								  "password": "definitely-wrong"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
	}

	@Test
	void protectedRouteWithoutTokenReturnsInvalidToken() throws Exception {
		mockMvc.perform(get("/v1/groups"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
	}

	@Test
	void protectedRouteWithExpiredOrGarbageTokenReturnsInvalidToken() throws Exception {
		mockMvc.perform(get("/v1/groups")
						.header("Authorization", "Bearer garbage.token.value"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
	}
}
