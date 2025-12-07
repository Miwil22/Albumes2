package org.example.albumes.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadUuidException;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Integra Mockito con JUnit5 para poder usar mocks, espías y capturadores en los tests
@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    private final Artista artista = Artista.builder().nombre("The Beatles").build();

    private final Album album1 = Album.builder()
            .id(1L)
            .nombre("Abbey Road")
            .artista(artista)
            .genero("Rock")
            .precio(19.99f)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2L)
            .nombre("Thriller")
            .artista(Artista.builder().nombre("Michael Jackson").build())
            .genero("Pop")
            .precio(15.29f)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumResponseDto albumResponse1;

    // usamos el repositorio totalmente simulado
    @Mock
    private AlbumRepository albumRepository;
    // usamos el servicio de artistas simulado
    @Mock
    private ArtistaService artistaService;
    // usamos el mapper real aunque en modo espía que nos permite simular algunas partes del mismo
    @Spy
    private AlbumMapper albumMapper;
    // La parte de WebSockets también simulada
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private AlbumNotificationMapper albumNotificationMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WebSocketHandler webSocketService;

    // Es la clase que se testea y a la que se inyectan los mocks y espías automáticamente
    @InjectMocks
    private AlbumServiceImpl albumService;

    // Capturador de argumentos
    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    @BeforeEach
    void setUp() {
        albumResponse1 = albumMapper.toAlbumResponseDto(album1);
        // Quizá no la necesitemos
        // albumResponse2 = albumMapper.toAlbumResponseDto(album2);
        albumService.setWebSocketService(webSocketService);
    }

    @Test
    void findAll_ShouldReturnAllAlbums_WhenNoParametersProvided() {
        // Arrange
        List<Album> expectedAlbums = Arrays.asList(album1, album2);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findAll()).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(null, null);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        // verifica que findAll() se ejecuta una vez
        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombre_WhenNombreParameterProvided() {
        // Arrange
        String nombre = "Abbey Road";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findByNombreContainingIgnoreCase(nombre)).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(nombre, null);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        // Verifica que solo se ejecuta este método
        verify(albumRepository, only()).findByNombreContainingIgnoreCase(nombre);
    }

    @Test
    void findAll_ShouldReturnAlbumsByArtista_WhenArtistaParameterProvided() {
        // Arrange
        String artistaNombre = "Beatles";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findByArtistaNombreContainingIgnoreCase(artistaNombre)).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(null, artistaNombre);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        verify(albumRepository, only()).findByArtistaNombreContainingIgnoreCase(artistaNombre);
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombreAndArtista_WhenBothParametersProvided() {
        // Arrange
        String nombre = "Abbey";
        String artistaNombre = "Beatles";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findByNombreAndArtista(nombre, artistaNombre)).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(nombre, artistaNombre);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        verify(albumRepository, only()).findByNombreAndArtista(nombre, artistaNombre);
    }

    @Test
    void findById_ShouldReturnAlbum_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;
        AlbumResponseDto expectedAlbumResponse = albumResponse1;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.findById(id);

        // Assert
        assertEquals(expectedAlbumResponse, actualAlbumResponse);

        // Verify
        verify(albumRepository, only()).findById(id);
    }

    @Test
    void findById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(AlbumNotFoundException.class, () -> albumService.findById(id));
        assertEquals("Álbum con id " + id + " no encontrado.", res.getMessage());

        // Verify
        // verifica que se ejecuta el método
        verify(albumRepository).findById(id);
    }

    @Test
    void findByUuid_ShouldReturnAlbum_WhenValidUuidProvided() {
        // Arrange
        UUID expectedUuid = album1.getUuid();
        AlbumResponseDto expectedAlbumResponse = albumResponse1;
        when(albumRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(album1));

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.findByUuid(expectedUuid.toString());

        // Assert
        assertEquals(expectedAlbumResponse, actualAlbumResponse);

        // Verify
        verify(albumRepository, only()).findByUuid(expectedUuid);
    }

    @Test
    void findByUuid_ShouldThrowAlbumBadUuid_WhenInvalidUuidProvided() {
        // Arrange
        String uuid = "1234";
        // Act & Assert
        var res = assertThrows(AlbumBadUuidException.class, () -> albumService.findByUuid(uuid));
        assertEquals("El UUID " + uuid + " no es válido", res.getMessage());

        // Verify
        // verifica que no se ha ejecutado
        verify(albumRepository, never()).findByUuid(any());
    }

    @Test
    void save_ShouldReturnSavedAlbum_WhenValidAlbumCreateDtoProvided() throws IOException {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road")
                .artista("The Beatles")
                .genero("Rock")
                .precio(19.99f)
                .build();

        Album expectedAlbum = Album.builder()
                .id(1L)
                .nombre("Abbey Road")
                .artista(artista)
                .genero("Rock")
                .precio(19.99f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        AlbumResponseDto expectedAlbumResponse = albumMapper.toAlbumResponseDto(expectedAlbum);

        when(artistaService.findByNombre(createDto.getArtista())).thenReturn(artista);
        when(albumRepository.save(any(Album.class))).thenReturn(expectedAlbum);
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.save(createDto);

        // Assert
        assertEquals(expectedAlbumResponse, actualAlbumResponse);

        // Verify
        verify(albumRepository).save(albumCaptor.capture());

        Album albumCaptured = albumCaptor.getValue();
        assertEquals(expectedAlbum.getNombre(), albumCaptured.getNombre());
        // equivalente con AsssertJ en lugar de JUnit
        //assertThat(albumCaptured.getNombre()).isEqualTo(expectedAlbum.getNombre());
    }

    @Test
    void update_ShouldReturnUpdatedAlbum_WhenValidIdAndAlbumUpdateDtoProvided() throws IOException {
        // Arrange
        Long id = 1L;
        Float nuevoPrecio = 500.0f;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .precio(nuevoPrecio)
                .build();

        Album albumUpdate = albumMapper.toAlbum(updateDto, album1);
        when(albumRepository.save(any(Album.class))).thenReturn(albumUpdate);

        albumResponse1.setPrecio(nuevoPrecio);
        AlbumResponseDto expectedAlbumResponse = albumResponse1;
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.update(id, updateDto);

        // Assert
        // con Junit da error si no se ignoran campos de fecha que cambian
        // assertEquals(expectedAlbumResponse, actualAlbumResponse);
        // con AssertJ podemos excluir algún campo
        assertThat(actualAlbumResponse)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(expectedAlbumResponse);

        // Verify
        verify(albumRepository).findById(id);
        verify(albumRepository).save(any());
    }

    @Test
    void update_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .precio(500.0f)
                .build();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        // con AssertJ
        assertThatThrownBy(
                () -> albumService.update(id, updateDto))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Álbum con id " + id + " no encontrado.");
        // con JUnit
        //var res = assertThrows(AlbumNotFoundException.class,
        //    () -> albumService.update(id, updateDto));
        //assertEquals("Álbum con id " + id + " no encontrado.", res.getMessage());

        // Verify
        verify(albumRepository).findById(id);
        verify(albumRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteAlbum_WhenValidIdProvided() throws IOException {
        // Arrange
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        // con AssertJ
        assertThatCode(() -> albumService.deleteById(id))
                .doesNotThrowAnyException();

        // Assert
        verify(albumRepository).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        // con JUnit
        //var res = assertThrows(AlbumNotFoundException.class, () -> albumService.deleteById(id));
        //assertEquals("Álbum con id " + id + " no encontrado.", res.getMessage());
        // El equivalente con AssertJ
        assertThatThrownBy(() -> albumService.deleteById(id))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Álbum con id " + id + " no encontrado.");

        // Verify
        verify(albumRepository, never()).deleteById(id);
    }

    @Test
    void onChange_ShouldSendMessage_WhenValidDataProvided() throws IOException {
        // Arrange
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        albumService.onChange(Notificacion.Tipo.CREATE, album1);
    }
}