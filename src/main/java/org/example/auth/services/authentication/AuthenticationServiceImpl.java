package org.example.auth.services.authentication;

import org.example.auth.dto.JwtAuthResponse;
import org.example.auth.dto.UserSignInRequest;
import org.example.auth.dto.UserSignUpRequest;
import org.example.auth.exceptions.AuthDifferentPasswords;
import org.example.auth.exceptions.AuthExistingUsernameOrEmail;
import org.example.auth.exceptions.AuthSignInNotValid;
import org.example.auth.repositories.AuthUsersRepository;
import org.example.auth.services.jwt.JwtService;
import org.example.users.models.Role;
import org.example.users.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthResponse signUp(UserSignUpRequest request) {
        log.info("Creando usuario: {}", request);
        if (request.getPassword().contentEquals(request.getPasswordComprobacion())) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .nombre(request.getNombre())
                    .apellidos(request.getApellidos())
                    .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                    .build();
            try {
                var userStored = authUsersRepository.save(user);
                return JwtAuthResponse.builder().token(jwtService.generateToken(userStored)).build();
            } catch (DataIntegrityViolationException ex) {
                throw new AuthExistingUsernameOrEmail("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe");
            }
        } else {
            throw new AuthDifferentPasswords("Las contraseñas no coinciden");
        }
    }

    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Autenticando usuario: {}", request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = authUsersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthSignInNotValid("Usuario o contraseña incorrectos"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}