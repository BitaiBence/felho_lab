package hu.avhga.g3.lib.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthorizationHelper {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationHelper.class);

	private AuthorizationHelper() {
	}

	public static boolean hasAuthority(String authority) {
		return hasAnyAuthority(List.of(authority));
	}

	public static boolean hasAuthority(String... authorities) {
		List<String> asList = Arrays.asList(authorities);
		return hasAnyAuthority(asList);
	}

	public static String getCurrentUserRealName() {
		Optional<JwtTokenUserData> data = getAuthenticationDetails();
		String realUserName = Stream.of(data.map(JwtTokenUserData::getFamilyName), data.map(JwtTokenUserData::getGivenName))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(StringUtils::hasText)
				.collect(Collectors.joining(" "));

		if ( !StringUtils.hasText(realUserName) ) {
			realUserName = data
					.map(JwtTokenUserData::getUserName)
					.orElse(null);
		}
		return realUserName;
	}

	public static String getToken() {
		return Optional.of(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.filter(UserTokenAuthentication.class::isInstance)
				.map(UserTokenAuthentication.class::cast)
				.map(UserTokenAuthentication::getToken)
				.orElse(null);
	}

	public static JwtTokenUserData getJwtTokenUserData() {
		return getAuthenticationDetails()
				.orElse(null);
	}

	public static String getCurrentUserName() {
		return getAuthenticationDetails()
				.map(JwtTokenUserData::getUserName)
				.orElse(null);
	}

	public static String getImpersonatorName() {
		return getAuthenticationDetails()
				.map(JwtTokenUserData::getImpersonatorName)
				.orElse(null);
	}

	public static Optional<JwtTokenUserData> getAuthenticationDetails() {
		return Optional.of(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getDetails)
				.map(JwtTokenUserData.class::cast)
				.map(JwtTokenUserData::copy);
	}

	public static List<String> getRoles() {
		return Optional.of(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getAuthorities)
				.stream()
				.flatMap(Collection::stream)
				.map(GrantedAuthority::getAuthority)
				.toList();
	}

	public static void setContextAuthentication(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private static boolean hasAnyAuthority(List<String> roles) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		for ( String role : roles ) {
			boolean passed = hasAnyAuthority(authorities, role);
			if ( passed ) {
				logger.trace("{} felhasznalonak van {} jogosultsaga", username, role);
				return true;
			}
		}
		logger.trace("{} felhasznalonak nincs egyetlen jogosultsaga ezek kozul: {}", username, roles);
		return false;
	}

	private static boolean hasAnyAuthority(Collection<? extends GrantedAuthority> authorities, String role) {
		for ( GrantedAuthority authority : authorities ) {
			if ( authority.getAuthority().equalsIgnoreCase(role) ) {
				return true;
			}
		}
		return false;
	}

}
