package albumes.repositories;

import org.example.Application;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.example.albumes.repositories.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ContextConfiguration(classes = Application.class)
@DataJpaTest
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Artista artista;

    @BeforeEach
    void setUp(){
        artista = Artista.builder().nombre("The Beatles").build();
        entityManager.persist(artista);

        Album album = Album.builder()
                .nombre("Abbey Road")
                .genero("Rock")
                .precio(19.99f)
                .artista(artista)
                .uuid(UUID.randomUUID())
                .build();
        entityManager.persist(album);
        entityManager.flush();
    }

    @Test
    void findAll(){
        List<Album> albumes = albumRepository.findAll();
        assertFalse(albumes.isEmpty());
        assertEquals(1, albumes.size());
    }

    @Test
    void findByNombreContainingIgnoreCase(){
        List<Album> albumes = albumRepository.findByNombreContainingIgnoreCase("abbey");
        assertEquals(1, albumes.size());
    }

    @Test
    void findByArtistaNombre(){
        List<Album> albumes = albumRepository.findByArtistaNombreContainingIgnoreCase("beatles");
        assertEquals(1, albumes.size());
    }
}
