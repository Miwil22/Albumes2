package org.example.users.dto;
import org.example.users.models.Role;
public record UserResponse(Long id, String username, String email, Role role) {}