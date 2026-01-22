package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider; // Inyectado desde ApplicationConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/api/*/auth/**", "/ws/**").permitAll()

                        // REGLA: Objetos (Albumes) pueden verse por todo el mundo
                        .requestMatchers(HttpMethod.GET, "/api/*/albumes/**").permitAll()

                        // REGLA: Objetos solo creados/modificados/eliminados por ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/*/albumes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/*/albumes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/*/albumes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/*/albumes/**").hasRole("ADMIN")

                        // REGLA: Usuario puede ver/modificar su perfil
                        .requestMatchers("/api/*/users/me/**").authenticated()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}