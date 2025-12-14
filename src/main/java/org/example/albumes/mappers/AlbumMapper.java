package org.example.albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AlbumMapper {

    // Ahora recibe un objeto Artista
    public Album toAlbum(AlbumCreateDto createDto, Artista artista) {
        return Album.builder()
                .nombre(createDto.getNombre())
                .genero(createDto.getGenero())
                .precio(createDto.getPrecio())
                .artista(artista)
                .build();
    }

    public Album toAlbum(AlbumUpdateDto updateDto, Album album) {
        return Album.builder()
                .id(album.getId())
                .uuid(album.getUuid())
                .createdAt(album.getCreatedAt())
                .artista(album.getArtista())
                .nombre(updateDto.getNombre() != null ? updateDto.getNombre() : album.getNombre())
                .genero(updateDto.getGenero() != null ? updateDto.getGenero() : album.getGenero())
                .precio(updateDto.getPrecio() != null ? updateDto.getPrecio() : album.getPrecio())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId())
                .nombre(album.getNombre())
                .genero(album.getGenero())
                .precio(album.getPrecio())
                .artista(album.getArtista().getNombre())
                .uuid(album.getUuid())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    // Mapeamos de modelo a DTO(lista)
    public java.util.List<AlbumResponseDto> toResponseDtoList(java.util.List<Album> albums) {
        return albums.stream().map(this::toAlbumResponseDto).toList();
    }

    // Mapeamos de modelo a DTO (page)
    public Page<AlbumResponseDto> toResponseDtoPage(Page<Album> albumes) {
        return albumes.map(this::toAlbumResponseDto);
    }
}