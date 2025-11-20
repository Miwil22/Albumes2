package org.example.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ArtistaConflictException extends RuntimeException {
    public ArtistaConflictException(String message) {
        super(message);
    }
}
