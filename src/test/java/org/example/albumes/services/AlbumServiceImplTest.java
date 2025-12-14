package org.example.albumes.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.websockets.notifications.mappers.AlbumNotificationMapper;
import org.example.websockets.notifications.models.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    private final Artista artista = Artista.builder().id(1L).nombre("The Beatles").build();

    private final Album album1 = Album.builder()
            .id(1L).nombre("Abbey Road").artista(artista).genero("Rock").precio(19.99f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2L).nombre("Thriller").artista(Artista.builder().id(2L).nombre("Michael Jackson").build())
            .genero("Pop").precio(15.29f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumResponseDto albumResponse1;

    @Mock private AlbumRepository albumRepository;
    @Mock private ArtistaService artistaService;
    @Spy private AlbumMapper albumMapper;
    @Mock private WebSocketConfig webSocketConfig;
    @Mock private AlbumNotificationMapper albumNotificationMapper;
    @Mock private ObjectMapper objectMapper;
    @Mock private WebSocketHandler webSocketService;

    @InjectMocks private AlbumServiceImpl albumService;
    @Captor private ArgumentCaptor<Album> albumCaptor;

    @BeforeEach
    void setUp() {
        albumResponse1 = albumMapper.toAlbumResponseDto(album1);
        albumService.setWebSocketService(webSocketService);
    }

    @Test
    void findAll_ShouldReturnAllAlbums() {
        // Arrange
        List<Album> expectedAlbums = Arrays.asList(album1, album2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Album> expectedPage = new PageImpl<>(expectedAlbums);

        when(albumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<AlbumResponseDto> actualPage = albumService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertNotNull(actualPage);
        assertEquals(2, actualPage.getTotalElements());

        // Verify
        verify(albumRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombre() {
        // Arrange
        Optional<String> nombre = Optional.of("Abbey");
        List<Album> expectedAlbums = List.of(album1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Album> expectedPage = new PageImpl<>(expectedAlbums);

        when(albumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<AlbumResponseDto> actualPage = albumService.findAll(nombre, Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());

        // Verify
        verify(albumRepository, only()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void save_ShouldReturnSavedAlbum() throws IOException {
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road").artista("The Beatles").genero("Rock").precio(19.99f).build();

        when(artistaService.findByNombre(createDto.getArtista())).thenReturn(artista);
        when(albumRepository.save(any(Album.class))).thenReturn(album1);

        // SIN doNothing()
        var res = albumService.save(createDto);
        assertEquals("Abbey Road", res.getNombre());
    }

    @Test
    void onChange_ShouldSendMessage() throws IOException, InterruptedException {
        // Arrange
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        albumService.onChange(Notificacion.Tipo.CREATE, album1);

        // AÑADIR SLEEP PARA QUE PASE EL TEST
        Thread.sleep(100);

        // Verify
        verify(webSocketService).sendMessage(any());
    }
}