package hu.avhga.g3.lib.security;

import hu.avhga.g3.lib.security.JwtTokenUserData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class UserTokenAuthentication implements Authentication {

	@Serial
	private static final long serialVersionUID = 1L;

	private final JwtTokenUserData details;
	private final List<SimpleGrantedAuthority> authorities;
	private final String token;

	public UserTokenAuthentication(JwtTokenUserData details, List<SimpleGrantedAuthority> authorities, String token) {
		this.details = details;
		this.authorities = authorities;
		this.token = token;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return details;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// üres
	}

	@Override
	public String getName() {
		return details.getUserName();
	}

	public String getToken() {
		return token;
	}
}
