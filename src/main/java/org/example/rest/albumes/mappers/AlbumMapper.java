package org.example.rest.albumes.mappers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.models.Album;
import org.example.rest.artistas.models.Artista;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlbumMapper {
    public Album toAlbum(AlbumCreateDto dto, Artista artista) {
        return Album.builder()
                .id(null)
                .titulo(dto.getTitulo())
                .genero(dto.getGenero())
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .precio(dto.getPrecio())
                .portada(dto.getPortada())
                .artista(artista)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Album toAlbum(AlbumUpdateDto dto, Album album) {
        return Album.builder()
                .id(album.getId())
                .titulo(dto.getTitulo() != null ? dto.getTitulo() : album.getTitulo())
                .genero(dto.getGenero() != null ? dto.getGenero() : album.getGenero())
                .fechaLanzamiento(dto.getFechaLanzamiento() != null ? dto.getFechaLanzamiento() : album.getFechaLanzamiento())
                .precio(dto.getPrecio() != null ? dto.getPrecio() : album.getPrecio())
                .portada(dto.getPortada() != null ? dto.getPortada() : album.getPortada())
                // Una vez creado el álbum, no se puede cambiar el artista
                .artista(album.getArtista())
                .createdAt(album.getCreatedAt())
                // no tenemos en cuenta este campo porque hemos definido valores por defecto en la entidad
                // y automatismos en la base de datos
                // .updatedAt(LocalDateTime.now())
                .uuid(album.getUuid())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId())
                .uuid(album.getUuid())
                .titulo(album.getTitulo())
                .genero(album.getGenero())
                .fechaLanzamiento(album.getFechaLanzamiento())
                .precio(album.getPrecio())
                .portada(album.getPortada())
                .isDeleted(album.getIsDeleted())
                .artista(album.getArtista() != null ? album.getArtista().getNombre() : null)
                .artistaId(album.getArtista() != null ? album.getArtista().getId() : null)
                .build();
    }

    // Mapeamos de modelo a DTO (lista)
    public List<AlbumResponseDto> toResponseDtoList(List<Album> albumes) {
        return albumes.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }

    // Mapeamos de modelo a DTO (page)
    public Page<AlbumResponseDto> toResponseDtoPage(Page<Album> albumes) {
        return albumes.map(this::toAlbumResponseDto);
    }
}