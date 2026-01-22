package org.example.users.controllers;

import lombok.RequiredArgsConstructor;
import org.example.config.security.jwt.JwtService;
import org.example.users.dto.*;
import org.example.users.models.Role;
import org.example.users.models.User;
import org.example.users.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .email(request.email())
                .role(Role.USER) // Por defecto USER
                .build();
        repository.save(user);
        var token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var user = repository.findByUsername(request.username()).orElseThrow();
        var token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}