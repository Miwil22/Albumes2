package org.example.albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista; // Importante
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
                .artista(artista) // Asignamos la entidad
                .build();
    }

    public Album toAlbum(AlbumUpdateDto updateDto, Album album) {
        return Album.builder()
                .id(album.getId())
                .uuid(album.getUuid())
                .createdAt(album.getCreatedAt())
                .artista(album.getArtista()) // Mantenemos el artista
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
                .artista(album.getArtista().getNombre()) // Devolvemos solo el nombre
                .uuid(album.getUuid())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    // Añade el método para listas si te falta
    public java.util.List<AlbumResponseDto> toResponseDtoList(java.util.List<Album> albums) {
        return albums.stream().map(this::toAlbumResponseDto).toList();
    }
}