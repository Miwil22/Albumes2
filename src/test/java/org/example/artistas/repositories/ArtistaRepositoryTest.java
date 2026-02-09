package org.example.artistas.repositories;

import org.example.artistas.models.Artista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ArtistaRepositoryTest {

    @Autowired
    private ArtistasRepository artistasRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Limpieza manual segura usando el repositorio
        artistasRepository.deleteAll();
    }

    @Test
    void findByNombreEqualsIgnoreCase_ShouldReturnArtista() {
        // Given
        Artista artista = Artista.builder()
                .nombre("Queen")
                .nacionalidad("UK")
                .fechaNacimiento(LocalDate.now())
                .build();

        // Usamos save() del repositorio en lugar de entityManager para simplificar
        // y asegurar que se maneja bien la transacción
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
}