package org.example.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArtistaConflictException extends ArtistaException {
    public ArtistaConflictException(String message) {
        super(message);
    }
}