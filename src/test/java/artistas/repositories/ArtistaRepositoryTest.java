package artistas.repositories;

import org.example.Application;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = Application.class)
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArtistaRepositoryTest {

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp(){
        Artista artista = Artista.builder().nombre("Queen").build();
        entityManager.persist(artista);
        entityManager.flush();
    }

    @Test
    void findByNombreEqualsIgnoreCase(){
        Optional<Artista> artista = artistaRepository.findByNombreEqualsIgnoreCase("queen");
        assertTrue(artista.isPresent());
        assertEquals("Queen", artista.get().getNombre());
    }

    @Test
    void findByNombreEqualsIgnoreCase_NotFound(){
        Optional<Artista> artista = artistaRepository.findByNombreEqualsIgnoreCase("Nirvana");
        assertTrue(artista.isEmpty());
    }

}