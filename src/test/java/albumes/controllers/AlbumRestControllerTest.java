package albumes.controllers;

import org.example.Application;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.services.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class AlbumRestControllerTest {

    private final String ENDPOINT = "/api/v1/albumes";

    private final AlbumResponseDto albumResponse1 = AlbumResponseDto.builder()
            .id(1L)
            .nombre("Abbey Road")
            .artista("The Beatles")
            .genero("Rock")
            .precio(19.99f)
            .build();

    private final AlbumResponseDto albumResponse2 = AlbumResponseDto.builder()
            .id(2L)
            .nombre("Thriller")
            .artista("Michael Jackson")
            .genero("Pop")
            .precio(15.29f)
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AlbumService albumService;

    @Test
    void getAll() {
        // Arrange
        var albumResponses = List.of(albumResponse1, albumResponse2);
        when(albumService.findAll(null, null)).thenReturn(albumResponses);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
                });

        // Verify
        verify(albumService, times(1)).findAll(null, null);
    }

    @Test
    void getAllByNombre() {
        // Arrange
        var albumResponses = List.of(albumResponse2);
        String queryString = "?nombre=" + albumResponse2.getNombre();
        when(albumService.findAll(anyString(), isNull())).thenReturn(albumResponses);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
                });

        // Verify
        verify(albumService, times(1)).findAll(anyString(), isNull());
    }

    @Test
    void getAllByArtista() {
        // Arrange
        var albumResponses = List.of(albumResponse2);
        String queryString = "?artista=" + albumResponse2.getArtista();
        when(albumService.findAll(isNull(), anyString())).thenReturn(albumResponses);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
                });

        // Verify
        verify(albumService, only()).findAll(isNull(), anyString());
    }

    @Test
    void getAllByNombreAndArtista() {
        // Arrange
        var albumResponses = List.of(albumResponse2);
        String queryString = "?nombre=" + albumResponse2.getNombre() + "&"
                + "artista=" + albumResponse2.getArtista();
        when(albumService.findAll(anyString(), anyString())).thenReturn(albumResponses);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
                });

        // Verify
        verify(albumService, only()).findAll(anyString(), anyString());
    }


    @Test
    void getById_shouldReturnJsonWithAlbum_whenValidIdProvided() {
        // Arrange
        Long id = albumResponse1.getId();
        when(albumService.findById(id)).thenReturn(albumResponse1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumResponse1);

        // Verify
        verify(albumService, only()).findById(anyLong());

    }

    @Test
    void getById_shouldThrowAlbumNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        when(albumService.findById(anyLong())).thenThrow(new AlbumNotFoundException(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus4xxClientError()
                // throws AlbumNotFoundException
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessageContaining("no encontrado.");

        // Verify
        verify(albumService, only()).findById(anyLong());

    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Un Verano Sin Ti",
              "artista": "Bad Bunny",
              "genero": "Pop",
              "precio": 25.99
           }
           """;

        var albumSaved = AlbumResponseDto.builder()
                .id(1L)
                .nombre("Un Verano Sin Ti")
                .artista("Bad Bunny")
                .genero("Pop")
                .precio(25.99f)
                .build();

        when(albumService.save(any(AlbumCreateDto.class))).thenReturn(albumSaved);

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumSaved);

        verify(albumService, only()).save(any(AlbumCreateDto.class));


    }

    @Test
    void create_whenBadRequest() {
        // Arrange
        String requestBody = """
           {
              "nombre": "",
              "artista": "",
              "genero": "Cumbia",
              "precio": -10.0
           }
           """;

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path -> {
                    assertThat(path).hasFieldOrProperty("nombre");
                    assertThat(path).hasFieldOrProperty("artista");
                    assertThat(path).hasFieldOrProperty("genero");
                    assertThat(path).hasFieldOrProperty("precio");
                });

        verify(albumService, never()).save(any(AlbumCreateDto.class));

    }

    @Test
    void update() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "precio": 500.0
           }
           """;

        var albumSaved = AlbumResponseDto.builder()
                .id(1L)
                .nombre("Abbey Road")
                .artista("The Beatles")
                .precio(500.0f)
                .build();

        when(albumService.update(anyLong(), any(AlbumUpdateDto.class))).thenReturn(albumSaved);

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT+ "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumSaved);

        verify(albumService, only()).update(anyLong(), any(AlbumUpdateDto.class));

    }

    @Test
    void update_shouldThrowAlbumNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        String requestBody = """
           {
              "precio": 500.0
           }
           """;
        when(albumService.update(anyLong(), any(AlbumUpdateDto.class))).thenThrow(new AlbumNotFoundException(id));

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws AlbumNotFoundException
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessageContaining("no encontrado.");

        // Verify
        verify(albumService, only()).update(anyLong(), any());
    }

    @Test
    void updatePartial() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "precio": 500.0
           }
           """;

        var albumSaved = AlbumResponseDto.builder()
                .id(1L)
                .nombre("Abbey Road")
                .artista("The Beatles")
                .precio(500.0f)
                .build();

        when(albumService.update(anyLong(), any(AlbumUpdateDto.class))).thenReturn(albumSaved);

        // Act
        var result = mockMvcTester.patch()
                .uri(ENDPOINT+ "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumSaved);

        verify(albumService, only()).update(anyLong(), any(AlbumUpdateDto.class));
    }

    @Test
    void delete() {
        // Arrange
        Long id = 1L;
        doNothing().when(albumService).deleteById(anyLong());
        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(albumService, only()).deleteById(anyLong());

    }

    @Test
    void delete_shouldThrowAlbumNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        doThrow(new AlbumNotFoundException(id)).when(albumService).deleteById(anyLong());

        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws AlbumNotFoundException
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessageContaining("no encontrado.");

        // Verify
        verify(albumService, only()).deleteById(anyLong());

    }
}