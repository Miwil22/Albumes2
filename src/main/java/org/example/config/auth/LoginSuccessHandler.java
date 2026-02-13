package org.example.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String COOKIE_NAME = "visitasApp";
    private static final int MAX_AGE = 365 * 24 * 60 * 60; // 1 año

    public LoginSuccessHandler() {
        // Valor por defecto si no hay saved-request: ir a la zona pública
        setDefaultTargetUrl("/public");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Lógica del contador de visitas mediante Cookies
        int val = 0;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName())) {
                    try {
                        val = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        val++;

        // Creamos o actualizamos la cookie
        Cookie newCookie = new Cookie(COOKIE_NAME, Integer.toString(val));
        newCookie.setPath("/");
        newCookie.setMaxAge(MAX_AGE);
        // Debe ser false para que JS pueda leerla si quieres mostrar el contador en el front
        newCookie.setHttpOnly(false);
        newCookie.setSecure(request.isSecure()); // Solo secure si estamos en HTTPS
        response.addCookie(newCookie);

        // Llamamos al padre para que haga la redirección
        super.onAuthenticationSuccess(request, response, authentication);
    }
}