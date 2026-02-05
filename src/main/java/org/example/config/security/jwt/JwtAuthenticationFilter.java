package org.example.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Iniciando el filtro de autenticación para: {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail = null;
        UserDetails userDetails = null;

        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.info("No se ha encontrado cabecera de autenticación válida, se ignora");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, se procesa");
        jwt = authHeader.substring(7);

        try {
            userEmail = jwtProvider.extractUserName(jwt);
        } catch (Exception e) {
            log.error("Token no válido: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido");
            return;
        }

        log.info("Usuario detectado en token: {}", userEmail);

        if (StringUtils.hasText(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                userDetails = userDetailsService.loadUserByUsername(userEmail);
            } catch (Exception e) {
                log.error("Usuario no encontrado en BDD: {}", userEmail);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }

            if (jwtProvider.isTokenValid(jwt, userDetails)) {
                log.info("JWT válido para el usuario {}", userEmail);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                log.warn("JWT no válido para el usuario {}", userEmail);
            }
        }

        filterChain.doFilter(request, response);
    }
}