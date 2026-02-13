package org.example.rest.albumes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de album no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlbumNotFoundException  extends AlbumException{
  public AlbumNotFoundException(Long id) {
    super("Album con id " + id + " no encontrado");
  }
  public AlbumNotFoundException(UUID uuid) {
    super("Album con uuid " + uuid + " no encontrado");
  }
}