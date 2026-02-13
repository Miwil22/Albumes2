package org.example.albumes.mappers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.mappers.AlbumMapper;
import org.example.rest.albumes.models.Album;
import org.example.rest.artistas.models.Artista;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMapperTest {

  private final Artista artista = Artista.builder().nombre("My Chemical Romance").build();

  // Inyectamos el mapper
  private final AlbumMapper albumMapper = new AlbumMapper();

  @Test
  void toAlbum_create() {
    // Arrange
    AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
        .titulo("The Black Parade")
        .genero("Rock")
        .fechaLanzamiento(LocalDate.of(2006,10,23))
        .artista("My Chemical Romance")
        .precio(19.99)
        .portada("https://example.com/cover.jpg")
        .build();
    // Act
    var res = albumMapper.toAlbum(albumCreateDto, artista);

    // Assert
    assertAll(
        () -> assertEquals(albumCreateDto.getTitulo(), res.getTitulo()),
        () -> assertEquals(albumCreateDto.getGenero(), res.getGenero()),
        () -> assertEquals(albumCreateDto.getFechaLanzamiento(), res.getFechaLanzamiento()),
        () -> assertEquals(albumCreateDto.getArtista(), res.getArtista().getNombre()),
        () -> assertEquals(albumCreateDto.getPrecio(), res.getPrecio()),
        () -> assertEquals(albumCreateDto.getPortada(), res.getPortada())
    );
  }

  @Test
  void toAlbum_update() {
    // Arrange
    Long id = 1L;
    AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
        .titulo("The Black Parade (Deluxe)")
        .genero("Rock")
        .fechaLanzamiento(LocalDate.of(2006,10,23))
        .precio(24.99)
        .portada("https://example.com/cover-deluxe.jpg")
        .build();

    Album album = Album.builder()
        .id(id)
        .titulo(albumUpdateDto.getTitulo())
        .genero(albumUpdateDto.getGenero())
        .fechaLanzamiento(albumUpdateDto.getFechaLanzamiento())
        .precio(albumUpdateDto.getPrecio())
        .portada(albumUpdateDto.getPortada())
        .build();
    // Act
    var res = albumMapper.toAlbum(albumUpdateDto, album);
    // Assert
    assertAll(
        () -> assertEquals(id, res.getId()),
        () -> assertEquals(albumUpdateDto.getTitulo(), res.getTitulo()),
        () -> assertEquals(albumUpdateDto.getGenero(), res.getGenero()),
        () -> assertEquals(albumUpdateDto.getFechaLanzamiento(), res.getFechaLanzamiento()),
        () -> assertEquals(albumUpdateDto.getPrecio(), res.getPrecio()),
        () -> assertEquals(albumUpdateDto.getPortada(), res.getPortada())
    );
  }

  @Test
  void toAlbumResponseDto() {
    // Arrange
    Album album = Album.builder()
        .id(1L)
        .titulo("The Black Parade")
        .genero("Rock")
        .fechaLanzamiento(LocalDate.of(2006,10,23))
        .artista(artista)
        .precio(19.99)
        .portada("https://example.com/cover.jpg")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
        .build();
    // Act
    var res = albumMapper.toAlbumResponseDto(album);
    // Assert
    assertAll(
        () -> assertEquals(album.getId(), res.getId()),
        () -> assertEquals(album.getTitulo(), res.getTitulo()),
        () -> assertEquals(album.getGenero(), res.getGenero()),
        () -> assertEquals(album.getFechaLanzamiento(), res.getFechaLanzamiento()),
        () -> assertEquals(album.getArtista().getNombre(), res.getArtista()),
        () -> assertEquals(album.getPrecio(), res.getPrecio()),
        () -> assertEquals(album.getPortada(), res.getPortada())
    );
  }
}