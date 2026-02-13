package org.example.rest.artistas.mappers;

import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.models.Artista;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ArtistasMapper {
    public Artista toArtista(ArtistaRequestDto artistaRequestDto) {
        return Artista.builder()
                .nombre(artistaRequestDto.getNombre())
                .nacionalidad(artistaRequestDto.getNacionalidad())
                .biografia(artistaRequestDto.getBiografia())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Artista toArtista(ArtistaRequestDto artistaRequestDto, Artista artista) {
        return Artista.builder()
                .id(artista.getId())
                .nombre(artistaRequestDto.getNombre() != null ? artistaRequestDto.getNombre() : artista.getNombre())
                .nacionalidad(artistaRequestDto.getNacionalidad() != null ? artistaRequestDto.getNacionalidad() : artista.getNacionalidad())
                .biografia(artistaRequestDto.getBiografia() != null ? artistaRequestDto.getBiografia() : artista.getBiografia())
                .isDeleted(artista.getIsDeleted())
                .createdAt(artista.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}