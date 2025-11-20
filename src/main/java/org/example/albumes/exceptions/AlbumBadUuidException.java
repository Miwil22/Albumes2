package org.example.albumes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlbumBadUuidException extends AlbumException {
    public AlbumBadUuidException(String uuid) {
        super("El UUID " + uuid + " no es válido");
    }
}