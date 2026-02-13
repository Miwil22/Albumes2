package org.example.rest.artistas.mappers;

import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.models.Artista;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ArtistasMapper {

    public Artista toArtista(ArtistaRequestDto dto) {
        return Artista.builder()
                .id(null)
                .nombre(dto.getNombre())
                .nacionalidad(dto.getNacionalidad())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    public Artista toArtista(ArtistaRequestDto dto, Artista artista) {
        return Artista.builder()
                .id(artista.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : artista.getNombre())
                .nacionalidad(dto.getNacionalidad() != null ? dto.getNacionalidad() : artista.getNacionalidad())
                .createdAt(artista.getCreatedAt())
                .updatedAt(LocalDateTime.now()) // Actualizamos fecha modificación
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : artista.getIsDeleted())
                .build();
    }
}