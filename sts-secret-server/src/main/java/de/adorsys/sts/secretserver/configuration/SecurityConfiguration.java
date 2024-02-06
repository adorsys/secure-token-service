package de.adorsys.sts.secretserver.configuration;

import de.adorsys.sts.filter.JWTAuthenticationFilter;
import de.adorsys.sts.token.authentication.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    @Autowired
    private CorsProperties corsProperties;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthenticationService tokenAuthenticationService) throws Exception {
        if (corsProperties.isDisbaled()) {
            http.cors().disable();
        } else {
            http.cors().configurationSource(request -> {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins()));
                corsConfiguration.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods()));
                corsConfiguration.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders()));
                return corsConfiguration;
            });
        }

        http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.GET, "/pop").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .anyRequest().authenticated());


        http.addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        Arrays.stream(corsProperties.getAllowedOrigins()).forEach(config::addAllowedOrigin);
        Arrays.asList(corsProperties.getAllowedHeaders()).forEach(config::addAllowedHeader);
        Arrays.stream(corsProperties.getAllowedMethods()).forEach(config::addAllowedMethod);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }


    @Bean
    public WebSecurityCustomizer customize() {
        return (web) -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/configuration/ui",
                "/swagger-resources/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }
}
