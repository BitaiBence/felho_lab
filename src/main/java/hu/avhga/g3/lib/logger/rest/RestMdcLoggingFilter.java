package hu.avhga.g3.lib.logger.rest;

import hu.avhga.g3.lib.logger.MdcUtilService;
import hu.avhga.g3.lib.security.JwtTokenUserData;
import hu.avhga.g3.lib.security.OidcProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.List;
import java.util.Set;

public class RestMdcLoggingFilter extends CommonsRequestLoggingFilter {
	private final MdcUtilService mdcUtilService;
	private final Set<String> hideFields;

	public RestMdcLoggingFilter(MdcUtilService mdcUtilService, OidcProperties.HeaderProperties headerProperties, Set<String> hideFields) {
		this.mdcUtilService = mdcUtilService;
		this.hideFields = hideFields;
		if ( !this.logger.isTraceEnabled() ) {
			this.hideFields.addAll(List.of(headerProperties.getUserTokenHeaderName(), headerProperties.getClientTokenHeaderName().toLowerCase()));
		}
	}

	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		JwtTokenUserData jwtData = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth != null && auth.getDetails() instanceof JwtTokenUserData details ) {
			jwtData = details;
		}
		mdcUtilService.addUserDataToMdc(request.getMethod() + " " + request.getServletPath(), jwtData);
		message = hideFieldsFromMessage(message);
		super.logger.debug(message);
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		message = hideFieldsFromMessage(message);
		super.logger.debug(message);
		mdcUtilService.removeUserDataFromMdc();
	}

	String hideFieldsFromMessage(String message) {
		for ( String field : hideFields ) {
			if ( StringUtils.hasText(field) ) {
				if ( message.contains("\"" + field + "\":") ) {
					message = message.replaceAll(field + "\":\\s*\"([\\s\\S]*?)[]\"]", field + "\":\"...\"");
				} else if ( message.contains(field + ":") ) {
					message = message.replaceAll(field + ":\"([\\s\\S]*?)\"", field + ":\"...\"");
				}
			}

		}

		return message;
	}

}