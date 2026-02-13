package org.example.rest.artistas.services;

import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.models.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ArtistasService {
    Page<Artista> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Artista findByNombre(String nombre);

    Artista findById(Long id);

    Artista save(ArtistaRequestDto artistaRequestDto);

    Artista update(Long id, ArtistaRequestDto artistaRequestDto);

    void deleteById(Long id);
}