package hu.avhga.g3.lib.security;

import hu.avhga.g3.lib.exception.NoResultException;
import hu.avhga.g3.lib.exception.ServiceException;
import hu.avhga.g3.lib.exception.ValidationException;
import hu.avhga.g3.lib.logger.LoggingProperties;
import hu.avhga.g3.lib.security.AuthorizationHelper;
import hu.avhga.g3.lib.security.OidcProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@ConditionalOnClass(OAuth2AuthorizedClientManager.class)
public class RestHeaderService {

	@Autowired
	private LoggingProperties loggingProperties;

	@Autowired
	private OAuth2AuthorizedClientManager authorizedClientManager;

	@Autowired
	private OAuth2ClientProperties properties;

	@Autowired
	private OidcProperties.HeaderProperties headerProperties;

	@PostConstruct
	public void validateConfig() {
		if ( headerProperties.getClientRegistrationId() != null ) {
			if ( !properties.getRegistration().containsKey(headerProperties.getClientRegistrationId()) ) {
				throw new ValidationException("Nem található a " + headerProperties.getClientRegistrationId() + " klines regisztrációs beállítása");
			}
		} else if ( properties.getRegistration().size() > 1 ) {
			throw new ValidationException("Több OIDC kliens konfiguráció van megadva, ilyenkor kötelező a oidc.header.clientRegistrationId beállítás");
		}
	}

	public void addHeaders(Consumer<String> setAccessToken, BiConsumer<String, String> addHeader) {
		String userAccessToken = AuthorizationHelper.getToken();
		if ( StringUtils.hasText(userAccessToken) ) {
			addHeader.accept(headerProperties.getUserTokenHeaderName(), userAccessToken);
		}
//		String modulAccessToken = getClientAccessToken();
//		setAccessToken.accept(modulAccessToken);

		String traceheader = "00-" + MDC.get("traceId") + "-" + MDC.get("spanId") + "-00";
		addHeader.accept(loggingProperties.getHeader().getTraceId(), traceheader);
	}

	private String getClientAccessToken() {
		try {
			Optional<String> token = getClientCredentialAccessToken();
			return token.orElseThrow(() -> new NoResultException("Nem található a " + findClientRegistrationId() + " access token."));
		} catch (Exception e) {
			throw new ServiceException("Hiba " + findClientRegistrationId() + " access token kérésekor:" + e.getMessage(), e);
		}
	}

	protected Optional<String> getClientCredentialAccessToken() {
		String clientRegistrationId = findClientRegistrationId();
		OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
				.principal(properties.getRegistration().get(clientRegistrationId).getClientId())
				.build();
		OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
		return Optional.ofNullable(authorizedClient)
				.map(OAuth2AuthorizedClient::getAccessToken)
				.map(OAuth2AccessToken::getTokenValue);
	}

	private String findClientRegistrationId() {
		if ( headerProperties.getClientRegistrationId() != null ) {
			return headerProperties.getClientRegistrationId();
		} else {
			return properties.getRegistration().keySet().stream().findFirst().get();
		}
	}
}
