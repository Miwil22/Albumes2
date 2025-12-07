package org.example.artistas.repositories;

import org.example.Application;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Igualamos las anotaciones del proyecto Tarjetas
@ContextConfiguration(classes = Application.class)
@DataJpaTest
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArtistaRepositoryTest {

    private final Artista artista = Artista.builder().nombre("Queen").build();

    @Autowired
    private ArtistaRepository repositorio;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.persist(artista);
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Artista> artistas = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(artistas),
                () -> assertFalse(artistas.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        // Act
        List<Artista> artistas = repositorio.findByNombreContainingIgnoreCase("Queen");

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(artistas),
                () -> assertFalse(artistas.isEmpty()),
                () -> assertEquals("Queen", artistas.getFirst().getNombre())
        );
    }

    @Test
    void findById() {
        // Act
        Artista artistaFound = repositorio.findById(artista.getId()).orElse(null);

        // Assert
        assertAll("findById",
                () -> assertNotNull(artistaFound),
                () -> assertEquals("Queen", artistaFound.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Artista artistaFound = repositorio.findById(100L).orElse(null);

        // Assert
        assertNull(artistaFound);
    }

    @Test
    void save() {
        // Act
        Artista newArtista = repositorio.save(Artista.builder().nombre("Nirvana").build());

        // Assert
        assertAll("save",
                () -> assertNotNull(newArtista),
                () -> assertEquals("Nirvana", newArtista.getNombre())
        );
    }

    @Test
    void update() {
        // Act
        var artistaExistente = repositorio.findById(artista.getId()).orElse(null);
        // assertNotNull(artistaExistente); // Seguro por setUp

        // Modificamos el objeto recuperado o creamos uno con el mismo ID
        artistaExistente.setNombre("Queen Remastered");
        Artista artistaActualizado = repositorio.save(artistaExistente);

        // Assert
        assertAll("update",
                () -> assertNotNull(artistaActualizado),
                () -> assertEquals("Queen Remastered", artistaActualizado.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        var artistaBorrar = repositorio.findById(artista.getId()).orElse(null);
        repositorio.delete(artistaBorrar);

        Artista artistaBorrado = repositorio.findById(artista.getId()).orElse(null);

        // Assert
        assertNull(artistaBorrado);
    }

    // Test equivalente a FetchType EAGER vs LAZY de Tarjetas
    @Test
    void test_FetchType_Check() {
        entityManager.clear();
        Artista artistaFound = repositorio.findById(artista.getId()).orElse(null);
        assertNotNull(artistaFound);
        // Aquí podríamos comprobar si carga los álbumes dependiendo de tu config (Lazy es default)
    }
}