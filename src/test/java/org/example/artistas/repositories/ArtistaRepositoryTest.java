package org.example.artistas.repositories;

import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArtistaRepositoryTest {

    @Autowired
    private ArtistasRepository artistasRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Aseguramos que la tabla esté limpia antes de cada test
        // Aunque reset.sql ya lo hace, esto es una doble seguridad para H2 en memoria
        artistasRepository.deleteAll();
    }

    @Test
    void findAll_ShouldReturnList() {
        // Given
        Artista artista = Artista.builder()
                .nombre("Queen")
                .nacionalidad("UK")
                .fechaNacimiento(LocalDate.now())
                .build();
        artistasRepository.save(artista);

        // When
        List<Artista> result = artistasRepository.findAll();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByNombreEqualsIgnoreCase_ShouldReturnArtista() {
        // Given
        Artista artista = Artista.builder()
                .nombre("Queen")
                .nacionalidad("UK")
                .fechaNacimiento(LocalDate.now())
                .build();
        artistasRepository.save(artista);

        // When
        Optional<Artista> found = artistasRepository.findByNombreEqualsIgnoreCase("queen");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Queen", found.get().getNombre());
    }

    @Test
    void findByNombreContainingIgnoreCase_ShouldReturnList() {
        // Given
        Artista artista1 = Artista.builder()
                .nombre("Queen")
                .nacionalidad("UK")
                .fechaNacimiento(LocalDate.now())
                .build();
        Artista artista2 = Artista.builder()
                .nombre("Queens of the Stone Age")
                .nacionalidad("USA")
                .fechaNacimiento(LocalDate.now())
                .build();
        Artista artista3 = Artista.builder()
                .nombre("Nirvana")
                .nacionalidad("USA")
                .fechaNacimiento(LocalDate.now())
                .build();

        artistasRepository.saveAll(List.of(artista1, artista2, artista3));

        // When
        List<Artista> found = artistasRepository.findByNombreContainingIgnoreCase("queen");

        // Then
        assertEquals(2, found.size());
    }

    @Test
    void findById_ShouldReturnArtista() {
        // Given
        Artista artista = Artista.builder()
                .nombre("Test")
                .nacionalidad("ES")
                .fechaNacimiento(LocalDate.now())
                .build();
        Artista saved = artistasRepository.save(artista);

        // When
        Optional<Artista> found = artistasRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getNombre());
    }

    @Test
    void save_ShouldPersistArtista() {
        // Given
        Artista artista = Artista.builder()
                .nombre("New Artista")
                .nacionalidad("IT")
                .fechaNacimiento(LocalDate.now())
                .build();

        // When
        Artista saved = artistasRepository.save(artista);

        // Then
        assertNotNull(saved.getId());
        assertEquals("New Artista", saved.getNombre());
    }

    @Test
    void update_ShouldModifyArtista() {
        // Given
        Artista artista = Artista.builder()
                .nombre("Original")
                .nacionalidad("ES")
                .fechaNacimiento(LocalDate.now())
                .build();
        Artista saved = artistasRepository.save(artista);

        // When
        saved.setNombre("Modified");
        Artista updated = artistasRepository.save(saved);

        // Then
        assertEquals("Modified", updated.getNombre());
    }

    @Test
    void delete_ShouldRemoveArtista() {
        // Given
        Artista artista = Artista.builder()
                .nombre("To Delete")
                .nacionalidad("DE")
                .fechaNacimiento(LocalDate.now())
                .build();
        Artista saved = artistasRepository.save(artista);

        // When
        artistasRepository.delete(saved);
        Optional<Artista> found = artistasRepository.findById(saved.getId());

        // Then
        assertFalse(found.isPresent());
    }
}