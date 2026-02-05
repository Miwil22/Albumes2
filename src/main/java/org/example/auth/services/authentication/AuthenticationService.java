package org.example.auth.services.authentication;

import org.example.auth.dto.JwtAuthResponse;
import org.example.auth.dto.UserSignInRequest;
import org.example.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);
    JwtAuthResponse signIn(UserSignInRequest request);
}