package org.example.artistas.controllers;

import org.example.Application;
import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class ArtistaRestControllerTest {

    private final String ENDPOINT = "/api/v1/artistas";
    private final Artista artista1 = Artista.builder().id(1L).nombre("Queen").build();
    private final Artista artista2 = Artista.builder().id(2L).nombre("AC/DC").build();

    @Autowired
    private MockMvcTester mockMvcTester; // USAMOS EL TESTER MODERNO

    @MockitoBean
    private ArtistaService artistaService;

    @Test
    void getAll() {
        var artistas = List.of(artista1, artista2);
        when(artistaService.findAll(null)).thenReturn(artistas);

        var result = mockMvcTester.get().uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result).hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(artistas.size());
                    assertThat(json).extractingPath("$[0]").convertTo(Artista.class).usingRecursiveComparison().isEqualTo(artista1);
                    assertThat(json).extractingPath("$[1]").convertTo(Artista.class).usingRecursiveComparison().isEqualTo(artista2);
                });
        verify(artistaService, times(1)).findAll(null);
    }

    @Test
    void getAllByNombre() {
        var artistas = List.of(artista2);
        String queryString = "?nombre=" + artista2.getNombre();
        when(artistaService.findAll(anyString())).thenReturn(artistas);

        var result = mockMvcTester.get().uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result).hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(artistas.size());
                    assertThat(json).extractingPath("$[0]").convertTo(Artista.class).usingRecursiveComparison().isEqualTo(artista2);
                });
        verify(artistaService, times(1)).findAll(anyString());
    }

    @Test
    void getById() {
        Long id = artista1.getId();
        when(artistaService.findById(id)).thenReturn(artista1);

        var result = mockMvcTester.get().uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result).hasStatusOk()
                .bodyJson().convertTo(Artista.class).usingRecursiveComparison().isEqualTo(artista1);
        verify(artistaService, only()).findById(anyLong());
    }

    @Test
    void getById_shouldThrowArtistaNotFound() {
        Long id = 3L;
        when(artistaService.findById(anyLong())).thenThrow(new ArtistaNotFoundException(id));

        var result = mockMvcTester.get().uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result).hasStatus4xxClientError()
                .hasFailed().failure().isInstanceOf(ArtistaNotFoundException.class).hasMessageContaining("no encontrado");
        verify(artistaService, only()).findById(anyLong());
    }

    @Test
    void create() {
        String requestBody = """
           { "nombre": "Nirvana" }
           """;
        var artistaSaved = Artista.builder().id(1L).nombre("Nirvana").build();
        when(artistaService.save(any(ArtistaRequestDto.class))).thenReturn(artistaSaved);

        var result = mockMvcTester.post().uri(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody).exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED)
                .bodyJson().convertTo(Artista.class).usingRecursiveComparison().isEqualTo(artistaSaved);
        verify(artistaService, only()).save(any(ArtistaRequestDto.class));
    }

    @Test
    void create_whenBadRequest() {
        String requestBody = """
           { "nombre": null }
           """;
        var result = mockMvcTester.post().uri(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody).exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().hasPathSatisfying("$.errores", path -> assertThat(path).hasFieldOrProperty("nombre"));
        verify(artistaService, never()).save(any(ArtistaRequestDto.class));
    }

    @Test
    void create_whenNombreExists() {
        String requestBody = """
           { "nombre": "Queen" }
           """;
        when(artistaService.save(any(ArtistaRequestDto.class))).thenThrow(new ArtistaConflictException("Ya existe un artista"));

        var result = mockMvcTester.post().uri(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody).exchange();

        assertThat(result).hasStatus(HttpStatus.CONFLICT)
                .hasFailed().failure().isInstanceOf(ArtistaConflictException.class);
        verify(artistaService, only()).save(any(ArtistaRequestDto.class));
    }

    @Test
    void update() {
        Long id = 1L;
        String requestBody = """
           { "nombre": "QUEEN" }
           """;
        var artistaSaved = Artista.builder().id(1L).nombre("QUEEN").build();
        when(artistaService.update(anyLong(), any(ArtistaRequestDto.class))).thenReturn(artistaSaved);

        var result = mockMvcTester.put().uri(ENDPOINT+ "/" + id).contentType(MediaType.APPLICATION_JSON).content(requestBody).exchange();

        assertThat(result).hasStatusOk()
                .bodyJson().convertTo(Artista.class).usingRecursiveComparison().isEqualTo(artistaSaved);
        verify(artistaService, only()).update(anyLong(), any(ArtistaRequestDto.class));
    }

    @Test
    void delete() {
        Long id = 1L;
        doNothing().when(artistaService).deleteById(anyLong());
        var result = mockMvcTester.delete().uri(ENDPOINT+ "/" + id).exchange();
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        verify(artistaService, only()).deleteById(anyLong());
    }
}