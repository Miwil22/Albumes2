package org.example.rest.artistas.mappers;

import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.models.Artista;
import org.springframework.stereotype.Component;

@Component
public class ArtistasMapper {
  public Artista toArtista(ArtistaRequestDto dto) {
    return Artista.builder()
        .id(null)
        .nombre(dto.getNombre())
        .nacionalidad(dto.getNacionalidad())
        .biografia(dto.getBiografia())
        .build();
  }

  public Artista toArtista(ArtistaRequestDto dto, Artista artista) {
    return Artista.builder()
        .id(artista.getId())
        .nombre(dto.getNombre() != null ? dto.getNombre() : artista.getNombre())
        .nacionalidad(dto.getNacionalidad() != null ? dto.getNacionalidad() : artista.getNacionalidad())
        .biografia(dto.getBiografia() != null ? dto.getBiografia() : artista.getBiografia())
        .createdAt(artista.getCreatedAt())
        //.updatedAt(LocalDateTime.now())
        .isDeleted(dto.getIsDeleted()  != null ? dto.getIsDeleted() : artista.getIsDeleted())
        .build();
  }
}