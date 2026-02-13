package org.example.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@Configuration
//@EnableWebSecurity // No hace falta en proyectos Spring Boot
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginSuccessHandler loginSuccessHandler;

    @Value("${api.version}")
    private String apiVersion;

    // --- FILTRO 1: API REST + GRAPHQL (Autenticación por Token JWT) ---
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        // Definimos qué rutas maneja este filtro
        String[] apiPaths = { "/api/**", "/error/**", "/ws/**", "/graphql", "/graphiql", "/graphiql/**" };

        http
                .securityMatcher(apiPaths)
                .csrf(AbstractHttpConfigurer::disable) // API REST no usa CSRF
                .cors(Customizer.withDefaults())       // Activamos CORS
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)) // API es Stateless
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        // Rutas API públicas
                        .requestMatchers("/api/" + apiVersion + "/**").permitAll()
                        // GraphQL y su consola (GraphiQL) públicos para pruebas
                        .requestMatchers("/graphql", "/graphiql", "/graphiql/**").permitAll()
                        // El resto requiere autenticación
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- FILTRO 2: SWAGGER / OPENAPI (Documentación) ---
    @Bean
    @Order(2)
    public SecurityFilterChain openapiFilterChain(HttpSecurity http) throws Exception {
        String[] swaggerPaths = { "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"};
        http
                .securityMatcher(swaggerPaths)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(swaggerPaths).permitAll());
        return http.build();
    }

    // --- FILTRO 3: CONSOLA H2 (Base de datos en memoria) ---
    @Bean
    @Order(3)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathRequest.toH2Console())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PathRequest.toH2Console()).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    // --- FILTRO 4: WEB MVC (HTML con Thymeleaf/Pebble + Login Form) ---
    @Bean
    @Order(4)
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                // En Web MVC sí solemos querer CSRF, pero a veces se desactiva para facilitar desarrollo
                //.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Zona Pública y recursos estáticos
                        .requestMatchers("/public", "/public/", "/public/**").permitAll()
                        .requestMatchers("/", "/auth/**", "/webjars/**", "/css/**", "/images/**").permitAll()
                        // Zona Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // El resto (ej: /app/**) requiere estar logueado
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login") // Página personalizada de login
                        .successHandler(loginSuccessHandler) // Lógica post-login (cookies, redirección)
                        .loginProcessingUrl("/auth/login-post") // Ruta donde el formulario hace POST
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/public") // Al salir vamos a la home pública
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        // Aceptamos peticiones desde tu frontend o localhost
        configuration.setAllowedOrigins(List.of("http://mifrontend.es", "http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}