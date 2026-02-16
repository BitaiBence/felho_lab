package hu.avhga.g3.lib.logger.rest;

import hu.avhga.g3.lib.security.AuthorizationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

	private final String accessTokenHeaderName;

	public HeaderRequestInterceptor(String accessTokenHeaderName) {
		this.accessTokenHeaderName = accessTokenHeaderName;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		String token = AuthorizationHelper.getToken();
		if ( token != null && !request.getHeaders().containsKey(accessTokenHeaderName) ) {
			request.getHeaders().add(accessTokenHeaderName, token);
		}

		return execution.execute(request, body);
	}
}