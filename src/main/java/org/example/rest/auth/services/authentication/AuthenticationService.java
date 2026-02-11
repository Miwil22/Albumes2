package org.example.rest.auth.services.authentication;

import org.example.rest.auth.dto.JwtAuthResponse;
import org.example.rest.auth.dto.UserSignInRequest;
import org.example.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}