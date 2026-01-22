package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.jwt.JwtAuthenticationFilter;
import org.example.users.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas Públicas (Auth y WebSockets)
                        .requestMatchers("/api/*/auth/**", "/ws/**").permitAll()

                        // 2. REGLA: Objetos (Albumes) pueden verse por todo el mundo
                        .requestMatchers(HttpMethod.GET, "/api/*/albumes/**").permitAll()

                        // 3. REGLA: Objetos solo creados/modificados/eliminados por ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/*/albumes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/*/albumes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/*/albumes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/*/albumes/**").hasRole("ADMIN")

                        // 4. REGLA: Usuario puede ver/modificar su perfil (Requiere estar logueado)
                        .requestMatchers("/api/*/users/me/**").authenticated()

                        // El resto requiere autenticación simple
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}