package org.example.albumes.mappers;

import lombok.RequiredArgsConstructor;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlbumMapper {

    private final ArtistaRepository artistaRepository;

    public Album toAlbum(AlbumCreateDto dto) {
        Artista artista = artistaRepository.findById(UUID.fromString(dto.getArtistaId()))
                .orElseThrow(() -> new RuntimeException("Artista no encontrado"));

        return Album.builder()
                .titulo(dto.getTitulo())
                .artista(artista)
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .genero(dto.getGenero())
                .portada(dto.getPortada())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio() != null ? dto.getPrecio() : 0.0)
                .build();
    }

    public Album toAlbum(AlbumUpdateDto dto, Album album) {
        if (dto.getTitulo() != null) album.setTitulo(dto.getTitulo());
        if (dto.getArtistaId() != null) {
            Artista artista = artistaRepository.findById(UUID.fromString(dto.getArtistaId()))
                    .orElseThrow(() -> new RuntimeException("Artista no encontrado"));
            album.setArtista(artista);
        }
        if (dto.getFechaLanzamiento() != null) album.setFechaLanzamiento(dto.getFechaLanzamiento());
        if (dto.getGenero() != null) album.setGenero(dto.getGenero());
        if (dto.getPortada() != null) album.setPortada(dto.getPortada());
        if (dto.getDescripcion() != null) album.setDescripcion(dto.getDescripcion());
        if (dto.getPrecio() != null) album.setPrecio(dto.getPrecio());
        return album;
    }

    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId().toString())
                .titulo(album.getTitulo())
                .artista(album.getArtista().getNombre())
                .artistaId(album.getArtista().getId().toString())
                .fechaLanzamiento(album.getFechaLanzamiento())
                .genero(album.getGenero())
                .portada(album.getPortada())
                .descripcion(album.getDescripcion())
                .precio(album.getPrecio())
                .build();
    }
}
