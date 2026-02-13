package org.example.albumes.services;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.mappers.AlbumMapper;
import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.repositories.AlbumRepository;
import org.example.rest.albumes.services.AlbumServiceImpl;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
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

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void save_ShouldSaveAlbum() {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .titulo("Test Album")
                .artista("Artista Test") // Cambiado de artistaId a artista (String)
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

