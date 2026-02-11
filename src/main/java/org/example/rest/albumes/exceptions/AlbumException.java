package org.example.rest.albumes.exceptions;

public abstract class AlbumException extends RuntimeException {
    public AlbumException(String message) {
        super(message);
    }
}