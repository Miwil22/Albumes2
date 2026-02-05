package org.example.albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlbumMapper {
    public Album toAlbum(AlbumCreateDto albumCreateDto, Artista artista) {
        return Album.builder()
                .id(null)
                .titulo(albumCreateDto.getTitulo())
                .genero(albumCreateDto.getGenero())
                .fechaLanzamiento(albumCreateDto.getFechaLanzamiento())
                .artista(artista)
                .precio(albumCreateDto.getPrecio())
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Album toAlbum(AlbumUpdateDto albumUpdateDto, Album album) {
        return Album.builder()
                .id(album.getId())
                .titulo(albumUpdateDto.getTitulo() != null ? albumUpdateDto.getTitulo() : album.getTitulo())
                .genero(albumUpdateDto.getGenero() != null ? albumUpdateDto.getGenero() : album.getGenero())
                .fechaLanzamiento(albumUpdateDto.getFechaLanzamiento() != null ? albumUpdateDto.getFechaLanzamiento() : album.getFechaLanzamiento())
                .artista(album.getArtista())
                .precio(albumUpdateDto.getPrecio() != null ? albumUpdateDto.getPrecio() : album.getPrecio())
                .createdAt(album.getCreatedAt())
                .uuid(album.getUuid())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId())
                .titulo(album.getTitulo())
                .genero(album.getGenero())
                .fechaLanzamiento(album.getFechaLanzamiento())
                .nombreArtista(album.getArtista().getNombre())
                .precio(album.getPrecio())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .uuid(album.getUuid())
                .build();
    }

    public List<AlbumResponseDto> toResponseDtoList(List<Album> albumes) {
        return albumes.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }

    public Page<AlbumResponseDto> toResponseDtoPage(Page<Album> albumes) {
        return albumes.map(this::toAlbumResponseDto);
    }
}