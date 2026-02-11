package org.example.rest.albumes.dto;

import org.example.rest.artistas.models.Artista;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class AlbumResponseDto {
    private final Long id;
    private final String titulo;
    private final String genero;
    private final LocalDate fechaLanzamiento;
    private final Double precio;
    private final String portada;
    private final String descripcion;
    private final Artista artista;
    private final Boolean isDeleted;
    private final UUID uuid;
}