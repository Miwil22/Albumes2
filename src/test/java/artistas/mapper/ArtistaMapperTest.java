package artistas.mapper;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.models.Artista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistaMapperTest {
    private final Artista artista = Artista.builder().id(1L).nombre("Queen").build();
    private final ArtistaMapper artistaMapper = new ArtistaMapper();
    private final ArtistaRequestDto artistaDto = ArtistaRequestDto.builder().nombre("QUEEN").build();

    @Test
    public void whenToArtista_thenReturnArtista() {
        Artista mappedArtista = artistaMapper.toArtista(artistaDto);
        assertAll("whenToArtista_thenReturnArtista",
                () -> assertEquals(artistaDto.getNombre(), mappedArtista.getNombre())
        );
    }

    @Test
    public void whenToArtistaWithExistingArtista_thenReturnUpdatedArtista() {
        Artista updatedArtista = artistaMapper.toArtista(artistaDto, artista);
        assertAll("whenToArtistaWithExistingArtista_thenReturnUpdatedArtista",
                () -> assertEquals(artistaDto.getNombre(), updatedArtista.getNombre())
        );
    }
}