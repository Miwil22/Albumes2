package albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import org.example.albumes.services.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    // Creamos un artista para las pruebas
    private final Artista artista = Artista.builder()
            .id(1L)
            .nombre("The Beatles")
            .build();

    private final Album album1 = Album.builder()
            .id(1L).nombre("Abbey Road")
            .artista(artista)
            .genero("Rock").precio(19.99f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2L).nombre("Thriller")
            .artista(Artista.builder().id(2L).nombre("Michael Jackson").build())
            .genero("Pop").precio(15.29f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumResponseDto responseDto1;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistaService artistaService; // <-- Necesitamos mockear esto

    @Spy
    private AlbumMapper albumMapper = new AlbumMapper();

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    void setUp() {
        responseDto1 = albumMapper.toAlbumResponseDto(album1);
    }

    @Test
    void findAll_ShouldReturnAllAlbums() {
        when(albumRepository.findAll()).thenReturn(List.of(album1, album2));
        List<AlbumResponseDto> res = albumService.findAll(null, null);
        assertEquals(2, res.size());
    }

    @Test
    void findById_ShouldReturnAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));
        var res = albumService.findById(1L);
        assertEquals(responseDto1, res);
    }

    @Test
    void save_ShouldReturnSavedAlbum() {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road")
                .artista("The Beatles")
                .precio(19.99f)
                .build();

        // El mapper ahora usa createDto y el artista.
        // Como es un @Spy, usará la lógica real.
        // Pero necesitamos que el artista exista:
        when(artistaService.findByNombre("The Beatles")).thenReturn(artista);

        // Configuramos el repo para que devuelva el álbum "guardado" (simulado)
        when(albumRepository.save(any(Album.class))).thenReturn(album1);

        // Act
        var res = albumService.save(createDto);

        // Assert
        assertEquals("Abbey Road", res.getNombre());
        assertEquals("The Beatles", res.getArtista());

        verify(artistaService).findByNombre("The Beatles");
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void update_ShouldReturnUpdatedAlbum() {
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .nombre("Updated Name")
                .precio(500.0f)
                .build();

        // Simulamos que el álbum existe
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));
        // Simulamos el guardado (Mockito devuelve lo que le pasemos o album1 modificado)
        // Para simplificar, hacemos que devuelva album1, aunque en la realidad el objeto
        // se modificaría dentro del servicio antes de llamar a save.
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var res = albumService.update(1L, updateDto);

        assertEquals("Updated Name", res.getNombre());

        verify(albumRepository).findById(1L);
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void deleteById_ShouldDelete() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));
        albumService.deleteById(1L);
        verify(albumRepository).deleteById(1L);
    }

    @Test
    void findById_NotFound() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(AlbumNotFoundException.class, () -> albumService.findById(99L));
    }
}