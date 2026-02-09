package org.example.artistas.services;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.mappers.ArtistasMapper;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceImplTest {

    @Mock
    private ArtistasRepository artistasRepository;

    @Mock
    private ArtistasMapper artistasMapper;

    @InjectMocks
    private ArtistasServiceImpl artistasService;

    @Test
    void findAll_ShouldReturnPage() {
        Pageable pageable = Pageable.unpaged();
        List<Artista> artistas = List.of(new Artista(), new Artista());
        Page<Artista> page = new PageImpl<>(artistas);

        when(artistasRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Artista> result = artistasService.findAll(Optional.empty(), Optional.empty(), pageable);

        assertEquals(2, result.getContent().size());
    }

    @Test
    void findById_ShouldReturnArtista() {
        Artista artista = new Artista();
        artista.setId(1L);

        when(artistasRepository.findById(1L)).thenReturn(Optional.of(artista));

        Artista result = artistasService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_ShouldThrowNotFound() {
        when(artistasRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ArtistaNotFoundException.class, () -> artistasService.findById(1L));
    }

    @Test
    void save_ShouldSaveArtista() {
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder().nombre("Test").nacionalidad("ES").build();
        Artista artista = Artista.builder().id(1L).nombre("Test").build();

        when(artistasRepository.findByNombreEqualsIgnoreCase("Test")).thenReturn(Optional.empty());
        when(artistasMapper.toArtista(requestDto)).thenReturn(artista);
        when(artistasRepository.save(artista)).thenReturn(artista);

        Artista result = artistasService.save(requestDto);

        assertNotNull(result);
        assertEquals("Test", result.getNombre());
    }

    @Test
    void save_ShouldThrowConflict_WhenNameExists() {
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder().nombre("Test").build();
        Artista existing = new Artista();

        when(artistasRepository.findByNombreEqualsIgnoreCase("Test")).thenReturn(Optional.of(existing));

        assertThrows(ArtistaConflictException.class, () -> artistasService.save(requestDto));
    }

    @Test
    void update_ShouldUpdateArtista() {
        Long id = 1L;
        ArtistaRequestDto requestDto = ArtistaRequestDto.builder().nombre("Updated").build();
        Artista existing = new Artista();
        existing.setId(id);
        Artista updated = Artista.builder().id(id).nombre("Updated").build();

        when(artistasRepository.findById(id)).thenReturn(Optional.of(existing));
        // when(artistasRepository.findByNombreEqualsIgnoreCase("Updated")).thenReturn(Optional.empty()); // No conflict
        when(artistasMapper.toArtista(requestDto, existing)).thenReturn(updated);
        when(artistasRepository.save(updated)).thenReturn(updated);

        Artista result = artistasService.update(id, requestDto);

        assertEquals("Updated", result.getNombre());
    }

    @Test
    void delete_ShouldDelete_WhenNoAlbums() {
        Long id = 1L;
        Artista artista = new Artista();
        artista.setId(id);

        when(artistasRepository.findById(id)).thenReturn(Optional.of(artista));
        when(artistasRepository.existsAlbumById(id)).thenReturn(false);

        artistasService.deleteById(id);

        verify(artistasRepository).deleteById(id);
    }

    @Test
    void delete_ShouldThrowConflict_WhenHasAlbums() {
        Long id = 1L;
        Artista artista = new Artista();
        artista.setId(id);

        when(artistasRepository.findById(id)).thenReturn(Optional.of(artista));
        when(artistasRepository.existsAlbumById(id)).thenReturn(true);

        assertThrows(ArtistaConflictException.class, () -> artistasService.deleteById(id));
        verify(artistasRepository, never()).deleteById(id);
    }
}