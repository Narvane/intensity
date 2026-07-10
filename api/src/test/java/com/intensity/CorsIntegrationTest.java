package com.intensity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CorsIntegrationTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void preflightAllowsLocalhostOrigin() throws Exception {
		mockMvc.perform(options("/v1/auth/login")
						.header("Origin", "http://localhost:5173")
						.header("Access-Control-Request-Method", "POST")
						.header("Access-Control-Request-Headers", "content-type"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
	}

	@Test
	void preflightAllowsCapacitorOrigin() throws Exception {
		mockMvc.perform(options("/v1/auth/login")
						.header("Origin", "capacitor://localhost")
						.header("Access-Control-Request-Method", "POST")
						.header("Access-Control-Request-Headers", "content-type"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "capacitor://localhost"));
	}

	@Test
	void preflightAllowsPatchOnGroups() throws Exception {
		mockMvc.perform(options("/v1/groups/98721e0c-7e7b-4ec1-8a58-f02ba656f92f")
						.header("Origin", "http://localhost:5173")
						.header("Access-Control-Request-Method", "PATCH")
						.header("Access-Control-Request-Headers", "authorization,content-type"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
				.andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PATCH")));
	}
}
