package org.example.albumes.repositories;

import org.example.Application;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = Application.class)
@DataJpaTest
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AlbumRepositoryTest {

    private final Artista artista1 = Artista.builder().nombre("The Beatles").build();
    private final Artista artista2 = Artista.builder().nombre("Michael Jackson").build();

    private final Album album1 = Album.builder()
            .nombre("Abbey Road").genero("Rock").precio(19.99f).artista(artista1)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .nombre("Thriller").genero("Pop").precio(29.99f).artista(artista2)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp(){
        entityManager.persist(artista1);
        entityManager.persist(artista2);
        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.flush();
    }

    @Test
    void findAll(){
        List<Album> albumes = albumRepository.findAll();
        assertAll("findAll",
                () -> assertNotNull(albumes),
                () -> assertEquals(2, albumes.size())
        );
    }

    @Test
    void findByNombreContainingIgnoreCase(){
        // Usamos minúsculas para probar el ignoreCase
        List<Album> albumes = albumRepository.findByNombreContainingIgnoreCase("abbey");
        assertAll("findByNombreContainingIgnoreCase",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(album1.getNombre(), albumes.get(0).getNombre())
        );
    }

    @Test
    void findByArtistaNombre(){
        List<Album> albumes = albumRepository.findByArtistaNombreContainingIgnoreCase("beatles");
        assertAll("findByArtistaNombre",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(artista1.getNombre(), albumes.get(0).getArtista().getNombre())
        );
    }

    @Test
    void findByNombreAndArtista(){
        // Pasamos parámetros en minúsculas para probar que la Specification con LOWER() funciona
        List<Album> albumes = albumRepository.findByNombreAndArtista("abbey", "beatles");
        assertAll("findByNombreAndArtista",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(album1.getNombre(), albumes.get(0).getNombre())
        );
    }
}