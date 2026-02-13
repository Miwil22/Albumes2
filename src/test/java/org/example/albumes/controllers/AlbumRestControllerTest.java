package org.example.albumes.controllers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.exceptions.AlbumNotFoundException;
import org.example.rest.albumes.services.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumRestControllerTest {

  private final String ENDPOINT = "/api/v1/albumes";

  private final AlbumResponseDto albumResponse1 = AlbumResponseDto.builder()
      .id(1L)
      .titulo("The Black Parade")
      .genero("Rock")
      .fechaLanzamiento(LocalDate.of(2006,10,23))
      .artista("My Chemical Romance")
      .precio(19.99)
      .build();

  private final AlbumResponseDto albumResponse2 = AlbumResponseDto.builder()
      .id(2L)
      .titulo("Random Access Memories")
      .genero("Electronic")
      .fechaLanzamiento(LocalDate.of(2013,5,17))
      .artista("Daft Punk")
      .precio(24.99)
      .build();

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private AlbumService albumService;

  @Test
  void getAll() {
    // Arrange
    var albumResponses = List.of(albumResponse1, albumResponse2);
    var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    var page = new PageImpl<>(albumResponses);
    when(albumService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable))
        .thenReturn(page);

    // Act. Consultar el endpoint
    var result = mockMvcTester.get()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse1);
          assertThat(json).extractingPath("$.content[1]")
              .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
        });

    // Verify
    verify(albumService, times(1))
        .findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
  }

  @Test
  void getAllByTitulo() {
    // Arrange
    var albumResponses = List.of(albumResponse2);
    String queryString = "?titulo=" + albumResponse2.getTitulo();
    Optional<String> titulo = Optional.of(albumResponse2.getTitulo());
    var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    var page = new PageImpl<>(albumResponses);
    when(albumService.findAll(titulo, Optional.empty(), Optional.empty(), pageable))
        .thenReturn(page);

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + queryString)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
        });

    // Verify
    verify(albumService, times(1))
        .findAll(titulo, Optional.empty(), Optional.empty(), pageable);
  }

  @Test
  void getAllByArtista() {
    // Arrange
    var albumResponses = List.of(albumResponse2);
    String queryString = "?artista=" + albumResponse2.getArtista();
    Optional<String> artista = Optional.of(albumResponse2.getArtista());
    var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    var page = new PageImpl<>(albumResponses);
    when(albumService.findAll(Optional.empty(), artista, Optional.empty(), pageable))
        .thenReturn(page);

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + queryString)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(AlbumResponseDto.class).isEqualTo(albumResponse2);
        });

    // Verify
    verify(albumService, only())
        .findAll(Optional.empty(), artista, Optional.empty(), pageable);
  }
}