package org.example.albumes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de álbum no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlbumNotFoundException extends AlbumException {
    public AlbumNotFoundException(Long id) {
        super("Álbum con id " + id + " no encontrado.");
    }
    public AlbumNotFoundException(UUID uuid) {
        super("Álbum con uuid " + uuid + " no encontrado.");
    }
}