package com.intensity;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	void registerAllowlistedEmailReturnsCreatedWithToken() throws Exception {
		mockMvc.perform(post("/v1/participants")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "displayName": "Carol",
								  "email": "carol@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.email").value("carol@example.com"));
	}

	@Test
	@Order(2)
	void registerNonAllowlistedEmailReturnsForbidden() throws Exception {
		mockMvc.perform(post("/v1/participants")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "displayName": "Denied",
								  "email": "denied@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("EMAIL_NOT_ALLOWLISTED"));
	}

	@Test
	@Order(3)
	void loginExperiencesReturnsSession() throws Exception {
		register("Alice", "alice@example.com");

		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "alice@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.accessMode").value("EXPERIENCES"))
				.andExpect(jsonPath("$.displayName").value("Alice"));
	}

	@Test
	@Order(4)
	void loginWithInvalidPasswordReturnsUnauthorized() throws Exception {
		register("Bob", "bob@example.com");

		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "bob@example.com",
								  "password": "wrong-password"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
	}

	@Test
	@Order(5)
	void jointLoginReturnsConflictWhenParticipantsBelongToDifferentGroups() throws Exception {
		mockMvc.perform(post("/v1/auth/group")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "credentials": [
								    { "email": "carol@example.com", "password": "password123" }
								  ]
								}
								"""))
				.andExpect(status().isOk());

		mockMvc.perform(post("/v1/auth/group")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "credentials": [
								    { "email": "bob@example.com", "password": "password123" }
								  ]
								}
								"""))
				.andExpect(status().isOk());

		mockMvc.perform(post("/v1/auth/group")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "credentials": [
								    { "email": "carol@example.com", "password": "password123" },
								    { "email": "bob@example.com", "password": "password123" }
								  ]
								}
								"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("GROUP_MEMBERSHIP_CONFLICT"))
				.andExpect(jsonPath("$.message").value("Credentials belong to different groups."));
	}

	@Test
	@Order(6)
	void jointLoginCreatesOrReopensGroup() throws Exception {
		register("Alice", "alice@example.com");
		register("Bob", "bob@example.com");

		String firstResponse = mockMvc.perform(post("/v1/auth/group")
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
				.andExpect(jsonPath("$.groupId", notNullValue()))
				.andExpect(jsonPath("$.members", hasSize(2)))
				.andExpect(jsonPath("$.accessMode").value("EXPERIENCE_BOX"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		String groupId = com.jayway.jsonpath.JsonPath.read(firstResponse, "$.groupId");

		mockMvc.perform(post("/v1/auth/group")
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
				.andExpect(jsonPath("$.groupId").value(groupId));
	}

	@Test
	@Order(7)
	void targetedJointLoginStaysOnSelectedGroupEvenWithPartialMembers() throws Exception {
		register("Alice", "alice@example.com");
		register("Bob", "bob@example.com");
		register("Carol", "carol@example.com");

		String pairResponse = mockMvc.perform(post("/v1/auth/group")
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
				.andReturn()
				.getResponse()
				.getContentAsString();
		String group1Id = com.jayway.jsonpath.JsonPath.read(pairResponse, "$.groupId");

		String aliceToken = loginToken("alice@example.com");
		String group2Response = mockMvc.perform(post("/v1/groups")
						.header("Authorization", "Bearer " + aliceToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "name": "Trio", "color": "teal" }
								"""))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String group2Id = com.jayway.jsonpath.JsonPath.read(group2Response, "$.id");

		inviteAndAccept(group2Id, aliceToken, "bob@example.com");
		inviteAndAccept(group2Id, aliceToken, "carol@example.com");

		mockMvc.perform(post("/v1/auth/group")
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
				.andExpect(jsonPath("$.groupId").value(group1Id));

		mockMvc.perform(post("/v1/auth/group")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "targetGroupId": "%s",
								  "requireAllMembers": false,
								  "credentials": [
								    { "email": "alice@example.com", "password": "password123" },
								    { "email": "bob@example.com", "password": "password123" }
								  ]
								}
								""".formatted(group2Id)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.groupId").value(group2Id))
				.andExpect(jsonPath("$.groupIds", hasSize(1)))
				.andExpect(jsonPath("$.members", hasSize(2)));

		mockMvc.perform(post("/v1/auth/group")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "targetGroupId": "%s",
								  "requireAllMembers": true,
								  "credentials": [
								    { "email": "alice@example.com", "password": "password123" },
								    { "email": "bob@example.com", "password": "password123" }
								  ]
								}
								""".formatted(group2Id)))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.code").value("GROUP_REQUIRES_ALL_MEMBERS"));
	}

	private void inviteAndAccept(String groupId, String hostToken, String inviteeEmail) throws Exception {
		String inviteResponse = mockMvc.perform(post("/v1/groups/{groupId}/invites", groupId)
						.header("Authorization", "Bearer " + hostToken))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String inviteId = com.jayway.jsonpath.JsonPath.read(inviteResponse, "$.id");
		String inviteeToken = loginToken(inviteeEmail);

		mockMvc.perform(post("/v1/invites/{inviteId}/accept", inviteId)
						.header("Authorization", "Bearer " + inviteeToken))
				.andExpect(status().isOk());
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
		String response = mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password123"
								}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		return com.jayway.jsonpath.JsonPath.read(response, "$.token");
	}
}
