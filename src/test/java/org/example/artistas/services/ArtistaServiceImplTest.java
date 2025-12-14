package org.example.artistas.services;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import org.example.artistas.services.ArtistaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceImplTest {

    private final Artista artista = Artista.builder().id(1L).nombre("Queen").build();
    private final ArtistaRequestDto artistaDto = ArtistaRequestDto.builder().nombre("Queen").build();

    @Mock
    private ArtistaRepository artistaRepository;

    @Spy
    private ArtistaMapper artistaMapper;

    @InjectMocks
    private ArtistaServiceImpl artistaService;

    @Test
    public void testFindAll() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(artista));
        when(artistaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        var res = artistaService.findAll(Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertNotNull(res);
        assertFalse(res.isEmpty());
        assertEquals(1, res.getTotalElements());

        // Verify
        verify(artistaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindByNombre() {
        when(artistaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(artista));
        var res = artistaService.findByNombre("Queen");
        assertEquals("Queen", res.getNombre());
    }

    @Test
    public void testFindById() {
        when(artistaRepository.findById(anyLong())).thenReturn(Optional.of(artista));
        var res = artistaService.findById(1L);
        assertEquals("Queen", res.getNombre());
    }

    @Test
    void save_ShouldSaveArtista(){
        when(artistaRepository.findByNombreEqualsIgnoreCase("Queen")).thenReturn(Optional.empty());
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        Artista result = artistaService.save(artistaDto);

        assertEquals("Queen", result.getNombre());
        verify(artistaRepository).save(any(Artista.class));
    }

    @Test
    void save_ShouldThrowConflict_IfExists(){
        when(artistaRepository.findByNombreEqualsIgnoreCase("Queen")).thenReturn(Optional.of(artista));
        assertThrows(ArtistaConflictException.class, () -> artistaService.save(artistaDto));
        verify(artistaRepository, never()).save(any(Artista.class));
    }

    @Test
    public void testUpdate() {
        when(artistaRepository.findById(anyLong())).thenReturn(Optional.of(artista));
        when(artistaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(artista)); // Mismo nombre, mismo ID
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        var res = artistaService.update(1L, artistaDto);
        assertEquals("Queen", res.getNombre());
    }

    @Test
    public void testUpdateConflict() {
        Artista otroArtista = Artista.builder().id(2L).nombre("Queen").build();
        when(artistaRepository.findById(anyLong())).thenReturn(Optional.of(artista));
        when(artistaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(otroArtista)); // Existe otro con ese nombre

        assertThrows(ArtistaConflictException.class, () -> artistaService.update(1L, artistaDto));
    }

    @Test
    void delete_ShouldDelete_IfNoAlbums(){
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(artistaRepository.existsAlbumById(1L)).thenReturn(false);

        artistaService.deleteById(1L);
        verify(artistaRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowConflict_IfHasAlbums(){
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(artistaRepository.existsAlbumById(1L)).thenReturn(true);

        assertThrows(ArtistaConflictException.class, () -> artistaService.deleteById(1L));
        verify(artistaRepository, never()).deleteById(anyLong());
    }
}