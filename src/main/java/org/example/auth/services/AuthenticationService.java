package org.example.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.JwtAuthResponse;
import org.example.auth.dto.UserLoginRequest;
import org.example.auth.dto.UserRegisterRequest;
import org.example.config.security.jwt.JwtAuthenticationProvider;
import org.example.users.models.Role;
import org.example.users.models.User;
import org.example.users.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public JwtAuthResponse signup(UserRegisterRequest request) {
        log.info("Registrando usuario: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? Role.valueOf(request.getRole().toUpperCase()) : Role.USER)
                .build();

        userRepository.save(user);

        String jwt = jwtProvider.generateToken(user);

        return JwtAuthResponse.builder()
                .token(jwt)
                .build();
    }

    public JwtAuthResponse signin(UserLoginRequest request) {
        log.info("Autenticando usuario: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwt = jwtProvider.generateToken(user);

        return JwtAuthResponse.builder()
                .token(jwt)
                .build();
    }
}
