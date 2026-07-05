package com.intensity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExperienceIntegrationTest extends AbstractMockMvcIntegrationTest {

	private static String aliceToken;
	private static String bobToken;
	private static String groupId;
	private static String boxId;
	private static String experienceId;
	private static String aliceSeal;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@Order(1)
	void setupGroupAndBox() throws Exception {
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
	}

	@Test
	@Order(2)
	void createExperienceReturnsSeal() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Sunset picnic in the park",
								  "reflection": "We all need slow evenings together.",
								  "intensity": 2,
								  "parameters": { "effort": 2, "unpredictability": 3, "novelty": 2 },
								  "type": "explore"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.seal", notNullValue()))
				.andExpect(jsonPath("$.summaryOnly").value(false))
				.andExpect(jsonPath("$.type").value("explore"))
				.andExpect(jsonPath("$.description").value("Sunset picnic in the park"))
				.andReturn();

		JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
		experienceId = body.get("id").asText();
		aliceSeal = body.get("seal").asText();
	}

	@Test
	@Order(3)
	void otherMemberSeesSummaryOnly() throws Exception {
		mockMvc.perform(get("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + bobToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].summaryOnly").value(true))
				.andExpect(jsonPath("$[0].description").doesNotExist())
				.andExpect(jsonPath("$[0].reflection").doesNotExist())
				.andExpect(jsonPath("$[0].seal").value(aliceSeal));
	}

	@Test
	@Order(4)
	void authorCanUpdateExperienceAndSealRecalculatesOnDescriptionChange() throws Exception {
		MvcResult descriptionUpdate = mockMvc.perform(put("/v1/experiences/{experienceId}", experienceId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Sunset picnic by the lake",
								  "reflection": "We all need slow evenings together.",
								  "intensity": 3,
								  "parameters": { "effort": 2, "unpredictability": 3, "novelty": 3 }
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.intensity").value(3))
				.andExpect(jsonPath("$.seal", notNullValue()))
				.andReturn();

		String updatedSeal = objectMapper
				.readTree(descriptionUpdate.getResponse().getContentAsString())
				.get("seal")
				.asText();
		org.junit.jupiter.api.Assertions.assertNotEquals(aliceSeal, updatedSeal);

		mockMvc.perform(put("/v1/experiences/{experienceId}", experienceId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Sunset picnic by the lake",
								  "reflection": "Updated reflection only.",
								  "intensity": 3,
								  "parameters": { "effort": 2, "unpredictability": 3, "novelty": 3 }
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.seal").value(updatedSeal));
	}

	@Test
	@Order(5)
	void nonAuthorCannotUpdateExperience() throws Exception {
		mockMvc.perform(put("/v1/experiences/{experienceId}", experienceId)
						.header("Authorization", "Bearer " + bobToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Bob tries to rewrite Alice",
								  "reflection": "Not allowed",
								  "intensity": 2,
								  "parameters": { "effort": 2, "unpredictability": 2, "novelty": 2 }
								}
								"""))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("NOT_AUTHOR"));
	}

	@Test
	@Order(6)
	void nonAuthorCannotDeleteExperience() throws Exception {
		mockMvc.perform(delete("/v1/experiences/{experienceId}", experienceId)
						.header("Authorization", "Bearer " + bobToken))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("NOT_AUTHOR"));
	}

	@Test
	@Order(7)
	void authorCanDeleteExperience() throws Exception {
		mockMvc.perform(delete("/v1/experiences/{experienceId}", experienceId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	@Order(8)
	void invalidIntensityReturns422() throws Exception {
		mockMvc.perform(post("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Too intense",
								  "reflection": "Nope",
								  "intensity": 6,
								  "parameters": { "effort": 2, "unpredictability": 2, "novelty": 2 }
								}
								"""))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@Order(9)
	void experienceBoxSessionSeesFullPoolForDraw() throws Exception {
		mockMvc.perform(post("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "description": "Draw pool entry",
								  "reflection": "For shared moment",
								  "intensity": 3,
								  "parameters": { "effort": 3, "unpredictability": 3, "novelty": 3 }
								}
								"""))
				.andExpect(status().isCreated());

		String experienceBoxToken = jointLoginToken();

		mockMvc.perform(get("/v1/boxes/{boxId}/experiences", boxId)
						.header("Authorization", "Bearer " + experienceBoxToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].description").value("Draw pool entry"))
				.andExpect(jsonPath("$[0].summaryOnly").value(false));
	}

	@Test
	@Order(10)
	void createBatchCreatesMultipleExperiences() throws Exception {
		mockMvc.perform(post("/v1/boxes/{boxId}/experiences/batch", boxId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "experiences": [
								    {
								      "description": "Blindfolded tasting round",
								      "intensity": 2,
								      "parameters": { "effort": 2, "unpredictability": 4, "novelty": 3 },
								      "type": "randomness"
								    },
								    {
								      "description": "Blindfolded tasting round with strangers",
								      "reflection": "A little braver together.",
								      "intensity": 3,
								      "parameters": { "effort": 2, "unpredictability": 5, "novelty": 4 }
								    }
								  ]
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].type").value("randomness"))
				.andExpect(jsonPath("$[0].reflection").doesNotExist())
				.andExpect(jsonPath("$[1].type").value("none"))
				.andExpect(jsonPath("$[1].reflection").value("A little braver together."));
	}

	@Test
	@Order(11)
	void createRejectsBatchOverLimit() throws Exception {
		mockMvc.perform(post("/v1/boxes/{boxId}/experiences/batch", boxId)
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "experiences": [
								    { "description": "1", "intensity": 1, "parameters": { "effort": 1, "unpredictability": 1, "novelty": 1 } },
								    { "description": "2", "intensity": 1, "parameters": { "effort": 1, "unpredictability": 1, "novelty": 1 } },
								    { "description": "3", "intensity": 1, "parameters": { "effort": 1, "unpredictability": 1, "novelty": 1 } },
								    { "description": "4", "intensity": 1, "parameters": { "effort": 1, "unpredictability": 1, "novelty": 1 } },
								    { "description": "5", "intensity": 1, "parameters": { "effort": 1, "unpredictability": 1, "novelty": 1 } },
								    { "description": "6", "intensity": 1, "parameters": { "effort": 1, "unpredictability": 1, "novelty": 1 } }
								  ]
								}
								"""))
				.andExpect(status().isUnprocessableEntity());
	}

	private String jointLoginToken() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/auth/group")
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

		return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
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
