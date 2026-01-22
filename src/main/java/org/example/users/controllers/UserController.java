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

    // Obtener MI perfil (El usuario logueado)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        ));
    }

    // Modificar MI perfil
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@AuthenticationPrincipal User user, @RequestBody UserResponse updates) {
        // Aquí actualizas los campos (nombre, email...)
        if(updates.email() != null) user.setEmail(updates.email());
        // Guardamos
        User updated = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(
                updated.getId(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getRole()
        ));
    }
}