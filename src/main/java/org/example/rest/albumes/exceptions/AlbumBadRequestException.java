package org.example.rest.albumes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Excepción de Bad Request
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlbumBadRequestException extends AlbumException {
    public AlbumBadRequestException(String message) {
        super(message);
    }
}