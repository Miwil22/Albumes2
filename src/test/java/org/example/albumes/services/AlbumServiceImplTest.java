package org.example.albumes.services;

import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.mappers.AlbumMapper;
import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.repositories.AlbumRepository;
import org.example.rest.albumes.services.AlbumServiceImpl;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
import org.example.websockets.notifications.mappers.AlbumNotificationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ArtistasRepository artistasRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private AlbumNotificationMapper albumNotificationMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WebSocketHandler webSocketHandler;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    void setUp() {
        // Configurar el WebSocketHandler para evitar NullPointerException
        albumService.setWebSocketService(webSocketHandler);
    }

    @Test
    void save_ShouldSaveAlbum() {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .titulo("Test Album")
                .artista("Artista Test")
                .precio(10.0)
                .fechaLanzamiento(java.time.LocalDate.now())
                .genero("Rock")
                .build();

        Artista artista = Artista.builder().id(1L).nombre("Artista Test").nacionalidad("Spain").build();
        Album album = Album.builder().id(1L).titulo("Test Album").artista(artista).build();
        AlbumResponseDto responseDto = AlbumResponseDto.builder().id(1L).titulo("Test Album").build();

        when(artistasRepository.findByNombreEqualsIgnoreCase("Artista Test")).thenReturn(Optional.of(artista));
        when(albumMapper.toAlbum(createDto, artista)).thenReturn(album);
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toAlbumResponseDto(album)).thenReturn(responseDto);

        // Act
        AlbumResponseDto result = albumService.save(createDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Album", result.getTitulo());
        verify(albumRepository).save(album);
    }
}

