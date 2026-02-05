package org.example.auth.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.JwtAuthResponse;
import org.example.auth.dto.UserSignInRequest;
import org.example.auth.dto.UserSignUpRequest;
import org.example.auth.services.authentication.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${api.version}/auth")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signIn(@Valid @RequestBody UserSignInRequest request) {
        log.info("Iniciando sesión para el usuario: {}", request.getUsername());
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponse> signUp(@Valid @RequestBody UserSignUpRequest request) {
        log.info("Registrando usuario: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signUp(request));
    }
}