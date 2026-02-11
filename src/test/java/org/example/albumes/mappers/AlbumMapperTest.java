package org.example.albumes.mappers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.mappers.AlbumMapper;
import org.example.rest.albumes.models.Album;
import org.example.rest.artistas.models.Artista;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlbumMapperTest {

    private final AlbumMapper albumMapper = new AlbumMapper();

    @Test
    void toAlbum_ShouldMapCorrectly() {
        AlbumCreateDto dto = AlbumCreateDto.builder()
                .titulo("Test Album")
                .genero("Rock")
                .precio(10.0)
                .fechaLanzamiento(LocalDate.now())
                .build();
        Artista artista = Artista.builder().id(1L).build();

        Album album = albumMapper.toAlbum(dto, artista);

        assertEquals("Test Album", album.getTitulo());
        assertEquals("Rock", album.getGenero());
    }

    @Test
    void toResponseDto_ShouldMapCorrectly() {
        Album album = Album.builder()
                .id(1L)
                .titulo("Test Album")
                .genero("Rock")
                .build();

        AlbumResponseDto dto = albumMapper.toAlbumResponseDto(album);

        assertEquals("Test Album", dto.getTitulo());
    }
}