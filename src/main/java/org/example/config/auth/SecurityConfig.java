package org.example.config.auth;

@RequiredArgsConstructor
@Configuration
//@EnableWebSecurity // No hace falta en proyectos Spring Boot
// Habilitamos la seguridad a nivel de método
// ahora prePostEnabled está a true por defecto y @Secured se considera desfasado
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${api.version}")
    private String apiVersion;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] apiPaths = { "/api/**", "/error/**", "/ws/**" };
        http
                .securityMatcher(apiPaths)
                // Podemos decir que forzamos el uso de HTTPS, para algunas rutas de la API o todas
                // Requerimos HTTPS para todas las peticiones, pero ojo que devuelve 302 para los test
                // .requiresChannel(channel -> channel.anyRequest().requiresSecure())

                // Deshabilitamos CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Sesiones
                .sessionManagement(
                        manager -> manager.sessionCreationPolicy(STATELESS))
                // Lo primero es decir a qué URLs queremos dar acceso libre
                // Lista blanca de comprobación

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error/**").permitAll()
                        // Websockets para notificaciones
                        .requestMatchers("/ws/**").permitAll()
                        // Otras rutas de la API podemos permitirlas o no....
                        .requestMatchers("/api/" + apiVersion + "/**").permitAll()
                        // Podríamos jugar con permisos, por ejemplo para una ruta concreta
                        //.requestMatchers("/" + apiVersion + "/auth/me").hasRole("ADMIN")
                        // O con un acción HTTP, POST, PUT, DELETE, etc.
                        //.requestMatchers(GET, "/" + apiVersion + "/auth/me").hasRole("ADMIN")
                        // O con un patrón de ruta
                        //.regexMatchers("/" + apiVersion + "/auth/me").hasRole("ADMIN")
                        // El resto de peticiones tienen que estar autenticadas
                        .anyRequest().authenticated())

                // Añadimos el filtro de autenticación
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devolvemos la configuración
        return http.build();
    }

    // Este filtro permite el acceso a la consola de H2. Quitar en producción
    @Bean
    @Order(2)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathRequest.toH2Console())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PathRequest.toH2Console()).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


}
