package hu.avhga.g3.lib.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtUtilService {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtilService.class);

	@Autowired
	private OidcProperties.AccessTokenProperties accessTokenProperties;

	@Autowired
	private OidcProperties.HeaderProperties headerProperties;

	public Optional<Authentication> createAuthenticationToken(HttpServletRequest request) throws JsonProcessingException {
		String token = getTokenFromHttpServletRequest(request);
		Map<String, Object> payload = getPayloadFromToken(token);
		if ( !payload.isEmpty() ) {
			JwtTokenUserData mdcUserData = getUserData(payload);
			List<SimpleGrantedAuthority> authorities = getAuthorities(payload);
			return Optional.of(new UserTokenAuthentication(mdcUserData, authorities, token));
		} else {
			return Optional.empty();
		}
	}

	private String getTokenFromHttpServletRequest(HttpServletRequest request) {
		if ( request.getHeader(headerProperties.getUserTokenHeaderName()) != null ) {
			return request.getHeader(headerProperties.getUserTokenHeaderName());
		} else if ( headerProperties.isUseClientTokenHeader() && request.getHeader(headerProperties.getClientTokenHeaderName()) != null ) {
			return request.getHeader(headerProperties.getClientTokenHeaderName());
		} else {
			logger.warn("Nem található access token a REST hívásban!");
		}
		return null;
	}

	private Map<String, Object> getPayloadFromToken(String token) throws JsonProcessingException {
		if ( token == null || token.trim().isBlank() ) {
			return Collections.emptyMap();
		}

		String[] parts = token.split("\\.");
		String payload = decode(parts[1]);
		return new ObjectMapper().readValue(payload, HashMap.class);
	}

	private static String decode(String encodedString) {
		return new String(Base64.getUrlDecoder().decode(encodedString), StandardCharsets.UTF_8);
	}

	private JwtTokenUserData getUserData(Map<String, Object> payload) {
		JwtTokenUserData dto = new JwtTokenUserData();
		if ( !payload.isEmpty() ) {
			String realm = (String) getValue(payload, accessTokenProperties.getRealmName());
			String userNameProperty = accessTokenProperties.getUserName();
			if ( accessTokenProperties.getAlternativeUserNameRealms().contains(realm) ) {
				userNameProperty = accessTokenProperties.getAlternativeUserName();
			}
			dto.setUserName((String) getValue(payload, userNameProperty));
			dto.setUserGroupName((String) getValue(payload, accessTokenProperties.getUserGroupName()));
			dto.setImpersonatorName((String) getValue(payload, accessTokenProperties.getImpersonatorName()));
			dto.setGivenName((String) getValue(payload, accessTokenProperties.getGivenName()));
			dto.setFamilyName((String) getValue(payload, accessTokenProperties.getFamilyName()));
			dto.setRealm(realm);
		}
		return dto;
	}

	private List<SimpleGrantedAuthority> getAuthorities(Map<String, Object> payload) {
		List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
		Set<String> uniqueRoleList = new HashSet<>();
		if ( !payload.isEmpty() ) {
			for ( String path : accessTokenProperties.getRolePaths() ) {
				List<String> roles = (List<String>) getValue(payload, path);
				addGrantedAuthorities(roles, uniqueRoleList, authorityList);
			}
		}
		return authorityList;
	}

	private Object getValue(Map<String, Object> payload, String pathKey) {
		if ( pathKey == null ) {
			return null;
		}
		String[] paths = pathKey.split("\\.");
		if ( paths.length == 1 ) {
			return payload.get(pathKey);
		} else if ( paths.length > 1 ) {
			Map<String, Object> leafMap = payload;
			for ( String path : paths ) {
				Object leaf = leafMap.get(path);
				if ( leaf == null ) {
					return null;
				} else if ( leaf instanceof Map<?, ?> map ) {
					leafMap = (Map<String, Object>) map;
				} else if ( leaf instanceof String str ) {
					return str;
				} else if ( leaf instanceof List<?> list ) {
					return list;
				} else {
					return leaf;
				}
			}
			return leafMap;
		} else {
			return null;
		}
	}

	private void addGrantedAuthorities(List<String> roles, Set<String> uniqueRoleList, List<SimpleGrantedAuthority> authorityList) {
		if ( roles != null ) {
			for ( String string : roles ) {
				if ( uniqueRoleList.add(string) ) {
					authorityList.add(new SimpleGrantedAuthority(string));
				}
			}
		}
	}

	public void setAccessTokenProperties(OidcProperties.AccessTokenProperties accessTokenProperties) {
		this.accessTokenProperties = accessTokenProperties;
	}

	public void setHeaderProperties(OidcProperties.HeaderProperties headerProperties) {
		this.headerProperties = headerProperties;
	}
}