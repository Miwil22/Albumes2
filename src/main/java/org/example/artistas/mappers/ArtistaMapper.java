package org.example.artistas.mappers;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.springframework.stereotype.Component;

@Component
public class ArtistaMapper {
    public Artista toArtista(ArtistaRequestDto dto){
        return Artista.builder()
                .id(null)
                .nombre(dto.getNombre())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false)
                .build();
    }

    public Artista toArtista(ArtistaRequestDto dto, Artista artista){
        return Artista.builder()
                .id(artista.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : artista.getNombre())
                .createdAt(artista.getCreatedAt())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : artista.getIsDeleted())
                .build();
    }
}
