package org.example.users.mappers;

import org.example.users.dto.UserInfoResponse;
import org.example.users.dto.UserRequest;
import org.example.users.dto.UserResponse;
import org.example.users.models.Role;
import org.example.users.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UsersMapper {

    // De Petición a Usuario (Para crear/registrar)
    public User toUser(UserRequest request) {
        return User.builder()
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(getSingleRole(request.getRoles())) // Convierte lista de roles a uno solo
                .build();
    }

    public User toUser(UserRequest request, Long id) {
        return User.builder()
                .id(id)
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(getSingleRole(request.getRoles()))
                .build();
    }

    // De Usuario a Respuesta (CORREGIDO: Usamos el Constructor)
    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getApellidos(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public UserInfoResponse toUserInfoResponse(User user, List<String> albumes) {

        return UserInfoResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(Set.of(user.getRole()))
                .isDeleted(false)
                .albumes(albumes)
                .build();
    }

    private Role getSingleRole(Set<Role> roles) {
        if (roles != null && !roles.isEmpty()) {
            return roles.iterator().next();
        }
        return Role.USER;
    }
}