package com.showtime.catalog.config;

import com.showtime.common.web.KeycloakJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Browsing the catalog is public. Every write needs a valid Keycloak token;
 * which role is needed is declared on each endpoint with @PreAuthorize.
 * CORS is handled once, at the gateway.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            KeycloakJwtAuthenticationConverter jwtConverter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                       // stateless API with bearer tokens
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/api/catalog/swagger-ui/**", "/api/catalog/swagger-ui.html",
                                "/api/catalog/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalog/venues/mine").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/catalog/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));
        return http.build();
    }
}
