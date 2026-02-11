package org.example.albumes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.services.AlbumService;
import org.example.rest.artistas.models.Artista;
import org.example.utils.pagination.PaginationLinksUtils;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
public class AlbumRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private PaginationLinksUtils paginationLinksUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private final Artista artista = Artista.builder()
            .id(1L)
            .nombre("Artista Test")
            .nacionalidad("Testland")
            .build();

    private final AlbumResponseDto albumResponseDto = AlbumResponseDto.builder()
            .id(1L)
            .titulo("Album Test") // CORREGIDO: titulo en lugar de nombre
            .genero("Rock")
            .precio(10.0)
            .artista(artista)
            .uuid(UUID.randomUUID())
            .build();

    @Test
    void findAll_ShouldReturnPageOfAlbums() throws Exception {
        Mockito.when(albumService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), PageRequest.of(0, 10, Sort.by("id").ascending())))
                .thenReturn(new PageImpl<>(List.of(albumResponseDto)));

        mockMvc.perform(get("/api/v1/albumes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo").value("Album Test")) // CORREGIDO
                .andExpect(jsonPath("$.content[0].genero").value("Rock"));
    }

    @Test
    void findById_ShouldReturnAlbum() throws Exception {
        Mockito.when(albumService.findById(1L)).thenReturn(albumResponseDto);

        mockMvc.perform(get("/api/v1/albumes/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Album Test")); // CORREGIDO
    }

    @Test
    void create_ShouldReturnCreatedAlbum() throws Exception {
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .titulo("New Album") // CORREGIDO
                .genero("Pop")
                .precio(15.0)
                .fechaLanzamiento(LocalDate.now())
                .artistaId(1L)
                .build();

        AlbumResponseDto createdResponse = AlbumResponseDto.builder()
                .id(2L)
                .titulo("New Album") // CORREGIDO
                .genero("Pop")
                .precio(15.0)
                .build();

        Mockito.when(albumService.save(any(AlbumCreateDto.class))).thenReturn(createdResponse);

        mockMvc.perform(post("/api/v1/albumes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("New Album")); // CORREGIDO
    }

    @Test
    void update_ShouldReturnUpdatedAlbum() throws Exception {
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .titulo("Updated Album") // CORREGIDO
                .precio(20.0)
                .build();

        AlbumResponseDto updatedResponse = AlbumResponseDto.builder()
                .id(1L)
                .titulo("Updated Album") // CORREGIDO
                .precio(20.0)
                .build();

        Mockito.when(albumService.update(eq(1L), any(AlbumUpdateDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/albumes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Updated Album")); // CORREGIDO
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(albumService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/albumes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(albumService, times(1)).deleteById(1L);
    }
}