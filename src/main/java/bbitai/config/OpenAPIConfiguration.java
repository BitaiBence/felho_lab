
package bbitai.config;

import bbitai.config.PsProperties.SwaggerUiProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;

@Configuration
public class OpenAPIConfiguration {

	@Autowired
	private SwaggerUiProperties swaggerUiProperties;

	@Bean
	public OpenAPI ffkOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("ss OpenAPI").version("v0").description(""))
				.addSecurityItem(new SecurityRequirement().addList("oidcAuth"))
				.components(new Components()
						.addSecuritySchemes("oidcAuth", new SecurityScheme()
								.name("oidcAuth")
								.type(SecurityScheme.Type.OAUTH2)
								.in(In.HEADER)
								.bearerFormat("JWT")
								.flows(new OAuthFlows()
										.implicit(new OAuthFlow()
												.authorizationUrl(swaggerUiProperties.getAuthorizationUrl())
												.tokenUrl(swaggerUiProperties.getAccessTokenUrl())
												.scopes(new Scopes()))
										.clientCredentials(new OAuthFlow()
												.authorizationUrl(swaggerUiProperties.getAuthorizationUrl())
												.tokenUrl(swaggerUiProperties.getAccessTokenUrl())
												.scopes(new Scopes())))));
	}
}