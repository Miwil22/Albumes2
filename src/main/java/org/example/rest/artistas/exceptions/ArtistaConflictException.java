package org.example.rest.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de conflicto en artista
 * Status 409
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ArtistaConflictException extends ArtistaException {

  public ArtistaConflictException(String message) {
    super(message);
  }
}