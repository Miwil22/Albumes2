package org.example.albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AlbumMapper {

    public Album toAlbum(AlbumCreateDto dto, Artista artista) {
        return Album.builder()
                .titulo(dto.getTitulo())
                .genero(dto.getGenero())
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .precio(dto.getPrecio())
                .portada(dto.getPortada())
                .descripcion(dto.getDescripcion())
                .artista(artista)
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Album toAlbum(AlbumUpdateDto dto, Album album, Artista artista) {
        return Album.builder()
                .id(album.getId())
                .uuid(album.getUuid())
                .titulo(dto.getTitulo() != null ? dto.getTitulo() : album.getTitulo())
                .genero(dto.getGenero() != null ? dto.getGenero() : album.getGenero())
                .fechaLanzamiento(dto.getFechaLanzamiento() != null ? dto.getFechaLanzamiento() : album.getFechaLanzamiento())
                .precio(dto.getPrecio() != null ? dto.getPrecio() : album.getPrecio())
                .portada(dto.getPortada() != null ? dto.getPortada() : album.getPortada())
                .descripcion(dto.getDescripcion() != null ? dto.getDescripcion() : album.getDescripcion())
                .artista(artista != null ? artista : album.getArtista())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : album.getIsDeleted())
                .createdAt(album.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId())
                .titulo(album.getTitulo())
                .genero(album.getGenero())
                .fechaLanzamiento(album.getFechaLanzamiento())
                .precio(album.getPrecio())
                .portada(album.getPortada())
                .descripcion(album.getDescripcion())
                .artista(album.getArtista())
                .isDeleted(album.getIsDeleted())
                .uuid(album.getUuid())
                .build();
    }
}