package de.adorsys.sts.secretserver.configuration;

import de.adorsys.sts.filter.JWTAuthenticationFilter;
import de.adorsys.sts.token.authentication.TokenAuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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


    private final CorsProperties corsProperties;

    public SecurityConfiguration(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthenticationService tokenAuthenticationService) throws Exception {
        if (corsProperties.isDisbaled()) { // Achten Sie auf die korrekte Schreibweise von isDisabled(), falls es ein
            // Tippfehler war.
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

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((requests) -> requests
                        // Erlauben Sie den Zugriff auf Swagger-Dokumentation und UI-Ressourcen
                        .requestMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers("/cloudfoundryapplication/**").permitAll()
                        // Erlauben Sie den Zugriff auf andere spezifische Endpunkte
                        .requestMatchers(HttpMethod.GET, "/pop").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        // Alle anderen Anfragen erfordern eine Authentifizierung
                        .anyRequest().authenticated()
                );

        // Fügt den JWTAuthenticationFilter vor dem UsernamePasswordAuthenticationFilter hinzu
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
}
