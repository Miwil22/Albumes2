package albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMapperTest {

    private final AlbumMapper albumMapper = new AlbumMapper();

    // Creamos un artista de prueba
    private final Artista artista = Artista.builder()
            .id(1L)
            .nombre("The Beatles")
            .build();

    @Test
    void toAlbum_create() {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road")
                .artista("The Beatles")
                .genero("Rock")
                .precio(19.99f)
                .build();

        // Act - AHORA PASAMOS EL ARTISTA, NO EL ID
        var res = albumMapper.toAlbum(createDto, artista);

        // Assert
        assertAll(
                () -> assertEquals(createDto.getNombre(), res.getNombre()),
                () -> assertEquals(artista, res.getArtista()), // Comprobamos que el artista es el correcto
                () -> assertEquals(createDto.getGenero(), res.getGenero()),
                () -> assertEquals(createDto.getPrecio(), res.getPrecio())
        );
    }

    @Test
    void toAlbum_update() {
        // Arrange
        Long id = 1L;
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .nombre("Abbey Road (Updated)")
                .genero("Rock")
                .precio(29.99f)
                .build();

        Album album = Album.builder()
                .id(id)
                .nombre(updateDto.getNombre())
                .genero(updateDto.getGenero())
                .precio(updateDto.getPrecio())
                .artista(artista)
                .build();

        // Act
        var res = albumMapper.toAlbum(updateDto, album);

        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(updateDto.getNombre(), res.getNombre()),
                () -> assertEquals(album.getArtista(), res.getArtista()), // El artista no cambia
                () -> assertEquals(updateDto.getGenero(), res.getGenero()),
                () -> assertEquals(updateDto.getPrecio(), res.getPrecio())
        );
    }

    @Test
    void toAlbumResponseDto() {
        // Arrange
        Album album = Album.builder()
                .id(1L)
                .nombre("Abbey Road")
                .artista(artista) // Usamos el objeto artista
                .genero("Rock")
                .precio(19.99f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        // Act
        var res = albumMapper.toAlbumResponseDto(album);

        // Assert
        assertAll(
                () -> assertEquals(album.getId(), res.getId()),
                () -> assertEquals(album.getNombre(), res.getNombre()),
                () -> assertEquals(album.getArtista().getNombre(), res.getArtista()), // Comprobamos el nombre del artista
                () -> assertEquals(album.getGenero(), res.getGenero()),
                () -> assertEquals(album.getPrecio(), res.getPrecio()),
                () -> assertEquals(album.getUuid(), res.getUuid())
        );
    }
}