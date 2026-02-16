package bbitai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

public class PsProperties {

	@Getter
	@Setter
	@Configuration("swaggerUiProperties")
	@ConfigurationProperties(prefix = "oidc.swagger-ui")
	public static class SwaggerUiProperties {
		private String authorizationUrl;
		private String accessTokenUrl;
	}

}
