package org.example.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtistaNotFoundException extends ArtistaException {
    public ArtistaNotFoundException(String message) {
        super(message);
    }

    public ArtistaNotFoundException(Long id) {
        super("Artista con id " + id + " no encontrado");
    }
}