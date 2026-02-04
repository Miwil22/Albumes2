package org.example.artistas.mappers;

import org.example.artistas.dto.ArtistaCreateDto;
import org.example.artistas.dto.ArtistaResponseDto;
import org.example.artistas.dto.ArtistaUpdateDto;
import org.example.artistas.models.Artista;
import org.springframework.stereotype.Component;

@Component
public class ArtistaMapper {

    public Artista toArtista(ArtistaCreateDto dto) {
        return Artista.builder()
                .nombre(dto.getNombre())
                .nacionalidad(dto.getNacionalidad())
                .fechaNacimiento(dto.getFechaNacimiento())
                .imagen(dto.getImagen())
                .biografia(dto.getBiografia())
                .build();
    }

    public Artista toArtista(ArtistaUpdateDto dto, Artista artista) {
        if (dto.getNombre() != null) artista.setNombre(dto.getNombre());
        if (dto.getNacionalidad() != null) artista.setNacionalidad(dto.getNacionalidad());
        if (dto.getFechaNacimiento() != null) artista.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getImagen() != null) artista.setImagen(dto.getImagen());
        if (dto.getBiografia() != null) artista.setBiografia(dto.getBiografia());
        return artista;
    }

    public ArtistaResponseDto toArtistaResponseDto(Artista artista) {
        return ArtistaResponseDto.builder()
                .id(artista.getId().toString())
                .nombre(artista.getNombre())
                .nacionalidad(artista.getNacionalidad())
                .fechaNacimiento(artista.getFechaNacimiento())
                .imagen(artista.getImagen())
                .biografia(artista.getBiografia())
                .build();
    }
}
