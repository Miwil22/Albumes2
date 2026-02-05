package org.example.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthSignInNotValid extends AuthException {
    public AuthSignInNotValid(String message) {
        super(message);
    }
}