package org.example.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ArtistaNotFoundException extends RuntimeException {
    public ArtistaNotFoundException(Long id) {
        super("Artista con id " + id + " no encontrado");
    }
    public ArtistaNotFoundException(String nombre){
        super("Artista " + nombre + " no encontrado");
    }
}
