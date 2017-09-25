package de.adorsys.sts.starter.config;

import de.adorsys.sts.tokenauth.JWTAuthenticationFilter;
import de.adorsys.sts.tokenauth.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private TokenService tokenAuthenticationService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers(HttpMethod.GET,
					"/",
					"/pop",
					"/api-docs/**",
					"/v2/api-docs/**",
					"/swagger-resources/**",
					"/health",
					"/health.json",
					"/info",
					"/info.json"
			).permitAll()
			.antMatchers("/token/**").permitAll()// TOken Endpoint
			.antMatchers(actuatorEndpoints()).hasRole("ADMIN")
			.antMatchers("/accounts").hasRole("USER")
			.antMatchers("/bankAccess").hasRole("ADMIN")
			.antMatchers(actuatorEndpoints()).denyAll()
			.anyRequest().authenticated()
//			.and()
//			.anonymous().disable()
			;
		// And filter other requests to check the presence of JWT in header
		 http
		 	.addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
		 	.addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)		
		 ;
	}
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Create a default account
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
	}
	
	private String[] actuatorEndpoints() {
        return new String[]{"/auditevents", "/auditevents.json", "/dump", "/dump.json", "/metrics/**", "/metrics", "/metrics.json",
        		"/beans", "/beans.json", "/loggers/**", "/loggers", "/loggers.json", "/trace", "/trace.json","/configprops", "/configprops.json",
        		"/heapdump", "/heapdump.json", "/autoconfig", "/autoconfig.json", "/mappings", "/mappings.json", "/env/**", "/env", "/env.json"};
    }	

	@Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST,proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Principal getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();

    }

    @Bean
	TokenService tokenService() {
		return new TokenService();
	}
}
