package hu.avhga.g3.lib.logger.rest;

import hu.avhga.g3.lib.logger.MdcUtilService;
import hu.avhga.g3.lib.logger.rest.RestMdcLoggingFilter;
import hu.avhga.g3.lib.security.OidcProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RestLoggingFilterConfiguration {

	@Value("#{'${logging.rest.hide-fields:}'.split('\\s*,\\s*')}")
	private Set<String> hideFields = new HashSet<>();

	@Value("${logging.rest.max-payload-length:10000}")
	private int maxPayloadLength;

	@Bean
	public CommonsRequestLoggingFilter logFilter(MdcUtilService mdcUtilService, OidcProperties.HeaderProperties headerProperties) {
		CommonsRequestLoggingFilter filter = new RestMdcLoggingFilter(mdcUtilService, headerProperties, hideFields);
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(maxPayloadLength);
		filter.setIncludeHeaders(true);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}
}
