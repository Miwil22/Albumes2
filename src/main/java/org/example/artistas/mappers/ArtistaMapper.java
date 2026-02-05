package org.example.artistas.mappers;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ArtistaMapper {
    public Artista toArtista(ArtistaRequestDto dto) {
        return Artista.builder()
                .id(null)
                .nombre(dto.getNombre())
                .nacionalidad(dto.getNacionalidad())
                .fechaNacimiento(dto.getFechaNacimiento())
                .imagen(dto.getImagen())
                .biografia(dto.getBiografia())
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
                .fechaNacimiento(dto.getFechaNacimiento() != null ? dto.getFechaNacimiento() : artista.getFechaNacimiento())
                .imagen(dto.getImagen() != null ? dto.getImagen() : artista.getImagen())
                .biografia(dto.getBiografia() != null ? dto.getBiografia() : artista.getBiografia())
                .createdAt(artista.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : artista.getIsDeleted())
                .build();
    }
}