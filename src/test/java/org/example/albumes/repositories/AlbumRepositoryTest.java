package org.example.albumes.repositories;

import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistasRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistasRepository artistasRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByTituloEqualsIgnoreCase_ShouldReturnAlbum() {
        Artista artista = Artista.builder()
                .nombre("Artista Test")
                .nacionalidad("Testland")
                .build();
        artistasRepository.save(artista);

        Album album = Album.builder()
                .titulo("Test Album")
                .genero("Rock")
                .precio(10.0)
                .fechaLanzamiento(LocalDate.now())
                .uuid(UUID.randomUUID())
                .artista(artista)
                .build();
        albumRepository.save(album);

        Optional<Album> found = albumRepository.findByTituloEqualsIgnoreCase("test album");

        assertTrue(found.isPresent());
    }
}