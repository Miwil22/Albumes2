package org.example.rest.albumes.exceptions;

// Excepción genérica para los álbumes
public abstract class AlbumException extends RuntimeException {
    public AlbumException(String message) {
        super(message);
    }
}