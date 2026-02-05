package org.example.artistas.exceptions;

public abstract class ArtistaException extends RuntimeException {
    public ArtistaException(String message) {
        super(message);
    }
}