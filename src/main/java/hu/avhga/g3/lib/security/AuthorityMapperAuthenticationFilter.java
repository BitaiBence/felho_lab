package hu.avhga.g3.lib.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.RestResponseException;
import hu.avhga.g3.lib.security.JwtUtilService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthorityMapperAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private JwtUtilService jwtUtilService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if ( SecurityContextHolder.getContext().getAuthentication() == null ) {
			Optional<Authentication> authentication = createAuthentication((HttpServletRequest) req);
			if(authentication.isPresent()){
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				context.setAuthentication(authentication.get());
				SecurityContextHolder.setContext(context);
			}
		} else if ( this.logger.isTraceEnabled() ) {
			this.logger.trace(LogMessage
					.of(() -> "Did not set SecurityContextHolder since already authenticated " + SecurityContextHolder.getContext().getAuthentication()));
		}

		chain.doFilter(req, res);
		SecurityContextHolder.clearContext();
	}

	private Optional<Authentication> createAuthentication(HttpServletRequest request) {
		try {
			return jwtUtilService.createAuthenticationToken(request);
		} catch (JsonProcessingException e) {
			throw new RestResponseException(BaseErrorCode.CL002, e);
		}

	}
}