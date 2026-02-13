package org.example.rest.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de artista no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtistaNotFoundException extends ArtistaException {
  public ArtistaNotFoundException(Long id) {
    super("Artista con id " + id + " no encontrado");
  }

  public ArtistaNotFoundException(String artista) {
    super("Artista " + artista + " no encontrado");
  }
}