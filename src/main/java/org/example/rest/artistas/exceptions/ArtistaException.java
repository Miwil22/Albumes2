package org.example.rest.artistas.exceptions;

// Excepción genérica para los artistas
public abstract class ArtistaException extends RuntimeException {
    public ArtistaException(String message) {
        super(message);
    }
}