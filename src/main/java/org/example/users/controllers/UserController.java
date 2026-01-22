package org.example.users.controllers;

import lombok.RequiredArgsConstructor;
import org.example.users.dto.UserResponse;
import org.example.users.models.User;
import org.example.users.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@AuthenticationPrincipal User user, @RequestBody UserResponse updates) {
        if(updates.nombre() != null) user.setNombre(updates.nombre());
        if(updates.apellidos() != null) user.setApellidos(updates.apellidos());
        if(updates.email() != null) user.setEmail(updates.email());

        User updated = userRepository.save(user);
        return ResponseEntity.ok(toResponse(updated));
    }

    // Método auxiliar manual para no depender del Mapper y evitar errores
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getApellidos(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}