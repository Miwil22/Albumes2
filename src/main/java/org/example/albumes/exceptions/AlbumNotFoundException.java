package org.example.albumes.exceptions;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(Long id) {
        super("Álbum con id " + id + " no encontrado");
    }
}
