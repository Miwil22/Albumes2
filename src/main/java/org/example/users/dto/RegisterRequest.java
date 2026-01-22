package org.example.users.dto;

// Debe ser 'record', si es 'class' con @Data tendrías que usar .getNombre()
public record RegisterRequest(
        String username,
        String password,
        String nombre,
        String apellidos,
        String email
) {}