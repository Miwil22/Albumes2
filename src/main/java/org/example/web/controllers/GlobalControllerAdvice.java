package org.example.web.controllers;

import org.example.rest.users.models.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;

// Aquí exponemos atributos globales para las vistas (HTML)
@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${spring.application.name:Albumes2}") // Valor por defecto si no está en properties
    private String appName;

    @ModelAttribute("appName")
    public String getAppName() {
        return appName;
    }

    @Value("${application.title:Gestión de Álbumes}") // Título de la web
    private String appDescription;

    @ModelAttribute("appDescription")
    public String getAppDescription() {
        return appDescription;
    }

    // Devuelve el usuario completo si está logueado
    @ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    // Devuelve true si hay alguien logueado
    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }

    // ⭐ MÉTODO HELPER PARA SABER SI ES ADMIN EN LAS VISTAS ⭐
    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            // Comprueba si "ADMIN" está en su lista de roles
            return user.getRoles().stream()
                    .anyMatch(role -> role.toString().equals("ADMIN"));
        }
        return false;
    }

    @ModelAttribute("username")
    public String getUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            // Muestra Nombre + Apellidos en la barra de navegación
            return user.getNombre() + " " + user.getApellidos();
        }
        return null;
    }

    @ModelAttribute("userRoles")
    public String getUserRoles(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return user.getRoles().stream().map(Object::toString)
                    .collect(Collectors.joining(","));
        }
        return null;
    }

    // --- SEGURIDAD CSRF (Para formularios) ---

    @ModelAttribute("csrfToken")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getToken() : "";
    }

    @ModelAttribute("csrfParamName")
    public String getCsrfParamName(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getParameterName() : "_csrf";
    }

    @ModelAttribute("csrfHeaderName")
    public String getCsrfHeaderName(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getHeaderName() : "X-CSRF-TOKEN";
    }

    // --- FECHAS (Para el footer, etc.) ---

    @ModelAttribute("currentDateTime")
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    @ModelAttribute("currentYear")
    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    @ModelAttribute("currentMonth")
    public String getCurrentMonth() {
        return LocalDate.now().getMonth()
                .getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
    }
}