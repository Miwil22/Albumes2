package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistasRepository;
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.websockets.notifications.mappers.AlbumNotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
    private ObjectMapper objectMapper; // Añadido mock de ObjectMapper

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void save_ShouldSaveAlbum() {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .titulo("Test Album")
                .artistaId(1L)
                .precio(10.0) // Añadido precio para evitar nulos
                .fechaLanzamiento(java.time.LocalDate.now()) // Añadida fecha
                .genero("Rock")
                .build();

        Artista artista = Artista.builder().id(1L).nombre("Artista").build();
        Album album = Album.builder().id(1L).titulo("Test Album").artista(artista).build();
        AlbumResponseDto responseDto = AlbumResponseDto.builder().id(1L).titulo("Test Album").build();

        when(artistasRepository.findById(1L)).thenReturn(Optional.of(artista));
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