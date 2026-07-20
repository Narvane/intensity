package com.intensity.participant;

import com.intensity.AbstractMockMvcIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intensity.participant.repository.ParticipantRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountDeletionIntegrationTest extends AbstractMockMvcIntegrationTest {

	private static String aliceToken;
	private static String bobToken;
	private static String groupId;
	private static String boxId;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ParticipantRepository participantRepository;

	@Test
	@Order(1)
	void setupSharedGroupWithExperience() throws Exception {
		register("Alice", "alice@example.com");
		register("Bob", "bob@example.com");

		MvcResult joint = mockMvc.perform(post("/v1/auth/group")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "credentials": [
								    { "email": "alice@example.com", "password": "password123" },
								    { "email": "bob@example.com", "password": "password123" }
								  ]
								}
								"""))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode jointBody = objectMapper.readTree(joint.getResponse().getContentAsString());
		groupId = jointBody.get("groupId").asText();
		aliceToken = loginToken("alice@example.com");
		bobToken = loginToken("bob@example.com");

		MvcResult box = mockMvc.perform(post("/v1/boxes")
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "groupId": "%s",
								  "name": "Ideas",
								  "type": "SAIDAS_COM_AMIGOS"
								}
								""".formatted(groupId)))
				.andExpect(status().isCreated())
				.andReturn();

		boxId = objectMapper.readTree(box.getResponse().getContentAsString()).get("id").asText();

		mockMvc.perform(post("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Sunset picnic",
								  "reflection": "Slow evenings together.",
								  "intensity": 2,
								  "parameters": { "effort": 2, "unpredictability": 3, "novelty": 2 }
								}
								"""))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(2)
	void deleteAccountRemovesParticipantAndAuthoredExperiences() throws Exception {
		mockMvc.perform(post("/v1/auth/delete-account")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "alice@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isNoContent());

		assertFalse(participantRepository.existsByEmailIgnoreCase("alice@example.com"));

		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "alice@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));

		mockMvc.perform(get("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + bobToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));

		mockMvc.perform(get("/v1/groups")
						.header("Authorization", "Bearer " + bobToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].memberCount").value(1));
	}

	@Test
	@Order(3)
	void deleteAccountWithInvalidPasswordReturnsUnauthorized() throws Exception {
		register("Carol", "carol@example.com");

		mockMvc.perform(post("/v1/auth/delete-account")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "carol@example.com",
								  "password": "wrong-password"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));

		assert participantRepository.existsByEmailIgnoreCase("carol@example.com");
	}

	private void register(String displayName, String email) throws Exception {
		mockMvc.perform(post("/v1/participants")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "displayName": "%s",
						  "email": "%s",
						  "password": "password123"
						}
						""".formatted(displayName, email)));
	}

	private String loginToken(String email) throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password123"
								}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
	}
}
