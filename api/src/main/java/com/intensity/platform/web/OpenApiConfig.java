package com.intensity.platform.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "demo"})
public class OpenApiConfig {

	@Bean
	public OpenAPI intensityOpenApi() {
		final String bearerScheme = "bearerAuth";
		return new OpenAPI()
				.info(new Info()
						.title("Intensity API")
						.description("REST API v1 — contract reference in /openapi/openapi.yaml")
						.version("1.0.0"))
				.addSecurityItem(new SecurityRequirement().addList(bearerScheme))
				.schemaRequirement(bearerScheme, new SecurityScheme()
						.name(bearerScheme)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"));
	}

}
