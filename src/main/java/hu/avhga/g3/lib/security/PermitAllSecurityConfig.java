package hu.avhga.g3.lib.security;

import hu.avhga.g3.lib.security.AuthorityMapperAuthenticationFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import java.util.List;

@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
public class PermitAllSecurityConfig {

	@Bean
	public SecurityFilterChain permitAllFilterChain(AuthorityMapperAuthenticationFilter filter) {
		return new DefaultSecurityFilterChain(new NegatedRequestMatcher(new AntPathRequestMatcher("/actuator/**")), List.of(filter));
	}
}