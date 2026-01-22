package org.example.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFound extends RuntimeException {
    public UserNotFound(Long id) {
        super("No se encontró el usuario con id " + id);
    }

    public UserNotFound(String message) {
        super(message);
    }
}