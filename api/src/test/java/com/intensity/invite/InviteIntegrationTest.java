package com.intensity.invite;

import com.intensity.AbstractMockMvcIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intensity.invite.entity.Invite;
import com.intensity.invite.repository.InviteRepository;
import com.intensity.group.entity.Group;
import com.intensity.group.repository.GroupRepository;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.ParticipantRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InviteIntegrationTest extends AbstractMockMvcIntegrationTest {

	private static String groupId;
	private static String aliceToken;
	private static String carolToken;
	private static String inviteId;
	private static String inviteCode;
	private static String inviteLinkToken;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InviteRepository inviteRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ParticipantRepository participantRepository;

	@Test
	@Order(1)
	void setupGroupAndTokens() throws Exception {
		register("Alice", "alice@example.com");
		register("Bob", "bob@example.com");
		register("Carol", "carol@example.com");

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

		groupId = objectMapper.readTree(joint.getResponse().getContentAsString()).get("groupId").asText();
		aliceToken = loginToken("alice@example.com");
		carolToken = loginToken("carol@example.com");
	}

	@Test
	@Order(2)
	void memberCanCreateAndListInvite() throws Exception {
		MvcResult created = mockMvc.perform(post("/v1/groups/{groupId}/invites", groupId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.code", notNullValue()))
				.andExpect(jsonPath("$.linkToken", notNullValue()))
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andReturn();

		JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
		inviteId = body.get("id").asText();
		inviteCode = body.get("code").asText();
		inviteLinkToken = body.get("linkToken").asText();

		mockMvc.perform(get("/v1/groups/{groupId}/invites", groupId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(inviteId));
	}

	@Test
	@Order(3)
	void validateByCodeOrLinkTokenReturnsPreview() throws Exception {
		mockMvc.perform(get("/v1/invites/validate").param("code", inviteCode))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.inviteId").value(inviteId))
				.andExpect(jsonPath("$.members", hasSize(2)))
				.andExpect(jsonPath("$.members[0].displayName", notNullValue()))
				.andExpect(jsonPath("$.members[0].email").doesNotExist());

		mockMvc.perform(get("/v1/invites/validate").param("t", inviteLinkToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.inviteId").value(inviteId));
	}

	@Test
	@Order(4)
	void inviteeCanAcceptInvite() throws Exception {
		mockMvc.perform(post("/v1/invites/{inviteId}/accept", inviteId)
						.header("Authorization", "Bearer " + carolToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.groupId").value(groupId))
				.andExpect(jsonPath("$.membershipConfirmed").value(true));

		MvcResult groupsResult = mockMvc.perform(get("/v1/groups").header("Authorization", "Bearer " + carolToken))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode groups = objectMapper.readTree(groupsResult.getResponse().getContentAsString());
		boolean joinedTargetGroup = false;
		for (JsonNode group : groups) {
			if (groupId.equals(group.get("id").asText()) && group.get("memberCount").asInt() == 3) {
				joinedTargetGroup = true;
				break;
			}
		}
		assertTrue(joinedTargetGroup);
	}

	@Test
	@Order(5)
	void acceptedInviteReturnsGone() throws Exception {
		mockMvc.perform(post("/v1/invites/{inviteId}/accept", inviteId)
						.header("Authorization", "Bearer " + carolToken))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.code").value("INVITE_GONE"));
	}

	@Test
	@Order(6)
	void alreadyMemberReturnsConflictOnAccept() throws Exception {
		MvcResult created = mockMvc.perform(post("/v1/groups/{groupId}/invites", groupId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isCreated())
				.andReturn();

		String secondInviteId =
				objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

		mockMvc.perform(post("/v1/invites/{inviteId}/accept", secondInviteId)
						.header("Authorization", "Bearer " + carolToken))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("ALREADY_GROUP_MEMBER"));
	}

	@Test
	@Order(7)
	void memberCanRevokeInvite() throws Exception {
		MvcResult created = mockMvc.perform(post("/v1/groups/{groupId}/invites", groupId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isCreated())
				.andReturn();

		String revokedInviteId =
				objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();
		String revokedCode = objectMapper
				.readTree(created.getResponse().getContentAsString())
				.get("code")
				.asText();

		mockMvc.perform(delete("/v1/invites/{inviteId}", revokedInviteId)
						.header("Authorization", "Bearer " + aliceToken))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/v1/invites/validate").param("code", revokedCode))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.code").value("INVITE_GONE"));
	}

	@Test
	@Order(8)
	void expiredInviteReturnsGone() throws Exception {
		Group group = groupRepository.findById(UUID.fromString(groupId)).orElseThrow();
		Participant creator = participantRepository
				.findByEmailIgnoreCase("alice@example.com")
				.orElseThrow();
		Instant createdAt = Instant.now().minusSeconds(8 * 24 * 3600L);
		Instant expiresAt = Instant.now().minusSeconds(3600);

		Invite expired = new Invite(
				group,
				creator,
				"ZX89WQ",
				UUID.randomUUID(),
				expiresAt,
				createdAt);
		inviteRepository.save(expired);

		mockMvc.perform(get("/v1/invites/validate").param("code", "ZX89WQ"))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.code").value("INVITE_GONE"));
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
