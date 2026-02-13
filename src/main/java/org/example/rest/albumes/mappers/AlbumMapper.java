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
  public Album toAlbum(AlbumCreateDto albumCreateDto, Artista artista) {
    return Album.builder()
        .id(null)
        .titulo(albumCreateDto.getTitulo())
        .genero(albumCreateDto.getGenero())
        .fechaLanzamiento(albumCreateDto.getFechaLanzamiento())
        .artista(artista)
        .precio(albumCreateDto.getPrecio())
        .portada(albumCreateDto.getPortada())
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
        // Una vez creado el album, no se puede cambiar el artista
        .artista(album.getArtista())
        .precio(albumUpdateDto.getPrecio() != null ? albumUpdateDto.getPrecio() : album.getPrecio())
        .portada(albumUpdateDto.getPortada() != null ? albumUpdateDto.getPortada() : album.getPortada())
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
        .titulo(album.getTitulo())
        .genero(album.getGenero())
        .fechaLanzamiento(album.getFechaLanzamiento())
        .artista(album.getArtista().getNombre())
        .precio(album.getPrecio())
        .portada(album.getPortada())
        .createdAt(album.getCreatedAt())
        .updatedAt(album.getUpdatedAt())
        .uuid(album.getUuid())
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