package org.example.artistas.mappers;

import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.mappers.ArtistasMapper;
import org.example.rest.artistas.models.Artista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistaMapperTest {

    private final ArtistasMapper mapper = new ArtistasMapper();

    @Test
    void toArtista_ShouldMapCorrectly() {
        ArtistaRequestDto dto = ArtistaRequestDto.builder()
                .nombre("Test")
                .nacionalidad("ES")
                .build();

        Artista artista = mapper.toArtista(dto);

        assertEquals("Test", artista.getNombre());
        assertEquals("ES", artista.getNacionalidad());
    }

    @Test
    void toArtista_Update_ShouldMapCorrectly() {
        Artista existing = Artista.builder().id(1L).nombre("Old").nacionalidad("FR").build();
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("New").build(); // Nacionalidad null

        Artista updated = mapper.toArtista(dto, existing);

        assertEquals(1L, updated.getId());
        assertEquals("New", updated.getNombre());
        assertEquals("FR", updated.getNacionalidad()); // Mantiene el valor antiguo
    }
}