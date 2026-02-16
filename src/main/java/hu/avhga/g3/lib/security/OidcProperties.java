package hu.avhga.g3.lib.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

public class OidcProperties {

	@Getter
	@Setter
	@Configuration("accessTokenProperties")
	@ConfigurationProperties(prefix = "oidc.access-token")
	public static class AccessTokenProperties {
		private List<String> rolePaths = Arrays.asList("realm_access.roles", "resource_access.account.roles");
		private List<String> alternativeUserNameRealms = Arrays.asList("https://keycloak.avhga.hu/realms/wsclients");
		private String userName = "preferred_username";
		private String alternativeUserName = "azp";
		private String userGroupName = "partnerId";
		private String impersonatorName = "impersonator.username";
		private String givenName = "given_name";
		private String familyName = "family_name";
		private String realmName = "iss";
	}

	@Getter
	@Setter
	@Configuration("headerProperties")
	@ConfigurationProperties(prefix = "oidc.header")
	public static class HeaderProperties {
		private boolean useClientTokenHeader = false;
		private String clientTokenHeaderName = HttpHeaders.AUTHORIZATION;
		private String userTokenHeaderName = "X-UserToken";
		private String clientRegistrationId;
	}
}