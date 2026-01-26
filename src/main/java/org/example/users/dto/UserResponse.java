package org.example.users.dto;

import org.example.users.models.Role;
import java.util.Set;

public record UserResponse(
        Long id,
        String nombre,
        String apellidos,
        String username,
        String email,
        Set<Role> roles
) {}