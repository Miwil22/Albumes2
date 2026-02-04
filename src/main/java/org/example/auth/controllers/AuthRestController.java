package org.example.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.JwtAuthResponse;
import org.example.auth.dto.UserLoginRequest;
import org.example.auth.dto.UserRegisterRequest;
import org.example.auth.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoint de autenticación de nuestra API REST")
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Registra un nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro no válidos"),
    })
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponse> signup(@Valid @RequestBody UserRegisterRequest request) {
        log.info("Registrando usuario: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signup(request));
    }

    @Operation(summary = "Login de usuario", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado correctamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales no válidas"),
    })
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signin(@Valid @RequestBody UserLoginRequest request) {
        log.info("Autenticando usuario: {}", request.getUsername());
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
