package org.example.albumes.repositories;

import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.repositories.AlbumRepository;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistasRepository artistasRepository;

    @Test
    void findByUuid_ShouldReturnAlbum() {
        Artista artista = Artista.builder()
                .nombre("Artista Test")
                .nacionalidad("Testland")
                .build();
        artistasRepository.save(artista);

        UUID uuid = UUID.randomUUID();
        Album album = Album.builder()
                .titulo("Test Album")
                .genero("Rock")
                .precio(10.0)
                .fechaLanzamiento(LocalDate.now())
                .uuid(uuid)
                .artista(artista)
                .build();
        albumRepository.save(album);

        assertTrue(albumRepository.findByUuid(uuid).isPresent());
    }

    @Test
    void existsByUuid_ShouldReturnTrue() {
        Artista artista = Artista.builder()
                .nombre("Artista Test 2")
                .nacionalidad("Spain")
                .build();
        artistasRepository.save(artista);

        UUID uuid = UUID.randomUUID();
        Album album = Album.builder()
                .titulo("Test Album 2")
                .genero("Pop")
                .precio(15.0)
                .fechaLanzamiento(LocalDate.now())
                .uuid(uuid)
                .artista(artista)
                .build();
        albumRepository.save(album);

        assertTrue(albumRepository.existsByUuid(uuid));
    }
}

