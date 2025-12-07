package artistas.controllers;

import org.example.Application;
import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class ArtistaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistaService artistaService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Artista artista1 = Artista.builder().id(1L).nombre("Queen").build();

    @Test
    void getAll() throws Exception {
        when(artistaService.findAll(null)).thenReturn(List.of(artista1));

        mockMvc.perform(get("/api/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Queen")));
    }

    @Test
    void getById() throws Exception {
        when(artistaService.findById(1L)).thenReturn(artista1);

        mockMvc.perform(get("/api/v1/artistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Queen")));
    }

    @Test
    void getById_NotFound() throws Exception {
        when(artistaService.findById(99L)).thenThrow(new ArtistaNotFoundException(99L));

        mockMvc.perform(get("/api/v1/artistas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturnCreated() throws Exception {
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("Queen").build();
        when(artistaService.save(any(ArtistaRequestDto.class))).thenReturn(artista1);

        mockMvc.perform(post("/api/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Queen")));
    }

    @Test
    void create_BadRequest() throws Exception {
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("").build(); // Nombre vacío

        mockMvc.perform(post("/api/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("Queen Updated").build();
        Artista updated = Artista.builder().id(1L).nombre("Queen Updated").build();
        when(artistaService.update(eq(1L), any(ArtistaRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Queen Updated")));
    }

    @Test
    void delete_OK() throws Exception {
        doNothing().when(artistaService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/artistas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_Conflict() throws Exception {
        doThrow(new ArtistaConflictException("Tiene albumes")).when(artistaService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/artistas/1"))
                .andExpect(status().isConflict());
    }
}