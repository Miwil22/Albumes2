package org.example.artistas.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.services.ArtistasService;
import org.example.rest.artistas.exceptions.ArtistaNotFoundException;
import org.example.rest.artistas.exceptions.ArtistaConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ArtistaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private ArtistasService artistasService;


    @Autowired
    private ObjectMapper objectMapper;

    private final Artista artista = Artista.builder()
            .id(1L)
            .nombre("Artista Test")
            .nacionalidad("Testland")
            .build();

    @Test
    void getAll_ShouldReturnPageOfArtistas() throws Exception {
        Mockito.when(artistasService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 10, Sort.by("id").ascending())))
                .thenReturn(new PageImpl<>(List.of(artista)));

        mockMvc.perform(get("/api/v1/artistas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Artista Test"))
                .andExpect(jsonPath("$.content[0].nacionalidad").value("Testland"));
    }

    @Test
    void getById_ShouldReturnArtista() throws Exception {
        Mockito.when(artistasService.findById(1L)).thenReturn(artista);

        mockMvc.perform(get("/api/v1/artistas/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Artista Test"));
    }

    @Test
    void getById_ShouldReturnNotFound() throws Exception {
        Mockito.when(artistasService.findById(1L)).thenThrow(new ArtistaNotFoundException(1L));

        mockMvc.perform(get("/api/v1/artistas/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturnCreatedArtista() throws Exception {
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder()
                .nombre("New Artista")
                .nacionalidad("USA")
                .build();

        Artista createdArtista = Artista.builder()
                .id(2L)
                .nombre("New Artista")
                .nacionalidad("USA")
                .build();

        Mockito.when(artistasService.save(any(ArtistaRequestDto.class))).thenReturn(createdArtista);

        mockMvc.perform(post("/api/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("New Artista"));
    }

    @Test
    void create_whenNombreExists_ShouldReturnConflict() throws Exception {
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder()
                .nombre("Existing Artista")
                .nacionalidad("Spain")
                .build();

        Mockito.when(artistasService.save(any(ArtistaRequestDto.class)))
                .thenThrow(new ArtistaConflictException("Conflicto"));

        mockMvc.perform(post("/api/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_ShouldReturnUpdatedArtista() throws Exception {
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder()
                .nombre("Updated Artista")
                .nacionalidad("Spain")
                .build();

        Artista updatedArtista = Artista.builder()
                .id(1L)
                .nombre("Updated Artista")
                .nacionalidad("Spain")
                .build();

        Mockito.when(artistasService.update(eq(1L), any(ArtistaRequestDto.class))).thenReturn(updatedArtista);

        mockMvc.perform(put("/api/v1/artistas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Updated Artista"));
    }

    @Test
    void update_ShouldReturnNotFound() throws Exception {
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder()
                .nombre("Updated Artista")
                .nacionalidad("Spain")
                .build();

        Mockito.when(artistasService.update(eq(99L), any(ArtistaRequestDto.class)))
                .thenThrow(new ArtistaNotFoundException(99L));

        mockMvc.perform(put("/api/v1/artistas/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(artistasService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/artistas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(artistasService, times(1)).deleteById(1L);
    }
}

